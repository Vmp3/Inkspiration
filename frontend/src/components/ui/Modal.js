import React from 'react';
import {
  View,
  Text,
  Modal as RNModal,
  Pressable,
  StyleSheet,
  Dimensions,
} from 'react-native';
import Button from './Button';

const { width } = Dimensions.get('window');

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
  const isMobile = width < 768;

  return (
    <RNModal
      visible={visible}
      transparent={true}
      animationType="fade"
      onRequestClose={onClose}
      {...props}
    >
      <Pressable style={styles.overlay} onPress={onClose}>
        <View style={styles.overlay} />
      </Pressable>
      <View style={styles.centeredView}>
        <Pressable>
          <View style={[styles.modal, isMobile && styles.mobileModal]}>
            <View style={styles.header}>
              <Text style={styles.title}>{title}</Text>
            </View>
            
            {description && (
              <View style={styles.content}>
                <Text style={styles.description}>{description}</Text>
              </View>
            )}
            <View style={styles.footer}>
              <Button
                label={cancelText}
                onPress={onClose}
                variant="secondary"
                style={styles.button}
              />
              <Button
                label={confirmText}
                onPress={onConfirm}
                variant={confirmVariant}
                style={styles.button}
              />
            </View>
          </View>
        </Pressable>
      </View>
    </RNModal>
  );
};

const styles = StyleSheet.create({
  overlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  centeredView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  modal: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 24,
    minWidth: 300,
    maxWidth: 500,
    width: '100%',
    boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.25)',
    elevation: 5,
  },
  mobileModal: {
    marginHorizontal: 20,
    maxWidth: '90%',
  },
  header: {
    marginBottom: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    color: '#1F2937',
    textAlign: 'center',
  },
  content: {
    marginBottom: 24,
  },
  description: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
    lineHeight: 20,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 12,
  },
  button: {
    flex: 1,
    marginHorizontal: 4,
  },
});

export default Modal; 