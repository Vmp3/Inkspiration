import React from 'react';
import { 
  View, 
  Text, 
  Modal as RNModal, 
  TouchableWithoutFeedback,
  StyleSheet,
  Dimensions 
} from 'react-native';
import Button from './Button';

const Modal = ({
  visible,
  onClose,
  title,
  description,
  confirmText = 'Confirmar',
  cancelText = 'Cancelar',
  onConfirm,
  confirmVariant = 'primary',
  ...props
}) => {
  const screenWidth = Dimensions.get('window').width;
  const isMobile = screenWidth < 768;

  return (
    <RNModal
      visible={visible}
      transparent={true}
      animationType="fade"
      onRequestClose={onClose}
      {...props}
    >
      <TouchableWithoutFeedback onPress={onClose}>
        <View style={styles.overlay}>
          <TouchableWithoutFeedback>
            <View style={[styles.modal, isMobile && styles.mobileModal]}>
              <View style={styles.header}>
                <Text style={styles.title}>{title}</Text>
              </View>
              
              <View style={styles.content}>
                <Text style={styles.description}>{description}</Text>
              </View>
              
              <View style={styles.footer}>
                <Button
                  variant="secondary"
                  label={cancelText}
                  onPress={onClose}
                  style={[styles.button, styles.cancelButton]}
                />
                <Button
                  variant={confirmVariant}
                  label={confirmText}
                  onPress={onConfirm}
                  style={styles.button}
                />
              </View>
            </View>
          </TouchableWithoutFeedback>
        </View>
      </TouchableWithoutFeedback>
    </RNModal>
  );
};

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  modal: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    width: '100%',
    maxWidth: 400,
    boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.25)',
    elevation: 8,
  },
  mobileModal: {
    maxWidth: '90%',
  },
  header: {
    padding: 24,
    paddingBottom: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    color: '#111827',
  },
  content: {
    paddingHorizontal: 24,
    paddingBottom: 16,
  },
  description: {
    fontSize: 14,
    color: '#6B7280',
    lineHeight: 20,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    padding: 24,
    paddingTop: 16,
    gap: 12,
  },
  button: {
    minWidth: 80,
  },
  cancelButton: {
    marginRight: 8,
  },
});

export default Modal; 