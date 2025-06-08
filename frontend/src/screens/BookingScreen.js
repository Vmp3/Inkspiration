import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  Alert,
  ActivityIndicator,
  Dimensions,
  SafeAreaView
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import { MaterialIcons, Feather } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';
import AgendamentoService from '../services/AgendamentoService';
import ProfessionalService from '../services/ProfessionalService';
import toastHelper from '../utils/toastHelper';

const BookingScreen = () => {
  const navigation = useNavigation();
  const route = useRoute();
  const { userData } = useAuth();
  const { professionalId } = route.params || {};

  const [step, setStep] = useState(1);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);
  const [selectedService, setSelectedService] = useState(null);
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth());
  const [description, setDescription] = useState('');
  const [professional, setProfessional] = useState(null);
  const [services, setServices] = useState([]);
  const [availableTimeSlots, setAvailableTimeSlots] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingTimes, setIsLoadingTimes] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [screenData, setScreenData] = useState(Dimensions.get('window'));

  const isMobile = screenData.width < 768;

  // Gerar meses disponíveis (apenas meses futuros do ano atual)
  const getAvailableMonths = () => {
    const currentDate = new Date();
    const currentMonth = currentDate.getMonth();
    const months = [];
    
    for (let i = currentMonth; i < 12; i++) {
      months.push({
        value: i,
        label: new Date(currentDate.getFullYear(), i).toLocaleDateString('pt-BR', { month: 'long' })
      });
    }
    
    return months;
  };

  const generateDates = () => {
    const dates = [];
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const firstDay = new Date(year, selectedMonth, 1);
    const lastDay = new Date(year, selectedMonth + 1, 0);
    
    const startDay = selectedMonth === currentDate.getMonth() 
      ? currentDate.getDate() 
      : 1;
    
    for (let i = startDay; i <= lastDay.getDate(); i++) {
      const date = new Date(year, selectedMonth, i);
      dates.push({
        date: date.toISOString().split('T')[0],
        day: new Intl.DateTimeFormat('pt-BR', { weekday: 'short' }).format(date),
        dayOfMonth: date.getDate(),
        fullDate: date
      });
    }
    return dates;
  };

  const dates = generateDates();
  const availableMonths = getAvailableMonths();

  useEffect(() => {
    const onChange = (result) => {
      setScreenData(result.window);
    };

    const subscription = Dimensions.addEventListener('change', onChange);
    return () => subscription?.remove();
  }, []);

  useEffect(() => {
    loadInitialData();
  }, [professionalId]);

  useEffect(() => {
    if (selectedDate && selectedService) {
      loadAvailableTimeSlots();
    }
  }, [selectedDate, selectedService]);

  const loadInitialData = async () => {
    try {
      setIsLoading(true);
      
      const professionalData = await ProfessionalService.getProfessionalCompleteById(professionalId);
      setProfessional(professionalData);

      const servicesData = await AgendamentoService.buscarTiposServico();
      setServices(servicesData);

      const today = new Date().toISOString().split('T')[0];
      setSelectedDate(today);
      
    } catch (error) {
      console.error('Erro ao carregar dados iniciais:', error);
      toastHelper.showError('Erro ao carregar dados do profissional');
    } finally {
      setIsLoading(false);
    }
  };

  const loadAvailableTimeSlots = async () => {
    try {
      setIsLoadingTimes(true);
      setAvailableTimeSlots([]);
      setSelectedTime(null);

      const backendServiceType = selectedService;

      const timeSlots = await AgendamentoService.buscarHorariosDisponiveis(
        professionalId,
        selectedDate,
        backendServiceType
      );

      setAvailableTimeSlots(timeSlots || []);
      
    } catch (error) {
      if (!error.message || !error.message.includes('204')) {
        console.error('Erro ao carregar horários disponíveis:', error);
        toastHelper.showError('Erro ao carregar horários disponíveis');
      }
      setAvailableTimeSlots([]);
    } finally {
      setIsLoadingTimes(false);
    }
  };

  const handleNextStep = () => {
    if (step < 4) {
      setStep(step + 1);
    }
  };

  const handlePrevStep = () => {
    if (step > 1) {
      setStep(step - 1);
    }
  };

  const handleConfirmBooking = async () => {
    try {
      setIsSubmitting(true);

      if (!userData?.idUsuario) {
        toastHelper.showError('Você precisa estar logado para fazer um agendamento');
        return;
      }

      if (!selectedService || !selectedDate || !selectedTime) {
        toastHelper.showError('Por favor, preencha todos os campos obrigatórios');
        return;
      }

      const agendamentoData = {
        idUsuario: userData.idUsuario,
        idProfissional: professionalId,
        tipoServico: selectedService,
        descricao: description,
        dtInicio: `${selectedDate}T${selectedTime}:00`
      };

      await AgendamentoService.criarAgendamento(agendamentoData);
      
      setStep(4); 
      toastHelper.showSuccess('Agendamento realizado com sucesso!');
      
    } catch (error) {
      console.error('Erro ao criar agendamento:', error);
      let errorMessage = 'Erro ao criar agendamento';
      
      if (error.response?.data) {
        if (typeof error.response.data === 'object' && !Array.isArray(error.response.data)) {
          const firstError = Object.values(error.response.data)[0];
          errorMessage = firstError || errorMessage;
        } 
        else if (typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        }
      }
      
      toastHelper.showError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleBackPress = () => {
    navigation.navigate('Artist', { id: professionalId });
  };

  const renderProgressBar = () => {
    return (
      <View style={styles.progressContainer}>
        <View style={styles.progressStep}>
          <View style={[styles.progressNumber, step >= 1 && styles.progressNumberActive]}>
            <Text style={[styles.progressNumberText, step >= 1 && styles.progressNumberTextActive]}>1</Text>
          </View>
          <Text style={[styles.progressText, step >= 1 && styles.progressTextActive]}>Serviço</Text>
        </View>

        <View style={styles.progressLine} />

        <View style={styles.progressStep}>
          <View style={[styles.progressNumber, step >= 2 && styles.progressNumberActive]}>
            <Text style={[styles.progressNumberText, step >= 2 && styles.progressNumberTextActive]}>2</Text>
          </View>
          <Text style={[styles.progressText, step >= 2 && styles.progressTextActive]}>Data e Hora</Text>
        </View>

        <View style={styles.progressLine} />

        <View style={styles.progressStep}>
          <View style={[styles.progressNumber, step >= 3 && styles.progressNumberActive]}>
            <Text style={[styles.progressNumberText, step >= 3 && styles.progressNumberTextActive]}>3</Text>
          </View>
          <Text style={[styles.progressText, step >= 3 && styles.progressTextActive]}>Detalhes</Text>
        </View>
      </View>
    );
  };

  const renderServiceSelection = () => {
    return (
      <View style={styles.stepContent}>
        <Text style={styles.stepTitle}>Selecione um Serviço</Text>
        <View style={styles.serviceList}>
          {services.map((service) => (
            <TouchableOpacity
              key={service.tipo}
              onPress={() => setSelectedService(service.tipo)}
            >
              <View style={[
                styles.serviceCard,
                selectedService === service.tipo && styles.serviceCardSelected
              ]}>
                <View style={styles.serviceInfo}>
                  <Text style={styles.serviceName}>{service.exemplo.split('-')[0].trim()}</Text>
                  <Text style={styles.serviceDuration}>
                    Duração: {service.duracaoHoras} horas
                  </Text>
                </View>
              </View>
            </TouchableOpacity>
          ))}
        </View>
      </View>
    );
  };

  const renderDateTimeSelection = () => {
    return (
      <View style={styles.stepContent}>
        <Text style={styles.stepTitle}>Selecione Data e Hora</Text>
        
        {/* Seleção de Mês */}
        <View style={styles.monthSection}>
          <View style={styles.sectionHeader}>
            <MaterialIcons name="event" size={16} color="#64748b" style={styles.sectionIcon} />
            <Text style={styles.sectionTitle}>Selecione o Mês</Text>
          </View>
          <ScrollView 
            horizontal 
            showsHorizontalScrollIndicator={false}
            style={styles.monthsScrollView}
            contentContainerStyle={styles.monthsContainer}
          >
            {availableMonths.map((month) => (
              <TouchableOpacity
                key={month.value}
                style={[
                  styles.monthCard,
                  selectedMonth === month.value && styles.monthCardSelected
                ]}
                onPress={() => {
                  setSelectedMonth(month.value);
                  setSelectedDate(null);
                  setSelectedTime(null);
                }}
              >
                <Text style={[
                  styles.monthText,
                  selectedMonth === month.value && styles.monthTextSelected
                ]}>
                  {month.label}
                </Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
        </View>

        {/* Seleção de Data */}
        <View style={styles.dateSection}>
          <View style={styles.sectionHeader}>
            <MaterialIcons name="calendar-today" size={16} color="#64748b" style={styles.sectionIcon} />
            <Text style={styles.sectionTitle}>Selecione uma Data</Text>
          </View>
          <ScrollView 
            horizontal 
            showsHorizontalScrollIndicator={false}
            style={styles.datesScrollView}
            contentContainerStyle={styles.datesContainer}
          >
            {dates.map((date) => (
              <TouchableOpacity
                key={date.date}
                style={[
                  styles.dateCard,
                  selectedDate === date.date && styles.dateCardSelected
                ]}
                onPress={() => {
                  setSelectedDate(date.date);
                  setSelectedTime(null);
                }}
              >
                <Text style={[
                  styles.dateDay,
                  selectedDate === date.date && styles.dateTextSelected
                ]}>
                  {date.day}
                </Text>
                <Text style={[
                  styles.dateDayNumber,
                  selectedDate === date.date && styles.dateTextSelected
                ]}>
                  {date.dayOfMonth}
                </Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
        </View>

        {/* Seleção de Hora */}
        <View style={styles.timeSection}>
          <View style={styles.sectionHeader}>
            <MaterialIcons name="access-time" size={16} color="#64748b" style={styles.sectionIcon} />
            <Text style={styles.sectionTitle}>Selecione um Horário</Text>
          </View>
          {isLoadingTimes ? (
            <ActivityIndicator size="small" color="#111" style={styles.loadingIndicator} />
          ) : availableTimeSlots.length > 0 ? (
            <View style={styles.timeGrid}>
              {availableTimeSlots.map((time) => (
                <TouchableOpacity
                  key={time}
                  style={[
                    styles.timeCard,
                    selectedTime === time && styles.timeCardSelected
                  ]}
                  onPress={() => setSelectedTime(time)}
                >
                  <Text style={[
                    styles.timeText,
                    selectedTime === time && styles.timeTextSelected
                  ]}>
                    {time}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          ) : (
            <Text style={styles.noTimesText}>
              Não há horários disponíveis para esta data
            </Text>
          )}
        </View>
      </View>
    );
  };

  const renderDetailsForm = () => {
    return (
      <View style={styles.stepContent}>
        <Text style={styles.stepTitle}>Detalhes do Agendamento</Text>
        <View style={styles.formGroup}>
          <Text style={styles.label}>Descrição da Tatuagem</Text>
          <TextInput
            style={styles.textarea}
            multiline
            numberOfLines={4}
            placeholder="Por favor, descreva o que você está procurando, incluindo tamanho, local e quaisquer imagens de referência"
            placeholderTextColor="#94a3b8"
            value={description}
            onChangeText={setDescription}
          />
        </View>
      </View>
    );
  };

  const renderConfirmation = () => {
    const selectedServiceObj = services.find(s => s.tipo === selectedService);
    const formattedDate = selectedDate ? new Date(selectedDate).toLocaleDateString('pt-BR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }) : '';

    return (
      <View style={styles.confirmationContainer}>
        <View style={styles.confirmationIcon}>
          <MaterialIcons name="check" size={32} color="#0066CC" />
        </View>
        <Text style={styles.confirmationTitle}>Agendamento Confirmado!</Text>
        <Text style={styles.confirmationDescription}>
          Seu agendamento foi realizado com sucesso. Enviamos um email de confirmação com todos os detalhes.
        </Text>
        
        <View style={styles.confirmationDetails}>
          <View style={styles.confirmationRow}>
            <Text style={styles.confirmationLabel}>Serviço:</Text>
            <Text style={styles.confirmationValue}>{selectedServiceObj?.exemplo.split('-')[0].trim()}</Text>
          </View>
          <View style={styles.confirmationRow}>
            <Text style={styles.confirmationLabel}>Data:</Text>
            <Text style={styles.confirmationValue}>{formattedDate}</Text>
          </View>
          <View style={styles.confirmationRow}>
            <Text style={styles.confirmationLabel}>Hora:</Text>
            <Text style={styles.confirmationValue}>{selectedTime}</Text>
          </View>
        </View>

        <TouchableOpacity
          style={styles.homeButton}
          onPress={() => navigation.navigate('Home')}
        >
          <Text style={styles.homeButtonText}>Voltar para Início</Text>
        </TouchableOpacity>
      </View>
    );
  };

  const renderCurrentStep = () => {
    switch (step) {
      case 1:
        return renderServiceSelection();
      case 2:
        return renderDateTimeSelection();
      case 3:
        return renderDetailsForm();
      case 4:
        return renderConfirmation();
      default:
        return renderServiceSelection();
    }
  };

  const canProceedToNext = () => {
    switch (step) {
      case 1:
        return selectedService !== null;
      case 2:
        return selectedDate !== null && selectedTime !== null;
      case 3:
        return true;
      default:
        return false;
    }
  };

  if (isLoading) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#10B981" />
          <Text style={styles.loadingText}>Carregando...</Text>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.scrollView}>
        <View style={styles.header}>
          <TouchableOpacity 
            style={styles.backButton}
            onPress={handleBackPress}
          >
            <MaterialIcons name="chevron-left" size={24} color="#64748b" />
            <Text style={styles.backButtonText}>Voltar</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.mainContainer}>
          <View style={styles.card}>
            <View style={styles.cardHeader}>
              <Text style={styles.cardTitle}>Agendar um Horário</Text>
              <Text style={styles.cardDescription}>
                Agende seu horário com {professional?.usuario?.nome || 'Profissional'}
              </Text>
            </View>

            {isLoading ? (
              <ActivityIndicator size="large" color="#0066CC" />
            ) : (
              <>
                {renderProgressBar()}
                <View style={styles.cardContent}>
                  {renderCurrentStep()}
                </View>
                {step < 4 && (
                  <View style={styles.cardFooter}>
                    {step > 1 ? (
                      <TouchableOpacity
                        style={styles.prevButton}
                        onPress={handlePrevStep}
                      >
                        <Text style={styles.prevButtonText}>Voltar</Text>
                      </TouchableOpacity>
                    ) : (
                      <View />
                    )}
                    <TouchableOpacity
                      style={[
                        styles.nextButton,
                        !canProceedToNext() && styles.nextButtonDisabled
                      ]}
                      onPress={step === 3 ? handleConfirmBooking : handleNextStep}
                      disabled={!canProceedToNext() || isSubmitting}
                    >
                      {isSubmitting ? (
                        <ActivityIndicator size="small" color="#FFFFFF" />
                      ) : (
                        <Text style={styles.nextButtonText}>
                          {step === 3 ? 'Confirmar Agendamento' : 'Próximo'}
                        </Text>
                      )}
                    </TouchableOpacity>
                  </View>
                )}
              </>
            )}
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  scrollView: {
    flex: 1,
  },
  header: {
    paddingHorizontal: 16,
    paddingVertical: 24,
    flexDirection: 'row',
    alignItems: 'center',
  },
  backButton: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  backButtonText: {
    marginLeft: 4,
    fontSize: 16,
    color: '#64748b',
  },
  mainContainer: {
    paddingHorizontal: 16,
    maxWidth: 768,
    alignSelf: 'center',
    width: '100%',
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    marginBottom: 32,
  },
  cardHeader: {
    padding: 24,
  },
  cardTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111',
    marginBottom: 8,
  },
  cardDescription: {
    fontSize: 16,
    color: '#64748b',
  },
  cardContent: {
    padding: 24,
  },
  cardFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 24,
    borderTopWidth: 1,
    borderTopColor: '#e2e8f0',
  },
  prevButton: {
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: '#e2e8f0',
  },
  prevButtonText: {
    fontSize: 14,
    color: '#111',
  },
  nextButton: {
    backgroundColor: '#111',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 6,
    minWidth: 100,
    alignItems: 'center',
  },
  nextButtonDisabled: {
    backgroundColor: '#94a3b8',
  },
  nextButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '500',
  },
  progressContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 32,
    paddingHorizontal: 24,
  },
  progressStep: {
    alignItems: 'center',
    flex: 1,
  },
  progressNumber: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: '#e2e8f0',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 8,
  },
  progressNumberActive: {
    backgroundColor: '#111',
  },
  progressNumberText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#64748b',
  },
  progressNumberTextActive: {
    color: '#fff',
  },
  progressText: {
    fontSize: 14,
    color: '#64748b',
  },
  progressTextActive: {
    color: '#111',
    fontWeight: '500',
  },
  progressLine: {
    flex: 1,
    height: 1,
    backgroundColor: '#e2e8f0',
    marginHorizontal: 8,
    marginTop: -20,
  },
  stepContent: {
    marginBottom: 24,
  },
  stepTitle: {
    fontSize: 18,
    fontWeight: '500',
    color: '#111',
    marginBottom: 16,
  },
  serviceList: {
    gap: 16,
  },
  serviceCard: {
    padding: 16,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e2e8f0',
    backgroundColor: '#fff',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  serviceCardSelected: {
    borderColor: '#111',
    backgroundColor: 'rgba(17, 17, 17, 0.05)',
  },
  serviceInfo: {
    flex: 1,
  },
  serviceName: {
    fontSize: 16,
    fontWeight: '500',
    color: '#111',
    marginBottom: 4,
  },
  serviceDuration: {
    fontSize: 14,
    color: '#64748b',
  },
  sectionContainer: {
    marginBottom: 24,
  },
  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  sectionIcon: {
    marginRight: 8,
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
  },
  dateSection: {
    marginBottom: 24,
  },
  timeSection: {
    marginBottom: 24,
  },
  datesScrollView: {
    marginHorizontal: -4,
  },
  datesContainer: {
    paddingHorizontal: 4,
  },
  dateCard: {
    width: 60,
    height: 80,
    marginHorizontal: 4,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e2e8f0',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#fff',
  },
  dateCardSelected: {
    backgroundColor: '#111',
    borderColor: '#111',
  },
  dateDay: {
    fontSize: 12,
    fontWeight: '500',
    color: '#64748b',
    marginBottom: 4,
    textTransform: 'capitalize',
  },
  dateDayNumber: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111',
  },
  dateTextSelected: {
    color: '#fff',
  },
  timeGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    margin: -4,
  },
  timeCard: {
    margin: 4,
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e2e8f0',
    backgroundColor: '#fff',
  },
  timeCardSelected: {
    backgroundColor: '#111',
    borderColor: '#111',
  },
  timeText: {
    fontSize: 14,
    color: '#111',
  },
  timeTextSelected: {
    color: '#fff',
  },
  loadingIndicator: {
    marginVertical: 20,
  },
  noTimesText: {
    fontSize: 14,
    color: '#64748b',
    textAlign: 'center',
    marginVertical: 20,
  },
  formGroup: {
    marginBottom: 16,
  },
  label: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
    marginBottom: 8,
  },
  textarea: {
    minHeight: 100,
    borderWidth: 1,
    borderColor: '#e2e8f0',
    borderRadius: 8,
    padding: 12,
    fontSize: 14,
    color: '#111',
    backgroundColor: '#fff',
    textAlignVertical: 'top',
  },
  confirmationContainer: {
    alignItems: 'center',
    paddingVertical: 32,
  },
  confirmationIcon: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: 'rgba(17, 17, 17, 0.1)',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 16,
  },
  confirmationTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111',
    marginBottom: 8,
    textAlign: 'center',
  },
  confirmationDescription: {
    fontSize: 14,
    color: '#64748b',
    textAlign: 'center',
    marginBottom: 24,
    maxWidth: 400,
  },
  confirmationDetails: {
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    padding: 16,
    width: '100%',
    maxWidth: 400,
    marginBottom: 24,
  },
  confirmationRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  confirmationLabel: {
    fontSize: 14,
    color: '#64748b',
  },
  confirmationValue: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
  },
  homeButton: {
    backgroundColor: '#111',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
    minWidth: 160,
    alignItems: 'center',
  },
  homeButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '500',
  },
  loadingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  loadingText: {
    marginLeft: 8,
    fontSize: 14,
    color: '#6B7280',
  },
  monthSection: {
    marginBottom: 24,
  },
  monthsScrollView: {
    marginHorizontal: -4,
  },
  monthsContainer: {
    paddingHorizontal: 4,
  },
  monthCard: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    marginHorizontal: 4,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e2e8f0',
    backgroundColor: '#fff',
  },
  monthCardSelected: {
    backgroundColor: '#111',
    borderColor: '#111',
  },
  monthText: {
    fontSize: 14,
    color: '#111',
    textTransform: 'capitalize',
  },
  monthTextSelected: {
    color: '#fff',
  },
});

export default BookingScreen;   