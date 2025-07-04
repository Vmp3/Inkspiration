import React, { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import Input from '../ui/Input';
import { isMobileView } from '../../utils/responsive';

const AddressForm = ({ 
  formData, 
  handleChange, 
  handleBlur,
  buscarCep,
  cepError,
  estadoError,
  cidadeError,
  bairroError,
  ruaError,
  enderecoValidationError
}) => {
  const [numeroError, setNumeroError] = useState('');
  const isMobile = isMobileView();

  const handleNumeroChange = (text) => {
    if (text !== '' && !/^\d+$/.test(text)) {
      setNumeroError('Apenas números são permitidos');
      return;
    }
    
    setNumeroError('');
    handleChange('numero', text);
  };

  return (
    <View style={styles.tabContent}>   
      {isMobile ? (
        <>
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>CEP</Text>
            <Input
              placeholder="00000-000"
              value={formData.cep}
              onChangeText={(text) => handleChange('cep', text)}
              keyboardType="numeric"
              style={[styles.inputField, cepError ? styles.inputError : null]}
              maxLength={9}
            />
            {cepError ? <Text style={styles.errorText}>{cepError}</Text> : null}
            {!cepError && <Text style={styles.helperText}>Digite o CEP para preenchimento automático</Text>}
          </View>
          
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>Estado</Text>
            <Input
              placeholder="UF"
              value={formData.estado}
              onChangeText={(text) => handleChange('estado', text)}
              onBlur={() => handleBlur && handleBlur('estado')}
              style={[styles.inputField, estadoError ? styles.inputError : null]}
              maxLength={2}
            />
            {estadoError ? <Text style={styles.errorText}>{estadoError}</Text> : null}
          </View>
        </>
      ) : (
        <View style={styles.formRow}>
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>CEP</Text>
            <Input
              placeholder="00000-000"
              value={formData.cep}
              onChangeText={(text) => handleChange('cep', text)}
              keyboardType="numeric"
              style={[styles.inputField, cepError ? styles.inputError : null]}
              maxLength={9}
            />
            {cepError ? <Text style={styles.errorText}>{cepError}</Text> : null}
            {!cepError && <Text style={styles.helperText}>Digite o CEP para preenchimento automático</Text>}
          </View>
          
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>Estado</Text>
            <Input
              placeholder="UF"
              value={formData.estado}
              onChangeText={(text) => handleChange('estado', text)}
              onBlur={() => handleBlur && handleBlur('estado')}
              style={[styles.inputField, estadoError ? styles.inputError : null]}
              maxLength={2}
            />
            {estadoError ? <Text style={styles.errorText}>{estadoError}</Text> : null}
          </View>
        </View>
      )}
      
      <View style={styles.formFullWidth}>
        <Text style={styles.formLabel}>Logradouro</Text>
        <Input
          placeholder="Seu logradouro"
          value={formData.rua}
          onChangeText={(text) => handleChange('rua', text)}
          onBlur={() => handleBlur && handleBlur('rua')}
          style={[styles.inputField, ruaError ? styles.inputError : null]}
        />
        {ruaError ? <Text style={styles.errorText}>{ruaError}</Text> : null}
      </View>
      
      {isMobile ? (
        <>
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>Número</Text>
            <Input
              placeholder="123"
              keyboardType="numeric"
              value={formData.numero}
              onChangeText={handleNumeroChange}
              style={[styles.inputField, numeroError ? styles.inputError : null]}
            />
            {numeroError ? <Text style={styles.errorText}>{numeroError}</Text> : null}
          </View>
          
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>Complemento</Text>
            <Input
              placeholder="Apto, bloco, etc."
              value={formData.complemento}
              onChangeText={(text) => handleChange('complemento', text)}
              style={styles.inputField}
            />
          </View>
        </>
      ) : (
        <View style={styles.formRow}>
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>Número</Text>
            <Input
              placeholder="123"
              keyboardType="numeric"
              value={formData.numero}
              onChangeText={handleNumeroChange}
              style={[styles.inputField, numeroError ? styles.inputError : null]}
            />
            {numeroError ? <Text style={styles.errorText}>{numeroError}</Text> : null}
          </View>
          
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>Complemento</Text>
            <Input
              placeholder="Apto, bloco, etc."
              value={formData.complemento}
              onChangeText={(text) => handleChange('complemento', text)}
              style={styles.inputField}
            />
          </View>
        </View>
      )}
      
      {/* Bairro e Cidade */}
      {isMobile ? (
        <>
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>Bairro</Text>
            <Input
              placeholder="Seu bairro"
              value={formData.bairro}
              onChangeText={(text) => handleChange('bairro', text)}
              onBlur={() => handleBlur && handleBlur('bairro')}
              style={[styles.inputField, bairroError ? styles.inputError : null]}
            />
            {bairroError ? <Text style={styles.errorText}>{bairroError}</Text> : null}
          </View>
          
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>Cidade</Text>
            <Input
              placeholder="Sua cidade"
              value={formData.cidade}
              onChangeText={(text) => handleChange('cidade', text)}
              onBlur={() => handleBlur && handleBlur('cidade')}
              style={[styles.inputField, cidadeError ? styles.inputError : null]}
            />
            {cidadeError ? <Text style={styles.errorText}>{cidadeError}</Text> : null}
          </View>
        </>
      ) : (
        <View style={styles.formRow}>
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>Bairro</Text>
            <Input
              placeholder="Seu bairro"
              value={formData.bairro}
              onChangeText={(text) => handleChange('bairro', text)}
              onBlur={() => handleBlur && handleBlur('bairro')}
              style={[styles.inputField, bairroError ? styles.inputError : null]}
            />
            {bairroError ? <Text style={styles.errorText}>{bairroError}</Text> : null}
          </View>
          
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>Cidade</Text>
            <Input
              placeholder="Sua cidade"
              value={formData.cidade}
              onChangeText={(text) => handleChange('cidade', text)}
              onBlur={() => handleBlur && handleBlur('cidade')}
              style={[styles.inputField, cidadeError ? styles.inputError : null]}
            />
            {cidadeError ? <Text style={styles.errorText}>{cidadeError}</Text> : null}
          </View>
        </View>
      )}
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
  helperText: {
    fontSize: 12,
    color: '#777',
    marginTop: 4,
  },
  inputError: {
    borderColor: '#ff0000',
  },
  errorText: {
    color: '#ff0000',
    fontSize: 12,
    marginTop: 4,
  },
  errorContainer: {
    backgroundColor: '#ffd7d7',
    padding: 10,
    borderRadius: 4,
    marginBottom: 24,
  },
  errorTextGeneral: {
    color: '#ff0000',
    fontSize: 14,
    fontWeight: '500',
  },
});

export default AddressForm; 