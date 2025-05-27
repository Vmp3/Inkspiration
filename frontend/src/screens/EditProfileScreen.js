import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, SafeAreaView } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import * as formatters from '../utils/formatters';
import toastHelper from '../utils/toastHelper';
import { useAuth } from '../context/AuthContext';

import Input from '../components/ui/Input';
import TabHeader from '../components/ui/TabHeader';
import PersonalForm from '../components/forms/PersonalForm';
import AddressForm from '../components/forms/AddressForm';
import SecurityForm from '../components/forms/SecurityForm';
import FormNavigation from '../components/ui/FormNavigation';
import ProfessionalForm from '../components/forms/ProfessionalForm';

const EditProfileScreen = () => {
  const navigation = useNavigation();
  const { userData, updateUserData } = useAuth();
  const [activeTab, setActiveTab] = useState('personal');
  const [isArtist, setIsArtist] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  
  // Form validation states
  const [nomeError, setNomeError] = useState('');
  const [sobrenomeError, setSobrenomeError] = useState('');
  const [fullNameError, setFullNameError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [phoneError, setPhoneError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  
  // Form data state
  const [formData, setFormData] = useState({
    // Dados pessoais
    nome: '',
    sobrenome: '',
    cpf: '',
    email: '',
    telefone: '',
    dataNascimento: '',
    
    // Endereço
    cep: '',
    rua: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    estado: '',
    
    // Senha
    senhaAtual: '',
    novaSenha: '',
    confirmarSenha: '',
    
    // Dados profissionais (quando aplicável)
    especialidades: [],
    bio: '',
    experiencia: '',
    redesSociais: {
      instagram: '',
      tiktok: '',
      facebook: '',
      twitter: '',
      website: ''
    }
  });

  // Load user data when component mounts
  useEffect(() => {
    if (userData) {
      // Check if user is an artist/professional
      setIsArtist(userData.role === 'ROLE_PROFESSIONAL');
      
      // Extract first and last name
      const nameParts = userData.nome ? userData.nome.split(' ') : ['', ''];
      const firstName = nameParts[0] || '';
      const lastName = nameParts.slice(1).join(' ') || '';
      
      // Set form data with user information
      setFormData({
        nome: firstName,
        sobrenome: lastName,
        cpf: userData.cpf || '',
        email: userData.email || '',
        telefone: userData.telefone || '',
        dataNascimento: userData.dataNascimento || '',
        
        // Endereço
        cep: userData.endereco?.cep || '',
        rua: userData.endereco?.rua || '',
        numero: userData.endereco?.numero || '',
        complemento: userData.endereco?.complemento || '',
        bairro: userData.endereco?.bairro || '',
        cidade: userData.endereco?.cidade || '',
        estado: userData.endereco?.estado || '',
        
        // Senha (deixar em branco por segurança)
        senhaAtual: '',
        novaSenha: '',
        confirmarSenha: '',
        
        // Dados profissionais (quando aplicável)
        especialidades: userData.especialidades || [],
        bio: userData.bio || '',
        experiencia: userData.experiencia || '',
        redesSociais: {
          instagram: userData.redesSociais?.instagram || '',
          tiktok: userData.redesSociais?.tiktok || '',
          facebook: userData.redesSociais?.facebook || '',
          twitter: userData.redesSociais?.twitter || '',
          website: userData.redesSociais?.website || ''
        }
      });
    }
  }, [userData]);

  const handleChange = (field, value) => {
    let formattedValue = value;
    
    // Apply appropriate formatter based on field type
    switch (field) {
      case 'nome':
        setNomeError('');
        if (formData.sobrenome) {
          if (!formatters.validateFullNameLength(value, formData.sobrenome)) {
            setFullNameError('Nome e sobrenome não podem ultrapassar 255 caracteres');
          } else {
            setFullNameError('');
          }
        }
        break;
      case 'sobrenome':
        setSobrenomeError('');
        if (formData.nome) {
          if (!formatters.validateFullNameLength(formData.nome, value)) {
            setFullNameError('Nome e sobrenome não podem ultrapassar 255 caracteres');
          } else {
            setFullNameError('');
          }
        }
        break;
      case 'cpf':
        // CPF is read-only in edit mode
        return;
      case 'cep':
        formattedValue = formatters.formatCEP(value);
        break;
      case 'telefone':
        formattedValue = formatters.formatPhone(value);
        setPhoneError('');
        break;
      case 'email':
        setEmailError('');
        break;
      case 'senhaAtual':
      case 'novaSenha':
        setPasswordError('');
        if (formData.confirmarSenha && formData.confirmarSenha !== value && field === 'novaSenha') {
          setConfirmPasswordError('As senhas não coincidem');
        } else {
          setConfirmPasswordError('');
        }
        break;
      case 'confirmarSenha':
        if (formData.novaSenha === value) {
          setConfirmPasswordError('');
        } else if (value) {
          setConfirmPasswordError('As senhas não coincidem');
        }
        break;
      case 'redesSociais':
        // For nested objects
        return setFormData(prev => ({
          ...prev,
          redesSociais: {
            ...prev.redesSociais,
            ...value
          }
        }));
      case 'especialidades':
        // Toggle specialty in array
        const specialties = [...formData.especialidades];
        if (specialties.includes(value)) {
          return setFormData(prev => ({
            ...prev,
            especialidades: specialties.filter(item => item !== value)
          }));
        } else {
          return setFormData(prev => ({
            ...prev,
            especialidades: [...specialties, value]
          }));
        }
    }

    // Default update for standard fields
    setFormData({
      ...formData,
      [field]: formattedValue,
    });

    // If the field is CEP and has 8 digits (without hyphen), fetch address
    if (field === 'cep' && value.replace(/\D/g, '').length === 8) {
      buscarCep(value);
    }
  };

  const handleBlur = (field) => {
    if (field === 'nome' && formData.nome) {
      if (!formatters.validateFirstName(formData.nome)) {
        setNomeError('Nome inválido');
      } else {
        setNomeError('');
      }
    }
    
    if (field === 'sobrenome' && formData.sobrenome) {
      if (!formatters.validateSurname(formData.sobrenome)) {
        setSobrenomeError('Sobrenome inválido');
      } else {
        setSobrenomeError('');
      }
    }
    
    if ((field === 'nome' || field === 'sobrenome') && formData.nome && formData.sobrenome) {
      if (!formatters.validateFullNameLength(formData.nome, formData.sobrenome)) {
        setFullNameError('Nome e sobrenome não podem ultrapassar 255 caracteres');
      } else {
        setFullNameError('');
      }
    }
    
    if (field === 'email' && formData.email) {
      if (!formatters.validateEmail(formData.email)) {
        setEmailError('Email inválido');
      } else {
        setEmailError('');
      }
    }

    if (field === 'telefone' && formData.telefone) {
      if (!formatters.validatePhone(formData.telefone)) {
        setPhoneError('Telefone inválido');
      } else {
        setPhoneError('');
      }
    }

    if (field === 'senhaAtual' && !formData.senhaAtual && (formData.novaSenha || formData.confirmarSenha)) {
      setPasswordError('Senha atual é obrigatória para alterar a senha');
    }

    if (field === 'novaSenha') {
      if (formData.novaSenha && formData.novaSenha.length < 6) {
        setPasswordError('A senha deve ter pelo menos 6 caracteres');
      } else {
        setPasswordError('');
      }
    }

    if (field === 'confirmarSenha') {
      if (formData.novaSenha && !formData.confirmarSenha) {
        setConfirmPasswordError('Confirmação de senha é obrigatória');
      } else if (formData.novaSenha && formData.novaSenha !== formData.confirmarSenha) {
        setConfirmPasswordError('As senhas não coincidem');
      } else {
        setConfirmPasswordError('');
      }
    }
  };

  const buscarCep = async (cep) => {
    try {
      // Remove caracteres não numéricos
      const cepLimpo = cep.replace(/\D/g, '');
      
      // URL da API ViaCEP
      const response = await axios.get(`https://viacep.com.br/ws/${cepLimpo}/json/`);
      
      if (response.data && !response.data.erro) {
        const endereco = response.data;
        
        // Atualiza os campos do formulário com os dados retornados
        setFormData(prev => ({
          ...prev,
          rua: endereco.logradouro || '',
          bairro: endereco.bairro || '',
          cidade: endereco.localidade || '',
          estado: endereco.uf || '',
        }));
      } else {
        console.log('CEP não encontrado');
      }
    } catch (error) {
      console.error('Erro ao buscar CEP:', error);
    }
  };

  const validatePersonalTab = () => {
    let isValid = true;

    if (!formData.nome) {
      toastHelper.showError('Nome é obrigatório');
      return false;
    }
    
    if (!formatters.validateFirstName(formData.nome)) {
      setNomeError('Nome inválido');
      toastHelper.showError('Nome inválido');
      return false;
    }
    
    if (!formData.sobrenome) {
      toastHelper.showError('Sobrenome é obrigatório');
      return false;
    }
    
    if (!formatters.validateSurname(formData.sobrenome)) {
      setSobrenomeError('Sobrenome inválido');
      toastHelper.showError('Sobrenome inválido');
      return false;
    }
    
    if (!formatters.validateFullNameLength(formData.nome, formData.sobrenome)) {
      setFullNameError('Nome e sobrenome não podem ultrapassar 255 caracteres');
      toastHelper.showError('Nome e sobrenome não podem ultrapassar 255 caracteres');
      return false;
    }
    
    if (!formData.email) {
      toastHelper.showError('Email é obrigatório');
      return false;
    }

    if (!formatters.validateEmail(formData.email)) {
      setEmailError('Email inválido');
      toastHelper.showError('Email inválido');
      return false;
    }
    
    if (!formData.telefone) {
      toastHelper.showError('Telefone é obrigatório');
      return false;
    }

    if (!formatters.validatePhone(formData.telefone)) {
      setPhoneError('Telefone inválido');
      toastHelper.showError('Telefone inválido');
      return false;
    }
    
    return true;
  };

  const validateAddressTab = () => {
    if (!formData.cep) {
      toastHelper.showError('CEP é obrigatório');
      return false;
    }
    
    if (!formData.rua) {
      toastHelper.showError('Rua é obrigatória');
      return false;
    }
    
    if (!formData.numero) {
      toastHelper.showError('Número é obrigatório');
      return false;
    }
    
    if (!formData.bairro) {
      toastHelper.showError('Bairro é obrigatório');
      return false;
    }
    
    if (!formData.cidade) {
      toastHelper.showError('Cidade é obrigatória');
      return false;
    }
    
    if (!formData.estado) {
      toastHelper.showError('Estado é obrigatório');
      return false;
    }
    
    return true;
  };

  const validateSecurityTab = () => {
    // Se não está alterando a senha, retorna true
    if (!formData.senhaAtual && !formData.novaSenha && !formData.confirmarSenha) {
      return true;
    }
    
    // Se está alterando a senha, valida os campos
    if (!formData.senhaAtual) {
      toastHelper.showError('Senha atual é obrigatória');
      return false;
    }
    
    if (!formData.novaSenha) {
      toastHelper.showError('Nova senha é obrigatória');
      return false;
    }
    
    if (formData.novaSenha.length < 6) {
      toastHelper.showError('A senha deve ter pelo menos 6 caracteres');
      return false;
    }

    if (!formData.confirmarSenha) {
      toastHelper.showError('Confirmação de senha é obrigatória');
      return false;
    }
    
    if (formData.novaSenha !== formData.confirmarSenha) {
      toastHelper.showError('As senhas não coincidem');
      return false;
    }
    
    return true;
  };

  const validateProfessionalTab = () => {
    // Validação mínima para profissionais
    if (isArtist && !formData.bio) {
      toastHelper.showError('Bio é obrigatória para profissionais');
      return false;
    }
    
    if (isArtist && formData.especialidades.length === 0) {
      toastHelper.showError('Selecione pelo menos uma especialidade');
      return false;
    }
    
    return true;
  };

  const handleNextTab = () => {
    if (activeTab === 'personal') {
      if (validatePersonalTab()) {
        setActiveTab('address');
      }
    } else if (activeTab === 'address') {
      if (validateAddressTab()) {
        if (isArtist) {
          setActiveTab('professional');
        } else {
          setActiveTab('security');
        }
      }
    } else if (activeTab === 'professional') {
      if (validateProfessionalTab()) {
        setActiveTab('security');
      }
    }
  };

  const handlePrevTab = () => {
    if (activeTab === 'security') {
      if (isArtist) {
        setActiveTab('professional');
      } else {
        setActiveTab('address');
      }
    } else if (activeTab === 'professional') {
      setActiveTab('address');
    } else if (activeTab === 'address') {
      setActiveTab('personal');
    }
  };

  const handleUpdateProfile = async () => {
    // Validate current tab
    if (activeTab === 'personal' && !validatePersonalTab()) return;
    if (activeTab === 'address' && !validateAddressTab()) return;
    if (activeTab === 'security' && !validateSecurityTab()) return;
    if (activeTab === 'professional' && !validateProfessionalTab()) return;

    try {
      setIsLoading(true);
      setErrorMessage('');

      // Preparar objeto de endereço
      const endereco = {
        cep: formData.cep,
        rua: formData.rua,
        numero: formData.numero,
        complemento: formData.complemento || '',
        bairro: formData.bairro,
        cidade: formData.cidade,
        estado: formData.estado
      };

      // Preparar dados para envio
      const userData = {
        nome: `${formData.nome} ${formData.sobrenome}`.trim(),
        email: formData.email,
        telefone: formData.telefone,
        endereco: endereco
      };

      // Adicionar dados profissionais se for artista
      if (isArtist) {
        userData.especialidades = formData.especialidades;
        userData.bio = formData.bio;
        userData.experiencia = formData.experiencia;
        userData.redesSociais = formData.redesSociais;
      }

      // Adicionar senha se estiver atualizando
      if (formData.senhaAtual && formData.novaSenha) {
        userData.senhaAtual = formData.senhaAtual;
        userData.novaSenha = formData.novaSenha;
      }

      const baseUrl = 'http://localhost:8080';
      const response = await fetch(`${baseUrl}/api/users/profile`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${await AsyncStorage.getItem('token')}`
        },
        body: JSON.stringify(userData)
      });

      const data = await response.json();

      if (!response.ok) {
        // Handle error messages
        if (data.message) {
          toastHelper.showError(data.message);
        } else {
          toastHelper.showError('Ocorreu um erro ao atualizar o perfil. Tente novamente.');
        }
        return;
      }

      // Update user data in context
      await updateUserData();
      
      // Show success message
      toastHelper.showSuccess('Perfil atualizado com sucesso!');
    } catch (error) {
      console.error('Erro ao atualizar perfil:', error);
      toastHelper.showError('Ocorreu um erro ao atualizar o perfil. Tente novamente.');
    } finally {
      setIsLoading(false);
    }
  };

  // Define tabs based on user role
  const getTabs = () => {
    const tabs = [
      { id: 'personal', label: 'Dados Pessoais' },
      { id: 'address', label: 'Endereço' }
    ];

    if (isArtist) {
      tabs.push({ id: 'professional', label: 'Profissional' });
    }

    tabs.push({ id: 'security', label: 'Segurança' });

    return tabs;
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContainer}>
        <View style={styles.contentContainer}>
          <View style={styles.pageHeaderContainer}>
            <Text style={styles.pageTitle}>Meu Perfil</Text>
            <Text style={styles.pageSubtitle}>Gerencie suas informações pessoais</Text>
          </View>
          
          <View style={styles.cardWrapper}>
            <View style={styles.formCard}>
              <View style={styles.tabHeaderWrapper}>
                <TabHeader 
                  tabs={getTabs()}
                  activeTab={activeTab}
                  setActiveTab={setActiveTab}
                />
              </View>
              
              <View style={styles.formContainer}>
                {activeTab === 'personal' && (
                  <>
                    <PersonalForm
                      formData={formData}
                      handleChange={handleChange}
                      handleBlur={handleBlur}
                      emailError={emailError}
                      phoneError={phoneError}
                      isArtist={isArtist}
                      setIsArtist={setIsArtist}
                      nomeError={nomeError}
                      sobrenomeError={sobrenomeError}
                      fullNameError={fullNameError}
                      isEditMode={true}
                    />
                    <FormNavigation
                      onNext={handleNextTab}
                      showPrev={false}
                      nextText="Próximo"
                    />
                  </>
                )}

                {activeTab === 'address' && (
                  <>
                    <AddressForm
                      formData={formData}
                      handleChange={handleChange}
                      buscarCep={buscarCep}
                    />
                    <FormNavigation
                      onPrev={handlePrevTab}
                      onNext={handleNextTab}
                    />
                  </>
                )}

                {isArtist && activeTab === 'professional' && (
                  <>
                    <ProfessionalForm 
                      formData={formData}
                      handleChange={handleChange}
                    />
                    <FormNavigation
                      onPrev={handlePrevTab}
                      onNext={handleNextTab}
                    />
                  </>
                )}

                {activeTab === 'security' && (
                  <>
                    <View style={styles.tabContent}>
                      <View style={styles.formRow}>
                        <View style={styles.formGroup}>
                          <Text style={styles.formLabel}>Senha Atual</Text>
                          <Input
                            placeholder="••••••••"
                            secureTextEntry
                            value={formData.senhaAtual}
                            onChangeText={(text) => handleChange('senhaAtual', text)}
                            onBlur={() => handleBlur('senhaAtual')}
                            style={[
                              styles.inputField,
                              passwordError && styles.inputError
                            ]}
                          />
                          {passwordError ? <Text style={styles.errorText}>{passwordError}</Text> : null}
                        </View>
                      </View>

                      <View style={styles.formRow}>
                        <View style={styles.formGroup}>
                          <Text style={styles.formLabel}>Nova Senha</Text>
                          <Input
                            placeholder="••••••••"
                            secureTextEntry
                            value={formData.novaSenha}
                            onChangeText={(text) => handleChange('novaSenha', text)}
                            onBlur={() => handleBlur('novaSenha')}
                            style={styles.inputField}
                          />
                        </View>
                        
                        <View style={styles.formGroup}>
                          <Text style={styles.formLabel}>Confirmar Nova Senha</Text>
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

                      <Text style={styles.securityNote}>
                        Deixe os campos em branco se não deseja alterar sua senha.
                      </Text>
                    </View>

                    <FormNavigation
                      onPrev={handlePrevTab}
                      onNext={handleUpdateProfile}
                      showNext={true}
                      nextText="Salvar Alterações"
                      isLoading={isLoading}
                    />
                  </>
                )}
              </View>
            </View>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollContainer: {
    flexGrow: 1,
    paddingHorizontal: 16,
  },
  contentContainer: {
    width: '100%',
    maxWidth: 900,
    alignSelf: 'center',
    marginTop: 40,
  },
  pageHeaderContainer: {
    marginBottom: 20,
    alignItems: 'center',
    zIndex: 2,
    marginTop: 15,
  },
  pageTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111',
    marginBottom: 8,
    textAlign: 'center',
  },
  pageSubtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  },
  cardWrapper: {
    marginTop: 20,
    paddingHorizontal: 16,
    paddingVertical: 20,
    zIndex: 1,
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
  },
  formCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: '#eaeaea',
    marginBottom: 30,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    position: 'relative',
    padding: 15,
  },
  tabHeaderWrapper: {
    marginBottom: 5,
    borderRadius: 8,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: '#eaeaea',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
    elevation: 1,
    backgroundColor: '#f8f8f8',
    width: '100%',
  },
  formContainer: {
    padding: 30,
  },
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
  securityNote: {
    fontSize: 12,
    color: '#666',
    marginTop: 8,
    fontStyle: 'italic',
  }
});

export default EditProfileScreen; 