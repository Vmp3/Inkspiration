import React, { useState, useEffect } from 'react';
import { View, StyleSheet, ScrollView, SafeAreaView } from 'react-native';
import axios from 'axios';
import * as ImagePicker from 'expo-image-picker';
import * as formatters from '../utils/formatters';
import { useAuth } from '../context/AuthContext';
import toastHelper from '../utils/toastHelper';

import PersonalForm from '../components/forms/PersonalForm';
import AddressForm from '../components/forms/AddressForm';
import FormNavigation from '../components/ui/FormNavigation';
import ProfessionalForm from '../components/forms/ProfessionalForm';
import PageHeader from '../components/EditProfile/PageHeader';
import FormContainer from '../components/EditProfile/FormContainer';
import SecuritySection from '../components/EditProfile/SecuritySection';

// Componentes modulares para profissionais
import BasicInfoForm from '../components/forms/BasicInfoForm';
import WorkHoursForm from '../components/forms/WorkHoursForm';
import PortfolioForm from '../components/forms/PortfolioForm';

// Hooks customizados
import useProfessionalData from '../components/EditProfile/hooks/useProfessionalData';
import useTabNavigation from '../components/EditProfile/hooks/useTabNavigation';
import useProfileUpdate from '../components/EditProfile/hooks/useProfileUpdate';
import useFormValidation from '../components/EditProfile/utils/formValidation';
import { editProfileMessages } from '../components/EditProfile/messages';

const EditProfileScreen = () => {
  const { userData } = useAuth();
  const [isArtist, setIsArtist] = useState(false);
  const [experienceDropdownOpen, setExperienceDropdownOpen] = useState(false);
  
  // Form validation states
  const [nomeError, setNomeError] = useState('');
  const [sobrenomeError, setSobrenomeError] = useState('');
  const [fullNameError, setFullNameError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [phoneError, setPhoneError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  const [bioError, setBioError] = useState('');
  const [biographyError, setBiographyError] = useState('');
  const [websiteError, setWebsiteError] = useState('');
  const [profileImage, setProfileImage] = useState(null);
  
  // Estados de validação de endereço
  const [cepError, setCepError] = useState('');
  const [estadoError, setEstadoError] = useState('');
  const [cidadeError, setCidadeError] = useState('');
  const [bairroError, setBairroError] = useState('');
  const [enderecoValidationError, setEnderecoValidationError] = useState('');
  const [dadosCep, setDadosCep] = useState(null);
  
  // Form data state
  const [formData, setFormData] = useState({
    nome: '',
    sobrenome: '',
    cpf: '',
    email: '',
    telefone: '',
    dataNascimento: '',
    cep: '',
    rua: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    estado: '',
    senhaAtual: '',
    novaSenha: '',
    confirmarSenha: '',
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
  
  // Hooks customizados
  const validation = useFormValidation();
  const professionalData = useProfessionalData(userData);
  const tabNavigation = useTabNavigation(isArtist, formData, professionalData.professionalFormData, {
    cepError,
    estadoError,
    cidadeError,
    bairroError,
    enderecoValidationError,
    forceAddressValidation: () => {
      if (dadosCep) {
        // Forçar validação igual ao RegisterScreen
        if (formData.estado && dadosCep.uf) {
          const estadoForm = formData.estado.toUpperCase().trim();
          const estadoCep = dadosCep.uf.toUpperCase().trim();
          
          if (estadoForm !== estadoCep) {
            setEstadoError(`Estado deve ser ${dadosCep.uf} para este CEP`);
          }
        }
        
        if (formData.cidade && dadosCep.localidade) {
          const cidadeForm = formData.cidade.toLowerCase().trim();
          const cidadeCep = dadosCep.localidade.toLowerCase().trim();
          
          if (cidadeForm !== cidadeCep) {
            setCidadeError(`Cidade deve ser ${dadosCep.localidade} para este CEP`);
          }
        }
        
        if (formData.bairro && dadosCep.bairro) {
          const bairroForm = formData.bairro.toLowerCase().trim();
          const bairroCep = dadosCep.bairro.toLowerCase().trim();
          
          if (bairroForm !== bairroCep) {
            setBairroError(`Bairro deve ser ${dadosCep.bairro} para este CEP`);
          }
        }
      }
    }
  });
  const profileUpdate = useProfileUpdate(isArtist, professionalData.updateProfessionalData);

  // Load user data when component mounts
  useEffect(() => {
    if (userData) {
      setIsArtist(userData.role === 'ROLE_PROF');
      
      const nameParts = userData.nome ? userData.nome.split(' ') : ['', ''];
      const firstName = nameParts[0] || '';
      const lastName = nameParts.slice(1).join(' ') || '';
      
      setFormData({
        nome: firstName,
        sobrenome: lastName,
        cpf: userData.cpf || '',
        email: userData.email || '',
        telefone: userData.telefone || '',
        dataNascimento: userData.dataNascimento || '',
        cep: userData.endereco?.cep || '',
        rua: userData.endereco?.rua || '',
        numero: userData.endereco?.numero || '',
        complemento: userData.endereco?.complemento || '',
        bairro: userData.endereco?.bairro || '',
        cidade: userData.endereco?.cidade || '',
        estado: userData.endereco?.estado || '',
        senhaAtual: '',
        novaSenha: '',
        confirmarSenha: '',
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

      // Carregar foto de perfil se disponível
      if (userData.imagemPerfil) {
        setProfileImage({
          uri: userData.imagemPerfil,
          base64: userData.imagemPerfil,
          type: 'image/jpeg',
          name: 'profile.jpg'
        });
      }

      // Validar CEP se já houver dados de endereço carregados
      if (userData.endereco?.cep) {
        setTimeout(() => {
          buscarCep(userData.endereco.cep);
        }, 500); // Pequeno delay para garantir que o estado foi atualizado
      }
    }
  }, [userData]);

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

  const validateBio = (text) => {
    if (!text || text.trim().length === 0) {
      return 'Biografia é obrigatória';
    }
    if (text.trim().length < 20) {
      return 'Biografia deve ter pelo menos 20 caracteres';
    }
    if (text.trim().length > 500) {
      return 'Biografia deve ter no máximo 500 caracteres';
    }
    return '';
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
        
        // Validação DUPLA de formato - MIME type e extensão
        const validMimeTypes = ['image/jpeg', 'image/png'];
        const validExtensions = ['.png', '.jpg', '.jpeg', '.jfif'];
        
        // Verificar MIME type
        if (!selectedImage.mimeType || !validMimeTypes.includes(selectedImage.mimeType)) {
          toastHelper.showError(editProfileMessages.imageUploadErrors.invalidFormat);
          return;
        }
        
        // Verificar extensão do arquivo
        if (selectedImage.fileName) {
          const fileExtension = selectedImage.fileName.toLowerCase().slice(selectedImage.fileName.lastIndexOf('.'));
          if (!validExtensions.includes(fileExtension)) {
            toastHelper.showError(editProfileMessages.imageUploadErrors.invalidFormat);
            return;
          }
        }
        
        // Validação de tamanho - limite de 5MB
        const maxSizeInMB = 5;
        const maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        
        if (selectedImage.fileSize && selectedImage.fileSize > maxSizeInBytes) {
          toastHelper.showError(editProfileMessages.imageUploadErrors.fileTooLarge);
          return;
        }
        
        // Validação adicional do base64 (que é ~33% maior que o arquivo original)
        const base64String = selectedImage.base64;
        const base64SizeInBytes = (base64String.length * 3) / 4;
        
        if (base64SizeInBytes > maxSizeInBytes) {
          toastHelper.showError(editProfileMessages.imageUploadErrors.processedImageTooLarge);
          return;
        }
        
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
      toastHelper.showError(editProfileMessages.imageUploadErrors.selectionFailed);
    }
  };

  const handleBiographyChange = (text) => {
    professionalData.setBiography(text);
    const error = validateBio(text);
    setBiographyError(error);
  };

  const handleChange = (field, value) => {
    let formattedValue = value;
    
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
        return; // CPF is read-only in edit mode
      case 'cep':
        formattedValue = formatters.formatCEP(value);
        setCepError('');
        setEnderecoValidationError('');
        break;
      case 'telefone':
        formattedValue = formatters.formatPhone(value);
        setPhoneError('');
        // Validar telefone quando completo (10 ou 11 dígitos)
        if (value.replace(/\D/g, '').length >= 10) {
          const errorMessage = formatters.getPhoneValidationMessage(formatters.formatPhone(value));
          if (errorMessage) {
            setPhoneError(errorMessage);
          }
        }
        break;
      case 'email':
        setEmailError('');
        break;
      case 'bio':
        setBioError('');
        const bioValidationError = validateBio(value);
        if (bioValidationError) {
          setBioError(bioValidationError);
        }
        formattedValue = value;
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
      case 'redesSociais':
        return setFormData(prev => ({
          ...prev,
          redesSociais: {
            ...prev.redesSociais,
            ...value
          }
        }));
      case 'especialidades':
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

    setFormData({
      ...formData,
      [field]: formattedValue,
    });

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
      const errorMessage = formatters.getPhoneValidationMessage(formData.telefone);
      if (errorMessage) {
        setPhoneError(errorMessage);
      } else {
        setPhoneError('');
      }
    }

    if (field === 'senhaAtual' && !formData.senhaAtual && (formData.novaSenha || formData.confirmarSenha)) {
      setPasswordError('Senha atual é obrigatória para alterar a senha');
    }

    if (field === 'novaSenha') {
      if (formData.novaSenha) {
        if (!formatters.validatePassword(formData.novaSenha)) {
          setPasswordError('A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial');
        } else {
          setPasswordError('');
        }
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
      setCepError('Erro ao consultar CEP. Verifique sua conexão.');
      setDadosCep(null);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContainer}>
        <View style={styles.contentContainer}>
          <PageHeader 
            title="Meu Perfil"
            subtitle="Gerencie suas informações pessoais"
          />
          
          <FormContainer 
            tabs={tabNavigation.getTabs()}
            activeTab={tabNavigation.activeTab}
            setActiveTab={tabNavigation.setActiveTab}
            onTabPress={tabNavigation.handleTabPress}
            availableTabs={tabNavigation.getAvailableTabs()}
          >
            {tabNavigation.activeTab === 'personal' && (
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
                  profileImage={profileImage}
                  pickImage={pickImage}
                />
                    <FormNavigation
                  onNext={tabNavigation.handleNextTab}
                      showPrev={false}
                      nextText="Próximo"
                      nextDisabled={!validation.isPersonalTabValid(formData)}
                    />
                  </>
                )}

            {tabNavigation.activeTab === 'address' && (
                  <>
                    <AddressForm
                      formData={formData}
                      handleChange={handleChange}
                      handleBlur={handleBlur}
                      buscarCep={buscarCep}
                      cepError={cepError}
                      estadoError={estadoError}
                      cidadeError={cidadeError}
                      bairroError={bairroError}
                      enderecoValidationError={enderecoValidationError}
                    />
                    <FormNavigation
                  onPrev={tabNavigation.handlePrevTab}
                  onNext={tabNavigation.handleNextTab}
                      nextDisabled={!validation.isAddressTabValid(formData, cepError, estadoError, cidadeError, bairroError, enderecoValidationError)}
                    />
                  </>
                )}
                
            {isArtist && tabNavigation.activeTab === 'basic-info' && (
                  <>
                    <BasicInfoForm 
                  experience={professionalData.professionalFormData.experience}
                  setExperience={(value) => professionalData.setProfessionalFormData(prev => ({ ...prev, experience: value }))}
                  specialties={professionalData.professionalFormData.specialties}
                  handleSpecialtyChange={professionalData.handleSpecialtyChange}
                  socialMedia={professionalData.professionalFormData.socialMedia}
                  handleSocialMediaChange={professionalData.handleSocialMediaChange}
                  handleNextTab={tabNavigation.handleNextTab}
                  experienceDropdownOpen={experienceDropdownOpen}
                  setExperienceDropdownOpen={setExperienceDropdownOpen}
                  tiposServico={professionalData.professionalFormData.tiposServico}
                  setTiposServico={(value) => professionalData.setProfessionalFormData(prev => ({ ...prev, tiposServico: value }))}
                  tipoServicoSelecionados={professionalData.professionalFormData.tipoServicoSelecionados}
                  handleTipoServicoChange={professionalData.handleTipoServicoChange}
                  precosServicos={professionalData.professionalFormData.precosServicos}
                  handlePrecoServicoChange={professionalData.handlePrecoServicoChange}
                  websiteError={websiteError}
                  setWebsiteError={setWebsiteError}
                    />
                    <FormNavigation
                  onPrev={tabNavigation.handlePrevTab}
                  onNext={tabNavigation.handleNextTab}
                      nextDisabled={!validation.isBasicInfoTabValid(professionalData.professionalFormData)}
                    />
                  </>
                )}
                
            {isArtist && tabNavigation.activeTab === 'hours' && (
                  <>
                    <WorkHoursForm 
                  workHours={professionalData.professionalFormData.workHours}
                  handleWorkHourChange={professionalData.handleWorkHourChange}
                  handlePrevTab={tabNavigation.handlePrevTab}
                  handleNextTab={tabNavigation.handleNextTab}
                    />
                    <FormNavigation
                  onPrev={tabNavigation.handlePrevTab}
                  onNext={tabNavigation.handleNextTab}
                      nextDisabled={!validation.isWorkHoursTabValid(professionalData.professionalFormData)}
                    />
                  </>
                )}
                
            {isArtist && tabNavigation.activeTab === 'portfolio' && (
                  <>
                                    <PortfolioForm 
                  biography={professionalData.professionalFormData.biography}
                  setBiography={professionalData.setBiography}
                  biographyError={biographyError}
                  handleBiographyChange={handleBiographyChange}
                  portfolioImages={professionalData.professionalFormData.portfolioImages}
                  handleAddPortfolioImage={professionalData.handleAddPortfolioImage}
                  handleRemovePortfolioImage={professionalData.handleRemovePortfolioImage}
                  pickImage={professionalData.pickImage}
                />
                    <FormNavigation
                  onPrev={tabNavigation.handlePrevTab}
                  onNext={tabNavigation.handleNextTab}
                      nextDisabled={!validation.isPortfolioTabValid(professionalData.professionalFormData)}
                    />
                  </>
                )}

            {!isArtist && tabNavigation.activeTab === 'professional' && (
                  <>
                    <ProfessionalForm 
                      formData={formData}
                      handleChange={handleChange}
                      bioError={bioError}
                    />
                    <FormNavigation
                  onPrev={tabNavigation.handlePrevTab}
                  onNext={tabNavigation.handleNextTab}
                      nextDisabled={false}
                    />
                  </>
                )}

            {tabNavigation.activeTab === 'security' && (
                  <>
                <SecuritySection
                  formData={formData}
                  handleChange={handleChange}
                  handleBlur={handleBlur}
                  passwordError={passwordError}
                  confirmPasswordError={confirmPasswordError}
                />

                    <FormNavigation
                  onPrev={tabNavigation.handlePrevTab}
                  onNext={() => profileUpdate.handleUpdateProfile(formData, tabNavigation.validateCurrentTab, professionalData.professionalFormData, profileImage)}
                      showNext={true}
                      nextText="Salvar Alterações"
                  isLoading={profileUpdate.isLoading}
                    />
                  </>
                )}
          </FormContainer>
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
    maxWidth: 800,
    alignSelf: 'center',
    marginTop: 20,
  },
});

export default EditProfileScreen; 