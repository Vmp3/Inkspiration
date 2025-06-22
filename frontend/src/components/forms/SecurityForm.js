import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Linking, Modal, ScrollView } from 'react-native';
import { Feather } from '@expo/vector-icons';
import Input from '../ui/Input';
import Checkbox from '../ui/Checkbox';
import FormNavigation from '../ui/FormNavigation';
import TermsAndPolicies from './TermsAndPolicies';
import { isMobileView } from '../../utils/responsive';

const SecurityForm = ({ 
  formData, 
  handleChange,
  handleBlur, 
  handleRegister, 
  handlePrevTab, 
  isLoading,
  passwordError,
  confirmPasswordError,
  isValid = true
}) => {
  const [showTermsModal, setShowTermsModal] = useState(false);
  const [showPrivacyModal, setShowPrivacyModal] = useState(false);
  const isMobile = isMobileView();

  const openTerms = () => {
    setShowTermsModal(true);
  };

  const openPrivacyPolicy = () => {
    setShowPrivacyModal(true);
  };

  return (
    <View style={styles.tabContent}>
      {/* Senha e Confirmar Senha */}
      {isMobile ? (
        // Layout mobile: um campo por linha
        <>
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>Senha</Text>
            <Input
              placeholder="••••••••"
              secureTextEntry
              value={formData.senha}
              onChangeText={(text) => handleChange('senha', text)}
              onBlur={() => handleBlur('senha')}
              style={[
                styles.inputField,
                passwordError && styles.inputError
              ]}
            />
            {passwordError ? <Text style={styles.errorText}>{passwordError}</Text> : null}
          </View>
          
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>Confirmar Senha</Text>
            <Input
              placeholder="••••••••"
              secureTextEntry
              value={formData.confirmarSenha}
              onChangeText={(text) => handleChange('confirmarSenha', text)}
              onBlur={() => handleBlur('confirmarSenha')}
              style={[
                styles.inputField,
                confirmPasswordError && styles.inputError
              ]}
            />
            {confirmPasswordError ? <Text style={styles.errorText}>{confirmPasswordError}</Text> : null}
          </View>
        </>
      ) : (
        // Layout web/tablet: dois campos por linha
        <View style={styles.formRow}>
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>Senha</Text>
            <Input
              placeholder="••••••••"
              secureTextEntry
              value={formData.senha}
              onChangeText={(text) => handleChange('senha', text)}
              onBlur={() => handleBlur('senha')}
              style={[
                styles.inputField,
                passwordError && styles.inputError
              ]}
            />
            {passwordError ? <Text style={styles.errorText}>{passwordError}</Text> : null}
          </View>
          
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>Confirmar Senha</Text>
            <Input
              placeholder="••••••••"
              secureTextEntry
              value={formData.confirmarSenha}
              onChangeText={(text) => handleChange('confirmarSenha', text)}
              onBlur={() => handleBlur('confirmarSenha')}
              style={[
                styles.inputField,
                confirmPasswordError && styles.inputError
              ]}
            />
            {confirmPasswordError ? <Text style={styles.errorText}>{confirmPasswordError}</Text> : null}
          </View>
        </View>
      )}

      <View style={styles.checkboxContainer}>
        <Checkbox
          checked={formData.termsAccepted}
          onPress={() => handleChange('termsAccepted', !formData.termsAccepted)}
        />
        <View style={styles.termsTextContainer}>
          <Text style={styles.termsText}>Eu aceito os </Text>
          <TouchableOpacity onPress={openTerms}>
            <Text style={styles.termsLink}>Termos de Uso</Text>
          </TouchableOpacity>
          <Text style={styles.termsText}> e a </Text>
          <TouchableOpacity onPress={openPrivacyPolicy}>
            <Text style={styles.termsLink}>Política de Privacidade</Text>
          </TouchableOpacity>
        </View>
      </View>
      
      <FormNavigation
        onPrev={handlePrevTab}
        onNext={handleRegister}
        showNext={true}
        showPrev={true}
        nextText={isLoading ? "Enviando email..." : "Criar Conta"}
        isLoading={isLoading}
        nextDisabled={!isValid}
      />

      {/* Modal de Termos de Uso */}
      <Modal
        visible={showTermsModal}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setShowTermsModal(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContainer}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Termos de Uso</Text>
              <TouchableOpacity onPress={() => setShowTermsModal(false)}>
                <Feather name="x" size={24} color="#111" />
              </TouchableOpacity>
            </View>
            <ScrollView style={styles.modalContent} showsVerticalScrollIndicator={true}>
              <TermsAndPolicies type="terms" />
            </ScrollView>
            <View style={styles.modalFooter}>
              <TouchableOpacity 
                style={styles.modalButton} 
                onPress={() => setShowTermsModal(false)}
              >
                <Text style={styles.modalButtonText}>Fechar</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* Modal de Política de Privacidade */}
      <Modal
        visible={showPrivacyModal}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setShowPrivacyModal(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContainer}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Política de Privacidade</Text>
              <TouchableOpacity onPress={() => setShowPrivacyModal(false)}>
                <Feather name="x" size={24} color="#111" />
              </TouchableOpacity>
            </View>
            <ScrollView style={styles.modalContent} showsVerticalScrollIndicator={true}>
              <TermsAndPolicies type="privacy" />
            </ScrollView>
            <View style={styles.modalFooter}>
              <TouchableOpacity 
                style={styles.modalButton} 
                onPress={() => setShowPrivacyModal(false)}
              >
                <Text style={styles.modalButtonText}>Fechar</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  tabContent: {
    flex: 1,
  },
  formRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 24,
    marginHorizontal: -10,
  },
  formGroup: {
    flex: 1,
    marginHorizontal: 10,
  },
  formFullWidth: {
    marginBottom: 24,
  },
  formLabel: {
    marginBottom: 8,
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
  },
  inputField: {
    height: 40,
    borderWidth: 1,
    borderColor: '#e2e2e2',
    borderRadius: 4,
    paddingHorizontal: 12,
    fontSize: 14,
    backgroundColor: '#fff',
  },
  inputError: {
    borderColor: '#ef5350',
  },
  errorText: {
    color: '#ef5350',
    fontSize: 12,
    marginTop: 4,
  },
  checkboxContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 16,
  },
  termsTextContainer: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginLeft: 8,
  },
  termsText: {
    fontSize: 14,
    color: '#666',
  },
  termsLink: {
    fontSize: 14,
    color: '#1976d2',
    textDecorationLine: 'underline',
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  modalContainer: {
    backgroundColor: '#fff',
    borderRadius: 12,
    width: '100%',
    maxWidth: 600,
    maxHeight: '80%',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.25,
    shadowRadius: 8,
    elevation: 8,
  },
  modalHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#111',
  },
  modalContent: {
    flex: 1,
    padding: 20,
  },
  modalText: {
    fontSize: 14,
    lineHeight: 20,
    color: '#333',
    textAlign: 'left',
  },
  modalFooter: {
    padding: 20,
    borderTopWidth: 1,
    borderTopColor: '#eee',
    alignItems: 'center',
  },
  modalButton: {
    backgroundColor: '#111',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 6,
    minWidth: 100,
    alignItems: 'center',
  },
  modalButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  formFullWidth: {
    flex: 1,
    marginHorizontal: 10,
  },
});

export default SecurityForm; 