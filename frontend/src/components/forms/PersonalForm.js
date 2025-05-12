import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import Input from '../ui/Input';
import Checkbox from '../ui/Checkbox';
import theme from '../../themes/theme';

const PersonalForm = ({ 
  formData, 
  handleChange, 
  handleBlur, 
  cpfError, 
  emailError,
  phoneError,
  birthDateError,
  nomeError,
  sobrenomeError,
  fullNameError,
  isArtist, 
  setIsArtist 
}) => {
  return (
    <View style={styles.tabContent}>
      <View style={styles.formRow}>
        <View style={styles.formGroup}>
          <Text style={styles.formLabel}>Nome</Text>
          <Input
            placeholder="Seu nome"
            value={formData.nome}
            onChangeText={(text) => handleChange('nome', text)}
            onBlur={() => handleBlur('nome')}
            style={[styles.inputField, nomeError && styles.inputError]}
          />
          {nomeError ? <Text style={styles.errorText}>{nomeError}</Text> : null}
        </View>
        
        <View style={styles.formGroup}>
          <Text style={styles.formLabel}>Sobrenome</Text>
          <Input
            placeholder="Seu sobrenome"
            value={formData.sobrenome}
            onChangeText={(text) => handleChange('sobrenome', text)}
            onBlur={() => handleBlur('sobrenome')}
            style={[styles.inputField, sobrenomeError && styles.inputError]}
          />
          {sobrenomeError ? <Text style={styles.errorText}>{sobrenomeError}</Text> : null}
        </View>
      </View>

      {fullNameError ? (
        <View style={styles.fullNameErrorContainer}>
          <Text style={styles.errorText}>{fullNameError}</Text>
        </View>
      ) : null}
      
      <View style={styles.formFullWidth}>
        <Text style={styles.formLabel}>CPF</Text>
        <Input
          placeholder="000.000.000-00"
          value={formData.cpf}
          onChangeText={(text) => handleChange('cpf', text)}
          onBlur={() => handleBlur('cpf')}
          keyboardType="numeric"
          style={[styles.inputField, cpfError && styles.inputError]}
        />
        {cpfError ? <Text style={styles.errorText}>{cpfError}</Text> : null}
      </View>
      
      <View style={styles.formRow}>
        <View style={styles.formGroup}>
          <Text style={styles.formLabel}>Email</Text>
          <Input
            placeholder="seu@email.com"
            keyboardType="email-address"
            value={formData.email}
            onChangeText={(text) => handleChange('email', text)}
            onBlur={() => handleBlur('email')}
            style={[styles.inputField, emailError && styles.inputError]}
          />
          {emailError ? <Text style={styles.errorText}>{emailError}</Text> : null}
        </View>
        
        <View style={styles.formGroup}>
          <Text style={styles.formLabel}>Telefone</Text>
          <Input
            placeholder="(00) 00000-0000"
            keyboardType="phone-pad"
            value={formData.telefone}
            onChangeText={(text) => handleChange('telefone', text)}
            onBlur={() => handleBlur('telefone')}
            style={[styles.inputField, phoneError && styles.inputError]}
          />
          {phoneError ? <Text style={styles.errorText}>{phoneError}</Text> : null}
        </View>
      </View>

      <View style={styles.formFullWidth}>
        <Text style={styles.formLabel}>Data de Nascimento</Text>
        <Input
          placeholder="DD/MM/AAAA"
          value={formData.dataNascimento}
          onChangeText={(text) => handleChange('dataNascimento', text)}
          onBlur={() => handleBlur('dataNascimento')}
          keyboardType="numeric"
          style={[styles.inputField, birthDateError && styles.inputError]}
        />
        {birthDateError ? <Text style={styles.errorText}>{birthDateError}</Text> : null}
      </View>
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
  fullNameErrorContainer: {
    marginBottom: 16,
    paddingHorizontal: 10,
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
  checkboxWrapper: {
    marginVertical: 16,
  },
  inputError: {
    borderColor: '#ff0000',
  },
  errorText: {
    color: '#ff0000',
    fontSize: 12,
    marginTop: 4,
  },
});

export default PersonalForm; 