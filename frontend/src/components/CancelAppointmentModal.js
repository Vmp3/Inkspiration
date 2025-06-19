import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TouchableWithoutFeedback,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

const CancelAppointmentModal = ({ visible, onClose, onConfirm }) => {
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
              <Text style={styles.modalTitle}>Cancelar Agendamento</Text>
              
              <View style={styles.warningContainer}>
                <MaterialIcons name="warning" size={24} color="#E11D48" />
                <Text style={styles.warningText}>
                  Tem certeza que deseja cancelar este agendamento? Esta ação
                  não pode ser desfeita.
                </Text>
              </View>
              
              <Text style={styles.infoText}>
                Se precisar reagendar, recomendamos editar o agendamento em vez
                de cancelá-lo.
              </Text>
              
              <View style={styles.buttonRow}>
                <TouchableOpacity 
                  style={styles.backButton} 
                  onPress={onClose}
                >
                  <Text style={styles.backButtonText}>Voltar</Text>
                </TouchableOpacity>
                
                <TouchableOpacity 
                  style={styles.confirmButton} 
                  onPress={onConfirm}
                >
                  <Text style={styles.confirmButtonText}>Cancelar Agendamento</Text>
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
    padding: 24,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111',
    marginBottom: 16,
  },
  warningContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    backgroundColor: '#FFF1F2',
    borderRadius: 8,
    padding: 16,
    marginBottom: 16,
  },
  warningText: {
    flex: 1,
    marginLeft: 12,
    fontSize: 14,
    color: '#E11D48',
  },
  infoText: {
    fontSize: 14,
    color: '#64748B',
    marginBottom: 24,
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  backButton: {
    flex: 1,
    paddingVertical: 12,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 8,
    marginRight: 8,
    backgroundColor: '#F1F5F9',
  },
  backButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
  },
  confirmButton: {
    flex: 2,
    paddingVertical: 12,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#E11D48',
    borderRadius: 8,
  },
  confirmButtonText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#fff',
  },
});

export default CancelAppointmentModal; 