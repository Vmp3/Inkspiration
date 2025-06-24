import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, ScrollView, SafeAreaView, Modal, TextInput, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import * as ImagePicker from 'expo-image-picker';
import * as formatters from '../utils/formatters';
import toastHelper from '../utils/toastHelper';
import { useAuth } from '../context/AuthContext';
import { isMobileView, isDesktopView } from '../utils/responsive';

import TabHeader from '../components/ui/TabHeader';
import PersonalForm from '../components/forms/PersonalForm';
import AddressForm from '../components/forms/AddressForm';
import SecurityForm from '../components/forms/SecurityForm';
import FormNavigation from '../components/ui/FormNavigation';
import PublicAuthService from '../services/PublicAuthService';
import { authMessages } from '../components/auth/messages';
import { useEmailTimeout, EMAIL_TIMEOUT_CONFIG } from '../components/ui/EmailTimeout';

const RegisterScreen = () => {
  const navigation = useNavigation();
  const { isAuthenticated, loading: authLoading } = useAuth();
  
  useEffect(() => {
    if (!authLoading && isAuthenticated) {
      navigation.replace('Home');
    }
  }, [isAuthenticated, authLoading, navigation]);

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
  const [showVerificationModal, setShowVerificationModal] = useState(false);
  const [verificationCode, setVerificationCode] = useState('');
  const [verificationEmail, setVerificationEmail] = useState('');
  const [isVerifyingEmail, setIsVerifyingEmail] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);
  const [cepError, setCepError] = useState('');
  const [estadoError, setEstadoError] = useState('');
  const [cidadeError, setCidadeError] = useState('');
  const [bairroError, setBairroError] = useState('');
  const [enderecoValidationError, setEnderecoValidationError] = useState('');
  const [dadosCep, setDadosCep] = useState(null);
  const [profileImage, setProfileImage] = useState(null);

  const emailTimeout = useEmailTimeout(EMAIL_TIMEOUT_CONFIG.DEFAULT_TIMEOUT);
  const resendTimeout = useEmailTimeout(EMAIL_TIMEOUT_CONFIG.RESEND_TIMEOUT);

  // Effect para controlar o countdown do reenvio
  useEffect(() => {
    let interval;
    if (resendCooldown > 0) {
      interval = setInterval(() => {
        setResendCooldown(prev => prev - 1);
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [resendCooldown]);

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

  // Effect para validar consistência de endereço automaticamente
  useEffect(() => {
    if (dadosCep && formData.estado && formData.cidade) {
      // Validar estado
      if (formData.estado.toUpperCase().trim() !== dadosCep.uf?.toUpperCase().trim()) {
        const errorMsg = `Estado deve ser ${dadosCep.uf} para este CEP`;
        setEstadoError(errorMsg);
      } else {
        setEstadoError('');
      }
      
      // Validar cidade
      if (formData.cidade.toLowerCase().trim() !== dadosCep.localidade?.toLowerCase().trim()) {
        const errorMsg = `Cidade deve ser ${dadosCep.localidade} para este CEP`;
        setCidadeError(errorMsg);
      } else {
        setCidadeError('');
      }
      
      // Validar bairro
      if (formData.bairro.toLowerCase().trim() !== dadosCep.bairro?.toLowerCase().trim()) {
        const errorMsg = `Bairro deve ser ${dadosCep.bairro} para este CEP`;
        setBairroError(errorMsg);
      } else {
        setBairroError('');
      }
    }
  }, [dadosCep, formData.estado, formData.cidade, formData.bairro]);

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
        setCpfError('');
        break;
      case 'cep':
        formattedValue = formatters.formatCEP(value);
        setCepError('');
        setEnderecoValidationError('');
        break;
      case 'telefone':
        formattedValue = formatters.formatPhone(value);
        setPhoneError('');
        // Validar telefone quando completo (11 dígitos)
        if (value.replace(/\D/g, '').length >= 10) {
          const errorMessage = formatters.getPhoneValidationMessage(formatters.formatPhone(value));
          if (errorMessage) {
            setPhoneError(errorMessage);
          }
        }
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
      case 'estado':
        setEstadoError('');
        setEnderecoValidationError('');
        break;
      case 'cidade':
        setCidadeError('');
        setEnderecoValidationError('');
        break;
      case 'bairro':
        setBairroError('');
        setEnderecoValidationError('');
        break;
      case 'rua':
      case 'numero':
      case 'complemento':
        setEnderecoValidationError('');
        break;
    }

    setFormData({
      ...formData,
      [field]: formattedValue,
    });

    if (field === 'cep' && value.replace(/\D/g, '').length === 8) {
      buscarCep(value);
    }
  };

  const handleBlur = (field) => {
    if (field === 'nome' && formData.nome && !formatters.validateFirstName(formData.nome)) {
      setNomeError(authMessages.registerErrors.invalidName);
    }

    if (field === 'sobrenome' && formData.sobrenome && !formatters.validateSurname(formData.sobrenome)) {
      setSobrenomeError(authMessages.registerErrors.invalidName);
    }

    if (field === 'cpf' && formData.cpf && !formatters.validateCPF(formData.cpf)) {
      setCpfError(authMessages.registerErrors.invalidCPF);
    }

    if (field === 'email' && formData.email && !formatters.validateEmail(formData.email)) {
      setEmailError(authMessages.registerErrors.invalidEmail);
    }

    if (field === 'telefone' && formData.telefone) {
      const errorMessage = formatters.getPhoneValidationMessage(formData.telefone);
      if (errorMessage) {
        setPhoneError(errorMessage);
      } else {
        setPhoneError('');
      }
    }

    if (field === 'dataNascimento' && formData.dataNascimento && !formatters.validateBirthDate(formData.dataNascimento)) {
      setBirthDateError(authMessages.registerErrors.invalidBirthDate);
    }

    if (field === 'senha') {
      if (formData.senha) {
        if (!formatters.validatePassword(formData.senha)) {
          setPasswordError('A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial');
        } else {
          setPasswordError('');
        }
      }
    }

    if (field === 'confirmarSenha') {
      if (formData.confirmarSenha) {
        if (formData.senha !== formData.confirmarSenha) {
          setConfirmPasswordError('As senhas não coincidem');
        } else {
          setConfirmPasswordError('');
        }
      }
    }

    // Validação de consistência de endereço quando sai do campo estado ou cidade
    if (field === 'estado' && formData.estado && dadosCep) {
      if (formData.estado.toUpperCase().trim() !== dadosCep.uf?.toUpperCase().trim()) {
        const errorMsg = `Estado deve ser ${dadosCep.uf} para este CEP`;
        setEstadoError(errorMsg);
      } else {
        setEstadoError('');
      }
    }

    if (field === 'cidade' && formData.cidade && dadosCep) {
      if (formData.cidade.toLowerCase().trim() !== dadosCep.localidade?.toLowerCase().trim()) {
        const errorMsg = `Cidade deve ser ${dadosCep.localidade} para este CEP`;
        setCidadeError(errorMsg);
      } else {
        setCidadeError('');
      }
    }

    if (field === 'bairro' && formData.bairro && dadosCep) {
      if (formData.bairro.toLowerCase().trim() !== dadosCep.bairro?.toLowerCase().trim()) {
        const errorMsg = `Bairro deve ser ${dadosCep.bairro} para este CEP`;
        setBairroError(errorMsg);
      } else {
        setBairroError('');
      }
    }
  };

  const pickImage = async () => {
    try {
      const result = await ImagePicker.launchImageLibraryAsync({
        mediaTypes: ImagePicker.MediaTypeOptions.Images,
        allowsEditing: true,
        aspect: [1, 1],
        quality: 0.8,
        base64: true,
      });

      if (!result.canceled) {
        const selectedImage = result.assets[0];
        
        const validMimeTypes = ['image/jpeg', 'image/png'];
        const validExtensions = ['.png', '.jpg', '.jpeg', '.jfif'];
        
        // Verificar MIME type
        if (!selectedImage.mimeType || !validMimeTypes.includes(selectedImage.mimeType)) {
          toastHelper.showError(authMessages.imageUploadErrors.invalidFormat);
          return;
        }
        
        // Verificar extensão do arquivo
        if (selectedImage.fileName) {
          const fileExtension = selectedImage.fileName.toLowerCase().slice(selectedImage.fileName.lastIndexOf('.'));
          if (!validExtensions.includes(fileExtension)) {
            toastHelper.showError(authMessages.imageUploadErrors.invalidFormat);
            return;
          }
        }
        
        // Validação de tamanho - limite de 5MB
        const maxSizeInMB = 5;
        const maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        
        if (selectedImage.fileSize && selectedImage.fileSize > maxSizeInBytes) {
          toastHelper.showError(authMessages.imageUploadErrors.fileTooLarge);
          return;
        }
        
        // Validação adicional do base64 (que é ~33% maior que o arquivo original)
        const base64String = selectedImage.base64;
        const base64SizeInBytes = (base64String.length * 3) / 4;
        
        if (base64SizeInBytes > maxSizeInBytes) {
          toastHelper.showError(authMessages.imageUploadErrors.processedImageTooLarge);
          return;
        }
        
        // Determinar formato correto baseado na extensão do arquivo
        const imageFormat = selectedImage.mimeType === 'image/png' ? 'png' : 'jpeg';
        const mimeType = selectedImage.mimeType === 'image/png' ? 'image/png' : 'image/jpeg';
        
        setProfileImage({
          uri: selectedImage.uri,
          base64: `data:${mimeType};base64,${selectedImage.base64}`,
          type: mimeType,
          name: `profile.${imageFormat === 'png' ? 'png' : 'jpg'}`
        });
      }
    } catch (error) {
      toastHelper.showError(authMessages.imageUploadErrors.selectionFailed);
    }
  };

  const buscarCep = async (cep) => {
    try {
      // Remove caracteres não numéricos
      const cepLimpo = cep.replace(/\D/g, '');
      
      if (cepLimpo.length !== 8) {
        setCepError('CEP deve conter exatamente 8 dígitos');
        setDadosCep(null);
        return;
      }
      
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
        setDadosCep(endereco);
        setCepError('');
        
        // Limpar erros de validação quando busca novo CEP
        setEstadoError('');
        setCidadeError('');
        setBairroError('');
        setEnderecoValidationError('');
      } else {
        setCepError('CEP não encontrado');
        setDadosCep(null);
      }
    } catch (error) {
      // console.error('Erro ao buscar CEP:', error);
      setCepError('Erro ao consultar CEP. Verifique sua conexão.');
      setDadosCep(null);
    }
  };

  const isPersonalTabValid = () => {
    return (
      formData.nome &&
      formatters.validateFirstName(formData.nome) &&
      formData.sobrenome &&
      formatters.validateSurname(formData.sobrenome) &&
      formatters.validateFullNameLength(formData.nome, formData.sobrenome) &&
      formData.cpf &&
      formatters.validateCPF(formData.cpf) &&
      formData.email &&
      formatters.validateEmail(formData.email) &&
      formData.telefone &&
      formatters.validatePhone(formData.telefone) &&
      formData.dataNascimento &&
      formatters.validateBirthDate(formData.dataNascimento) &&
      !nomeError &&
      !sobrenomeError &&
      !fullNameError &&
      !cpfError &&
      !emailError &&
      !phoneError &&
      !birthDateError
    );
  };

  const validatePersonalTab = () => {
    let isValid = true;

    if (!formData.nome) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    if (!formatters.validateFirstName(formData.nome)) {
      setNomeError(authMessages.registerErrors.invalidName);
      toastHelper.showError(authMessages.registerErrors.invalidName);
      return false;
    }
    
    if (!formData.sobrenome) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    if (!formatters.validateSurname(formData.sobrenome)) {
      setSobrenomeError(authMessages.registerErrors.invalidSurname);
      toastHelper.showError(authMessages.registerErrors.invalidSurname);
      return false;
    }
    
    if (!formatters.validateFullNameLength(formData.nome, formData.sobrenome)) {
      setFullNameError(authMessages.registerErrors.invalidFullName);
      toastHelper.showError(authMessages.registerErrors.invalidFullName);
      return false;
    }
    
    if (!formData.cpf) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }

    if (!formatters.validateCPF(formData.cpf)) {
      setCpfError(authMessages.registerErrors.invalidCpf);
      toastHelper.showError(authMessages.registerErrors.invalidCpf);
      return false;
    }
    
    if (!formData.email) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }

    if (!formatters.validateEmail(formData.email)) {
      setEmailError(authMessages.registerErrors.invalidEmail);
      toastHelper.showError(authMessages.registerErrors.invalidEmail);
      return false;
    }
    
    if (!formData.telefone) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }

    if (!formatters.validatePhone(formData.telefone)) {
      setPhoneError(authMessages.registerErrors.invalidPhone);
      toastHelper.showError(authMessages.registerErrors.invalidPhone);
      return false;
    }
    
    if (!formData.dataNascimento) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }

    if (!formatters.validateBirthDate(formData.dataNascimento)) {
      setBirthDateError(authMessages.registerErrors.invalidBirthDate);
      toastHelper.showError(authMessages.registerErrors.invalidBirthDate);
      return false;
    }
    
    return true;
  };

  const isAddressTabValid = () => {
    const basicFieldsValid = (
      formData.cep &&
      formData.rua &&
      formData.numero &&
      formData.bairro &&
      formData.cidade &&
      formData.estado &&
      !cepError &&
      !estadoError &&
      !cidadeError &&
      !bairroError &&
      !enderecoValidationError
    );
    
          // Se os campos básicos não estão válidos, retornar false
      if (!basicFieldsValid) {
        return false;
      }
      
      // Verificar se os dados do CEP existem e se há consistência básica
      if (dadosCep) {
        // Verificar consistência sem atualizar estados
        const estadoConsistente = !formData.estado || !dadosCep.uf || 
          formData.estado.toUpperCase() === dadosCep.uf.toUpperCase();
        
        const cidadeConsistente = !formData.cidade || !dadosCep.localidade || 
          formData.cidade.toLowerCase() === dadosCep.localidade.toLowerCase();

        const bairroConsistente = !formData.bairro || !dadosCep.bairro || 
          formData.bairro.toLowerCase() === dadosCep.bairro.toLowerCase();
        
        return estadoConsistente && cidadeConsistente && bairroConsistente;
      }
    
    // Se não tem dados do CEP, só considerar válido se não há erro de CEP
    return !cepError;
  };

  const validateAddressTab = () => {
    if (!formData.cep) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    if (cepError) {
      toastHelper.showError(cepError);
      return false;
    }
    
    if (!dadosCep) {
      toastHelper.showError('Busque um CEP válido primeiro');
      return false;
    }
    
    if (!formData.rua) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    if (!formData.numero) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    if (!formData.bairro) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    if (!formData.cidade) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    if (!formData.estado) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    // Validar consistência do endereço
    if (dadosCep) {
      if (formData.estado && dadosCep.uf && formData.estado.toUpperCase() !== dadosCep.uf.toUpperCase()) {
        toastHelper.showError(`Estado deve ser ${dadosCep.uf} para este CEP`);
        return false;
      }
      
      if (formData.cidade && dadosCep.localidade && formData.cidade.toLowerCase() !== dadosCep.localidade.toLowerCase()) {
        toastHelper.showError(`Cidade deve ser ${dadosCep.localidade} para este CEP`);
        return false;
      }
      
      if (formData.bairro && dadosCep.bairro && formData.bairro.toLowerCase() !== dadosCep.bairro.toLowerCase()) {
        toastHelper.showError(`Bairro deve ser ${dadosCep.bairro} para este CEP`);
        return false;
      }
    }
    
    return true;
  };

  const isSecurityTabValid = () => {
    return (
      formData.senha &&
      formatters.validatePassword(formData.senha) &&
      formData.confirmarSenha &&
      formData.senha === formData.confirmarSenha &&
      formData.termsAccepted &&
      !passwordError &&
      !confirmPasswordError
    );
  };

  const validateSecurityTab = () => {
    if (!formData.senha) {
      toastHelper.showError('Senha é obrigatória');
      return false;
    }
    
    if (!formatters.validatePassword(formData.senha)) {
      toastHelper.showError('A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial');
      return false;
    }

    if (!formData.confirmarSenha) {
      toastHelper.showError(authMessages.registerErrors.requiredFields);
      return false;
    }
    
    if (formData.senha !== formData.confirmarSenha) {
      toastHelper.showError(authMessages.registerErrors.passwordMismatch);
      return false;
    }
    
    if (!formData.termsAccepted) {
      toastHelper.showError(authMessages.registerErrors.requiredTerms);
      return false;
    }
    
    return true;
  };

  const getAvailableTabs = () => {
    const availableTabs = ['personal'];
    
    if (isPersonalTabValid()) {
      availableTabs.push('address');
    }
    
    if (isPersonalTabValid() && isAddressTabValid()) {
      availableTabs.push('security');
    }
    
    return availableTabs;
  };

  const handleTabPress = (tabId) => {
    const availableTabs = getAvailableTabs();
    
    if (availableTabs.includes(tabId)) {
      setActiveTab(tabId);
    } else {
      if (tabId === 'address' && !isPersonalTabValid()) {
        toastHelper.showWarning(authMessages.warnings.completePersonalDataFirst);
      } else if (tabId === 'security' && (!isPersonalTabValid() || !isAddressTabValid())) {
        if (!isPersonalTabValid()) {
          toastHelper.showWarning(authMessages.warnings.completePersonalDataFirst);
        } else {
          toastHelper.showWarning(authMessages.warnings.completeAddressDataFirst);
        }
      }
    }
  };

  const handleNextTab = () => {
    if (activeTab === 'personal') {
      if (validatePersonalTab()) {
        setActiveTab('address');
      }
    } else if (activeTab === 'address') {
      if (validateAddressTab()) {
        setActiveTab('security');
      } else {
        if (dadosCep) {
          // Limpar erros anteriores
          setEstadoError('');
          setCidadeError('');
          
          // Validar estado
          if (formData.estado && dadosCep.uf) {
            const estadoForm = formData.estado.toUpperCase().trim();
            const estadoCep = dadosCep.uf.toUpperCase().trim();
            
            if (estadoForm !== estadoCep) {
              setEstadoError(`Estado deve ser ${dadosCep.uf} para este CEP`);
            }
          }
          
          // Validar cidade
          if (formData.cidade && dadosCep.localidade) {
            const cidadeForm = formData.cidade.toLowerCase().trim();
            const cidadeCep = dadosCep.localidade.toLowerCase().trim();
            
            if (cidadeForm !== cidadeCep) {
              setCidadeError(`Cidade deve ser ${dadosCep.localidade} para este CEP`);
            }
          }
          
          // Validar bairro
          if (formData.bairro && dadosCep.bairro) {
            const bairroForm = formData.bairro.toLowerCase().trim();
            const bairroCep = dadosCep.bairro.toLowerCase().trim();
            
            if (bairroForm !== bairroCep) {
              setBairroError(`Bairro deve ser ${dadosCep.bairro} para este CEP`);
            }
          }
        }
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
      cpf: formData.cpf.replace(/[^\d]/g, ''),
      email: formData.email,
      dataNascimento: dataNascFormatada,
      telefone: formData.telefone,
      senha: formData.senha,
      endereco: endereco,
      role: 'user'
    };

    // Adicionar foto de perfil se disponível
    if (profileImage) {
      userData.imagemPerfil = profileImage.base64;
    }

    try {
      // Mostrar mensagem de loading específica para envio de email
      toastHelper.showInfo(authMessages.info.sendingEmailConfirmation);
      
      await emailTimeout.executeWithTimeout(
        () => PublicAuthService.requestEmailVerification(userData),
        {
          successMessage: 'Email de verificação enviado! Verifique sua caixa de entrada.',
          timeoutMessage: 'Tempo limite para envio do email esgotado. Verifique sua conexão e tente novamente.',
          errorMessage: authMessages.registerErrors.serverError,
          onSuccess: () => {
            setVerificationEmail(formData.email);
            setShowVerificationModal(true);
          },
          onTimeout: () => {
            setIsLoading(false);
          },
          onError: () => {
            setIsLoading(false);
          }
        }
      );
    } catch (error) {
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerifyEmail = async () => {
    if (!verificationCode.trim()) {
      toastHelper.showError(authMessages.emailVerificationErrors.verificationCodeRequired);
      return;
    }

    try {
      setIsVerifyingEmail(true);

      await PublicAuthService.verifyEmail(verificationEmail, verificationCode);

      // Exibe mensagem de sucesso
              toastHelper.showSuccess(authMessages.success.accountCreated);

      setShowVerificationModal(false);
      setTimeout(() => {
        navigation.navigate('Login');
      }, 1000);

    } catch (error) {
              toastHelper.showError(error.message || authMessages.emailVerificationErrors.invalidOrExpiredCode);
    } finally {
      setIsVerifyingEmail(false);
    }
  };

  const handleResendCode = async () => {
    if (resendCooldown > 0) {
      toastHelper.showWarning(authMessages.info.waitToResend(resendCooldown));
      return;
    }

    try {
      await resendTimeout.executeWithTimeout(
        () => PublicAuthService.resendVerificationCode(verificationEmail),
        {
          successMessage: 'Código reenviado com sucesso!',
          timeoutMessage: 'Tempo limite para reenvio do código esgotado. Tente novamente.',
          errorMessage: 'Erro ao reenviar código.',
          onSuccess: () => {
            setResendCooldown(60);
          }
        }
      );
    } catch (error) {
    }
  };

  const tabs = [
    { id: 'personal', label: 'Dados Pessoais' },
    { id: 'address', label: 'Endereço' },
    { id: 'security', label: 'Segurança' }
  ];

  const isMobile = isMobileView();
  const isDesktop = isDesktopView();

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContainer}>
        <View style={[styles.contentContainer, isDesktop && styles.contentContainerDesktop]}>
          <View style={[styles.pageHeaderContainer, isDesktop && styles.pageHeaderContainerDesktop]}>
            <Text style={styles.pageTitle}>Criar Conta</Text>
            <Text style={styles.pageSubtitle}>Registre-se para encontrar os melhores tatuadores</Text>
          </View>
          
          <View style={[styles.loginPrompt, isMobile && styles.loginPromptMobile]}>
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
          
          <View style={[styles.cardWrapper, isDesktop && styles.cardWrapperDesktop]}>
            <View style={styles.formCard}>
              <View style={styles.tabHeaderWrapper}>
                <TabHeader 
                  tabs={tabs}
                  activeTab={activeTab}
                  setActiveTab={setActiveTab}
                  onTabPress={handleTabPress}
                  availableTabs={getAvailableTabs()}
                />
              </View>
              
              <View style={[styles.formContainer, isDesktop && styles.formContainerDesktop]}>
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
                  profileImage={profileImage}
                  pickImage={pickImage}
                />
                    <FormNavigation
                      onNext={handleNextTab}
                      showPrev={false}
                      nextDisabled={!isPersonalTabValid()}
                    />
                  </>
                )}

                {activeTab === 'address' && (
                  <>
                    <AddressForm
                      formData={formData}
                      handleChange={handleChange}
                      buscarCep={buscarCep}
                      cepError={cepError}
                      estadoError={estadoError}
                      cidadeError={cidadeError}
                      bairroError={bairroError}
                      enderecoValidationError={enderecoValidationError}
                    />
                    <FormNavigation
                      onPrev={handlePrevTab}
                      onNext={handleNextTab}
                      nextDisabled={!isAddressTabValid()}
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
                    isValid={isSecurityTabValid()}
                  />
                )}
              </View>
            </View>
          </View>
        </View>
      </ScrollView>

      {/* Modal de Verificação de Email */}
      <Modal
        visible={showVerificationModal}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setShowVerificationModal(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContainer}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Verificação de Email</Text>
              <Text style={styles.modalSubtitle}>
                Enviamos um código de 6 dígitos para{'\n'}
                <Text style={styles.emailText}>{verificationEmail}</Text>
              </Text>
            </View>

            <View style={styles.modalBody}>
              <Text style={styles.inputLabel}>Código de Verificação</Text>
              <TextInput
                style={styles.codeInput}
                value={verificationCode}
                onChangeText={setVerificationCode}
                placeholder="000000"
                keyboardType="numeric"
                maxLength={6}
                textAlign="center"
              />

              <View style={styles.modalButtons}>
                <TouchableOpacity
                  style={[styles.modalButton, styles.verifyButton]}
                  onPress={handleVerifyEmail}
                  disabled={isVerifyingEmail}
                >
                  <Text style={styles.verifyButtonText}>
                    {isVerifyingEmail ? 'Verificando...' : 'Verificar'}
                  </Text>
                </TouchableOpacity>

                <TouchableOpacity
                  style={[
                    styles.modalButton, 
                    styles.resendButton,
                    (resendTimeout.isLoading || resendCooldown > 0) && styles.resendButtonDisabled
                  ]}
                  onPress={handleResendCode}
                  disabled={resendTimeout.isLoading || resendCooldown > 0}
                >
                  <Text style={[
                    styles.resendButtonText,
                    (resendTimeout.isLoading || resendCooldown > 0) && styles.resendButtonTextDisabled
                  ]}>
                    {resendTimeout.isLoading 
                      ? 'Reenviando...' 
                      : resendCooldown > 0 
                      ? `Reenviar em ${resendCooldown}s` 
                      : 'Reenviar Código'
                    }
                  </Text>
                </TouchableOpacity>

                <TouchableOpacity
                  style={[styles.modalButton, styles.cancelButton]}
                  onPress={() => setShowVerificationModal(false)}
                >
                  <Text style={styles.cancelButtonText}>Cancelar</Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>
        </View>
      </Modal>
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
  contentContainerDesktop: {
    marginTop: 15,
  },
  pageHeaderContainer: {
    marginBottom: 20,
    alignItems: 'center',
    zIndex: 2,
    marginTop: 15,
  },
  pageHeaderContainerDesktop: {
    marginBottom: 5,
    marginTop: 10,
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
  cardWrapperDesktop: {
    marginTop: 5,
    paddingVertical: 5,
  },
  formCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: '#eaeaea',
    marginBottom: 30,
    boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)',
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
    boxShadow: '0px 1px 2px rgba(0, 0, 0, 0.05)',
    elevation: 1,
    backgroundColor: '#f8f8f8',
    width: '100%',
  },
  formContainer: {
    padding: 30,
  },
  formContainerDesktop: {
    padding: 5,
  },
  loginPrompt: {
    alignItems: 'center',
    marginTop: 16,
  },
  loginPromptMobile: {
    marginTop: 8,
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
  // Estilos do Modal de Verificação
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  modalContainer: {
    backgroundColor: '#fff',
    borderRadius: 16,
    width: '100%',
    maxWidth: 400,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.25,
    shadowRadius: 8,
    elevation: 8,
  },
  modalHeader: {
    padding: 24,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    alignItems: 'center',
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#111',
    marginBottom: 8,
  },
  modalSubtitle: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    lineHeight: 20,
  },
  emailText: {
    fontWeight: '600',
    color: '#111',
  },
  modalBody: {
    padding: 24,
  },
  inputLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111',
    marginBottom: 8,
  },
  codeInput: {
    textAlign: 'center',
    borderWidth: 2,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 16,
    fontSize: 24,
    fontWeight: 'bold',
    letterSpacing: 8,
    marginBottom: 24,
    backgroundColor: '#f9f9f9',
  },
  modalButtons: {
    gap: 12,
  },
  modalButton: {
    paddingVertical: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  verifyButton: {
    backgroundColor: '#111',
  },
  verifyButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  resendButton: {
    backgroundColor: '#f5f5f5',
    borderWidth: 1,
    borderColor: '#ddd',
  },
  resendButtonText: {
    color: '#111',
    fontSize: 16,
    fontWeight: '500',
  },
  resendButtonDisabled: {
    opacity: 0.5,
  },
  resendButtonTextDisabled: {
    color: '#999',
  },
  cancelButton: {
    backgroundColor: 'transparent',
  },
  cancelButtonText: {
    color: '#666',
    fontSize: 16,
  },
});

export default RegisterScreen;
