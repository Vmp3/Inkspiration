import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  ActivityIndicator,
  Dimensions,
  SafeAreaView,
  RefreshControl,
  Alert
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { MaterialIcons } from '@expo/vector-icons';
import { differenceInDays } from 'date-fns';
import AgendamentoService from '../services/AgendamentoService';
import toastHelper from '../utils/toastHelper';
import Footer from '../components/Footer';
import AppointmentCard from '../components/AppointmentCard';
import AppointmentDetailsModal from '../components/AppointmentDetailsModal';
import CancelAppointmentModal from '../components/CancelAppointmentModal';
import EditAppointmentModal from '../components/EditAppointmentModal';

const MyAppointmentsScreen = () => {
  const navigation = useNavigation();
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingMoreFuture, setIsLoadingMoreFuture] = useState(false);
  const [isLoadingMorePast, setIsLoadingMorePast] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [futureAppointments, setFutureAppointments] = useState([]);
  const [pastAppointments, setPastAppointments] = useState([]);
  const [currentFuturePage, setCurrentFuturePage] = useState(0);
  const [currentPastPage, setCurrentPastPage] = useState(0);
  const [hasMoreFuturePages, setHasMoreFuturePages] = useState(true);
  const [hasMorePastPages, setHasMorePastPages] = useState(true);
  const [screenData, setScreenData] = useState(Dimensions.get('window'));
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isCancelModalVisible, setIsCancelModalVisible] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isEditModalVisible, setIsEditModalVisible] = useState(false);

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

  const loadAppointments = async (shouldRefresh = false) => {
    try {
      setIsLoading(true);
      await Promise.all([
        loadFutureAppointments(0, shouldRefresh),
        loadPastAppointments(0, shouldRefresh)
      ]);
    } catch (error) {
      console.error('Erro ao carregar agendamentos:', error);
      toastHelper.showError('Erro ao carregar seus agendamentos');
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  };

  const loadFutureAppointments = async (page = 0, shouldRefresh = false) => {
    try {
      if (page === 0) {
        setIsLoading(true);
      } else {
        setIsLoadingMoreFuture(true);
      }

      const response = await AgendamentoService.listarMeusAgendamentosFuturos(page);
      const newAppointments = response?.content || [];
      
      setHasMoreFuturePages(!response?.last);
      
      if (shouldRefresh || page === 0) {
        setFutureAppointments(newAppointments);
      } else {
        setFutureAppointments(prev => [...prev, ...newAppointments]);
      }
      
      setCurrentFuturePage(page);
    } catch (error) {
      console.error('Erro ao carregar agendamentos futuros:', error);
      throw error;
    } finally {
      setIsLoadingMoreFuture(false);
    }
  };

  const loadPastAppointments = async (page = 0, shouldRefresh = false) => {
    try {
      if (page === 0) {
        setIsLoading(true);
      } else {
        setIsLoadingMorePast(true);
      }

      const response = await AgendamentoService.listarMeusAgendamentosPassados(page);
      const newAppointments = response?.content || [];
      
      setHasMorePastPages(!response?.last);
      
      if (shouldRefresh || page === 0) {
        setPastAppointments(newAppointments);
      } else {
        setPastAppointments(prev => [...prev, ...newAppointments]);
      }
      
      setCurrentPastPage(page);
    } catch (error) {
      console.error('Erro ao carregar agendamentos passados:', error);
      throw error;
    } finally {
      setIsLoadingMorePast(false);
    }
  };

  const handleLoadMoreFuture = () => {
    if (!isLoadingMoreFuture && hasMoreFuturePages) {
      loadFutureAppointments(currentFuturePage + 1);
    }
  };

  const handleLoadMorePast = () => {
    if (!isLoadingMorePast && hasMorePastPages) {
      loadPastAppointments(currentPastPage + 1);
    }
  };

  const handleRefresh = () => {
    setIsRefreshing(true);
    loadAppointments(true);
  };

  const isCloseToBottom = ({ layoutMeasurement, contentOffset, contentSize }) => {
    const paddingToBottom = 20;
    return layoutMeasurement.height + contentOffset.y >= contentSize.height - paddingToBottom;
  };

  const handleAppointmentPress = (appointment) => {
    console.log('Dados do agendamento selecionado:', appointment);
    setSelectedAppointment(appointment);
    setIsModalVisible(true);
  };

  const handleCloseModal = () => {
    setIsModalVisible(false);
    setSelectedAppointment(null);
  };

  const handleEditAppointment = () => {
    if (!selectedAppointment) return;
    
    const today = new Date();
    const appointmentDate = new Date(selectedAppointment.dtInicio);
    const daysDiff = differenceInDays(appointmentDate, today);
    
    if (daysDiff < 3) {
      toastHelper.showError("A edição só é permitida com no mínimo 3 dias de antecedência.");
      return;
    }
    
    setIsModalVisible(false);
    setIsEditModalVisible(true);
  };

  const handleEditSuccess = () => {
    loadAppointments(true);
  };

  const handleCloseEditModal = () => {
    setIsEditModalVisible(false);
    setSelectedAppointment(null);
  };

  const handleCancelAppointment = () => {
    const today = new Date();
    const appointmentDate = new Date(selectedAppointment.dtInicio);
    const daysDiff = differenceInDays(appointmentDate, today);
    
    if (daysDiff < 3) {
      toastHelper.showError("O cancelamento só é permitido com no mínimo 3 dias de antecedência.");
      return;
    }
    
    setIsModalVisible(false);
    setIsCancelModalVisible(true);
  };

  const handleCloseCancelModal = () => {
    setIsCancelModalVisible(false);
    setSelectedAppointment(null);
  };

  const handleConfirmCancel = async () => {
    try {
      setIsSubmitting(true);
      
      await AgendamentoService.atualizarStatusAgendamento(
        selectedAppointment.idAgendamento,
        'CANCELADO'
      );
      
      toastHelper.showSuccess('Agendamento cancelado com sucesso');
      setIsCancelModalVisible(false);
      setSelectedAppointment(null);
      
      loadAppointments(true);
    } catch (error) {
      console.error('Erro ao cancelar agendamento:', error);
      
      let errorMessage = 'Erro ao cancelar o agendamento';
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }
      
      toastHelper.showError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderFutureAppointments = () => {
    return (
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Agendamentos Futuros</Text>
        {futureAppointments.length > 0 ? (
          <>
            {futureAppointments.map(appointment => (
              <AppointmentCard
                key={appointment.idAgendamento}
                appointment={appointment}
                onPress={() => handleAppointmentPress(appointment)}
              />
            ))}
            {isLoadingMoreFuture && (
              <View style={styles.loadingMoreContainer}>
                <ActivityIndicator size="small" color="#111" />
                <Text style={styles.loadingMoreText}>Carregando mais agendamentos...</Text>
              </View>
            )}
          </>
        ) : (
          <Text style={styles.emptyText}>Você não possui agendamentos futuros.</Text>
        )}
      </View>
    );
  };

  const renderPastAppointments = () => {
    return (
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Histórico de Agendamentos</Text>
        {pastAppointments.length > 0 ? (
          <>
            {pastAppointments.map(appointment => (
              <AppointmentCard
                key={appointment.idAgendamento}
                appointment={appointment}
                onPress={() => handleAppointmentPress(appointment)}
              />
            ))}
            {isLoadingMorePast && (
              <View style={styles.loadingMoreContainer}>
                <ActivityIndicator size="small" color="#111" />
                <Text style={styles.loadingMoreText}>Carregando mais agendamentos...</Text>
              </View>
            )}
          </>
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
            handleLoadMoreFuture();
            handleLoadMorePast();
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
          {futureAppointments.length === 0 && pastAppointments.length === 0 ? (
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
            </>
          )}
        </View>
        <Footer />
      </ScrollView>

      <AppointmentDetailsModal
        visible={isModalVisible}
        appointment={selectedAppointment}
        onClose={handleCloseModal}
        onEdit={handleEditAppointment}
        onCancel={handleCancelAppointment}
      />

      <CancelAppointmentModal
        visible={isCancelModalVisible}
        onClose={handleCloseCancelModal}
        onConfirm={handleConfirmCancel}
      />

      <EditAppointmentModal
        visible={isEditModalVisible}
        appointment={selectedAppointment}
        onClose={handleCloseEditModal}
        onSuccess={handleEditSuccess}
      />
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