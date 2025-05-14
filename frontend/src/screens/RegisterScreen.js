import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, SafeAreaView } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Toast from 'react-native-toast-message';
import axios from 'axios';
import * as formatters from '../utils/formatters';

import TabHeader from '../components/ui/TabHeader';
import PersonalForm from '../components/forms/PersonalForm';
import AddressForm from '../components/forms/AddressForm';
import SecurityForm from '../components/forms/SecurityForm';
import FormNavigation from '../components/ui/FormNavigation';

const RegisterScreen = () => {
  const navigation = useNavigation();
  const [activeTab, setActiveTab] = useState('personal');
  const [isArtist, setIsArtist] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [nomeError, setNomeError] = useState('');
  const [sobrenomeError, setSobrenomeError] = useState('');
  const [fullNameError, setFullNameError] = useState('');
  const [cpfError, setCpfError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [phoneError, setPhoneError] = useState('');
  const [birthDateError, setBirthDateError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  
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
    senha: '',
    confirmarSenha: '',
    termsAccepted: false
  });

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
        formattedValue = formatters.formatCPF(value);
        // Clear error when typing
        setCpfError('');
        break;
      case 'cep':
        formattedValue = formatters.formatCEP(value);
        break;
      case 'telefone':
        formattedValue = formatters.formatPhone(value);
        setPhoneError('');
        break;
      case 'dataNascimento':
        formattedValue = formatters.formatBirthDate(value);
        setBirthDateError('');
        break;
      case 'email':
        setEmailError('');
        break;
      case 'senha':
        setPasswordError('');
        if (formData.confirmarSenha && formData.confirmarSenha !== value) {
          setConfirmPasswordError('As senhas não coincidem');
        } else {
          setConfirmPasswordError('');
        }
        break;
      case 'confirmarSenha':
        if (formData.senha === value) {
          setConfirmPasswordError('');
        } else if (value) {
          setConfirmPasswordError('As senhas não coincidem');
        }
        break;
    }

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
    
    if (field === 'cpf' && formData.cpf) {
      if (!formatters.validateCPF(formData.cpf)) {
        setCpfError('CPF inválido');
      } else {
        setCpfError('');
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

    if (field === 'dataNascimento' && formData.dataNascimento) {
      if (!formatters.validateBirthDate(formData.dataNascimento)) {
        setBirthDateError('Você deve ter pelo menos 18 anos para se registrar');
      } else {
        setBirthDateError('');
      }
    }

    if (field === 'senha') {
      if (!formData.senha) {
        setPasswordError('Senha é obrigatória');
      } else if (formData.senha.length < 6) {
        setPasswordError('A senha deve ter pelo menos 6 caracteres');
      } else {
        setPasswordError('');
      }
    }

    if (field === 'confirmarSenha') {
      if (!formData.confirmarSenha) {
        setConfirmPasswordError('Confirmação de senha é obrigatória');
      } else if (formData.senha !== formData.confirmarSenha) {
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
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Nome é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formatters.validateFirstName(formData.nome)) {
      setNomeError('Nome inválido');
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Nome inválido',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.sobrenome) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Sobrenome é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formatters.validateSurname(formData.sobrenome)) {
      setSobrenomeError('Sobrenome inválido');
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Sobrenome inválido',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formatters.validateFullNameLength(formData.nome, formData.sobrenome)) {
      setFullNameError('Nome e sobrenome não podem ultrapassar 255 caracteres');
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Nome e sobrenome não podem ultrapassar 255 caracteres',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.cpf) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'CPF é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }

    if (!formatters.validateCPF(formData.cpf)) {
      setCpfError('CPF inválido');
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'CPF inválido',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.email) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Email é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }

    if (!formatters.validateEmail(formData.email)) {
      setEmailError('Email inválido');
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Email inválido',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.telefone) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Telefone é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }

    if (!formatters.validatePhone(formData.telefone)) {
      setPhoneError('Telefone inválido');
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Telefone inválido',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.dataNascimento) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Data de nascimento é obrigatória',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }

    if (!formatters.validateBirthDate(formData.dataNascimento)) {
      setBirthDateError('Você deve ter pelo menos 18 anos para se registrar');
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Você deve ter pelo menos 18 anos para se registrar',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    return true;
  };

  const validateAddressTab = () => {
    if (!formData.cep) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'CEP é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.rua) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Rua é obrigatória',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.numero) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Número é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.bairro) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Bairro é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.cidade) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Cidade é obrigatória',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.estado) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Estado é obrigatório',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    return true;
  };

  const validateSecurityTab = () => {
    if (!formData.senha) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Senha é obrigatória',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (formData.senha.length < 6) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'A senha deve ter pelo menos 6 caracteres',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }

    if (!formData.confirmarSenha) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Confirmação de senha é obrigatória',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (formData.senha !== formData.confirmarSenha) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'As senhas não coincidem',
        position: 'top',
        visibilityTime: 3000,
      });
      return false;
    }
    
    if (!formData.termsAccepted) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Você precisa aceitar os termos de uso para continuar',
        position: 'top',
        visibilityTime: 3000,
      });
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
        setActiveTab('security');
      }
    }
  };

  const handlePrevTab = () => {
    if (activeTab === 'security') {
      setActiveTab('address');
    } else if (activeTab === 'address') {
      setActiveTab('personal');
    }
  };

  const validateForm = () => {
    return validateSecurityTab();
  };

  const formatDateToBackend = (dateString) => {
    if (!dateString) return null;
    
    // Remove caracteres não numéricos
    const numbers = dateString.replace(/\D/g, '');
    
    // Garantir que a data esteja no formato DD/MM/YYYY
    if (numbers.length <= 2) return numbers;
    if (numbers.length <= 4) {
      const day = numbers.slice(0, 2);
      const month = numbers.slice(2);
      return `${day}/${month}`;
    }
    
    const day = numbers.slice(0, 2);
    const month = numbers.slice(2, 4);
    const year = numbers.slice(4, 8);
    
    // Validar dia (1-31) e mês (1-12)
    const validDay = Math.min(parseInt(day), 31);
    const validMonth = Math.min(parseInt(month), 12);
    
    return `${validDay.toString().padStart(2, '0')}/${validMonth.toString().padStart(2, '0')}/${year}`;
  };

  const handleRegister = async () => {
    if (!validateForm()) return;

    try {
      setIsLoading(true);
      setErrorMessage('');

      // Formatar data de nascimento para o formato esperado pelo backend (DD/MM/YYYY)
      const dataNascFormatada = formatDateToBackend(formData.dataNascimento);
      
      // Preparar objeto de endereço conforme esperado pelo backend
      const endereco = {
        cep: formData.cep,
        rua: formData.rua,
        numero: formData.numero,
        complemento: formData.complemento || '',
        bairro: formData.bairro,
        cidade: formData.cidade,
        estado: formData.estado
      };

      // Preparar dados para envio conforme o DTO do backend
      const userData = {
        nome: `${formData.nome} ${formData.sobrenome}`.trim(),
        cpf: formData.cpf.replace(/[^\d]/g, ''), // Remove caracteres não numéricos
        email: formData.email,
        dataNascimento: dataNascFormatada,
        telefone: formData.telefone,
        senha: formData.senha,
        endereco: endereco,
        role: 'user' // Registrando como usuário comum
      };

      const baseUrl = 'http://localhost:8080'; 
      const response = await fetch(`${baseUrl}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
      });

      const data = await response.json();

      if (!response.ok) {
        // Verifica se há erros diretos no objeto data
        const errorMessages = {};
        
        // Verifica cada propriedade do objeto data para encontrar mensagens de erro
        Object.entries(data).forEach(([field, message]) => {
          if (typeof message === 'string') {
            errorMessages[field] = message;
          }
        });

        // Se encontrou mensagens de erro
        if (Object.keys(errorMessages).length > 0) {
          Object.entries(errorMessages).forEach(([field, message]) => {
            Toast.show({
              type: 'error',
              text1: 'Erro de Validação',
              text2: message,
              position: 'top',
              visibilityTime: 4000,
            });
          });
        } else if (data.message) {
          // Se houver uma mensagem de erro geral
          Toast.show({
            type: 'error',
            text1: 'Erro',
            text2: data.message,
            position: 'top',
            visibilityTime: 4000,
          });
        } else {
          // Mensagem genérica de erro
          Toast.show({
            type: 'error',
            text1: 'Erro',
            text2: 'Ocorreu um erro ao cadastrar. Tente novamente.',
            position: 'top',
            visibilityTime: 4000,
          });
        }
        return;
      }

      // Exibe mensagem de sucesso do backend ou uma mensagem padrão
      Toast.show({
        type: 'success',
        text1: 'Sucesso',
        text2: data.message || 'Cadastro realizado com sucesso!',
        position: 'top',
        visibilityTime: 4000,
      });

      // Aguarda um momento para mostrar o toast antes de navegar
      setTimeout(() => {
        navigation.navigate('Login');
      }, 1000);
    } catch (error) {
      Toast.show({
        type: 'error',
        text1: 'Erro',
        text2: 'Ocorreu um erro ao cadastrar. Tente novamente.',
        position: 'top',
        visibilityTime: 4000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const tabs = [
    { id: 'personal', label: 'Dados Pessoais' },
    { id: 'address', label: 'Endereço' },
    { id: 'security', label: 'Segurança' }
  ];

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContainer}>
        <View style={styles.contentContainer}>
          <View style={styles.pageHeaderContainer}>
            <Text style={styles.pageTitle}>Criar Conta</Text>
            <Text style={styles.pageSubtitle}>Registre-se para encontrar os melhores tatuadores</Text>
          </View>
          
          <View style={styles.cardWrapper}>
            <View style={styles.formCard}>
              <View style={styles.tabHeaderWrapper}>
                <TabHeader 
                  tabs={tabs}
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
                      cpfError={cpfError}
                      emailError={emailError}
                      phoneError={phoneError}
                      birthDateError={birthDateError}
                      isArtist={isArtist}
                      setIsArtist={setIsArtist}
                      nomeError={nomeError}
                      sobrenomeError={sobrenomeError}
                      fullNameError={fullNameError}
                    />
                    <FormNavigation
                      onNext={handleNextTab}
                      showPrev={false}
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

                {activeTab === 'security' && (
                  <SecurityForm
                    formData={formData}
                    handleChange={handleChange}
                    handleBlur={handleBlur}
                    handleRegister={handleRegister}
                    handlePrevTab={handlePrevTab}
                    isLoading={isLoading}
                    passwordError={passwordError}
                    confirmPasswordError={confirmPasswordError}
                  />
                )}
              </View>
            </View>
          </View>
          
          <View style={styles.loginPrompt}>
            <Text style={styles.loginPromptText}>
              Já tem uma conta?{' '}
              <Text 
                style={styles.loginLink}
                onPress={() => navigation.navigate('Login')}
              >
                Entrar
              </Text>
            </Text>
          </View>
        </View>
      </ScrollView>
      <Toast />
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
  loginPrompt: {
    alignItems: 'center',
    marginTop: 16,
  },
  loginPromptText: {
    fontSize: 14,
    color: '#666',
  },
  loginLink: {
    color: '#000',
    fontWeight: '500',
    textDecorationLine: 'underline',
  },
});

export default RegisterScreen;
