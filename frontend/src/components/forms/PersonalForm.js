import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Image } from 'react-native';
import { Feather } from '@expo/vector-icons';
import Input from '../ui/Input';
import { isMobileView } from '../../utils/responsive';

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
  setIsArtist,
  isEditMode = false,
  profileImage,
  pickImage
}) => {
  const isMobile = isMobileView();

  return (
    <View style={styles.tabContent}>
      {/* Seção de Foto de Perfil */}
      {(profileImage || pickImage) && (
        <View style={styles.formFullWidth}>
          <Text style={styles.formLabel}>Foto de Perfil</Text>
          <View style={styles.profileSection}>
            <TouchableOpacity 
              style={styles.profileImageContainer}
              onPress={() => pickImage && pickImage('profile')}
            >
              {profileImage ? (
                <Image 
                  source={{ uri: profileImage.uri }}
                  style={styles.profileImage}
                  resizeMode="cover"
                />
              ) : (
                <View style={styles.profileImagePlaceholder}>
                  <Feather name="user-plus" size={32} color="#999" />
                </View>
              )}
            </TouchableOpacity>
            <View style={styles.profileImageInfo}>
              <Text style={styles.profileImageText}>
                {profileImage ? 'Toque para alterar sua foto' : 'Toque para adicionar sua foto'}
              </Text>
              <Text style={styles.profileImageSubtext}>
                Recomendado: imagem quadrada, máximo 5MB
              </Text>
            </View>
          </View>
        </View>
      )}

      {/* Nome e Sobrenome */}
      {isMobile ? (
        // Layout mobile: um campo por linha
        <>
          <View style={styles.formFullWidth}>
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
          
          <View style={styles.formFullWidth}>
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
        </>
      ) : (
        // Layout web/tablet: dois campos por linha
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
      )}

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
          style={[
            styles.inputField, 
            cpfError && styles.inputError,
            isEditMode && styles.disabledInput
          ]}
          editable={!isEditMode}
        />
        {isEditMode ? (
          <Text style={styles.helperText}>O CPF não pode ser alterado</Text>
        ) : null}
        {cpfError ? (
          <Text style={styles.errorText}>{cpfError}</Text>
        ) : null}
      </View>
      
      {/* Email e Telefone */}
      {isMobile ? (
        // Layout mobile: um campo por linha
        <>
          <View style={styles.formFullWidth}>
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
          
          <View style={styles.formFullWidth}>
            <Text style={styles.formLabel}>Telefone</Text>
            <Input
              placeholder="(00) 00000-0000"
              keyboardType="phone-pad"
              value={formData.telefone}
              onChangeText={(text) => handleChange('telefone', text)}
              onBlur={() => handleBlur('telefone')}
              maxLength={15}
              style={[styles.inputField, phoneError && styles.inputError]}
            />
            {phoneError ? <Text style={styles.errorText}>{phoneError}</Text> : null}
          </View>
        </>
      ) : (
        // Layout web/tablet: dois campos por linha
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
              maxLength={15}
              style={[styles.inputField, phoneError && styles.inputError]}
            />
            {phoneError ? <Text style={styles.errorText}>{phoneError}</Text> : null}
          </View>
        </View>
      )}

      <View style={styles.formFullWidth}>
        <Text style={styles.formLabel}>Data de Nascimento</Text>
        <Input
          placeholder="DD/MM/AAAA"
          value={formData.dataNascimento}
          onChangeText={(text) => handleChange('dataNascimento', text)}
          onBlur={() => handleBlur('dataNascimento')}
          keyboardType="numeric"
          maxLength={10}
          style={[
            styles.inputField, 
            birthDateError && styles.inputError,
            isEditMode && styles.disabledInput
          ]}
          editable={!isEditMode}
        />
        {isEditMode && <Text style={styles.helperText}>A data de nascimento não pode ser alterada</Text>}
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
  disabledInput: {
    backgroundColor: '#f5f5f5',
    color: '#999',
  },
  helperText: {
    fontSize: 12,
    color: '#777',
    marginTop: 4,
  },
  profileSection: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 8,
  },
  profileImageContainer: {
    width: 120,
    height: 120,
    borderRadius: 60,
    borderWidth: 3,
    borderColor: '#E5E7EB',
    borderStyle: 'solid',
    overflow: 'hidden',
    backgroundColor: '#F9FAFB',
    marginRight: 20,
  },
  profileImage: {
    width: '100%',
    height: '100%',
  },
  profileImagePlaceholder: {
    width: '100%',
    height: '100%',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F3F4F6',
  },
  profileImageInfo: {
    flex: 1,
  },
  profileImageText: {
    fontSize: 16,
    color: '#374151',
    fontWeight: '500',
    marginBottom: 4,
  },
  profileImageSubtext: {
    fontSize: 14,
    color: '#6B7280',
    lineHeight: 20,
  },
});

export default PersonalForm; 