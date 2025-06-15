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
  RefreshControl
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { MaterialIcons } from '@expo/vector-icons';
import AgendamentoService from '../services/AgendamentoService';
import toastHelper from '../utils/toastHelper';
import Footer from '../components/Footer';
import AppointmentCard from '../components/AppointmentCard';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';

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
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [reviewStars, setReviewStars] = useState(0);
  const [reviewComment, setReviewComment] = useState('');

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
  };

  const handleOpenReviewModal = (appointment) => {
    setSelectedAppointment(appointment);
    setShowReviewModal(true);
    setReviewStars(0);
    setReviewComment('');
  };

  const handleCloseReviewModal = () => {
    setShowReviewModal(false);
    setSelectedAppointment(null);
    setReviewStars(0);
    setReviewComment('');
  };

  const handleSendReview = () => {
    setShowReviewModal(false);
    setSelectedAppointment(null);
    setReviewStars(0);
    setReviewComment('');
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
              <View key={appointment.idAgendamento}>
                <AppointmentCard
                  appointment={appointment}
                  onPress={() => handleAppointmentPress(appointment)}
                />
                <TouchableOpacity
                  style={{ alignSelf: 'flex-end', marginBottom: 12, backgroundColor: '#2563eb', borderRadius: 6, paddingVertical: 6, paddingHorizontal: 16 }}
                  onPress={() => handleOpenReviewModal(appointment)}
                >
                  <Text style={{ color: '#fff', fontWeight: '600' }}>Avaliação</Text>
                </TouchableOpacity>
              </View>
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
      {showReviewModal && selectedAppointment && (
        <Modal
          visible={showReviewModal}
          onClose={handleCloseReviewModal}
          title="Avaliar Artista"
          description={`Compartilhe sua experiência com ${selectedAppointment.nomeProfissional}`}
          confirmText={undefined}
          cancelText={undefined}
          onConfirm={undefined}
          confirmVariant={undefined}
        >
          <View style={{ alignItems: 'center', paddingHorizontal: 8, paddingTop: 8, width: 320 }}>
            <Text style={{ fontWeight: '500', fontSize: 16, alignSelf: 'flex-start', marginBottom: 8 }}>Sua Nota</Text>
            <View style={{ flexDirection: 'row', justifyContent: 'center', marginBottom: 20 }}>
              {[1,2,3,4,5].map((star) => (
                <TouchableOpacity key={star} onPress={() => setReviewStars(star)}>
                  <View style={{ position: 'relative', marginHorizontal: 4, width: 38, height: 38, alignItems: 'center', justifyContent: 'center' }}>
                    {/* Estrela "borda" */}
                    <MaterialIcons
                      name="star"
                      size={38}
                      color="#6B7280"
                      style={{ position: 'absolute', top: 0, left: 0 }}
                    />
                    {/* Estrela "preenchida" */}
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
            <Text style={{ fontWeight: '500', fontSize: 16, alignSelf: 'flex-start', marginBottom: 8 }}>Seu comentário (opcional)</Text>
            <Input
              placeholder="Conte como foi sua experiência..."
              value={reviewComment}
              onChangeText={setReviewComment}
              multiline
              numberOfLines={4}
              style={{ minHeight: 80, width: '100%', marginBottom: 24 }}
            />
            <View style={{ flexDirection: 'row', justifyContent: 'space-between', width: '100%', marginTop: 8 }}>
              <TouchableOpacity
                style={{ backgroundColor: '#fff', borderWidth: 1, borderColor: '#111', borderRadius: 6, paddingVertical: 10, paddingHorizontal: 24 }}
                onPress={handleCloseReviewModal}
              >
                <Text style={{ color: '#111', fontWeight: '600', fontSize: 16 }}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={{ backgroundColor: '#111', borderRadius: 6, paddingVertical: 10, paddingHorizontal: 24 }}
                onPress={handleSendReview}
              >
                <Text style={{ color: '#fff', fontWeight: '600', fontSize: 16 }}>Enviar avaliação</Text>
              </TouchableOpacity>
            </View>
          </View>
        </Modal>
      )}
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