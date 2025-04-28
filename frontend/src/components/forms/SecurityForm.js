import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Linking } from 'react-native';
import Input from '../ui/Input';
import Checkbox from '../ui/Checkbox';
import FormNavigation from '../ui/FormNavigation';

const SecurityForm = ({ 
  formData, 
  handleChange, 
  handleRegister, 
  handlePrevTab, 
  isLoading
}) => {
  const handleTermsPress = () => {
    Linking.openURL('http://localhost:8081/termos-de-uso');
  };

  const handlePrivacyPress = () => {
    Linking.openURL('http://localhost:8081/politica-de-privacidade');
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
            style={[
              styles.inputField,
              formData.senha && formData.senha.length < 6 && styles.inputError
            ]}
          />
        </View>
        
        <View style={styles.formGroup}>
          <Text style={styles.formLabel}>Confirmar Senha</Text>
          <Input
            placeholder="••••••••"
            secureTextEntry
            value={formData.confirmarSenha}
            onChangeText={(text) => handleChange('confirmarSenha', text)}
            style={[
              styles.inputField,
              formData.senha !== formData.confirmarSenha && formData.confirmarSenha && styles.inputError
            ]}
          />
        </View>
      </View>

      <View style={styles.checkboxContainer}>
        <Checkbox
          checked={formData.termsAccepted}
          onPress={() => handleChange('termsAccepted', !formData.termsAccepted)}
        />
        <View style={styles.termsTextContainer}>
          <Text style={styles.termsText}>Eu aceito os </Text>
          <TouchableOpacity onPress={handleTermsPress}>
            <Text style={styles.termsLink}>Termos de Uso</Text>
          </TouchableOpacity>
          <Text style={styles.termsText}> e a </Text>
          <TouchableOpacity onPress={handlePrivacyPress}>
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