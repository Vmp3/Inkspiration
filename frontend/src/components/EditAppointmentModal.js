import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TouchableWithoutFeedback,
  TextInput,
  ScrollView,
  ActivityIndicator,
  Dimensions
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { differenceInDays } from 'date-fns';
import Toast from 'react-native-toast-message';
import AgendamentoService from '../services/AgendamentoService';
import toastHelper from '../utils/toastHelper';
import { editAppointmentMessages } from './editAppointment/messages';
import { formatCurrency } from '../utils/formatters';
import toastConfig from '../config/toastConfig';

const EditAppointmentModal = ({ visible, appointment, onClose, onSuccess }) => {
  const [step, setStep] = useState(1);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);
  const [selectedService, setSelectedService] = useState(null);
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth());
  const [description, setDescription] = useState('');
  const [descriptionError, setDescriptionError] = useState('');
  
  const [services, setServices] = useState([]);
  const [availableTimeSlots, setAvailableTimeSlots] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingTimes, setIsLoadingTimes] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [screenData, setScreenData] = useState(Dimensions.get('window'));
  const [dates, setDates] = useState([]);

  const orderServices = (services) => {
    const orderMap = {
      'TATUAGEM_PEQUENA': 1,
      'TATUAGEM_MEDIA': 2,
      'TATUAGEM_GRANDE': 3,
      'SESSAO': 4
    };

    return services.sort((a, b) => {
      const orderA = orderMap[a.tipo] || Number.MAX_VALUE;
      const orderB = orderMap[b.tipo] || Number.MAX_VALUE;
      return orderA - orderB;
    });
  };

  const isMobile = screenData.width < 768;

  useEffect(() => {
    const onChange = (result) => {
      setScreenData(result.window);
    };

    const subscription = Dimensions.addEventListener('change', onChange);
    return () => subscription?.remove();
  }, []);

  useEffect(() => {
    if (visible && appointment) {
      loadInitialData();
    }
  }, [visible, appointment]);

  useEffect(() => {
    if (selectedDate && selectedService) {
      loadAvailableTimeSlots();
    }
  }, [selectedDate, selectedService]);

  useEffect(() => {
    setDates(generateDates());
  }, [selectedMonth]);

  const canEdit = () => {
    if (!appointment) return false;
    
    const today = new Date();
    const appointmentDate = new Date(appointment.dtInicio);
    return differenceInDays(appointmentDate, today) >= 3;
  };
  
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
      ? currentDate.getDate() + 1 
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

  const availableMonths = getAvailableMonths();
  
  const loadInitialData = async () => {
    try {
      if (!appointment) return;
      
      setIsLoading(true);
      setStep(1);
      
      setSelectedService('');
      
      const servicesData = await AgendamentoService.buscarTiposServicoPorProfissional(appointment.idProfissional);
      setServices(orderServices(servicesData));
      
      const tipoOriginal = appointment.tipoServico;
      
      setTimeout(() => {
        setSelectedService(tipoOriginal);
      }, 0);
      
      const currentDescription = appointment.descricao || '';
      setDescription(currentDescription);
      setDescriptionError(validateDescription(currentDescription));
      
      const now = new Date();
      const tomorrow = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);
      const tomorrowDate = tomorrow.getFullYear() + '-' + 
        String(tomorrow.getMonth() + 1).padStart(2, '0') + '-' + 
        String(tomorrow.getDate()).padStart(2, '0');
      
      setSelectedDate(tomorrowDate);
      setSelectedMonth(tomorrow.getMonth());
      
      setSelectedTime(null);
      
      setIsLoading(false);
    } catch (error) {
      // // console.error('Erro ao carregar dados para edição:', error);
      toastHelper.showError(editAppointmentMessages.errors.loadAppointment);
      onClose();
    }
  };
  
  const loadAvailableTimeSlots = async () => {
    try {
      if (!appointment) return;
      
      setIsLoadingTimes(true);
      setAvailableTimeSlots([]);
      
      const timeSlots = await AgendamentoService.buscarHorariosDisponiveis(
        appointment.idProfissional,
        selectedDate,
        selectedService
      );
      
      const currentTimeString = new Date(appointment.dtInicio).toTimeString().substring(0, 5);
      let allTimeSlots = timeSlots || [];
      
      if (selectedDate === appointment.dtInicio.split('T')[0] && 
          !allTimeSlots.includes(currentTimeString)) {
        allTimeSlots = [...allTimeSlots, currentTimeString].sort();
      }
      
      setAvailableTimeSlots(allTimeSlots);
      
      if (allTimeSlots.length > 0 && 
          selectedDate === appointment.dtInicio.split('T')[0] && 
          !selectedTime) {
        setSelectedTime(currentTimeString);
      }
    } catch (error) {
      if (!error.message || !error.message.includes('204')) {
        // console.error('Erro ao carregar horários disponíveis:', error);
        toastHelper.showError(editAppointmentMessages.errors.loadSchedules);
      }
      setAvailableTimeSlots([]);
    } finally {
      setIsLoadingTimes(false);
    }
  };
  
  const handleNextStep = () => {
    if (step < 3) {
      setStep(step + 1);
    }
  };

  const handlePrevStep = () => {
    if (step > 1) {
      setStep(step - 1);
    }
  };

  const validateDescription = (text) => {
    if (!text || text.trim().length === 0) {
      return 'Descrição é obrigatória';
    }
    if (text.trim().length < 20) {
      return 'Descrição deve ter pelo menos 20 caracteres';
    }
    if (text.trim().length > 500) {
      return 'Descrição deve ter no máximo 500 caracteres';
    }
    return '';
  };

  const handleDescriptionChange = (text) => {
    setDescription(text);
    const error = validateDescription(text);
    setDescriptionError(error);
  };
  
  const handleUpdateAppointment = async () => {
    try {
      if (!canEdit()) {
        toastHelper.showError(editAppointmentMessages.errors.editTimeLimit);
        return;
      }
      
      if (!selectedService || !selectedDate || !selectedTime) {
        toastHelper.showError(editAppointmentMessages.errors.requiredFields);
        return;
      }
      
      setIsSubmitting(true);
      
      const updatedData = {
        tipoServico: selectedService,
        descricao: description,
        dtInicio: `${selectedDate}T${selectedTime}:00`
      };
      
      await AgendamentoService.atualizarAgendamento(appointment.idAgendamento, updatedData);
      
      toastHelper.showSuccess(editAppointmentMessages.success.appointmentUpdated);
      onSuccess();
      onClose();
    } catch (error) {
      // console.error('Erro ao atualizar agendamento:', error);
      
      let errorMessage = editAppointmentMessages.errors.updateFailed;
      if (error.response?.data) {
        if (typeof error.response.data === 'object' && !Array.isArray(error.response.data)) {
          const firstError = Object.values(error.response.data)[0];
          errorMessage = firstError || errorMessage;
        } else if (typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        }
      }
      
      toastHelper.showError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
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
    const normalizeTipoServico = (tipo) => {
      if (!tipo) return '';
      
      let normalizado = tipo.toLowerCase();
      
      if (normalizado.startsWith('tatuagem_')) {
        normalizado = normalizado.replace('tatuagem_', '');
      }
      
      if (normalizado.includes('pequena')) return 'pequena';
      if (normalizado.includes('media')) return 'media';
      if (normalizado.includes('grande')) return 'grande';
      if (normalizado.includes('sessao')) return 'sessao';
      
      return normalizado;
    };

    return (
      <View style={styles.stepContent}>
        <Text style={styles.stepTitle}>Selecione um Serviço</Text>
        <View style={styles.serviceList}>
          {services.map((service) => {
            const serviceType = service.tipo;
            const normalizedServiceType = normalizeTipoServico(serviceType);
            const normalizedSelectedType = normalizeTipoServico(selectedService);
            
            const isSelected = normalizedServiceType === normalizedSelectedType;
            
            return (
              <TouchableOpacity
                key={serviceType}
                onPress={() => setSelectedService(serviceType)}
              >
                <View style={[
                  styles.serviceCard,
                  isSelected && styles.serviceCardSelected
                ]}>
                  <View style={styles.serviceInfo}>
                    <Text style={[
                      styles.serviceName,
                      isSelected ? {fontWeight: 'bold'} : {}
                    ]}>
                      {service.exemplo.split('-')[0].trim()}
                    </Text>
                    <Text style={styles.serviceDuration}>
                      Duração: {service.duracaoHoras} horas
                    </Text>
                  </View>
                </View>
              </TouchableOpacity>
            );
          })}
        </View>
      </View>
    );
  };

  const renderDateTimeSelection = () => {
    return (
      <View style={styles.stepContent}>
        <Text style={styles.stepTitle}>Selecione Data e Hora</Text>
        
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

        <View style={styles.dateSection}>
          <View style={styles.sectionHeader}>
            <MaterialIcons name="calendar-today" size={16} color="#64748b" style={styles.sectionIcon} />
            <Text style={styles.sectionTitle}>Selecione uma Data</Text>
          </View>
          <ScrollView 
            horizontal 
            showsHorizontalScrollIndicator={true}
            style={styles.datesScrollView}
            contentContainerStyle={styles.datesContainer}
            indicatorStyle="black"
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
    const isValid = descriptionError === '' && description.trim().length >= 20;
    
    return (
      <View style={styles.stepContent}>
        <Text style={styles.stepTitle}>Detalhes do Agendamento</Text>
        {appointment.valor && (
          <View style={styles.valorContainer}>
            <Text style={styles.valorLabel}>Valor do Serviço:</Text>
            <Text style={styles.valorValue}>
              {formatCurrency(appointment.valor)}
            </Text>
          </View>
        )}
        <View style={styles.formGroup}>
          <Text style={styles.label}>Descrição da Tatuagem *</Text>
          <TextInput
            style={[
              styles.textarea,
              descriptionError ? styles.textareaError : null,
              isValid ? styles.textareaValid : null
            ]}
            multiline
            numberOfLines={4}
            placeholder="Por favor, descreva o que você está procurando (mínimo 20 caracteres)"
            placeholderTextColor="#94a3b8"
            value={description}
            onChangeText={handleDescriptionChange}
            maxLength={500}
          />
          <View style={styles.inputInfo}>
            <Text style={[
              styles.characterCount,
              description.length > 500 ? styles.characterCountError : null
            ]}>
              {description.length}/500 caracteres
            </Text>
          </View>
          {descriptionError ? (
            <Text style={styles.errorText}>{descriptionError}</Text>
          ) : null}
        </View>
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
        return canEdit() && 
               description.trim().length >= 20 && 
               description.trim().length <= 500 && 
               descriptionError === '';
      default:
        return false;
    }
  };

  if (!visible || !appointment) return null;

  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="none"
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={onClose}>
        <View style={styles.modalBackdrop}>
          <TouchableWithoutFeedback onPress={(e) => e.stopPropagation()}>
            <View style={styles.modalContainer}>
              <View style={styles.modalHeader}>
                <Text style={styles.modalTitle}>Editar Agendamento</Text>
                <TouchableOpacity style={styles.closeButton} onPress={onClose}>
                  <MaterialIcons name="close" size={24} color="#64748b" />
                </TouchableOpacity>
              </View>
              
              {isLoading ? (
                <View style={styles.loadingContainer}>
                  <ActivityIndicator size="large" color="#111" />
                  <Text style={styles.loadingText}>Carregando...</Text>
                </View>
              ) : !canEdit() ? (
                <View style={styles.errorContainer}>
                  <MaterialIcons name="error-outline" size={64} color="#EF4444" />
                  <Text style={styles.errorTitle}>Edição não permitida</Text>
                  <Text style={styles.errorMessage}>
                    A edição só é permitida com no mínimo 3 dias de antecedência.
                  </Text>
                  <TouchableOpacity 
                    style={styles.closeErrorButton}
                    onPress={onClose}
                  >
                    <Text style={styles.closeErrorButtonText}>Fechar</Text>
                  </TouchableOpacity>
                </View>
              ) : (
                <>
                  {renderProgressBar()}
                  
                  <ScrollView style={styles.modalContent}>
                    {renderCurrentStep()}
                  </ScrollView>
                  
                  <View style={styles.modalFooter}>
                    {step > 1 ? (
                      <TouchableOpacity
                        style={styles.prevButton}
                        onPress={handlePrevStep}
                      >
                        <Text style={styles.prevButtonText}>Voltar</Text>
                      </TouchableOpacity>
                    ) : (
                      <View style={styles.emptyButton} />
                    )}
                    
                    <TouchableOpacity
                      style={[
                        styles.nextButton,
                        !canProceedToNext() && styles.nextButtonDisabled
                      ]}
                      onPress={step === 3 ? handleUpdateAppointment : handleNextStep}
                      disabled={!canProceedToNext() || isSubmitting}
                    >
                      {isSubmitting ? (
                        <ActivityIndicator size="small" color="#FFFFFF" />
                      ) : (
                        <Text style={styles.nextButtonText}>
                          {step === 3 ? 'Salvar Alterações' : 'Próximo'}
                        </Text>
                      )}
                    </TouchableOpacity>
                  </View>
                </>
              )}
            </View>
          </TouchableWithoutFeedback>
        </View>
      </TouchableWithoutFeedback>
      
      <Toast 
        config={toastConfig} 
        style={{ 
          zIndex: 999999, 
          elevation: 999999,
          position: 'absolute',
          bottom: 50,
          left: 0,
          right: 0
        }} 
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
    maxWidth: 600,
    maxHeight: '95%',
    backgroundColor: '#fff',
    borderRadius: 8,
    overflow: 'hidden',
  },
  modalHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    borderBottomWidth: 1,
    borderBottomColor: '#E2E8F0',
    padding: 16,
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#111',
  },
  closeButton: {
    padding: 4,
  },
  modalContent: {
    padding: 16,
    maxHeight: 500,
  },
  modalFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 16,
    borderTopWidth: 1,
    borderTopColor: '#E2E8F0',
  },
  loadingContainer: {
    padding: 40,
    alignItems: 'center',
    justifyContent: 'center',
  },
  loadingText: {
    marginTop: 16,
    fontSize: 16,
    color: '#64748B',
  },
  errorContainer: {
    padding: 32,
    alignItems: 'center',
    justifyContent: 'center',
  },
  errorTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111',
    marginTop: 16,
  },
  errorMessage: {
    fontSize: 14,
    color: '#64748B',
    textAlign: 'center',
    marginTop: 8,
    marginBottom: 24,
  },
  closeErrorButton: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    backgroundColor: '#F1F5F9',
    borderRadius: 8,
  },
  closeErrorButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
  },
  progressContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 24,
    paddingVertical: 16,
  },
  progressStep: {
    alignItems: 'center',
    width: 80,
  },
  progressNumber: {
    width: 24,
    height: 24,
    borderRadius: 12,
    backgroundColor: '#E2E8F0',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 4,
  },
  progressNumberActive: {
    backgroundColor: '#111',
  },
  progressNumberText: {
    fontSize: 12,
    fontWeight: '500',
    color: '#64748B',
  },
  progressNumberTextActive: {
    color: '#FFFFFF',
  },
  progressText: {
    fontSize: 12,
    color: '#64748B',
  },
  progressTextActive: {
    fontWeight: '500',
    color: '#111',
  },
  progressLine: {
    flex: 1,
    height: 1,
    backgroundColor: '#E2E8F0',
    marginHorizontal: 8,
  },
  stepContent: {
    paddingVertical: 16,
  },
  stepTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111',
    marginBottom: 16,
  },
  serviceList: {
    marginBottom: 16,
  },
  serviceCard: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
    padding: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  serviceCardSelected: {
    borderColor: '#111',
    backgroundColor: '#F8FAFC',
  },
  serviceInfo: {
    flex: 1,
  },
  serviceName: {
    fontSize: 16,
    fontWeight: '500',
    color: '#111',
  },
  serviceDuration: {
    fontSize: 14,
    color: '#64748B',
    marginTop: 4,
  },
  monthSection: {
    marginBottom: 16,
  },
  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  sectionIcon: {
    marginRight: 4,
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111',
  },
  monthsScrollView: {
    marginLeft: -8,
  },
  monthsContainer: {
    paddingLeft: 8,
    paddingBottom: 8,
  },
  monthCard: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    backgroundColor: '#F1F5F9',
    borderRadius: 16,
    marginRight: 8,
    alignItems: 'center',
    justifyContent: 'center',
  },
  monthCardSelected: {
    backgroundColor: '#111',
  },
  monthText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#64748B',
    textTransform: 'capitalize',
  },
  monthTextSelected: {
    color: '#FFFFFF',
  },
  dateSection: {
    marginBottom: 16,
  },
  datesScrollView: {
    marginLeft: -8,
  },
  datesContainer: {
    paddingLeft: 8,
    paddingBottom: 8,
  },
  dateCard: {
    width: 64,
    height: 64,
    backgroundColor: '#F1F5F9',
    borderRadius: 8,
    marginRight: 8,
    alignItems: 'center',
    justifyContent: 'center',
  },
  dateCardSelected: {
    backgroundColor: '#111',
  },
  dateDay: {
    fontSize: 12,
    color: '#64748B',
    textTransform: 'uppercase',
  },
  dateDayNumber: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111',
    marginTop: 4,
  },
  dateTextSelected: {
    color: '#FFFFFF',
  },
  timeSection: {
    marginBottom: 16,
  },
  loadingIndicator: {
    marginVertical: 16,
  },
  timeGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -4,
  },
  timeCard: {
    width: '30%',
    marginHorizontal: '1.66%',
    marginBottom: 8,
    paddingVertical: 12,
    alignItems: 'center',
    backgroundColor: '#F1F5F9',
    borderRadius: 8,
  },
  timeCardSelected: {
    backgroundColor: '#111',
  },
  timeText: {
    fontSize: 14,
    color: '#111',
  },
  timeTextSelected: {
    color: '#FFFFFF',
  },
  noTimesText: {
    fontSize: 14,
    color: '#64748B',
    textAlign: 'center',
    marginVertical: 16,
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
    height: 120,
    borderWidth: 1,
    borderColor: '#E2E8F0',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    color: '#111',
    textAlignVertical: 'top',
  },
  textareaError: {
    borderColor: '#ef4444',
    backgroundColor: '#fef2f2',
  },
  textareaValid: {
    borderColor: '#10b981',
    backgroundColor: '#f0fdf4',
  },
  inputInfo: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginTop: 4,
    marginBottom: 4,
  },
  characterCount: {
    fontSize: 12,
    color: '#64748b',
  },
  characterCountError: {
    color: '#ef4444',
  },
  errorText: {
    fontSize: 12,
    color: '#ef4444',
    marginTop: 4,
  },
  prevButton: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    backgroundColor: '#F1F5F9',
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
    marginRight: 8,
  },
  emptyButton: {
    flex: 1,
    marginRight: 8,
  },
  prevButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
  },
  nextButton: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    backgroundColor: '#111',
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    flex: 2,
  },
  nextButtonDisabled: {
    backgroundColor: '#CBD5E1',
  },
  nextButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#FFFFFF',
  },
  valorContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#F8FAFC',
    padding: 12,
    borderRadius: 8,
    marginBottom: 16,
  },
  valorLabel: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
  },
  valorValue: {
    fontSize: 16,
    fontWeight: '600',
    color: '#059669',
  },
});

export default EditAppointmentModal;