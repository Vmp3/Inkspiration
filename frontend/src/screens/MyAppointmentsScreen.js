import React, { useState, useEffect } from 'react';
import DefaultUser from '../../assets/default_user.png'
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  Image,
  ActivityIndicator,
  Dimensions,
  SafeAreaView,
  RefreshControl
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { MaterialIcons } from '@expo/vector-icons';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import AgendamentoService from '../services/AgendamentoService';
import toastHelper from '../utils/toastHelper';
import Footer from '../components/Footer';

const MyAppointmentsScreen = () => {
  const navigation = useNavigation();
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [appointments, setAppointments] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMorePages, setHasMorePages] = useState(true);
  const [screenData, setScreenData] = useState(Dimensions.get('window'));

  const isMobile = screenData.width < 768;

  useEffect(() => {
    const onChange = (result) => {
      setScreenData(result.window);
    };

    const subscription = Dimensions.addEventListener('change', onChange);
    return () => subscription?.remove();
  }, []);

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async (page = 0, shouldRefresh = false) => {
    try {
      if (page === 0) {
        setIsLoading(true);
      } else {
        setIsLoadingMore(true);
      }

      const response = await AgendamentoService.listarMeusAgendamentos(page);
      console.log('Resposta da API:', response); // Debug

      // Garantir que temos um array de agendamentos
      const newAppointments = Array.isArray(response) ? response : (response?.content || []);
      
      // Debug dos novos agendamentos
      console.log('Novos agendamentos:', newAppointments);

      setHasMorePages(response?.last === false);
      
      if (shouldRefresh || page === 0) {
        setAppointments(newAppointments);
      } else {
        setAppointments(prev => [...prev, ...newAppointments]);
      }
      
      setCurrentPage(page);
    } catch (error) {
      console.error('Erro ao carregar agendamentos:', error);
      toastHelper.showError('Erro ao carregar seus agendamentos');
    } finally {
      setIsLoading(false);
      setIsLoadingMore(false);
      setIsRefreshing(false);
    }
  };

  const handleLoadMore = () => {
    if (!isLoadingMore && hasMorePages) {
      loadAppointments(currentPage + 1);
    }
  };

  const handleRefresh = () => {
    setIsRefreshing(true);
    loadAppointments(0, true);
  };

  const isCloseToBottom = ({ layoutMeasurement, contentOffset, contentSize }) => {
    const paddingToBottom = 20;
    return layoutMeasurement.height + contentOffset.y >= contentSize.height - paddingToBottom;
  };

  const handleAppointmentPress = (appointment) => {
    setSelectedAppointment(appointment);
  };

  const renderAppointmentCard = (appointment) => {
    console.log('Renderizando card para agendamento:', appointment); // Debug
    if (!appointment) return null;

    const isCompleted = new Date(appointment.dtInicio) < new Date();
    const status = isCompleted ? 'concluído' : 'agendado';

    return (
      <TouchableOpacity
        key={appointment.idAgendamento}
        style={styles.appointmentCard}
        onPress={() => handleAppointmentPress(appointment)}
      >
        <View style={styles.cardHeader}>
          <View style={styles.artistInfo}>
            <Image
              source={{ 
                uri: appointment.profissional?.usuario?.imagemPerfil || DefaultUser
              }}
              style={styles.artistImage}
            />
            <View>
              <Text style={styles.artistName}>
                {appointment.profissional?.usuario?.nome || 'Nome não disponível'}
              </Text>
              <Text style={styles.serviceDetail}>
                {appointment.tipoServico || 'Serviço não especificado'}
              </Text>
            </View>
          </View>
          <MaterialIcons name="chevron-right" size={24} color="#64748b" />
        </View>

        <View style={styles.appointmentDetails}>
          <View style={styles.detailItem}>
            <MaterialIcons name="event" size={16} color="#64748b" />
            <Text style={styles.detailText}>
              {format(new Date(appointment.dtInicio), "dd 'de' MMMM", { locale: ptBR })}
            </Text>
          </View>
          <View style={styles.detailItem}>
            <MaterialIcons name="access-time" size={16} color="#64748b" />
            <Text style={styles.detailText}>
              {format(new Date(appointment.dtInicio), 'HH:mm')}
            </Text>
          </View>
          <View style={styles.detailItem}>
            <MaterialIcons name="location-on" size={16} color="#64748b" />
            <Text style={styles.detailText}>
              {appointment.profissional?.endereco || 'Local não informado'}
            </Text>
          </View>
        </View>

        <View style={styles.statusContainer}>
          <View style={[
            styles.statusBadge,
            status === 'concluído' ? styles.completedBadge : styles.scheduledBadge
          ]}>
            <Text style={[
              styles.statusText,
              status === 'concluído' ? styles.completedText : styles.scheduledText
            ]}>
              {status.charAt(0).toUpperCase() + status.slice(1)}
            </Text>
          </View>
        </View>
      </TouchableOpacity>
    );
  };

  const renderFutureAppointments = () => {
    console.log('Renderizando agendamentos futuros. Total de agendamentos:', appointments.length); // Debug
    const futureAppointments = appointments.filter(
      appointment => new Date(appointment.dtInicio) >= new Date()
    );
    console.log('Agendamentos futuros filtrados:', futureAppointments.length); // Debug

    return (
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Agendamentos Futuros</Text>
        {futureAppointments.length > 0 ? (
          futureAppointments.map(appointment => renderAppointmentCard(appointment))
        ) : (
          <Text style={styles.emptyText}>Você não possui agendamentos futuros.</Text>
        )}
      </View>
    );
  };

  const renderPastAppointments = () => {
    console.log('Renderizando agendamentos passados. Total de agendamentos:', appointments.length); // Debug
    const pastAppointments = appointments.filter(
      appointment => new Date(appointment.dtInicio) < new Date()
    );
    console.log('Agendamentos passados filtrados:', pastAppointments.length); // Debug

    return (
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Histórico de Agendamentos</Text>
        {pastAppointments.length > 0 ? (
          pastAppointments.map(appointment => renderAppointmentCard(appointment))
        ) : (
          <Text style={styles.emptyText}>Você não possui histórico de agendamentos.</Text>
        )}
      </View>
    );
  };

  if (isLoading) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#111" />
          <Text style={styles.loadingText}>Carregando agendamentos...</Text>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView 
        style={styles.scrollView}
        refreshControl={
          <RefreshControl
            refreshing={isRefreshing}
            onRefresh={handleRefresh}
            colors={["#111"]}
            tintColor="#111"
          />
        }
        onScroll={({ nativeEvent }) => {
          if (isCloseToBottom(nativeEvent)) {
            handleLoadMore();
          }
        }}
        scrollEventThrottle={400}
      >
        <View style={styles.header}>
          <TouchableOpacity 
            style={styles.backButton}
            onPress={() => navigation.goBack()}
          >
            <MaterialIcons name="arrow-back" size={24} color="#111" />
          </TouchableOpacity>
          <Text style={styles.pageTitle}>Meus Agendamentos</Text>
        </View>

        <View style={styles.content}>
          {appointments.length === 0 ? (
            <View style={styles.emptyState}>
              <MaterialIcons name="event" size={48} color="#64748b" />
              <Text style={styles.emptyStateTitle}>Nenhum agendamento encontrado</Text>
              <Text style={styles.emptyStateText}>
                Você ainda não realizou nenhum agendamento com nossos artistas.
              </Text>
              <TouchableOpacity
                style={styles.exploreButton}
                onPress={() => navigation.navigate('Explore')}
              >
                <Text style={styles.exploreButtonText}>Explorar Artistas</Text>
              </TouchableOpacity>
            </View>
          ) : (
            <>
              {renderFutureAppointments()}
              {renderPastAppointments()}
              {isLoadingMore && (
                <View style={styles.loadingMoreContainer}>
                  <ActivityIndicator size="small" color="#111" />
                  <Text style={styles.loadingMoreText}>Carregando mais agendamentos...</Text>
                </View>
              )}
            </>
          )}
        </View>
        <Footer />
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollView: {
    flex: 1,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#e2e8f0',
  },
  backButton: {
    marginRight: 16,
  },
  pageTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111',
  },
  content: {
    padding: 16,
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: '600',
    color: '#111',
    marginBottom: 16,
  },
  appointmentCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#e2e8f0',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  artistInfo: {
    flexDirection: 'row',
    alignItems: 'center',
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
  serviceDetail: {
    fontSize: 14,
    color: '#64748b',
    marginTop: 2,
  },
  appointmentDetails: {
    marginBottom: 16,
  },
  detailItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  detailText: {
    fontSize: 14,
    color: '#64748b',
    marginLeft: 8,
  },
  statusContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
  },
  statusBadge: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  scheduledBadge: {
    backgroundColor: '#e0f2fe',
  },
  completedBadge: {
    backgroundColor: '#dcfce7',
  },
  statusText: {
    fontSize: 14,
    fontWeight: '500',
  },
  scheduledText: {
    color: '#0369a1',
  },
  completedText: {
    color: '#15803d',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    marginTop: 16,
    fontSize: 16,
    color: '#64748b',
  },
  emptyState: {
    alignItems: 'center',
    padding: 24,
  },
  emptyStateTitle: {
    fontSize: 20,
    fontWeight: '600',
    color: '#111',
    marginTop: 16,
    marginBottom: 8,
  },
  emptyStateText: {
    fontSize: 16,
    color: '#64748b',
    textAlign: 'center',
    marginBottom: 24,
  },
  exploreButton: {
    backgroundColor: '#111',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 8,
  },
  exploreButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '500',
  },
  emptyText: {
    fontSize: 16,
    color: '#64748b',
    textAlign: 'center',
    marginTop: 8,
  },
  loadingMoreContainer: {
    padding: 16,
    alignItems: 'center',
  },
  loadingMoreText: {
    marginTop: 8,
    fontSize: 14,
    color: '#64748b',
  },
});

export default MyAppointmentsScreen; 