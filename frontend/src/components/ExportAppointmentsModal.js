import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TouchableWithoutFeedback,
  ActivityIndicator
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { Picker } from '@react-native-picker/picker';
import AgendamentoService from '../services/AgendamentoService';
import PDFExportService from '../services/PDFExportService';
import toastHelper from '../utils/toastHelper';

const ExportAppointmentsModal = ({ visible, onClose }) => {
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [years, setYears] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (visible) {
      generateYearOptions();
    }
  }, [visible]);

  const generateYearOptions = () => {
    const currentYear = new Date().getFullYear();
    const yearOptions = [];
    
    for (let i = 0; i < 5; i++) {
      yearOptions.push(currentYear - i);
    }
    
    setYears(yearOptions);
    setSelectedYear(currentYear);
  };

  const handleExportPDF = async () => {
    try {
      setIsLoading(true);
      
      const response = await AgendamentoService.exportarAgendamentosPDF(selectedYear);
      const filename = `agendamentos-${selectedYear}.pdf`;
      
      await PDFExportService.exportToPDF(response, filename);
      
      onClose();
    } catch (error) {
      let errorMessage = 'Erro ao gerar o PDF. Tente novamente.';
      
      if (error.response?.status === 404) {
        errorMessage = `Nenhum agendamento concluído foi encontrado para o ano ${selectedYear}. Selecione um ano diferente.`;
      } else if (error.response?.status === 500 && 
          error.response?.data && 
          typeof error.response.data === 'string' && 
          error.response.data.includes('Nenhum agendamento concluído encontrado')) {
        errorMessage = `Nenhum agendamento concluído foi encontrado para o ano ${selectedYear}. Selecione um ano diferente.`;
      } else if (error.message && error.message.includes('Nenhum agendamento concluído encontrado')) {
        errorMessage = `Nenhum agendamento concluído foi encontrado para o ano ${selectedYear}. Selecione um ano diferente.`;
      } else if (error.response?.status === 401) {
        errorMessage = 'Sessão expirada. Faça login novamente.';
      } else if (error.response?.status === 403) {
        errorMessage = 'Você não tem permissão para exportar estes dados.';
      }
      
      toastHelper.showError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="fade"
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={onClose}>
        <View style={styles.modalBackdrop}>
          <TouchableWithoutFeedback onPress={(e) => e.stopPropagation()}>
            <View style={styles.modalContainer}>
              <View style={styles.modalHeader}>
                <Text style={styles.modalTitle}>Exportar Agendamentos</Text>
                <TouchableOpacity style={styles.closeButton} onPress={onClose}>
                  <MaterialIcons name="close" size={24} color="#64748b" />
                </TouchableOpacity>
              </View>

              <View style={styles.modalContent}>
                <Text style={styles.label}>Selecione o ano dos agendamentos:</Text>
                
                <View style={styles.pickerContainer}>
                  <Picker
                    selectedValue={selectedYear}
                    onValueChange={(itemValue) => setSelectedYear(itemValue)}
                    style={styles.picker}
                    itemStyle={styles.pickerItem}
                  >
                    {years.map((year) => (
                      <Picker.Item key={year} label={year.toString()} value={year} />
                    ))}
                  </Picker>
                </View>
                
                <Text style={styles.note}>
                  Serão exportados todos os agendamentos concluídos do ano selecionado.
                </Text>
              </View>

              <View style={styles.modalFooter}>
                <TouchableOpacity
                  style={styles.cancelButton}
                  onPress={onClose}
                  disabled={isLoading}
                >
                  <Text style={styles.cancelButtonText}>Cancelar</Text>
                </TouchableOpacity>
                
                <TouchableOpacity
                  style={styles.exportButton}
                  onPress={handleExportPDF}
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <ActivityIndicator size="small" color="#fff" />
                  ) : (
                    <>
                      <MaterialIcons name="picture-as-pdf" size={18} color="#fff" style={styles.buttonIcon} />
                      <Text style={styles.exportButtonText}>Baixar em PDF</Text>
                    </>
                  )}
                </TouchableOpacity>
              </View>
            </View>
          </TouchableWithoutFeedback>
        </View>
      </TouchableWithoutFeedback>
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
  },
  modalHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#E2E8F0',
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111',
  },
  closeButton: {
    padding: 4,
  },
  modalContent: {
    padding: 16,
  },
  label: {
    fontSize: 16,
    fontWeight: '500',
    color: '#111',
    marginBottom: 12,
  },
  pickerContainer: {
    borderWidth: 1,
    borderColor: '#E2E8F0',
    borderRadius: 8,
    marginBottom: 16,
    backgroundColor: '#F8FAFC',
  },
  picker: {
    height: 50,
    width: '100%',
  },
  pickerItem: {
    fontSize: 16,
  },
  note: {
    fontSize: 14,
    color: '#64748B',
    marginBottom: 16,
    fontStyle: 'italic',
  },
  modalFooter: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    padding: 16,
    borderTopWidth: 1,
    borderTopColor: '#E2E8F0',
  },
  cancelButton: {
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 8,
    marginRight: 8,
    backgroundColor: '#F1F5F9',
  },
  cancelButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#64748B',
  },
  exportButton: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#111',
  },
  buttonIcon: {
    marginRight: 8,
  },
  exportButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#fff',
  },
});

export default ExportAppointmentsModal; 