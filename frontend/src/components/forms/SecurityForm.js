import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Linking } from 'react-native';
import Input from '../ui/Input';
import Checkbox from '../ui/Checkbox';
import FormNavigation from '../ui/FormNavigation';
import ApiService from '../../services/ApiService';

const SecurityForm = ({ 
  formData, 
  handleChange,
  handleBlur, 
  handleRegister, 
  handlePrevTab, 
  isLoading,
  passwordError,
  confirmPasswordError
}) => {
  const openTerms = () => {
    Linking.openURL('https://app.inkspiration.com.br/termos-de-uso');
  };

  const openPrivacyPolicy = () => {
    Linking.openURL('https://app.inkspiration.com.br/politica-de-privacidade');
  };

  return (
    <View style={styles.tabContent}>
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
        nextText="Criar Conta"
        isLoading={isLoading}
        nextDisabled={!formData.termsAccepted}
      />
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
  }
});

export default SecurityForm; 