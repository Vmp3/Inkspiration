import { useState } from 'react';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../../../context/AuthContext';
import ApiService from '../../../services/ApiService';
import toastHelper from '../../../utils/toastHelper';
import { editProfileMessages } from '../messages';

const useProfileUpdate = (isArtist, updateProfessionalData) => {
  const [isLoading, setIsLoading] = useState(false);
  const navigation = useNavigation();
  const { updateUserData, userData } = useAuth();

  const handleUpdateProfile = async (formData, validateCurrentTab, professionalFormData) => {
    if (!validateCurrentTab()) return;

    try {
      setIsLoading(true);

      if (!userData?.idUsuario) {
        toastHelper.showError(editProfileMessages.errors.loadProfile);
        return;
      }

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
      const updateData = {
        nome: `${formData.nome} ${formData.sobrenome}`.trim(),
        email: formData.email,
        telefone: formData.telefone,
        cpf: formData.cpf.replace(/\D/g, ''),
        dataNascimento: formData.dataNascimento,
        endereco: endereco,
        senha: 'SENHA_NAO_ALTERADA',
        manterSenhaAtual: true
      };

      // Adicionar imagem de perfil se existir
      if (professionalFormData && professionalFormData.profileImage && professionalFormData.profileImage.base64) {
        updateData.imagemPerfil = professionalFormData.profileImage.base64;
      }

      if (isArtist) {
        updateData.especialidades = formData.especialidades || [];
        updateData.bio = formData.bio || '';
        updateData.experiencia = formData.experiencia || '';
        updateData.redesSociais = formData.redesSociais || {};
      }

      // Se estiver mudando a senha
      if (formData.senhaAtual && formData.novaSenha) {
        updateData.senha = formData.novaSenha;
        updateData.senhaAtual = formData.senhaAtual;
        delete updateData.manterSenhaAtual;
      }

      await ApiService.put(`/usuario/atualizar/${userData.idUsuario}`, updateData);

      // Se for profissional, atualizar também os dados profissionais
      if (isArtist && updateProfessionalData) {
        await updateProfessionalData();
      }

      await updateUserData();
      
      // Show success message
      toastHelper.showSuccess(editProfileMessages.success.profileUpdated);
      
      // Redirecionar para a tela inicial
      navigation.navigate('Home');
    } catch (error) {
      // console.error('Erro ao atualizar perfil:', error);
      // Novo tratamento para mensagem do backend
      if (error.response && error.response.data && typeof error.response.data.error === 'string') {
        const msg = error.response.data.error;
        if (msg.includes('Senha atual incorreta')) {
          toastHelper.showError(editProfileMessages.validations.passwordIncorrect);
          return;
        }
        toastHelper.showError(msg);
        return;
      }
      if (error.message && error.message.includes('Senha atual incorreta')) {
        toastHelper.showError(editProfileMessages.validations.passwordIncorrect);
      } else {
        toastHelper.showError(editProfileMessages.errors.saveProfile);
      }
    } finally {
      setIsLoading(false);
    }
  };

  return {
    isLoading,
    handleUpdateProfile
  };
};

export default useProfileUpdate; 