import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  Image,
  ScrollView,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import DefaultUser from '../../assets/default_user.png';

const CompletedAppointmentDetailsModal = ({ visible, appointment, onClose }) => {
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

  const handleRateAppointment = () => {
    // TODO: implementar avaliação
  };

  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="fade"
      onRequestClose={onClose}
    >
      <View style={styles.modalBackdrop}>
        <View style={styles.modalContainer}>
          <View style={styles.modalHeader}>
            <Text style={styles.modalTitle}>Detalhes do Agendamento</Text>
            <Text style={styles.modalDate}>{formatDate(appointment.dtInicio)}</Text>
          </View>

          <ScrollView style={styles.modalContent}>
            <View style={styles.artistInfo}>
              <Image
                source={appointment.imagemPerfilProfissional ? 
                  { uri: appointment.imagemPerfilProfissional } : 
                  DefaultUser
                }
                style={styles.artistImage}
              />
              <View>
                <Text style={styles.artistName}>
                  {appointment.nomeProfissional || 'Nome não disponível'}
                </Text>
                <View style={styles.badgeContainer}>
                  <MaterialIcons name="person" size={12} color="#64748B" />
                  <Text style={styles.badgeText}>Tatuador</Text>
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

            <View style={styles.statusSection}>
              <Text style={styles.statusLabel}>Status</Text>
              <View style={styles.statusBadge}>
                <Text style={styles.statusText}>{getStatusLabel(appointment.status)}</Text>
              </View>
            </View>
          </ScrollView>

          <View style={styles.buttonRow}>
            <TouchableOpacity 
              style={styles.rateButton} 
              onPress={handleRateAppointment}
            >
              <MaterialIcons name="star" size={20} color="#000" />
              <Text style={styles.rateButtonText}>Avaliação</Text>
            </TouchableOpacity>
          </View>

          <TouchableOpacity 
            style={styles.closeButton} 
            onPress={onClose}
          >
            <Text style={styles.closeButtonText}>Fechar</Text>
          </TouchableOpacity>
        </View>
      </View>
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
    backgroundColor: '#F1F5F9',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 16,
    width: '80%',
  },
  rateButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#000',
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
});

export default CompletedAppointmentDetailsModal; 