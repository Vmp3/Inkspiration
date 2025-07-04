import { useState, useEffect } from 'react';
import * as ImagePicker from 'expo-image-picker';
import ApiService from '../../../services/ApiService';
import toastHelper from '../../../utils/toastHelper';
import { editProfileMessages } from '../messages';

const useProfessionalData = (userData) => {
  const [professionalFormData, setProfessionalFormData] = useState({
    experience: '1-3 anos',
    specialties: {
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
    },
    socialMedia: {
      instagram: '',
      tiktok: '',
      facebook: '',
      twitter: '',
      website: ''
    },
    workHours: [
      {
        day: 'Segunda',
        available: true,
        morning: { enabled: true, start: '07:00', end: '11:00' },
        afternoon: { enabled: true, start: '13:00', end: '20:00' }
      },
      {
        day: 'Terça',
        available: true,
        morning: { enabled: true, start: '07:00', end: '11:00' },
        afternoon: { enabled: true, start: '13:00', end: '20:00' }
      },
      {
        day: 'Quarta',
        available: true,
        morning: { enabled: true, start: '07:00', end: '11:00' },
        afternoon: { enabled: true, start: '13:00', end: '20:00' }
      },
      {
        day: 'Quinta',
        available: true,
        morning: { enabled: true, start: '07:00', end: '11:00' },
        afternoon: { enabled: true, start: '13:00', end: '20:00' }
      },
      {
        day: 'Sexta',
        available: true,
        morning: { enabled: true, start: '07:00', end: '11:00' },
        afternoon: { enabled: true, start: '13:00', end: '20:00' }
      },
      {
        day: 'Sábado',
        available: false,
        morning: { enabled: false, start: '07:00', end: '11:00' },
        afternoon: { enabled: false, start: '13:00', end: '20:00' }
      },
      {
        day: 'Domingo',
        available: false,
        morning: { enabled: false, start: '07:00', end: '11:00' },
        afternoon: { enabled: false, start: '13:00', end: '20:00' }
      }
    ],
    biography: '',
    portfolioImages: [],
    profileImage: userData?.imagemPerfil ? {
      uri: userData.imagemPerfil,
      base64: userData.imagemPerfil,
      type: 'image/jpeg',
      name: 'profile.jpg'
    } : null,
    tiposServico: [],
    tipoServicoSelecionados: {},
    precosServicos: {}
  });

  // Carregar dados profissionais
  const loadProfessionalData = async () => {
    if (!userData?.idUsuario || userData.role !== 'ROLE_PROF') {
      return;
    }

    try {
      const response = await ApiService.get(`/profissional/usuario/${userData.idUsuario}/completo`);
      
      if (response && response.profissional) {
        const { profissional, portfolio, imagens, disponibilidades, tiposServico, precosServicos } = response;
        
        const allTiposServico = await ApiService.get('/tipos-servico');
        
        const tipoServicoSelecionados = {};
        allTiposServico.forEach(tipo => {
          tipoServicoSelecionados[tipo.nome] = tiposServico.includes(tipo.nome);
        });
        
        // Carregar preços dos serviços
        const precosCarregados = {};
        if (precosServicos && typeof precosServicos === 'object') {
          Object.entries(precosServicos).forEach(([tipo, preco]) => {
            // Converter números para string formatada para exibição
            if (typeof preco === 'number') {
              precosCarregados[tipo] = preco.toString().replace('.', ',');
            } else {
              precosCarregados[tipo] = preco?.toString() || '';
            }
          });
        }

        // Transformar especialidades
        const specialties = portfolio?.especialidade ? 
          portfolio.especialidade.split(', ').reduce((acc, style) => {
            acc[style] = true;
            return acc;
          }, {
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
          }) : {
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
          };

        // Transformar horários de trabalho
        const workHours = [
          { day: 'Segunda', available: false, morning: { enabled: false, start: '08:00', end: '11:00' }, afternoon: { enabled: false, start: '13:00', end: '18:00' } },
          { day: 'Terça', available: false, morning: { enabled: false, start: '08:00', end: '11:00' }, afternoon: { enabled: false, start: '13:00', end: '18:00' } },
          { day: 'Quarta', available: false, morning: { enabled: false, start: '08:00', end: '11:00' }, afternoon: { enabled: false, start: '13:00', end: '18:00' } },
          { day: 'Quinta', available: false, morning: { enabled: false, start: '08:00', end: '11:00' }, afternoon: { enabled: false, start: '13:00', end: '18:00' } },
          { day: 'Sexta', available: false, morning: { enabled: false, start: '08:00', end: '11:00' }, afternoon: { enabled: false, start: '13:00', end: '18:00' } },
          { day: 'Sábado', available: false, morning: { enabled: false, start: '08:00', end: '11:00' }, afternoon: { enabled: false, start: '13:00', end: '18:00' } },
          { day: 'Domingo', available: false, morning: { enabled: false, start: '08:00', end: '11:00' }, afternoon: { enabled: false, start: '13:00', end: '18:00' } }
        ];

        if (disponibilidades && Object.keys(disponibilidades).length > 0) {
          Object.entries(disponibilidades).forEach(([day, horarios]) => {
            const dayIndex = workHours.findIndex(wh => wh.day === day);
            
            if (dayIndex >= 0 && Array.isArray(horarios) && horarios.length > 0) {
              workHours[dayIndex].available = true;
              
              horarios.forEach(horario => {
                if (horario && horario.inicio && horario.fim) {
                  const startHour = parseInt(horario.inicio.split(':')[0]);
                  const endHour = parseInt(horario.fim.split(':')[0]);
                  
                  if (endHour <= 12) {
                    workHours[dayIndex].morning.enabled = true;
                    workHours[dayIndex].morning.start = horario.inicio;
                    workHours[dayIndex].morning.end = horario.fim;
                  }
                  else if (startHour >= 12) {
                    workHours[dayIndex].afternoon.enabled = true;
                    workHours[dayIndex].afternoon.start = horario.inicio;
                    workHours[dayIndex].afternoon.end = horario.fim;
                  }
                  else {
                    workHours[dayIndex].morning.enabled = true;
                    workHours[dayIndex].morning.start = horario.inicio;
                    workHours[dayIndex].morning.end = "11:59";
                    
                    workHours[dayIndex].afternoon.enabled = true;
                    workHours[dayIndex].afternoon.start = "12:00";
                    workHours[dayIndex].afternoon.end = horario.fim;
                  }
                }
              });
            }
          });
        }

        setProfessionalFormData(prev => ({
          ...prev,
          experience: portfolio?.experiencia || '1-3 anos',
          specialties,
          socialMedia: {
            instagram: portfolio?.instagram || '',
            tiktok: portfolio?.tiktok || '',
            facebook: portfolio?.facebook || '',
            twitter: portfolio?.twitter || '',
            website: portfolio?.website || ''
          },
          workHours,
          biography: portfolio?.descricao || '',
          portfolioImages: (imagens || []).map(img => ({
            uri: img.imagemBase64 || img.imagem,
            base64: img.imagemBase64 || img.imagem,
            type: 'image/jpeg',
            name: `portfolio_${img.idImagem || Date.now()}.jpg`
          })),
          profileImage: userData?.imagemPerfil ? {
            uri: userData.imagemPerfil,
            base64: userData.imagemPerfil,
            type: 'image/jpeg',
            name: 'profile.jpg'
          } : null,
          tiposServico: allTiposServico,
          tipoServicoSelecionados,
          precosServicos: precosCarregados
        }));
      }
    } catch (error) {
              toastHelper.showError(editProfileMessages.validations.professionalDataError);
    }
  };

  // Atualizar dados profissionais
  const updateProfessionalData = async () => {
    try {
      const disponibilidades = {};
      professionalFormData.workHours.forEach(day => {
        if (day.available) {
          const horariosDia = [];
          if (day.morning.enabled) {
            horariosDia.push({
              inicio: day.morning.start,
              fim: day.morning.end
            });
          }
          if (day.afternoon.enabled) {
            horariosDia.push({
              inicio: day.afternoon.start,
              fim: day.afternoon.end
            });
          }
          if (horariosDia.length > 0) {
            disponibilidades[day.day] = horariosDia;
          }
        }
      });

      const tiposServicoSelecionados = Object.entries(professionalFormData.tipoServicoSelecionados)
        .filter(([_, selected]) => selected)
        .map(([nome]) => nome);

      const especialidades = Object.entries(professionalFormData.specialties)
        .filter(([_, selected]) => selected)
        .map(([name]) => name)
        .join(', ');

      const portfolioData = {
        descricao: professionalFormData.biography,
        especialidade: especialidades,
        experiencia: professionalFormData.experience,
        instagram: professionalFormData.socialMedia.instagram || null,
        tiktok: professionalFormData.socialMedia.tiktok || null,
        facebook: professionalFormData.socialMedia.facebook || null,
        twitter: professionalFormData.socialMedia.twitter || null,
        website: professionalFormData.socialMedia.website || null
      };

      // Preparar preços formatados para o backend
      const precosFormatados = {};
      Object.entries(professionalFormData.precosServicos || {}).forEach(([tipo, preco]) => {
        if (preco) {
          // Converter vírgula para ponto e garantir formato decimal
          const precoLimpo = typeof preco === 'string' ? preco.replace(',', '.') : preco.toString();
          const precoNumerico = parseFloat(precoLimpo);
          if (!isNaN(precoNumerico) && precoNumerico > 0) {
            precosFormatados[tipo] = precoNumerico;
          }
        }
      });

      const requestData = {
        profissional: {},
        portfolio: portfolioData,
        imagens: professionalFormData.portfolioImages.map(img => ({
          imagemBase64: img.base64
        })),
        disponibilidades,
        tiposServico: tiposServicoSelecionados,
        precosServicos: precosFormatados
      };

      await ApiService.put(`/profissional/usuario/${userData.idUsuario}/atualizar-completo-com-imagens`, requestData);
              toastHelper.showSuccess(editProfileMessages.success.profileUpdated);
      return true;
    } catch (error) {
              toastHelper.showError(editProfileMessages.errors.saveProfile);
      return false;
    }
  };

  const handleSpecialtyChange = (specialty) => {
    setProfessionalFormData(prev => ({
      ...prev,
      specialties: {
        ...prev.specialties,
        [specialty]: !prev.specialties[specialty]
      }
    }));
  };
  
  const handleSocialMediaChange = (platform, value) => {
    setProfessionalFormData(prev => ({
      ...prev,
      socialMedia: {
        ...prev.socialMedia,
        [platform]: value
      }
    }));
  };
  
  const handleWorkHourChange = (index, period, field, value) => {
    const newWorkHours = [...professionalFormData.workHours];
    
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
    
    setProfessionalFormData(prev => ({
      ...prev,
      workHours: newWorkHours
    }));
  };
  
  const handleAddPortfolioImage = () => {
    pickImage('portfolio');
  };
  
  const handleRemovePortfolioImage = (index) => {
    const newImages = [...professionalFormData.portfolioImages];
    newImages.splice(index, 1);
    setProfessionalFormData(prev => ({
      ...prev,
      portfolioImages: newImages
    }));
  };
  
  const pickImage = async (imageType, index = null) => {
    try {
      // Configurações diferentes para imagem de perfil vs portfólio
      const imagePickerOptions = {
        mediaTypes: ImagePicker.MediaTypeOptions.Images,
        allowsEditing: true,
        quality: 0.8,
        base64: true
      };

      // Para imagem de perfil, usar aspect ratio circular
      // Para portfólio, formato retangular
      if (imageType === 'profile') {
        imagePickerOptions.aspect = [1, 1];
        imagePickerOptions.allowsMultipleSelection = false;
      } else {
        imagePickerOptions.aspect = [4, 3];
      }

      const result = await ImagePicker.launchImageLibraryAsync(imagePickerOptions);
      if (!result.canceled) {
        const selectedImage = result.assets[0];
        
        const validMimeTypes = ['image/jpeg', 'image/png'];
        const validExtensions = ['.png', '.jpg', '.jpeg', '.jfif'];
        
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
        
        const maxSizeInMB = imageType === 'portfolio' ? 10 : 5;
        const maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        
        if (selectedImage.fileSize && selectedImage.fileSize > maxSizeInBytes) {
          if (imageType === 'portfolio') {
            toastHelper.showError(editProfileMessages.imageUploadErrors.portfolioFileTooLarge);
          } else {
            toastHelper.showError(editProfileMessages.imageUploadErrors.fileTooLarge);
          }
          return;
        }
        
        const base64String = selectedImage.base64;
        const base64SizeInBytes = (base64String.length * 3) / 4;
        
        if (base64SizeInBytes > maxSizeInBytes) {
          if (imageType === 'portfolio') {
            toastHelper.showError(editProfileMessages.imageUploadErrors.portfolioProcessedImageTooLarge);
          } else {
            toastHelper.showError(editProfileMessages.imageUploadErrors.processedImageTooLarge);
          }
          return;
        }
        
        const imageFormat = selectedImage.mimeType === 'image/png' ? 'png' : 'jpeg';
        const mimeType = selectedImage.mimeType === 'image/png' ? 'image/png' : 'image/jpeg';
        
        const imageUri = selectedImage.uri;
        const imageBase64 = `data:${mimeType};base64,${selectedImage.base64}`;
        
        if (imageType === 'portfolio') {
          setProfessionalFormData(prev => ({
            ...prev,
            portfolioImages: [
              ...prev.portfolioImages,
              {
                uri: imageUri,
                base64: imageBase64,
                type: mimeType,
                name: `portfolio_${prev.portfolioImages.length}.${imageFormat === 'png' ? 'png' : 'jpg'}`
              }
            ]
          }));
        } else if (imageType === 'profile') {
          setProfessionalFormData(prev => ({
            ...prev,
            profileImage: {
              uri: imageUri,
              base64: imageBase64,
              type: mimeType,
              name: `profile.${imageFormat === 'png' ? 'png' : 'jpg'}`
            }
          }));
        }
      }
    } catch (error) {
              toastHelper.showError(editProfileMessages.imageUploadErrors.selectionFailed);
    }
  };
  
  const setBiography = (value) => {
    setProfessionalFormData(prev => ({
      ...prev,
      biography: value
    }));
  };

  const handleTipoServicoChange = (tipoNome) => {
    const isSelected = !professionalFormData.tipoServicoSelecionados[tipoNome];
    
    setProfessionalFormData(prev => ({
      ...prev,
      tipoServicoSelecionados: {
        ...prev.tipoServicoSelecionados,
        [tipoNome]: isSelected
      },
      // Se o serviço foi desmarcado, remove o preço
      precosServicos: isSelected ? prev.precosServicos : {
        ...prev.precosServicos,
        [tipoNome]: undefined
      }
    }));
  };
  
  const handlePrecoServicoChange = (tipoNome, valor) => {
    const valorLimpo = valor.replace(/[^\d,.]/, '');
    
    setProfessionalFormData(prev => ({
      ...prev,
      precosServicos: {
        ...prev.precosServicos,
        [tipoNome]: valorLimpo
      }
    }));
  };

  // Carregar dados quando userData mudar
  useEffect(() => {
    if (userData?.role === 'ROLE_PROF') {
      loadProfessionalData();
    }
    
    // Atualizar imagem de perfil quando userData mudar
    if (userData?.imagemPerfil) {
      setProfessionalFormData(prev => ({
        ...prev,
        profileImage: {
          uri: userData.imagemPerfil,
          base64: userData.imagemPerfil,
          type: 'image/jpeg',
          name: 'profile.jpg'
        }
      }));
    }
  }, [userData]);

  return {
    professionalFormData,
    setProfessionalFormData,
    loadProfessionalData,
    updateProfessionalData,
    handleSpecialtyChange,
    handleSocialMediaChange,
    handleWorkHourChange,
    handleAddPortfolioImage,
    handleRemovePortfolioImage,
    pickImage,
    setBiography,
    handleTipoServicoChange,
    handlePrecoServicoChange
  };
};

export default useProfessionalData; 