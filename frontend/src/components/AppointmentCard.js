import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import DefaultUser from '../../assets/default_user.png';
import { formatCurrency } from '../utils/formatters';
import ImageWithAlt from './ui/ImageWithAlt';

const AppointmentCard = ({ appointment, onPress, isProfessional = false }) => {
  const getStatusStyle = (status) => {
    switch (status) {
      case 'AGENDADO':
        return {
          badge: styles.scheduledBadge,
          text: styles.scheduledText
        };
      case 'CANCELADO':
        return {
          badge: styles.canceledBadge,
          text: styles.canceledText
        };
      case 'CONCLUIDO':
        return {
          badge: styles.completedBadge,
          text: styles.completedText
        };
      default:
        return {
          badge: styles.scheduledBadge,
          text: styles.scheduledText
        };
    }
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

  const formatDateTime = (dtInicio, dtFim) => {
    const dataFormatada = format(new Date(dtInicio), 'dd/MM/yyyy');
    const horaInicio = format(new Date(dtInicio), 'HH:mm');
    const horaFim = format(new Date(dtFim), 'HH:mm');
    return {
      data: dataFormatada,
      horario: `${horaInicio} - ${horaFim}`
    };
  };

  const formatEndereco = (appointment) => {
    if (!appointment.rua && !appointment.cidade) return 'Local não informado';

    const partes = [];
    
    if (appointment.rua) {
      partes.push(appointment.rua);
      if (appointment.numero) {
        partes[0] += `, ${appointment.numero}`;
      }
    }
    
    if (appointment.complemento) {
      partes[0] += `, ${appointment.complemento}`;
    }
    
    if (appointment.bairro) {
      partes.push(appointment.bairro);
    }
    
    if (appointment.cidade && appointment.estado) {
      partes.push(`${appointment.cidade}/${appointment.estado}`);
    } else if (appointment.cidade) {
      partes.push(appointment.cidade);
    } else if (appointment.estado) {
      partes.push(appointment.estado);
    }
    
    return partes.join(' - ');
  };

  const statusStyle = getStatusStyle(appointment.status);
  const { data, horario } = formatDateTime(appointment.dtInicio, appointment.dtFim);
  const endereco = formatEndereco(appointment);

  return (
    <TouchableOpacity
      style={styles.card}
      onPress={onPress}
    >
      <View style={styles.cardContent}>
        <View style={styles.leftContent}>
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
          <View style={styles.infoContainer}>
            <Text style={styles.artistName}>
              {isProfessional ? 
                (appointment.nomeUsuario || 'Nome não disponível') :
                (appointment.nomeProfissional || 'Nome não disponível')
              }
            </Text>
            <Text style={styles.serviceType}>
              {formatServiceType(appointment.tipoServico)}
            </Text>
          </View>
        </View>
        <MaterialIcons name="chevron-right" size={24} color="#94A3B8" />
      </View>

      <View style={styles.appointmentInfo}>
        <View style={styles.infoRow}>
          <MaterialIcons name="event" size={16} color="#94A3B8" />
          <Text style={styles.infoText}>
            {data}
          </Text>
        </View>
        <View style={styles.infoRow}>
          <MaterialIcons name="access-time" size={16} color="#94A3B8" />
          <Text style={styles.infoText}>
            {horario}
          </Text>
        </View>
        <View style={[styles.infoRow, styles.addressRow]}>
          <MaterialIcons name="location-on" size={16} color="#94A3B8" />
          <Text style={[styles.infoText, styles.addressText]} numberOfLines={2}>
            {endereco}
          </Text>
        </View>
        {appointment.valor && (
          <View style={styles.infoRow}>
            <MaterialIcons name="attach-money" size={16} color="#94A3B8" />
            <Text style={styles.infoText}>
              {formatCurrency(appointment.valor)}
            </Text>
          </View>
        )}
      </View>

      <View style={styles.statusContainer}>
        <View style={[styles.statusBadge, statusStyle.badge]}>
          <Text style={[styles.statusText, statusStyle.text]}>
            {appointment.status || 'AGENDADO'}
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardContent: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  leftContent: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
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
  infoContainer: {
    flex: 1,
  },
  artistName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1E293B',
    marginBottom: 2,
  },
  serviceType: {
    fontSize: 14,
    color: '#64748B',
  },
  appointmentInfo: {
    marginBottom: 16,
  },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  infoText: {
    fontSize: 14,
    color: '#64748B',
    marginLeft: 8,
  },
  statusContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
  },
  statusBadge: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  statusText: {
    fontSize: 14,
    fontWeight: '500',
  },
  scheduledBadge: {
    backgroundColor: '#E0F2FE',
  },
  scheduledText: {
    color: '#0369A1',
  },
  canceledBadge: {
    backgroundColor: '#FEE2E2',
  },
  canceledText: {
    color: '#DC2626',
  },
  completedBadge: {
    backgroundColor: '#DCFCE7',
  },
  completedText: {
    color: '#15803D',
  },
  addressRow: {
    alignItems: 'flex-start',
    flex: 1,
  },
  addressText: {
    flex: 1,
    flexWrap: 'wrap',
  },
});

export default AppointmentCard; 