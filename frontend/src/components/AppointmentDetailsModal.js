import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TouchableWithoutFeedback,
  Image,
  ScrollView,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import Toast from 'react-native-toast-message';
import DefaultUser from '../../assets/default_user.png';
import { formatCurrency } from '../utils/formatters';
import toastConfig from '../config/toastConfig';
import AvaliacaoService from '../services/AvaliacaoService';
import RatingModal from './RatingModal';


const AppointmentDetailsModal = ({ visible, appointment, onClose, onEdit, onCancel, onRefresh, isProfessional = false, showEditButton = true, showCancelButton = true }) => {
  const [isRatingModalVisible, setIsRatingModalVisible] = useState(false);

  // Usar as informações que já vêm do backend
  const canRate = appointment?.podeAvaliar === true;
  const hasRated = appointment?.podeAvaliar === false;
  const existingRating = hasRated ? {
    idAvaliacao: appointment?.idAvaliacao,
    rating: appointment?.ratingAvaliacao,
    descricao: appointment?.descricaoAvaliacao
  } : null;

  const handleRatePress = () => {
    setIsRatingModalVisible(true);
  };

  const handleRatingSuccess = () => {
    // Fechar modal e atualizar a lista
    setIsRatingModalVisible(false);
    onClose();
    if (onRefresh) {
      onRefresh();
    }
  };

  if (!appointment) return null;

  const formatDate = (date) => {
    return format(new Date(date), "d 'de' MMMM 'de' yyyy", { locale: ptBR });
  };

  const formatTime = (start, end) => {
    const startTime = format(new Date(start), 'HH:mm');
    const endTime = format(new Date(end), 'HH:mm');
    return `${startTime} - ${endTime}`;
  };

  const formatServiceType = (type) => {
    if (!type) return 'Serviço não especificado';
    switch (type) {
      case 'TATUAGEM_PEQUENA':
        return 'Tatuagem Pequena';
      case 'TATUAGEM_MEDIA':
        return 'Tatuagem Média';
      case 'TATUAGEM_GRANDE':
        return 'Tatuagem Grande';
      case 'SESSAO':
        return 'Sessão';
      default:
        return type.replace('TATUAGEM_', '').toLowerCase()
          .split('_')
          .map(word => word.charAt(0).toUpperCase() + word.slice(1))
          .join(' ');
    }
  };

  const formatAddress = (appointment) => {
    const parts = [];

    if (appointment.rua) {
      let endereco = appointment.rua;
      if (appointment.numero) {
        endereco += `, ${appointment.numero}`;
      }
      parts.push(endereco);
    }
    
    if (appointment.complemento) {
      parts.push(appointment.complemento);
    }
    
    if (appointment.bairro) {
      parts.push(appointment.bairro);
    }
    
    if (appointment.cidade && appointment.estado) {
      parts.push(`${appointment.cidade}/${appointment.estado}`);
    } else if (appointment.cidade) {
      parts.push(appointment.cidade);
    } else if (appointment.estado) {
      parts.push(appointment.estado);
    }
    
    return parts.join('\n');
  };

  const getStatusLabel = (status) => {
    switch(status) {
      case 'AGENDADO': return 'Agendado';
      case 'CANCELADO': return 'Cancelado';
      case 'CONCLUIDO': return 'Concluído';
      default: return status || 'Agendado';
    }
  };
  
  const isCanceled = appointment.status?.toUpperCase() === 'CANCELADO';
  
  const getStatusBadgeStyle = () => {
    switch(appointment.status?.toUpperCase()) {
      case 'AGENDADO': 
        return { backgroundColor: '#E0F2FE', textColor: '#0369A1' };
      case 'CANCELADO': 
        return { backgroundColor: '#FEE2E2', textColor: '#DC2626' };
      case 'CONCLUIDO': 
        return { backgroundColor: '#DCFCE7', textColor: '#16A34A' };
      default: 
        return { backgroundColor: '#E0F2FE', textColor: '#0369A1' };
    }
  };
  
  const statusStyle = getStatusBadgeStyle();

  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="fade"
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={onClose}>
        <View style={styles.modalBackdrop}>
          <TouchableWithoutFeedback onPress={(e) => e.stopPropagation()}>
            <View style={styles.modalContainer}>
              <View style={styles.modalHeader}>
                <Text style={styles.modalTitle}>Detalhes do Agendamento</Text>
                <Text style={styles.modalDate}>{formatDate(appointment.dtInicio)}</Text>
              </View>

              <ScrollView style={styles.modalContent}>
                <View style={styles.artistInfo}>
                  {!isProfessional && (
                    <Image
                      source={appointment.imagemPerfilProfissional ? 
                        { uri: appointment.imagemPerfilProfissional } : 
                        DefaultUser
                      }
                      style={styles.artistImage}
                    />
                  )}
                  {isProfessional && (
                    <View style={styles.avatarPlaceholder}>
                      <Text style={styles.avatarText}>
                        {appointment.nomeUsuario ? appointment.nomeUsuario.charAt(0).toUpperCase() : 'U'}
                      </Text>
                    </View>
                  )}
                  <View>
                    <Text style={styles.artistName}>
                      {isProfessional ? 
                        (appointment.nomeUsuario || 'Nome não disponível') :
                        (appointment.nomeProfissional || 'Nome não disponível')
                      }
                    </Text>
                    <View style={styles.badgeContainer}>
                      <MaterialIcons name="person" size={12} color="#64748B" />
                      <Text style={styles.badgeText}>
                        {isProfessional ? 'Cliente' : 'Tatuador'}
                      </Text>
                    </View>
                  </View>
                </View>

                <View style={styles.divider} />

                <View style={styles.detailSection}>
                  <View style={styles.detailRow}>
                    <MaterialIcons name="design-services" size={18} color="#111" />
                    <Text style={styles.detailLabel}>Serviço</Text>
                  </View>
                  <Text style={styles.detailValue}>
                    {formatServiceType(appointment.tipoServico)}
                  </Text>
                </View>

                <View style={styles.detailSection}>
                  <View style={styles.detailRow}>
                    <MaterialIcons name="event" size={18} color="#111" />
                    <Text style={styles.detailLabel}>Data</Text>
                  </View>
                  <Text style={styles.detailValue}>
                    {formatDate(appointment.dtInicio)}
                  </Text>
                </View>

                <View style={styles.detailSection}>
                  <View style={styles.detailRow}>
                    <MaterialIcons name="access-time" size={18} color="#111" />
                    <Text style={styles.detailLabel}>Horário</Text>
                  </View>
                  <Text style={styles.detailValue}>
                    {formatTime(appointment.dtInicio, appointment.dtFim)}
                  </Text>
                </View>

                {appointment.valor && (
                  <View style={styles.detailSection}>
                    <View style={styles.detailRow}>
                      <MaterialIcons name="attach-money" size={18} color="#111" />
                      <Text style={styles.detailLabel}>Valor</Text>
                    </View>
                    <Text style={styles.detailValue}>
                      {formatCurrency(appointment.valor)}
                    </Text>
                  </View>
                )}

                <View style={styles.detailSection}>
                  <View style={styles.detailRow}>
                    <MaterialIcons name="location-on" size={18} color="#111" />
                    <Text style={styles.detailLabel}>Local</Text>
                  </View>
                  <Text style={styles.detailValue}>
                    {formatAddress(appointment)}
                  </Text>
                </View>

                {appointment.descricao && (
                  <View style={styles.detailSection}>
                    <View style={styles.detailRow}>
                      <MaterialIcons name="description" size={18} color="#111" />
                      <Text style={styles.detailLabel}>Descrição</Text>
                    </View>
                    <Text style={styles.detailValue}>
                      {appointment.descricao}
                    </Text>
                  </View>
                )}

                {existingRating && (
                  <View style={styles.ratingSection}>
                    <View style={styles.detailRow}>
                      <MaterialIcons name="star" size={18} color="#FFD700" />
                      <Text style={styles.detailLabel}>Sua Avaliação</Text>
                    </View>
                    <View style={styles.ratingDisplay}>
                      <View style={styles.starsRow}>
                        {[1, 2, 3, 4, 5].map((star) => (
                          <MaterialIcons
                            key={star}
                            name={star <= existingRating.rating ? 'star' : 'star-border'}
                            size={16}
                            color={star <= existingRating.rating ? '#FFD700' : '#CBD5E1'}
                          />
                        ))}
                        <Text style={styles.ratingValue}>
                          {existingRating.rating}/5
                        </Text>
                      </View>
                      {existingRating.descricao && (
                        <Text style={styles.ratingComment}>
                          "{existingRating.descricao}"
                        </Text>
                      )}
                    </View>
                  </View>
                )}

                <View style={styles.statusSection}>
                  <Text style={styles.statusLabel}>Status</Text>
                  <View style={[styles.statusBadge, { backgroundColor: statusStyle.backgroundColor }]}>
                    <Text style={[styles.statusText, { color: statusStyle.textColor }]}>
                      {getStatusLabel(appointment.status)}
                    </Text>
                  </View>
                </View>
              </ScrollView>

              {(!isCanceled && (showEditButton || showCancelButton)) || (canRate && !isProfessional) ? (
                <View style={styles.buttonRow}>
                  {!isCanceled && showEditButton && (
                    <TouchableOpacity 
                                              style={[styles.editButton, (!showCancelButton && !canRate) && { marginRight: 0 }]} 
                      onPress={onEdit}
                    >
                      <MaterialIcons name="edit" size={20} color="#000" />
                      <Text style={styles.editButtonText}>Editar</Text>
                    </TouchableOpacity>
                  )}
                  {!isCanceled && showCancelButton && (
                    <TouchableOpacity 
                                              style={[styles.cancelButton, (!showEditButton && !canRate) && { marginLeft: 0 }]} 
                      onPress={onCancel}
                    >
                      <MaterialIcons name="cancel" size={20} color="#E11D48" />
                      <Text style={styles.cancelButtonText}>Cancelar</Text>
                    </TouchableOpacity>
                  )}
                  {!isProfessional && canRate && (
                    <TouchableOpacity 
                      style={[styles.rateButton, (!showEditButton && !showCancelButton) && { marginLeft: 0, marginRight: 0 }]} 
                      onPress={handleRatePress}
                    >
                      <MaterialIcons 
                        name="star" 
                        size={20} 
                        color="#FFD700" 
                      />
                      <Text style={styles.rateButtonText}>
                        Avaliar
                      </Text>
                    </TouchableOpacity>
                  )}
                </View>
              ) : null}

              <TouchableOpacity 
                style={styles.closeButton} 
                onPress={onClose}
              >
                <Text style={styles.closeButtonText}>Fechar</Text>
              </TouchableOpacity>
            </View>
          </TouchableWithoutFeedback>
        </View>
      </TouchableWithoutFeedback>
      
      <RatingModal
        visible={isRatingModalVisible}
        appointment={appointment}
        onClose={() => setIsRatingModalVisible(false)}
        onSuccess={handleRatingSuccess}
      />
      
      <Toast 
        config={toastConfig} 
        style={{ 
          zIndex: 999999, 
          elevation: 999999,
          position: 'absolute',
          bottom: 50,
          left: 0,
          right: 0
        }} 
      />
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalBackdrop: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContainer: {
    width: '90%',
    maxWidth: 400,
    backgroundColor: '#fff',
    borderRadius: 12,
    overflow: 'hidden',
    maxHeight: '80%',
  },
  modalHeader: {
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#E2E8F0',
    backgroundColor: '#F8FAFC',
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111',
  },
  modalDate: {
    fontSize: 14,
    color: '#64748B',
    marginTop: 4,
  },
  modalContent: {
    padding: 16,
  },
  artistInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  artistImage: {
    width: 48,
    height: 48,
    borderRadius: 24,
    marginRight: 12,
  },
  avatarPlaceholder: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#E2E8F0',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  avatarText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#64748B',
  },
  artistName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111',
  },
  badgeContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 4,
  },
  badgeText: {
    fontSize: 12,
    color: '#64748B',
    marginLeft: 4,
  },
  divider: {
    height: 1,
    backgroundColor: '#E2E8F0',
    marginVertical: 16,
  },
  detailSection: {
    marginBottom: 16,
  },
  detailRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 4,
  },
  detailLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111',
    marginLeft: 8,
  },
  detailValue: {
    fontSize: 14,
    color: '#64748B',
    paddingLeft: 26,
  },
  statusSection: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginVertical: 8,
  },
  statusLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111',
  },
  statusBadge: {
    backgroundColor: '#E0F2FE',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  statusText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#0369A1',
  },
  buttonRow: {
    flexDirection: 'row',
    borderTopWidth: 1,
    borderTopColor: '#E2E8F0',
    padding: 16,
  },
  editButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#F1F5F9',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 16,
    marginRight: 8,
    flex: 1,
  },
  editButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#000',
    marginLeft: 6,
  },
  cancelButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FFF1F2',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 16,
    flex: 1,
  },
  cancelButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#E11D48',
    marginLeft: 6,
  },
  closeButton: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: 16,
    borderTopWidth: 1,
    borderTopColor: '#E2E8F0',
  },
  closeButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#64748B',
  },
  ratingSection: {
    marginBottom: 16,
  },
  ratingDisplay: {
    paddingLeft: 26,
  },
  starsRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  ratingValue: {
    fontSize: 14,
    color: '#64748B',
    marginLeft: 8,
    fontWeight: '500',
  },
  ratingComment: {
    fontSize: 14,
    color: '#64748B',
    fontStyle: 'italic',
  },
  rateButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FFF8DC',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 16,
    flex: 1,
    marginLeft: 8,
  },
  rateButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#B8860B',
    marginLeft: 6,
  },
});

export default AppointmentDetailsModal; 