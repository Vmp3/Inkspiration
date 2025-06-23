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
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import { MaterialIcons } from '@expo/vector-icons';
import { differenceInDays } from 'date-fns';
import AgendamentoService from '../services/AgendamentoService';
import toastHelper from '../utils/toastHelper';
import Footer from '../components/Footer';
import AppointmentCard from '../components/AppointmentCard';
import AppointmentDetailsModal from '../components/AppointmentDetailsModal';
import CompletedAppointmentDetailsModal from '../components/CompletedAppointmentDetailsModal';
import CancelAppointmentModal from '../components/CancelAppointmentModal';
import ExportAttendancesModal from '../components/ExportAttendancesModal';
import { useAuth } from '../context/AuthContext';

const MyAttendancesScreen = () => {
  const navigation = useNavigation();
  const { userData } = useAuth();
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
  const [isCancelModalVisible, setIsCancelModalVisible] = useState(false);
  const [isExportModalVisible, setIsExportModalVisible] = useState(false);
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

  useFocusEffect(
    React.useCallback(() => {
      const onChange = (result) => {
        if (result?.type === 'success') {
          loadAttendances(true);
        }
      };

      loadAttendances();

      return onChange;
    }, [])
  );

  const loadAttendances = async (shouldRefresh = false) => {
    if (shouldRefresh) {
      setIsRefreshing(true);
      setCurrentFuturePage(0);
      setCurrentPastPage(0);
    } else {
      setIsLoading(true);
    }

    try {
      await Promise.all([
        loadFutureAttendances(0, shouldRefresh),
        loadPastAttendances(0, shouldRefresh)
      ]);
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  };

  const loadFutureAttendances = async (page = 0, shouldRefresh = false) => {
    if (!shouldRefresh && page > 0) {
      setIsLoadingFuture(true);
    }

    try {
      const response = await AgendamentoService.listarMeusAtendimentosFuturos(page, pageSize);
      
      if (page === 0 || shouldRefresh) {
        setFutureAttendances(response.content || []);
      } else {
        setFutureAttendances(prev => [...prev, ...(response.content || [])]);
      }
      
      setHasMoreFuturePages(!response.last);
      
      if (page === 0 || shouldRefresh) {
        setCurrentFuturePage(0);
      }
    } catch (error) {
      console.error('Erro ao carregar atendimentos futuros:', error);
      toastHelper.showError('Erro ao carregar atendimentos futuros');
    } finally {
      setIsLoadingFuture(false);
    }
  };

  const loadPastAttendances = async (page = 0, shouldRefresh = false) => {
    if (!shouldRefresh && page > 0) {
      setIsLoadingPast(true);
    }

    try {
      const response = await AgendamentoService.listarMeusAtendimentosPassados(page, pageSize);
      
      if (page === 0 || shouldRefresh) {
        setPastAttendances(response.content || []);
      } else {
        setPastAttendances(prev => [...prev, ...(response.content || [])]);
      }
      
      setHasMorePastPages(!response.last);
      
      if (page === 0 || shouldRefresh) {
        setCurrentPastPage(0);
      }
    } catch (error) {
      console.error('Erro ao carregar atendimentos passados:', error);
      toastHelper.showError('Erro ao carregar atendimentos passados');
    } finally {
      setIsLoadingPast(false);
    }
  };

  const handleNextFuturePage = () => {
    if (hasMoreFuturePages && !isLoadingFuture) {
      const nextPage = currentFuturePage + 1;
      setCurrentFuturePage(nextPage);
      loadFutureAttendances(nextPage);
    }
  };

  const handlePrevFuturePage = () => {
    if (currentFuturePage > 0 && !isLoadingFuture) {
      const prevPage = currentFuturePage - 1;
      setCurrentFuturePage(prevPage);
      loadFutureAttendances(prevPage);
    }
  };

  const handleNextPastPage = () => {
    if (hasMorePastPages && !isLoadingPast) {
      const nextPage = currentPastPage + 1;
      setCurrentPastPage(nextPage);
      loadPastAttendances(nextPage);
    }
  };

  const handlePrevPastPage = () => {
    if (currentPastPage > 0 && !isLoadingPast) {
      const prevPage = currentPastPage - 1;
      setCurrentPastPage(prevPage);
      loadPastAttendances(prevPage);
    }
  };

  const handleRefresh = () => {
    loadAttendances(true);
  };

  const handleGoBack = () => {
    if (navigation.canGoBack()) {
      navigation.goBack();
    } else {
      navigation.navigate('Home');
    }
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

  const handleCloseCancelModal = () => {
    setIsCancelModalVisible(false);
  };

  const handleCancelAppointment = () => {
    if (!selectedAttendance) {
      toastHelper.showError('Erro ao identificar o agendamento');
      return;
    }

    if (selectedAttendance.status?.toUpperCase() !== 'AGENDADO') {
      toastHelper.showError('Apenas agendamentos com status "Agendado" podem ser cancelados');
      return;
    }

    setIsModalVisible(false);
    setIsCancelModalVisible(true);
  };

  const handleConfirmCancel = async (reason) => {
    try {
      await AgendamentoService.atualizarStatusAgendamento(selectedAttendance.idAgendamento, 'CANCELADO');
      
      toastHelper.showSuccess('Agendamento cancelado com sucesso');
      
      await loadAttendances(true);
      
    } catch (error) {
      console.error('Erro ao cancelar agendamento:', error);
      let errorMessage = 'Erro ao cancelar agendamento. Tente novamente.';
      
      if (error.response?.status === 400) {
        errorMessage = error.response.data || 'Não foi possível cancelar o agendamento';
      } else if (error.response?.status === 403) {
        errorMessage = 'Você não tem permissão para cancelar este agendamento';
      }
      
      toastHelper.showError(errorMessage);
    } finally {
      setIsCancelModalVisible(false);
      setSelectedAttendance(null);
    }
  };

  const renderFutureAttendances = () => {
    return (
      <View style={styles.section}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Próximos Atendimentos</Text>
          
          {!isLoading && futureAttendances.length > 0 && (
            <View style={styles.paginationContainer}>
              <TouchableOpacity 
                style={[
                  styles.paginationButton, 
                  currentFuturePage === 0 && styles.paginationButtonDisabled
                ]}
                onPress={handlePrevFuturePage}
                disabled={currentFuturePage === 0 || isLoadingFuture}
              >
                <MaterialIcons 
                  name="chevron-left" 
                  size={24} 
                  color={currentFuturePage === 0 ? "#CBD5E1" : "#111"} 
                />
              </TouchableOpacity>
              
              <Text style={styles.paginationText}>{currentFuturePage + 1}</Text>
              
              <TouchableOpacity 
                style={[
                  styles.paginationButton, 
                  !hasMoreFuturePages && styles.paginationButtonDisabled
                ]}
                onPress={handleNextFuturePage}
                disabled={!hasMoreFuturePages || isLoadingFuture}
              >
                <MaterialIcons 
                  name="chevron-right" 
                  size={24} 
                  color={!hasMoreFuturePages ? "#CBD5E1" : "#111"} 
                />
              </TouchableOpacity>
            </View>
          )}
        </View>
        
        {futureAttendances.length > 0 ? (
          <>
            {futureAttendances.map(attendance => (
              <AppointmentCard
                key={attendance.idAgendamento}
                appointment={attendance}
                onPress={() => handleAttendancePress(attendance)}
                isProfessional={true}
              />
            ))}
            
            {isLoadingFuture && (
              <View style={styles.loadingMoreContainer}>
                <ActivityIndicator size="small" color="#111" />
                <Text style={styles.loadingMoreText}>Carregando atendimentos...</Text>
              </View>
            )}
          </>
        ) : (
          <Text style={styles.emptyText}>Você não possui atendimentos futuros.</Text>
        )}
      </View>
    );
  };

  const renderPastAttendances = () => {
    return (
      <View style={styles.section}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Histórico de Atendimentos</Text>
          
          {!isLoading && pastAttendances.length > 0 && (
            <View style={styles.paginationContainer}>
              <TouchableOpacity 
                style={[
                  styles.paginationButton, 
                  currentPastPage === 0 && styles.paginationButtonDisabled
                ]}
                onPress={handlePrevPastPage}
                disabled={currentPastPage === 0 || isLoadingPast}
              >
                <MaterialIcons 
                  name="chevron-left" 
                  size={24} 
                  color={currentPastPage === 0 ? "#CBD5E1" : "#111"} 
                />
              </TouchableOpacity>
              
              <Text style={styles.paginationText}>{currentPastPage + 1}</Text>
              
              <TouchableOpacity 
                style={[
                  styles.paginationButton, 
                  !hasMorePastPages && styles.paginationButtonDisabled
                ]}
                onPress={handleNextPastPage}
                disabled={!hasMorePastPages || isLoadingPast}
              >
                <MaterialIcons 
                  name="chevron-right" 
                  size={24} 
                  color={!hasMorePastPages ? "#CBD5E1" : "#111"} 
                />
              </TouchableOpacity>
            </View>
          )}
        </View>
        
        {pastAttendances.length > 0 ? (
          <>
            {pastAttendances.map(attendance => (
              <AppointmentCard
                key={attendance.idAgendamento}
                appointment={attendance}
                onPress={() => handleAttendancePress(attendance)}
                isProfessional={true}
              />
            ))}
            
            {isLoadingPast && (
              <View style={styles.loadingMoreContainer}>
                <ActivityIndicator size="small" color="#111" />
                <Text style={styles.loadingMoreText}>Carregando atendimentos...</Text>
              </View>
            )}
          </>
        ) : (
          <Text style={styles.emptyText}>Você não possui histórico de atendimentos.</Text>
        )}
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
        style={styles.scrollView}
        contentContainerStyle={styles.scrollViewContent}
        refreshControl={
          <RefreshControl refreshing={isRefreshing} onRefresh={handleRefresh} />
        }
      >
        <View style={styles.header}>
          <View style={styles.headerLeft}>
            <TouchableOpacity
              style={styles.backButton}
              onPress={handleGoBack}
            >
              <MaterialIcons name="arrow-back" size={24} color="#111" />
            </TouchableOpacity>
            <Text style={styles.title}>Meus Atendimentos</Text>
          </View>
          
          <TouchableOpacity 
            style={styles.exportButton}
            onPress={() => setIsExportModalVisible(true)}
          >
            <MaterialIcons name="file-download" size={20} color="#111" />
            <Text style={styles.exportButtonText}>Exportar</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.content}>
          {futureAttendances.length === 0 && pastAttendances.length === 0 ? (
            <View style={styles.emptyState}>
              <MaterialIcons name="event" size={48} color="#64748b" />
              <Text style={styles.emptyStateTitle}>Nenhum atendimento encontrado</Text>
              <Text style={styles.emptyStateText}>
                Você ainda não possui agendamentos de clientes.
              </Text>
            </View>
          ) : (
            <>
              {renderFutureAttendances()}
              {renderPastAttendances()}
            </>
          )}
        </View>

        <Footer />
      </ScrollView>

      {selectedAttendance && (
        <>
          <AppointmentDetailsModal
            visible={isModalVisible}
            appointment={selectedAttendance}
            onClose={handleCloseModal}
            onCancel={selectedAttendance?.status?.toUpperCase() === 'AGENDADO' ? handleCancelAppointment : undefined}
            showEditButton={false}
            showCancelButton={selectedAttendance?.status?.toUpperCase() === 'AGENDADO'}
            isProfessional={true}
          />

          <CompletedAppointmentDetailsModal
            visible={isCompletedModalVisible}
            appointment={selectedAttendance}
            onClose={handleCloseCompletedModal}
            isProfessional={true}
          />

          <CancelAppointmentModal
            visible={isCancelModalVisible}
            onClose={handleCloseCancelModal}
            onConfirm={handleConfirmCancel}
            appointmentDetails={{
              date: selectedAttendance?.dtInicio,
              service: selectedAttendance?.tipoServico,
              clientName: selectedAttendance?.nomeUsuario
            }}
          />
        </>
      )}

      <ExportAttendancesModal
        visible={isExportModalVisible}
        onClose={() => setIsExportModalVisible(false)}
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
  scrollViewContent: {
    flexGrow: 1,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 16,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#e2e8f0',
  },
  headerLeft: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  backButton: {
    marginRight: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111',
  },
  exportButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F1F5F9',
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 8,
  },
  exportButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
    marginLeft: 4,
  },
  content: {
    flex: 1,
    padding: 16,
    minHeight: '100%',
  },
  section: {
    marginBottom: 24,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: '600',
    color: '#111',
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
  paginationContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
  paginationButton: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: '#F1F5F9',
    justifyContent: 'center',
    alignItems: 'center',
  },
  paginationButtonDisabled: {
    backgroundColor: '#F8FAFC',
  },
  paginationText: {
    fontSize: 14,
    color: '#64748B',
    marginHorizontal: 8,
    minWidth: 20,
    textAlign: 'center',
  },
});

export default MyAttendancesScreen; 