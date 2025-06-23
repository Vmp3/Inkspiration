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
import { appointmentsMessages } from '../components/appointments/messages';
import AppointmentCard from '../components/AppointmentCard';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import AvaliacaoService from '../services/AvaliacaoService';
import AppointmentDetailsModal from '../components/AppointmentDetailsModal';
import CancelAppointmentModal from '../components/CancelAppointmentModal';
import EditAppointmentModal from '../components/EditAppointmentModal';
import CompletedAppointmentDetailsModal from '../components/CompletedAppointmentDetailsModal';
import ExportAppointmentsModal from '../components/ExportAppointmentsModal';

const MyAppointmentsScreen = () => {
  const navigation = useNavigation();
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingFuture, setIsLoadingFuture] = useState(false);
  const [isLoadingPast, setIsLoadingPast] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [futureAppointments, setFutureAppointments] = useState([]);
  const [pastAppointments, setPastAppointments] = useState([]);
  const [currentFuturePage, setCurrentFuturePage] = useState(0);
  const [currentPastPage, setCurrentPastPage] = useState(0);
  const [hasMoreFuturePages, setHasMoreFuturePages] = useState(true);
  const [hasMorePastPages, setHasMorePastPages] = useState(true);
  const [screenData, setScreenData] = useState(Dimensions.get('window'));
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [reviewStars, setReviewStars] = useState(0);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewAppointment, setReviewAppointment] = useState(null);
  const [isSubmittingReview, setIsSubmittingReview] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isCompletedModalVisible, setIsCompletedModalVisible] = useState(false);
  const [isCancelModalVisible, setIsCancelModalVisible] = useState(false);
  const [isEditModalVisible, setIsEditModalVisible] = useState(false);
  const [isExportModalVisible, setIsExportModalVisible] = useState(false);
  const [pageSize] = useState(5);
  const [existingReviewId, setExistingReviewId] = useState(null);

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
      // console.error('Erro ao carregar agendamentos:', error);
      toastHelper.showError(appointmentsMessages.errors.loadAppointments);
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  };

  const loadFutureAppointments = async (page = 0, shouldRefresh = false) => {
    try {
      if (page === 0 && !shouldRefresh) {
        setIsLoading(true);
      } else {
        setIsLoadingFuture(true);
      }

      const response = await AgendamentoService.listarMeusAgendamentosFuturos(page, pageSize);
      const newAppointments = response?.content || [];
      
      setHasMoreFuturePages(!response?.last);
      
      setFutureAppointments(newAppointments);
      
      setCurrentFuturePage(page);
    } catch (error) {
      // console.error('Erro ao carregar agendamentos futuros:', error);
      throw error;
    } finally {
      setIsLoadingFuture(false);
      setIsLoading(false);
    }
  };

  const loadPastAppointments = async (page = 0, shouldRefresh = false) => {
    try {
      if (page === 0 && !shouldRefresh) {
        setIsLoading(true);
      } else {
        setIsLoadingPast(true);
      }

      const response = await AgendamentoService.listarMeusAgendamentosPassados(page, pageSize);
      const newAppointments = response?.content || [];
      
      setHasMorePastPages(!response?.last);
      
      setPastAppointments(newAppointments);
      
      setCurrentPastPage(page);
    } catch (error) {
      // console.error('Erro ao carregar agendamentos passados:', error);
      throw error;
    } finally {
      setIsLoadingPast(false);
      setIsLoading(false);
    }
  };

  const handleNextFuturePage = () => {
    if (!isLoadingFuture && hasMoreFuturePages) {
      loadFutureAppointments(currentFuturePage + 1);
    }
  };

  const handlePrevFuturePage = () => {
    if (!isLoadingFuture && currentFuturePage > 0) {
      loadFutureAppointments(currentFuturePage - 1);
    }
  };

  const handleNextPastPage = () => {
    if (!isLoadingPast && hasMorePastPages) {
      loadPastAppointments(currentPastPage + 1);
    }
  };

  const handlePrevPastPage = () => {
    if (!isLoadingPast && currentPastPage > 0) {
      loadPastAppointments(currentPastPage - 1);
    }
  };

  const handleRefresh = () => {
    setIsRefreshing(true);
    loadAppointments(true);
  };

  const handleGoBack = () => {
    if (navigation.canGoBack()) {
      navigation.goBack();
    } else {
      navigation.navigate('Home');
    }
  };

  const handleAppointmentPress = (appointment) => {
    setSelectedAppointment(appointment);
    
    if (appointment.status?.toUpperCase() === 'CONCLUIDO') {
      setIsCompletedModalVisible(true);
    } else if (appointment.status?.toUpperCase() === 'CANCELADO') {
      setIsModalVisible(true);
    } else {
      setIsModalVisible(true);
    }
  };

  const handleCloseModal = () => {
    setIsModalVisible(false);
    setSelectedAppointment(null);
  };
  
  const handleCloseCompletedModal = () => {
    setIsCompletedModalVisible(false);
    setSelectedAppointment(null);
  };

  const handleEditAppointment = () => {
    if (!selectedAppointment) return;
    
    const today = new Date();
    const appointmentDate = new Date(selectedAppointment.dtInicio);
    const daysDiff = differenceInDays(appointmentDate, today);
    
    if (daysDiff < 3) {
      toastHelper.showError(appointmentsMessages.errors.editTimeLimit);
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
      toastHelper.showError(appointmentsMessages.errors.cancelTimeLimit);
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
      setIsSubmittingReview(true);
      
      await AgendamentoService.atualizarStatusAgendamento(
        selectedAppointment.idAgendamento,
        'CANCELADO'
      );
      
      toastHelper.showSuccess(appointmentsMessages.success.appointmentCanceled);
      setIsCancelModalVisible(false);
      setSelectedAppointment(null);
      
      loadAppointments(true);
    } catch (error) {
      // console.error('Erro ao cancelar agendamento:', error);
      
      let errorMessage = appointmentsMessages.errors.cancelAppointment;
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }
      
      toastHelper.showError(errorMessage);
    } finally {
      setIsSubmittingReview(false);
    }
  };

  const handleOpenReviewModal = async (appointment) => {
    setIsCompletedModalVisible(false);
    setReviewAppointment(appointment);
    
    // Reseta os estados
    setReviewStars(0);
    setReviewComment('');
    setExistingReviewId(null);

    try {
      const avaliacao = await AvaliacaoService.buscarPorAgendamento(appointment.idAgendamento);
      if (avaliacao) {
        setReviewStars(avaliacao.rating || 0);
        setReviewComment(avaliacao.descricao || '');
        setExistingReviewId(avaliacao.idAvaliacao); // Guarda o ID da avaliação existente
      }
    } catch (error) {
      console.error('Erro ao buscar avaliação:', error);
    } finally {
      setTimeout(() => {
        setShowReviewModal(true);
      }, 0);
    }
  };

  const handleCloseReviewModal = () => {
    setShowReviewModal(false);
    setReviewAppointment(null);
    setReviewStars(0);
    setReviewComment('');
    setExistingReviewId(null); // Limpa o ID ao fechar
  };

  const handleSendReview = async () => {
    if (reviewStars === 0) {
      toastHelper.showError('Por favor, selecione uma nota para o artista');
      return;
    }
    setIsSubmittingReview(true);
    try {
      if (existingReviewId) {
        // ATUALIZA avaliação existente
        await AvaliacaoService.atualizarAvaliacao(
          existingReviewId,
          reviewComment,
          reviewStars
        );
        toastHelper.showSuccess('Avaliação atualizada com sucesso!');
      } else {
        // CRIA nova avaliação
        await AvaliacaoService.criarAvaliacao(
          reviewAppointment.idAgendamento,
          reviewComment,
          reviewStars
        );
        toastHelper.showSuccess('Avaliação enviada com sucesso!');
      }
      handleCloseReviewModal();
      loadAppointments(true);
    } catch (error) {
      toastHelper.showError('Erro ao salvar avaliação. Tente novamente.');
    } finally {
      setIsSubmittingReview(false);
    }
  };

  const renderFutureAppointments = () => {
    return (
      <View style={styles.section}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Agendamentos Futuros</Text>
          
          {!isLoading && futureAppointments.length > 0 && (
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
        
        {futureAppointments.length > 0 ? (
          <>
            {futureAppointments.map(appointment => (
              <AppointmentCard
                key={appointment.idAgendamento}
                appointment={appointment}
                onPress={() => handleAppointmentPress(appointment)}
              />
            ))}
            
            {isLoadingFuture && (
              <View style={styles.loadingMoreContainer}>
                <ActivityIndicator size="small" color="#111" />
                <Text style={styles.loadingMoreText}>Carregando agendamentos...</Text>
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
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Histórico de Agendamentos</Text>
          
          {!isLoading && pastAppointments.length > 0 && (
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
        
        {pastAppointments.length > 0 ? (
          <>
            {pastAppointments.map(appointment => (
              <View key={appointment.idAgendamento}>
                <AppointmentCard
                  appointment={appointment}
                  onPress={() => handleAppointmentPress(appointment)}
                />
              </View>
            ))}
            
            {isLoadingPast && (
              <View style={styles.loadingMoreContainer}>
                <ActivityIndicator size="small" color="#111" />
                <Text style={styles.loadingMoreText}>Carregando agendamentos...</Text>
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
        <Footer />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
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
            <Text style={styles.pageTitle}>Meus Agendamentos</Text>
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
      
      <CompletedAppointmentDetailsModal
        visible={isCompletedModalVisible}
        appointment={selectedAppointment}
        onClose={handleCloseCompletedModal}
        onOpenReview={() => handleOpenReviewModal(selectedAppointment)}
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
      
      <ExportAppointmentsModal
        visible={isExportModalVisible}
        onClose={() => setIsExportModalVisible(false)}
      />

      <Modal
        visible={showReviewModal}
        onClose={handleCloseReviewModal}
        title="Avaliar Artista"
        description={`Compartilhe sua experiência com ${reviewAppointment?.nomeProfissional || ''}`}
      >
        <Text style={{ fontWeight: '500', fontSize: 16, alignSelf: 'flex-start', marginBottom: 8 }}>Sua Nota</Text>
        <View style={{ flexDirection: 'row', justifyContent: 'center', marginBottom: 20 }}>
          {[1,2,3,4,5].map((star) => (
            <TouchableOpacity key={star} onPress={() => setReviewStars(star)}>
              <View style={{ position: 'relative', marginHorizontal: 4, width: 38, height: 38, alignItems: 'center', justifyContent: 'center' }}>
                <MaterialIcons
                  name="star"
                  size={38}
                  color="#6B7280"
                  style={{ position: 'absolute', top: 0, left: 0 }}
                />
                <MaterialIcons
                  name="star"
                  size={32}
                  color={star <= reviewStars ? '#FFD700' : '#E5E7EB'}
                  style={{ position: 'absolute', top: 3, left: 3 }}
                />
              </View>
            </TouchableOpacity>
          ))}
        </View>
        <Text style={{ fontWeight: '500', fontSize: 16, alignSelf: 'flex-start', marginBottom: 8 }}>
          Seu comentário (opcional)
          <Text style={{ fontWeight: '400', fontSize: 14, color: reviewComment.length > 500 ? '#EF4444' : '#6B7280' }}>  {reviewComment.length}/500</Text>
        </Text>
        <Input
          placeholder="Conte como foi sua experiência..."
          value={reviewComment}
          onChangeText={text => {
            if (text.length <= 500) setReviewComment(text);
          }}
          multiline
          numberOfLines={4}
          style={{ minHeight: 80, width: '100%', marginBottom: 24 }}
        />
        <View style={{ flexDirection: 'row', justifyContent: 'space-between', width: '100%', marginTop: 8 }}>
          <TouchableOpacity
            style={{ backgroundColor: '#fff', borderWidth: 1, borderColor: '#111', borderRadius: 6, paddingVertical: 10, paddingHorizontal: 24 }}
            onPress={handleCloseReviewModal}
            disabled={isSubmittingReview}
          >
            <Text style={{ color: '#111', fontWeight: '600', fontSize: 16 }}>Cancelar</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={{ backgroundColor: '#111', borderRadius: 6, paddingVertical: 10, paddingHorizontal: 24, opacity: isSubmittingReview || reviewComment.length > 500 ? 0.7 : 1 }}
            onPress={handleSendReview}
            disabled={isSubmittingReview || reviewComment.length > 500}
          >
            <Text style={{ color: '#fff', fontWeight: '600', fontSize: 16 }}>
              {isSubmittingReview ? 'Salvando...' : (existingReviewId ? 'Atualizar' : 'Enviar avaliação')}
            </Text>
          </TouchableOpacity>
        </View>
      </Modal>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    minHeight: '100vh',
    backgroundColor: '#fff',
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 16,
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
  pageTitle: {
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
    padding: 16,
    paddingBottom: 24,
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

export default MyAppointmentsScreen; 