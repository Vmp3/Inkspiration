import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  ActivityIndicator
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { Picker } from '@react-native-picker/picker';
import AgendamentoService from '../services/AgendamentoService';
import PDFExportService from '../services/PDFExportService';
import toastHelper from '../utils/toastHelper';

const ExportAttendancesModal = ({ visible, onClose }) => {
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [years, setYears] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const months = [
    { value: 1, label: 'Janeiro' },
    { value: 2, label: 'Fevereiro' },
    { value: 3, label: 'Março' },
    { value: 4, label: 'Abril' },
    { value: 5, label: 'Maio' },
    { value: 6, label: 'Junho' },
    { value: 7, label: 'Julho' },
    { value: 8, label: 'Agosto' },
    { value: 9, label: 'Setembro' },
    { value: 10, label: 'Outubro' },
    { value: 11, label: 'Novembro' },
    { value: 12, label: 'Dezembro' }
  ];

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
    setSelectedMonth(new Date().getMonth() + 1);
  };

  const handleExportPDF = async () => {
    try {
      setIsLoading(true);
      
      const response = await AgendamentoService.exportarAtendimentosPDF(selectedYear, selectedMonth);
      const filename = `atendimentos-${selectedMonth.toString().padStart(2, '0')}-${selectedYear}.pdf`;
      
      await PDFExportService.exportToPDF(response, filename);
      
      onClose();
    } catch (error) {
      let errorMessage = 'Erro ao gerar o PDF. Tente novamente.';
      
      if (error.response?.status === 404) {
        const monthName = months.find(m => m.value === selectedMonth)?.label;
        errorMessage = `Nenhum atendimento concluído foi encontrado para ${monthName}/${selectedYear}. Selecione um período diferente.`;
      } else if (error.response?.status === 500 && 
          error.response?.data && 
          typeof error.response.data === 'string' && 
          error.response.data.includes('Nenhum atendimento concluído encontrado')) {
        const monthName = months.find(m => m.value === selectedMonth)?.label;
        errorMessage = `Nenhum atendimento concluído foi encontrado para ${monthName}/${selectedYear}. Selecione um período diferente.`;
      } else if (error.message && error.message.includes('Nenhum atendimento concluído encontrado')) {
        const monthName = months.find(m => m.value === selectedMonth)?.label;
        errorMessage = `Nenhum atendimento concluído foi encontrado para ${monthName}/${selectedYear}. Selecione um período diferente.`;
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
      <View style={styles.modalBackdrop}>
        <View style={styles.modalContainer}>
          <View style={styles.modalHeader}>
            <Text style={styles.modalTitle}>Exportar Atendimentos</Text>
            <TouchableOpacity style={styles.closeButton} onPress={onClose}>
              <MaterialIcons name="close" size={24} color="#64748b" />
            </TouchableOpacity>
          </View>

          <View style={styles.modalContent}>
            <Text style={styles.label}>Selecione o período dos atendimentos:</Text>
            
            <View style={styles.pickerRow}>
              <View style={styles.pickerContainer}>
                <Text style={styles.pickerLabel}>Mês:</Text>
                <Picker
                  selectedValue={selectedMonth}
                  onValueChange={(itemValue) => setSelectedMonth(itemValue)}
                  style={styles.picker}
                  itemStyle={styles.pickerItem}
                >
                  {months.map((month) => (
                    <Picker.Item key={month.value} label={month.label} value={month.value} />
                  ))}
                </Picker>
              </View>
              
              <View style={styles.pickerContainer}>
                <Text style={styles.pickerLabel}>Ano:</Text>
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
            </View>
            
            <Text style={styles.note}>
              Serão exportados todos os atendimentos concluídos do período selecionado.
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
      </View>
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
    marginBottom: 16,
  },
  pickerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  pickerContainer: {
    flex: 1,
    marginHorizontal: 4,
  },
  pickerLabel: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
    marginBottom: 8,
  },
  picker: {
    height: 50,
    width: '100%',
    borderWidth: 1,
    borderColor: '#E2E8F0',
    borderRadius: 8,
    backgroundColor: '#F8FAFC',
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

export default ExportAttendancesModal; 