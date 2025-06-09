import React, { useState, useEffect, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  SafeAreaView,
  TextInput,
  TouchableOpacity,
  Image,
  Platform,
  Modal,
  TouchableWithoutFeedback,
  ActivityIndicator,
  Alert
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { Feather } from '@expo/vector-icons';
import * as ImagePicker from 'expo-image-picker';
import { useAuth } from '../context/AuthContext';
import TabHeader from '../components/ui/TabHeader';
import Button from '../components/ui/Button';
import FormNavigation from '../components/ui/FormNavigation';
import toastHelper from '../utils/toastHelper';
import { TimeInput } from '../components/TimeInput';
import AuthService from '../services/AuthService';
import ApiService from '../services/ApiService';
import ProfessionalService from '../services/ProfessionalService';
import { professionalMessages } from '../components/professional/messages';

// Componentes modulares para as diferentes seções do formulário
import BasicInfoForm from '../components/forms/BasicInfoForm';
import WorkHoursForm from '../components/forms/WorkHoursForm';
import PortfolioForm from '../components/forms/PortfolioForm';

const ProfessionalRegisterScreen = () => {
  const navigation = useNavigation();
  const { userData, updateUserData } = useAuth();
  const [activeTab, setActiveTab] = useState('basic');
  const [isLoading, setIsLoading] = useState(false);
  const [showExperienceOptions, setShowExperienceOptions] = useState(false);
  const [experienceDropdownOpen, setExperienceDropdownOpen] = useState(false);
  
  // Estados para as informações básicas
  const [experience, setExperience] = useState('1-3 anos');
  const [specialties, setSpecialties] = useState({
    Tradicional: false,
    Blackwork: false,
    'Neo-Tradicional': false,
    Fineline: false,
    Realista: false,
    Geométrico: false,
    Minimalista: false,
    Aquarela: false,
    Japonês: false,
    'Old School': false
  });
  const [socialMedia, setSocialMedia] = useState({
    instagram: '',
    tiktok: '',
    facebook: '',
    twitter: '',
    website: ''
  });
  
  // Estado para horários de trabalho
  const [workHours, setWorkHours] = useState([
    {
      day: 'Segunda',
      available: true,
      morning: {
        enabled: true,
        start: '08:00',
        end: '12:00'
      },
      afternoon: {
        enabled: true,
        start: '13:00',
        end: '18:00'
      }
    },
    {
      day: 'Terça',
      available: true,
      morning: {
        enabled: true,
        start: '08:00',
        end: '12:00'
      },
      afternoon: {
        enabled: true,
        start: '13:00',
        end: '18:00'
      }
    },
    {
      day: 'Quarta',
      available: true,
      morning: {
        enabled: true,
        start: '08:00',
        end: '12:00'
      },
      afternoon: {
        enabled: true,
        start: '13:00',
        end: '18:00'
      }
    },
    {
      day: 'Quinta',
      available: true,
      morning: {
        enabled: true,
        start: '08:00',
        end: '12:00'
      },
      afternoon: {
        enabled: true,
        start: '13:00',
        end: '18:00'
      }
    },
    {
      day: 'Sexta',
      available: true,
      morning: {
        enabled: true,
        start: '08:00',
        end: '12:00'
      },
      afternoon: {
        enabled: true,
        start: '13:00',
        end: '18:00'
      }
    },
    {
      day: 'Sábado',
      available: true,
      morning: {
        enabled: true,
        start: '08:00',
        end: '12:00'
      },
      afternoon: {
        enabled: false,
        start: '13:00',
        end: '18:00'
      }
    },
    {
      day: 'Domingo',
      available: false,
      morning: {
        enabled: false,
        start: '08:00',
        end: '12:00'
      },
      afternoon: {
        enabled: false,
        start: '13:00',
        end: '18:00'
      }
    }
  ]);
  
  // Estado para portfólio
  const [biography, setBiography] = useState('');
  const [portfolioImages, setPortfolioImages] = useState([]);
  const [profileImage, setProfileImage] = useState(null);
  
  // Opções de experiência
  const experienceOptions = [
    'Menos de 1 ano',
    '1-3 anos',
    '3-5 anos',
    '5-10 anos',
    'Mais de 10 anos'
  ];
  
  const dropdownRef = useRef(null);
  
  useEffect(() => {
    if (Platform.OS === 'web') {
      // Função para remover o z-index:0 dos elementos com a classe .css-view-175oi2r
      const removeZIndexFromViews = () => {
        const cssViewElements = document.querySelectorAll('.css-view-175oi2r');
        cssViewElements.forEach(element => {
          if (element.style.zIndex === '0') {
            element.style.zIndex = 'auto';
          }
        });

        if (dropdownRef.current) {
          const parentElements = [];
          let currentParent = dropdownRef.current.parentElement;
          
          // Percorre os elementos pais até encontrar o body
          while (currentParent && currentParent !== document.body) {
            parentElements.push(currentParent);
            currentParent = currentParent.parentElement;
          }
          
          parentElements.forEach(el => {
            const currentZIndex = window.getComputedStyle(el).zIndex;
            if (currentZIndex === 'auto' || currentZIndex === '0') {
              el.style.zIndex = 'auto';
            }
          });
        }
      };
      
      removeZIndexFromViews();
      
      setTimeout(removeZIndexFromViews, 100);
      
      if (experienceDropdownOpen) {
        removeZIndexFromViews();
      }
    }
  }, [experienceDropdownOpen]);
  
  // Verificar se o usuário está logado
  useEffect(() => {
    if (!userData) {
      navigation.navigate('Login');
    } else if (userData.role === 'ROLE_PROF') {
      navigation.navigate('Home');
    }
  }, [userData, navigation]);
  
  // Fechar dropdown quando clicar fora do componente
  useEffect(() => {
    if (Platform.OS === 'web') {
      const handleClickOutside = (event) => {
        if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
          setExperienceDropdownOpen(false);
        }
      };

      document.addEventListener('mousedown', handleClickOutside);
      return () => {
        document.removeEventListener('mousedown', handleClickOutside);
      };
    }
  }, []);
  
  const tabs = [
    { id: 'basic', label: 'Informações Básicas' },
    { id: 'hours', label: 'Horário de Trabalho' },
    { id: 'portfolio', label: 'Portfólio' }
  ];
  
  const handleSpecialtyChange = (specialty) => {
    setSpecialties(prev => ({
      ...prev,
      [specialty]: !prev[specialty]
    }));
  };
  
  const handleSocialMediaChange = (platform, value) => {
    setSocialMedia(prev => ({
      ...prev,
      [platform]: value
    }));
  };
  
  const handleWorkHourChange = (index, period, field, value) => {
    const newWorkHours = [...workHours];
    
    if (field === 'available') {
      newWorkHours[index].available = value;
      if (!value) {
        newWorkHours[index].morning.enabled = false;
        newWorkHours[index].afternoon.enabled = false;
      }
    }
    else if (period === 'morning' || period === 'afternoon') {
      newWorkHours[index][period][field] = value;
    }
    
    setWorkHours(newWorkHours);
  };
  
  const handleAddPortfolioImage = () => {
    pickImage('portfolio');
  };
  
  const handleRemovePortfolioImage = (index) => {
    const newImages = [...portfolioImages];
    newImages.splice(index, 1);
    setPortfolioImages(newImages);
  };
  
  const pickImage = async (imageType, index = null) => {
    try {
      const result = await ImagePicker.launchImageLibraryAsync({
        mediaTypes: ImagePicker.MediaTypeOptions.Images,
        allowsEditing: true,
        aspect: [1, 1],
        quality: 0.8,
        base64: true
      });
      if (!result.canceled) {
        const selectedImage = result.assets[0];
        const imageUri = selectedImage.uri;
        const imageBase64 = `data:image/jpeg;base64,${selectedImage.base64}`;
        if (imageType === 'portfolio') {
          setPortfolioImages(prev => [
            ...prev,
            {
              uri: imageUri,
              base64: imageBase64,
              type: 'image/jpeg',
              name: `portfolio_${prev.length}.jpg`
            }
          ]);
        } else if (imageType === 'profile') {
          setProfileImage({
            uri: imageUri,
            base64: imageBase64,
            type: 'image/jpeg',
            name: 'profile.jpg'
          });
        }
      }
    } catch (error) {
      toastHelper.showError('Falha ao selecionar imagem. Tente novamente.');
    }
  };
  
  // Upload de imagens para o servidor
  const uploadImages = async (profissionalId) => {
    try {
      // Upload da imagem de perfil
      if (profileImage && profileImage.base64) {
        try {
          await ApiService.put(`/usuario/${userData.idUsuario}/foto-perfil`, { imagemBase64: profileImage.base64 });
        } catch (error) {
          console.error('Falha ao enviar imagem de perfil:', error);
        }
      }
      
      // Upload das imagens do portfólio em base64
      for (const image of portfolioImages) {
        if (image && image.base64) {
          const base64Data = image.base64;
          const imagemBase64 = base64Data.startsWith('data:') ? base64Data : `data:image/jpeg;base64,${base64Data}`;
          
          const imagemDTO = {
            imagemBase64,
            idPortifolio: profissionalId
          };
          
          try {
            await ApiService.post('/imagens', imagemDTO);
          } catch (error) {
            console.error('Falha ao enviar imagem do portfólio:', error);
          }
        }
      }
      
      return true;
    } catch (error) {
      console.error('Erro ao fazer upload das imagens:', error);
      return false;
    }
  };
  
  const handleNextTab = () => {
    if (activeTab === 'basic') {
      setActiveTab('hours');
    } else if (activeTab === 'hours') {
      setActiveTab('portfolio');
    }
  };
  
  const handlePrevTab = () => {
    if (activeTab === 'hours') {
      setActiveTab('basic');
    } else if (activeTab === 'portfolio') {
      setActiveTab('hours');
    }
  };
  
  const handleSubmit = async () => {
    setIsLoading(true);
    
    try {
      // Validar se pelo menos uma especialidade foi selecionada
      const selectedSpecialties = Object.keys(specialties).filter(key => specialties[key]);
      if (selectedSpecialties.length === 0) {
        toastHelper.showError('Selecione pelo menos uma especialidade');
        setIsLoading(false);
        return;
      }
      
      // Validar biografia
      if (!biography || biography.trim().length < 20) {
        toastHelper.showError('A biografia deve conter pelo menos 20 caracteres');
        setIsLoading(false);
        return;
      }
      
      // Preparar disponibilidades (horários de trabalho)
      const disponibilidades = [];
      workHours.forEach(day => {
        if (day.available) {
          if (day.morning.enabled) {
            disponibilidades.push({
              hrAtendimento: `${day.day}-${day.morning.start}-${day.morning.end}`
            });
          }
          if (day.afternoon.enabled) {
            disponibilidades.push({
              hrAtendimento: `${day.day}-${day.afternoon.start}-${day.afternoon.end}`
            });
          }
        }
      });
      
      // Validar se pelo menos um horário foi selecionado
      if (disponibilidades.length === 0) {
        toastHelper.showError('Defina pelo menos um horário de disponibilidade');
        setIsLoading(false);
        return;
      }
      
      // Verificar se o usuário está logado e tem os dados necessários
      if (!userData?.idUsuario) {
        toastHelper.showError('Não foi possível identificar seu usuário. Faça login novamente.');
        setIsLoading(false);
        return;
      }
      
      // Buscar os dados do usuário para obter o endereço
      try {
        const userDetails = await ApiService.get(`/usuario/${userData.idUsuario}`);
        
        if (!userDetails.idEndereco) {
          toastHelper.showError('Seu cadastro não possui um endereço. Atualize seu perfil antes de continuar.');
          setIsLoading(false);
          return;
        }
        
        // Preparar objeto com formato esperado pelo backend (ProfissionalCriacaoDTO)
        const professionalData = {
          idUsuario: userData.idUsuario,
          idEndereco: userDetails.idEndereco,
          experiencia: experience,
          especialidade: selectedSpecialties.join(', '),
          descricao: biography,
          estilosTatuagem: selectedSpecialties,
          instagram: socialMedia.instagram || null,
          tiktok: socialMedia.tiktok || null,
          facebook: socialMedia.facebook || null,
          twitter: socialMedia.twitter || null,
          website: socialMedia.website || null,
          disponibilidades: disponibilidades
        };
        
        // Enviar dados para o backend
        const profissionalCadastrado = await ApiService.post('/auth/register/profissional-completo', professionalData);
        
        // Se houver imagens, fazer o upload
        if ((profileImage && profileImage.uri) || portfolioImages.some(img => img && img.uri)) {
          toastHelper.showInfo('Enviando imagens...');
          
          try {
            // Tentativa de envio das imagens
            await uploadImages(profissionalCadastrado.idProfissional);
          } catch (imageError) {
            console.error('Erro ao enviar imagens:', imageError);
            toastHelper.showWarning('Profissional cadastrado, mas houve um problema ao enviar as imagens.');
          }
        }
        
        toastHelper.showSuccess('Cadastro de profissional realizado com sucesso!');
        
        // Atualizar o token para refletir a nova role (ROLE_PROF)
        try {
          const tokenUpdated = await AuthService.reautenticar(userData.idUsuario);
          
          if (tokenUpdated) {
            await updateUserData();
            
            setTimeout(() => {
              navigation.navigate('Home');
            }, 1000);
          } else {
            console.error('Falha ao atualizar token - redirecionando para login');
            toastHelper.showWarning('Por favor, faça login novamente para atualizar suas permissões');
            await AuthService.logout();
            setTimeout(() => {
              navigation.navigate('Login');
            }, 2000);
          }
        } catch (tokenError) {
          console.error('Erro ao atualizar token:', tokenError);
          
          toastHelper.showWarning('Por favor, faça login novamente para atualizar suas permissões');
          await AuthService.logout();
          setTimeout(() => {
            navigation.navigate('Login');
          }, 2000);
        }
      } catch (userError) {
        console.error('Erro ao obter dados do usuário ou cadastrar profissional:', userError);
        toastHelper.showError('Ocorreu um erro ao processar sua solicitação. Tente novamente.');
      }
      
    } catch (error) {
      console.error('Erro ao cadastrar profissional:', error);
      toastHelper.showError(error.message || 'Ocorreu um erro ao tentar cadastrar. Tente novamente.');
    } finally {
      setIsLoading(false);
    }
  };
  
  const handleExperienceSelect = (option) => {
    setExperience(option);
    setExperienceDropdownOpen(false);
  };
  
  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContainer}>
        <View style={styles.contentContainer}>
          <View style={styles.header}>
            <Text style={styles.title}>Cadastro de Profissional</Text>
            <Text style={styles.subtitle}>Cadastre-se como tatuador e atraia mais clientes</Text>
          </View>
          
          <View style={styles.cardWrapper}>
            <View style={styles.card}>
              <TabHeader tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab} />
              
              {activeTab === 'basic' && (
                <View style={styles.tabContentWrapper}>
                  <BasicInfoForm 
                    experience={experience}
                    setExperience={setExperience}
                    specialties={specialties}
                    handleSpecialtyChange={handleSpecialtyChange}
                    socialMedia={socialMedia}
                    handleSocialMediaChange={handleSocialMediaChange}
                    experienceDropdownOpen={experienceDropdownOpen}
                    setExperienceDropdownOpen={setExperienceDropdownOpen}
                  />
                  <View style={styles.formNavigationWrapper}>
                    <FormNavigation
                      onNext={handleNextTab}
                      showPrev={false}
                      nextText="Próximo"
                    />
                  </View>
                </View>
              )}
              
              {activeTab === 'hours' && (
                <View style={styles.tabContentWrapper}>
                  <WorkHoursForm 
                    workHours={workHours}
                    handleWorkHourChange={handleWorkHourChange}
                  />
                  <View style={styles.formNavigationWrapper}>
                    <FormNavigation
                      onPrev={handlePrevTab}
                      onNext={handleNextTab}
                      nextText="Próximo"
                    />
                  </View>
                </View>
              )}
              
              {activeTab === 'portfolio' && (
                <View style={styles.tabContentWrapper}>
                  <PortfolioForm 
                    biography={biography}
                    setBiography={setBiography}
                    portfolioImages={portfolioImages}
                    profileImage={profileImage}
                    handleAddPortfolioImage={handleAddPortfolioImage}
                    handleRemovePortfolioImage={handleRemovePortfolioImage}
                    pickImage={pickImage}
                  />
                  <View style={styles.formNavigationWrapper}>
                    <FormNavigation
                      onPrev={handlePrevTab}
                      onNext={handleSubmit}
                      nextText={isLoading ? "Finalizando..." : "Finalizar Cadastro"}
                      isLoading={isLoading}
                    />
                  </View>
                </View>
              )}
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
    paddingBottom: 40,
  },
  header: {
    marginBottom: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    marginTop: 4,
  },
  cardWrapper: {
    marginHorizontal: 'auto',
    width: '100%',
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#eaeaea',
    overflow: 'hidden',
  },
  tabContentWrapper: {
    padding: 16,
  },
  formGroup: {
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  dropdownContainer: {
    position: 'relative',
    zIndex: 9999,
  },
  selectField: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    padding: 12,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#fff',
    zIndex: 9999,
  },
  dropdownList: {
    position: 'absolute',
    top: '100%',
    left: 0,
    right: 0,
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    marginTop: 2,
    maxHeight: 300,
    zIndex: 10000,
    elevation: 5,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    ...(Platform.OS === 'web' ? { 
      position: 'absolute', 
      overflow: 'auto',
      width: '100%'
    } : {}),
  },
  dropdownItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  dropdownItemSelected: {
    backgroundColor: '#f5f5f5',
  },
  dropdownItemText: {
    fontSize: 14,
    color: '#333',
  },
  dropdownItemTextSelected: {
    fontSize: 14,
    color: '#000',
    fontWeight: 'bold',
  },
  checkboxGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  checkboxItem: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '33.33%',
    marginBottom: 12,
  },
  checkbox: {
    width: 20,
    height: 20,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 4,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 8,
  },
  checkboxChecked: {
    backgroundColor: '#000',
    borderColor: '#000',
  },
  checkboxLabel: {
    fontSize: 14,
  },
  socialInputRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  socialInput: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    padding: 10,
    marginLeft: 10,
  },
  // Estilos para a aba de horários
  workHoursTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 6,
  },
  workHoursSubtitle: {
    fontSize: 14,
    color: '#666',
    marginBottom: 16,
  },
  daysContainer: {
    marginBottom: 20,
  },
  dayCard: {
    borderWidth: 1,
    borderColor: '#eaeaea',
    borderRadius: 4,
    padding: 12,
    marginBottom: 12,
  },
  dayHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  dayName: {
    fontSize: 16,
    fontWeight: '500',
  },
  availableCheckbox: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  dayHours: {
    marginTop: 12,
  },
  periodRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  periodCheckbox: {
    flexDirection: 'row',
    alignItems: 'center',
    width: 80,
  },
  timeInputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  timeInputSeparator: {
    marginHorizontal: 8,
  },
  // Estilos para a aba de portfólio
  biographyInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    padding: 12,
    height: 120,
    textAlignVertical: 'top',
  },
  portfolioHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  addButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#000',
    borderRadius: 4,
    paddingHorizontal: 10,
    paddingVertical: 6,
  },
  addButtonIcon: {
    marginRight: 5,
  },
  addButtonText: {
    color: '#000',
    fontWeight: '500',
    fontSize: 14,
  },
  portfolioGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -8,
  },
  portfolioItem: {
    width: '50%',
    padding: 8,
  },
  portfolioImageContainer: {
    aspectRatio: 1,
    backgroundColor: '#f1f1f1',
    borderRadius: 4,
    overflow: 'hidden',
  },
  portfolioImage: {
    width: '100%',
    height: '100%',
  },
  profileImageContainer: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderStyle: 'dashed',
    borderRadius: 8,
    overflow: 'hidden',
  },
  profileImage: {
    width: '100%',
    height: 200,
  },
  profileImagePlaceholder: {
    height: 200,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  profileImageText: {
    textAlign: 'center',
    marginVertical: 8,
    color: '#666',
    fontSize: 14,
  },
  profileImageSubtext: {
    textAlign: 'center',
    color: '#999',
    fontSize: 12,
    marginBottom: 16,
  },
  loadingOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.8)',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    fontWeight: 'bold',
    color: '#000',
  },
  emptyImagePlaceholder: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  emptyImageText: {
    marginTop: 8,
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
  },
  removeImageButton: {
    position: 'absolute',
    top: 12,
    right: 12,
    backgroundColor: 'rgba(255,255,255,0.9)',
    borderRadius: 15,
    width: 30,
    height: 30,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 2,
    elevation: 2,
    zIndex: 1,
  },
  portfolioHelpText: {
    fontSize: 14,
    color: '#666',
    marginBottom: 16,
    fontStyle: 'italic',
  },
  formNavigationWrapper: {
    marginTop: -10,
  },
});

export default ProfessionalRegisterScreen; 