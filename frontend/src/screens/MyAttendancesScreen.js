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
import CompletedAppointmentDetailsModal from '../components/CompletedAppointmentDetailsModal';

const MyAttendancesScreen = () => {
  const navigation = useNavigation();
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingFuture, setIsLoadingFuture] = useState(false);
  const [isLoadingPast, setIsLoadingPast] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [futureAttendances, setFutureAttendances] = useState([]);
  const [pastAttendances, setPastAttendances] = useState([]);
  const [currentFuturePage, setCurrentFuturePage] = useState(0);
  const [currentPastPage, setCurrentPastPage] = useState(0);
  const [hasMoreFuturePages, setHasMoreFuturePages] = useState(true);
  const [hasMorePastPages, setHasMorePastPages] = useState(true);
  const [screenData, setScreenData] = useState(Dimensions.get('window'));
  const [selectedAttendance, setSelectedAttendance] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isCompletedModalVisible, setIsCompletedModalVisible] = useState(false);
  const [pageSize] = useState(5);

  const isMobile = screenData.width < 768;

  useEffect(() => {
    const onChange = (result) => {
      setScreenData(result.window);
    };

    const subscription = Dimensions.addEventListener('change', onChange);
    return () => subscription?.remove();
  }, []);

  useEffect(() => {
    loadAttendances();
  }, []);

  const loadAttendances = async (shouldRefresh = false) => {
    try {
      setIsLoading(true);
      await Promise.all([
        loadFutureAttendances(0, shouldRefresh),
        loadPastAttendances(0, shouldRefresh)
      ]);
    } catch (error) {
      console.error('Erro ao carregar atendimentos:', error);
      toastHelper.showError('Erro ao carregar seus atendimentos');
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  };

  const loadFutureAttendances = async (page = 0, shouldRefresh = false) => {
    try {
      if (page === 0 && !shouldRefresh) {
        setIsLoading(true);
      } else {
        setIsLoadingFuture(true);
      }

      const response = await AgendamentoService.listarMeusAtendimentosFuturos(page, pageSize);
      const newAttendances = response?.content || [];
      
      setHasMoreFuturePages(!response?.last);
      
      setFutureAttendances(newAttendances);
      
      setCurrentFuturePage(page);
    } catch (error) {
      console.error('Erro ao carregar atendimentos futuros:', error);
      throw error;
    } finally {
      setIsLoadingFuture(false);
      setIsLoading(false);
    }
  };

  const loadPastAttendances = async (page = 0, shouldRefresh = false) => {
    try {
      if (page === 0 && !shouldRefresh) {
        setIsLoading(true);
      } else {
        setIsLoadingPast(true);
      }

      const response = await AgendamentoService.listarMeusAtendimentosPassados(page, pageSize);
      const newAttendances = response?.content || [];
      
      setHasMorePastPages(!response?.last);
      
      setPastAttendances(newAttendances);
      
      setCurrentPastPage(page);
    } catch (error) {
      console.error('Erro ao carregar atendimentos passados:', error);
      throw error;
    } finally {
      setIsLoadingPast(false);
      setIsLoading(false);
    }
  };

  const handleNextFuturePage = () => {
    if (!isLoadingFuture && hasMoreFuturePages) {
      loadFutureAttendances(currentFuturePage + 1);
    }
  };

  const handlePrevFuturePage = () => {
    if (!isLoadingFuture && currentFuturePage > 0) {
      loadFutureAttendances(currentFuturePage - 1);
    }
  };

  const handleNextPastPage = () => {
    if (!isLoadingPast && hasMorePastPages) {
      loadPastAttendances(currentPastPage + 1);
    }
  };

  const handlePrevPastPage = () => {
    if (!isLoadingPast && currentPastPage > 0) {
      loadPastAttendances(currentPastPage - 1);
    }
  };

  const handleRefresh = () => {
    setIsRefreshing(true);
    loadAttendances(true);
  };

  const handleAttendancePress = (attendance) => {
    setSelectedAttendance(attendance);
    
    if (attendance.status?.toUpperCase() === 'CONCLUIDO') {
      setIsCompletedModalVisible(true);
    } else {
      setIsModalVisible(true);
    }
  };

  const handleCloseModal = () => {
    setIsModalVisible(false);
    setSelectedAttendance(null);
  };
  
  const handleCloseCompletedModal = () => {
    setIsCompletedModalVisible(false);
    setSelectedAttendance(null);
  };

  const renderFutureAttendances = () => {
    if (isLoading && currentFuturePage === 0) {
      return (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#111" />
          <Text style={styles.loadingText}>Carregando atendimentos futuros...</Text>
        </View>
      );
    }

    if (futureAttendances.length === 0) {
      return (
        <View style={styles.emptyContainer}>
          <MaterialIcons name="event-available" size={64} color="#94A3B8" />
          <Text style={styles.emptyText}>Nenhum atendimento futuro</Text>
          <Text style={styles.emptySubtext}>
            Quando clientes agendarem serviços com você, eles aparecerão aqui.
          </Text>
        </View>
      );
    }

    return (
      <View style={styles.sectionContainer}>
        <View style={styles.appointmentsList}>
          {futureAttendances.map((attendance) => (
            <AppointmentCard
              key={attendance.idAgendamento}
              appointment={attendance}
              onPress={() => handleAttendancePress(attendance)}
              showActions={false}
              isProfessional={true}
            />
          ))}
        </View>

        <View style={styles.paginationContainer}>
          <TouchableOpacity
            style={[
              styles.paginationButton,
              currentFuturePage === 0 && styles.paginationButtonDisabled
            ]}
            onPress={handlePrevFuturePage}
            disabled={currentFuturePage === 0 || isLoadingFuture}
          >
            {isLoadingFuture ? (
              <ActivityIndicator size="small" color="#64748B" />
            ) : (
              <MaterialIcons name="chevron-left" size={20} color="#64748B" />
            )}
          </TouchableOpacity>

          <Text style={styles.paginationText}>
            Página {currentFuturePage + 1}
          </Text>

          <TouchableOpacity
            style={[
              styles.paginationButton,
              !hasMoreFuturePages && styles.paginationButtonDisabled
            ]}
            onPress={handleNextFuturePage}
            disabled={!hasMoreFuturePages || isLoadingFuture}
          >
            {isLoadingFuture ? (
              <ActivityIndicator size="small" color="#64748B" />
            ) : (
              <MaterialIcons name="chevron-right" size={20} color="#64748B" />
            )}
          </TouchableOpacity>
        </View>
      </View>
    );
  };

  const renderPastAttendances = () => {
    if (isLoading && currentPastPage === 0) {
      return (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#111" />
          <Text style={styles.loadingText}>Carregando histórico de atendimentos...</Text>
        </View>
      );
    }

    if (pastAttendances.length === 0) {
      return (
        <View style={styles.emptyContainer}>
          <MaterialIcons name="history" size={64} color="#94A3B8" />
          <Text style={styles.emptyText}>Nenhum atendimento no histórico</Text>
          <Text style={styles.emptySubtext}>
            Seus atendimentos concluídos aparecerão aqui.
          </Text>
        </View>
      );
    }

    return (
      <View style={styles.sectionContainer}>
        <View style={styles.appointmentsList}>
          {pastAttendances.map((attendance) => (
            <AppointmentCard
              key={attendance.idAgendamento}
              appointment={attendance}
              onPress={() => handleAttendancePress(attendance)}
              showActions={false}
              isProfessional={true}
            />
          ))}
        </View>

        <View style={styles.paginationContainer}>
          <TouchableOpacity
            style={[
              styles.paginationButton,
              currentPastPage === 0 && styles.paginationButtonDisabled
            ]}
            onPress={handlePrevPastPage}
            disabled={currentPastPage === 0 || isLoadingPast}
          >
            {isLoadingPast ? (
              <ActivityIndicator size="small" color="#64748B" />
            ) : (
              <MaterialIcons name="chevron-left" size={20} color="#64748B" />
            )}
          </TouchableOpacity>

          <Text style={styles.paginationText}>
            Página {currentPastPage + 1}
          </Text>

          <TouchableOpacity
            style={[
              styles.paginationButton,
              !hasMorePastPages && styles.paginationButtonDisabled
            ]}
            onPress={handleNextPastPage}
            disabled={!hasMorePastPages || isLoadingPast}
          >
            {isLoadingPast ? (
              <ActivityIndicator size="small" color="#64748B" />
            ) : (
              <MaterialIcons name="chevron-right" size={20} color="#64748B" />
            )}
          </TouchableOpacity>
        </View>
      </View>
    );
  };

  if (isLoading) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#111" />
          <Text style={styles.loadingText}>Carregando seus atendimentos...</Text>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView
        style={styles.scrollContainer}
        contentContainerStyle={styles.scrollContent}
        refreshControl={
          <RefreshControl
            refreshing={isRefreshing}
            onRefresh={handleRefresh}
            colors={['#111']}
            tintColor="#111"
          />
        }
      >
        <View style={styles.header}>
          <TouchableOpacity
            style={styles.backButton}
            onPress={() => navigation.goBack()}
          >
            <MaterialIcons name="arrow-back" size={24} color="#111" />
          </TouchableOpacity>
          <Text style={styles.title}>Meus Atendimentos</Text>
        </View>

        <View style={styles.content}>
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>
              <MaterialIcons name="schedule" size={20} color="#111" style={styles.sectionIcon} />
              Próximos Atendimentos
            </Text>
            {renderFutureAttendances()}
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>
              <MaterialIcons name="history" size={20} color="#111" style={styles.sectionIcon} />
              Histórico de Atendimentos
            </Text>
            {renderPastAttendances()}
          </View>
        </View>
      </ScrollView>

      <Footer />

      {selectedAttendance && (
        <>
          <AppointmentDetailsModal
            visible={isModalVisible}
            appointment={selectedAttendance}
            onClose={handleCloseModal}
            showEditButton={false}
            showCancelButton={false}
            isProfessional={true}
          />

          <CompletedAppointmentDetailsModal
            visible={isCompletedModalVisible}
            appointment={selectedAttendance}
            onClose={handleCloseCompletedModal}
            isProfessional={true}
          />
        </>
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8fafc',
  },
  scrollContainer: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: 100,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#E2E8F0',
  },
  backButton: {
    marginRight: 16,
    padding: 8,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#111',
  },
  content: {
    padding: 16,
  },
  section: {
    marginBottom: 32,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111',
    marginBottom: 16,
    flexDirection: 'row',
    alignItems: 'center',
  },
  sectionIcon: {
    marginRight: 8,
  },
  sectionContainer: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  appointmentsList: {
    marginBottom: 16,
  },
  paginationContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingTop: 16,
    borderTopWidth: 1,
    borderTopColor: '#E2E8F0',
  },
  paginationButton: {
    padding: 8,
    borderRadius: 8,
    backgroundColor: '#F1F5F9',
    marginHorizontal: 8,
  },
  paginationButtonDisabled: {
    opacity: 0.5,
  },
  paginationText: {
    fontSize: 14,
    color: '#64748B',
    marginHorizontal: 16,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#64748B',
  },
  emptyContainer: {
    alignItems: 'center',
    padding: 32,
  },
  emptyText: {
    fontSize: 18,
    fontWeight: '600',
    color: '#475569',
    marginTop: 16,
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#64748B',
    textAlign: 'center',
    lineHeight: 20,
  },
});

export default MyAttendancesScreen; 