import toastHelper from '../../../utils/toastHelper';
import * as formatters from '../../../utils/formatters';

export const useFormValidation = () => {
  const validatePersonalTab = (formData) => {
    if (!formData.nome) {
      toastHelper.showError('Nome é obrigatório');
      return false;
    }
    
    if (!formatters.validateFirstName(formData.nome)) {
      toastHelper.showError('Nome inválido');
      return false;
    }
    
    if (!formData.sobrenome) {
      toastHelper.showError('Sobrenome é obrigatório');
      return false;
    }
    
    if (!formatters.validateSurname(formData.sobrenome)) {
      toastHelper.showError('Sobrenome inválido');
      return false;
    }
    
    if (!formatters.validateFullNameLength(formData.nome, formData.sobrenome)) {
      toastHelper.showError('Nome e sobrenome não podem ultrapassar 255 caracteres');
      return false;
    }
    
    if (!formData.email) {
      toastHelper.showError('Email é obrigatório');
      return false;
    }

    if (!formatters.validateEmail(formData.email)) {
      toastHelper.showError('Email inválido');
      return false;
    }
    
    if (!formData.telefone) {
      toastHelper.showError('Telefone é obrigatório');
      return false;
    }

    if (!formatters.validatePhone(formData.telefone)) {
      toastHelper.showError('Telefone inválido');
      return false;
    }
    
    return true;
  };

  const validateAddressTab = (formData) => {
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

  const validateSecurityTab = (formData) => {
    // Se não está alterando a senha, retorna true
    if (!formData.senhaAtual && !formData.novaSenha && !formData.confirmarSenha) {
      return true;
    }
    
    // Se está alterando a senha parcialmente
    if ((formData.senhaAtual || formData.novaSenha || formData.confirmarSenha) && 
        !(formData.senhaAtual && formData.novaSenha && formData.confirmarSenha)) {
      
      if (!formData.senhaAtual) {
        toastHelper.showError('Senha atual é obrigatória para alterar a senha');
        return false;
      }
      
      if (!formData.novaSenha) {
        toastHelper.showError('Nova senha é obrigatória');
        return false;
      }
      
      if (!formData.confirmarSenha) {
        toastHelper.showError('Confirmação de senha é obrigatória');
        return false;
      }
    }
    
    if (formData.senhaAtual && formData.novaSenha && formData.confirmarSenha) {
      if (formData.novaSenha.length < 6) {
        toastHelper.showError('A senha deve ter pelo menos 6 caracteres');
        return false;
      }
      
      if (formData.novaSenha !== formData.confirmarSenha) {
        toastHelper.showError('As senhas não coincidem');
        return false;
      }
    }
    
    return true;
  };

  const validateProfessionalTab = (formData, isArtist) => {
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
  
  const validateBasicInfoTab = (professionalFormData) => {
    const selectedSpecialties = Object.keys(professionalFormData.specialties).filter(key => professionalFormData.specialties[key]);
    if (selectedSpecialties.length === 0) {
      toastHelper.showError('Selecione pelo menos uma especialidade');
      return false;
    }
    return true;
  };
  
  const validateWorkHoursTab = (professionalFormData) => {
    const hasWorkHours = professionalFormData.workHours.some(day => 
      day.available && (day.morning.enabled || day.afternoon.enabled)
    );
    if (!hasWorkHours) {
      toastHelper.showError('Defina pelo menos um horário de disponibilidade');
      return false;
    }
    return true;
  };
  
  const validatePortfolioTab = (professionalFormData) => {
    if (!professionalFormData.biography || professionalFormData.biography.trim().length < 20) {
      toastHelper.showError('A biografia deve conter pelo menos 20 caracteres');
      return false;
    }
    return true;
  };

  return {
    validatePersonalTab,
    validateAddressTab,
    validateSecurityTab,
    validateProfessionalTab,
    validateBasicInfoTab,
    validateWorkHoursTab,
    validatePortfolioTab
  };
};

export default useFormValidation; 