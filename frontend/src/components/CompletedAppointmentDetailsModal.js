import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TouchableWithoutFeedback,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import DefaultUser from '../../assets/default_user.png';
import Input from './ui/Input';
import { formatCurrency } from '../utils/formatters';
import AvaliacaoService from '../services/AvaliacaoService';
import toastHelper from '../utils/toastHelper';
import StarRating from './ui/StarRating';
import RatingModal from './RatingModal';
import ImageWithAlt from './ui/ImageWithAlt';

const CompletedAppointmentDetailsModal = ({ visible, appointment, onClose, onRefresh, isProfessional = false, onOpenReview }) => {
  const [avaliacao, setAvaliacao] = useState(null);
  const [isLoadingAvaliacao, setIsLoadingAvaliacao] = useState(false);
  const [isRatingModalVisible, setIsRatingModalVisible] = useState(false);

  // Usar as informações que já vêm do backend
  const canRate = appointment?.podeAvaliar === true;
  const hasRated = appointment?.podeAvaliar === false;
  const existingRating = hasRated ? {
    idAvaliacao: appointment?.idAvaliacao,
    rating: appointment?.ratingAvaliacao,
    descricao: appointment?.descricaoAvaliacao
  } : null;

  useEffect(() => {
    if (visible && appointment && isProfessional) {
      loadAvaliacao();
    }
  }, [visible, appointment]);

  const loadAvaliacao = async () => {
    try {
      setIsLoadingAvaliacao(true);
      const avaliacaoData = await AvaliacaoService.buscarPorAgendamento(appointment.idAgendamento);
      setAvaliacao(avaliacaoData);
    } catch (error) {
      console.error('Erro ao carregar avaliação:', error);
    } finally {
      setIsLoadingAvaliacao(false);
    }
  };

  const handleRateAppointment = () => {
    setIsRatingModalVisible(true);
  };

  const handleRatingSuccess = () => {
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
                    <ImageWithAlt
                      source={{ uri: appointment.imagemPerfilProfissional }}
                      alt={`Foto de perfil do tatuador ${appointment.nomeProfissional}`}
                      style={styles.artistImage}
                      resizeMode="cover"
                      accessibilityLabel={`Foto de perfil do tatuador ${appointment.nomeProfissional}`}
                      fallbackIconName="person"
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
                  <View style={styles.avaliacaoSection}>
                    <Text style={styles.avaliacaoTitle}>Avaliação</Text>
                    <View style={styles.starsContainer}>
                      {[1, 2, 3, 4, 5].map((star) => (
                        <MaterialIcons
                          key={star}
                          name={star <= existingRating.rating ? "star" : "star-border"}
                          size={20}
                          color={star <= existingRating.rating ? "#FFD700" : "#E2E8F0"}
                          style={styles.starIcon}
                        />
                      ))}
                    </View>
                    {existingRating.descricao && (
                      <Text style={styles.avaliacaoComment}>
                        "{existingRating.descricao}"
                      </Text>
                    )}
                  </View>
                )}

                <View style={styles.statusSection}>
                  <Text style={styles.statusLabel}>Status</Text>
                  <View style={styles.statusBadge}>
                    <Text style={styles.statusText}>{getStatusLabel(appointment.status)}</Text>
                  </View>
                </View>

                {isProfessional && (
                  <View style={styles.avaliacaoSection}>
                    <Text style={styles.avaliacaoTitle}>Avaliação do Cliente</Text>
                    {isLoadingAvaliacao ? (
                      <View style={styles.loadingContainer}>
                        <ActivityIndicator size="small" color="#111" />
                        <Text style={styles.loadingText}>Carregando avaliação...</Text>
                      </View>
                    ) : avaliacao ? (
                      <>
                        <View style={styles.avaliacaoRating}>
                          <StarRating value={avaliacao.rating} size={28} editable={false} />
                          <Text style={styles.ratingText}>{avaliacao.rating}/5</Text>
                        </View>
                        {avaliacao.descricao && (
                          <View style={styles.avaliacaoComentario}>
                            <Text style={styles.comentarioLabel}>Comentário:</Text>
                            <Text style={styles.comentarioText}>{avaliacao.descricao}</Text>
                          </View>
                        )}
                      </>
                    ) : (
                      <Text style={styles.semAvaliacaoText}>
                        Este agendamento ainda não foi avaliado pelo cliente.
                      </Text>
                    )}
                  </View>
                )}
              </ScrollView>

              {(!isProfessional && onOpenReview && canRate) && (
                <View style={styles.buttonRow}>
                  <TouchableOpacity 
                    style={styles.rateButton} 
                    onPress={onOpenReview}
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
                </View>
              )}

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
    justifyContent: 'center',
  },
  rateButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FFF8DC',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 16,
    width: '80%',
  },
  rateButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#B8860B',
    marginLeft: 6,
  },
  avaliacaoSection: {
    marginBottom: 20,
    marginTop: 8,
  },
  avaliacaoTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111',
    marginBottom: 8,
  },
  starsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  starIcon: {
    marginRight: 2,
  },
  avaliacaoComment: {
    fontSize: 14,
    color: '#64748B',
    fontStyle: 'italic',
    lineHeight: 20,
    marginTop: 4,
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
  avaliacaoRating: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  ratingText: {
    marginLeft: 8,
    fontSize: 14,
    color: '#64748B',
    fontWeight: '500',
  },
  avaliacaoComentario: {
    marginTop: 8,
  },
  comentarioLabel: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
    marginBottom: 4,
  },
  comentarioText: {
    fontSize: 14,
    color: '#64748B',
    lineHeight: 20,
  },
  semAvaliacaoText: {
    fontSize: 14,
    color: '#64748B',
    fontStyle: 'italic',
  },
  loadingContainer: {
    alignItems: 'center',
    paddingVertical: 12,
  },
  loadingText: {
    marginTop: 8,
    fontSize: 14,
    color: '#64748B',
  },
});

export default CompletedAppointmentDetailsModal; 