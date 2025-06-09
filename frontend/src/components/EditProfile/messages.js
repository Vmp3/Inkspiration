export const editProfileMessages = {
  // Mensagens de carregamento
  loading: {
    profile: 'Carregando dados do perfil...',
    saving: 'Salvando alterações...',
    updating: 'Atualizando perfil...',
    uploadingImages: 'Enviando imagens...',
    searchingCep: 'Buscando CEP...'
  },

  // Mensagens de erro
  errors: {
    loadProfile: 'Erro ao carregar dados do perfil',
    saveProfile: 'Erro ao salvar alterações do perfil',
    invalidName: 'Nome inválido',
    invalidSurname: 'Sobrenome inválido',
    invalidFullName: 'Nome e sobrenome não podem ultrapassar 255 caracteres',
    invalidEmail: 'Email inválido',
    invalidPhone: 'Telefone inválido',
    invalidPassword: 'A senha deve ter pelo menos 6 caracteres',
    passwordMismatch: 'As senhas não coincidem',
    requiredCurrentPassword: 'Senha atual é obrigatória para alterar a senha',
    requiredConfirmPassword: 'Confirmação de senha é obrigatória',
    invalidCep: 'CEP inválido',
    cepNotFound: 'CEP não encontrado',
    requiredField: 'Este campo é obrigatório',
    uploadImage: 'Erro ao enviar imagem',
    selectImage: 'Erro ao selecionar imagem',
    twoFactorToggle: 'Erro ao alterar configuração de autenticação de dois fatores',
    twoFactorStatus: 'Erro ao carregar status da autenticação de dois fatores',
    generic: 'Ocorreu um erro inesperado'
  },

  // Mensagens de sucesso
  success: {
    profileUpdated: 'Perfil atualizado com sucesso!',
    passwordChanged: 'Senha alterada com sucesso!',
    imageUploaded: 'Imagem enviada com sucesso',
    cepFound: 'CEP encontrado e endereço preenchido',
    twoFactorEnabled: 'Autenticação de dois fatores ativada com sucesso',
    twoFactorDisabled: 'Autenticação de dois fatores desativada com sucesso'
  },

  // Mensagens informativas
  info: {
    fillRequired: 'Preencha todos os campos obrigatórios',
    navigatingToNext: 'Avançando para próxima etapa...',
    validatingData: 'Validando dados...',
    twoFactorInfo: 'A autenticação de dois fatores adiciona uma camada extra de segurança',
    profileImageInfo: 'Selecione uma foto para seu perfil',
    portfolioImageInfo: 'Adicione imagens ao seu portfólio'
  },

  // Mensagens de confirmação
  confirmations: {
    saveChanges: 'Tem certeza que deseja salvar as alterações?',
    discardChanges: 'Tem certeza que deseja descartar as alterações?',
    deleteImage: 'Tem certeza que deseja remover esta imagem?',
    enableTwoFactor: 'Tem certeza que deseja ativar a autenticação de dois fatores?',
    disableTwoFactor: 'Tem certeza que deseja desativar a autenticação de dois fatores?'
  },

  // Validações
  validations: {
    selectSpecialty: 'Selecione pelo menos uma especialidade',
    minBiography: 'A biografia deve conter pelo menos 20 caracteres',
    selectWorkHours: 'Defina pelo menos um horário de disponibilidade',
    invalidUrl: 'URL inválida',
    invalidSocialMedia: 'Link de rede social inválido'
  }
}; 