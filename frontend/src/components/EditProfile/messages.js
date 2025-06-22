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
    invalidPassword: 'A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial',
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

  // Mensagens de aviso para navegação
  warnings: {
    completePersonalDataFirst: 'Complete todos os dados pessoais primeiro',
    completeAddressDataFirst: 'Complete todos os dados de endereço primeiro',
    completeBasicInfoFirst: 'Complete todas as informações básicas primeiro',
    selectWorkScheduleFirst: 'Configure os horários de trabalho primeiro',
    completePortfolioFirst: 'Complete o portfólio primeiro'
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
    invalidSocialMedia: 'Link de rede social inválido',
    nameRequired: 'Nome é obrigatório',
    nameInvalid: 'Nome inválido',
    surnameRequired: 'Sobrenome é obrigatório', 
    surnameInvalid: 'Sobrenome inválido',
    nameTooBig: 'Nome e sobrenome não podem ultrapassar 255 caracteres',
    emailRequired: 'Email é obrigatório',
    emailInvalid: 'Email inválido',
    phoneRequired: 'Telefone é obrigatório',
    phoneInvalid: 'Telefone inválido',
    cepRequired: 'CEP é obrigatório',
    streetRequired: 'Rua é obrigatória',
    numberRequired: 'Número é obrigatório',
    districtRequired: 'Bairro é obrigatório',
    cityRequired: 'Cidade é obrigatória',
    stateRequired: 'Estado é obrigatório',
    currentPasswordRequired: 'Senha atual é obrigatória para alterar a senha',
    newPasswordRequired: 'Nova senha é obrigatória',
    confirmPasswordRequired: 'Confirmação de senha é obrigatória',
    passwordMinLength: 'A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, um número e um caractere especial',
    passwordsDontMatch: 'As senhas não coincidem',
    bioRequired: 'Bio é obrigatória para profissionais',
    scheduleRequired: 'Defina pelo menos um horário de disponibilidade',
    bioMinLength: 'A biografia deve conter pelo menos 20 caracteres',
    imageSelectionFailed: 'Falha ao selecionar imagem. Tente novamente.',
    fixInvalidSchedules: 'Corrija os horários inválidos antes de continuar.',
    professionalDataError: 'Erro ao obter informações profissionais',
    passwordIncorrect: 'Senha atual incorreta',
    socialMediaTooLong: 'Redes sociais devem ter no máximo 50 caracteres',
    websiteTooLong: 'Website deve ter no máximo 255 caracteres'
  },

  // Mensagens de erro - Image Upload
  imageUploadErrors: {
    fileTooLarge: 'Imagem muito grande. Tamanho máximo permitido: 5MB',
    processedImageTooLarge: 'Imagem muito grande após processamento. Tamanho máximo permitido: 5MB',
    portfolioFileTooLarge: 'Imagem do portfólio muito grande. Tamanho máximo permitido: 10MB',
    portfolioProcessedImageTooLarge: 'Imagem do portfólio muito grande após processamento. Tamanho máximo permitido: 10MB',
    invalidFormat: 'Formato de imagem inválido. Apenas PNG, JPG, JPEG e JFIF são permitidos',
    selectionFailed: 'Erro ao selecionar imagem',
    uploadFailed: 'Erro ao enviar imagem'
  },

  // Mensagens de erro - Preço dos Serviços
  priceErrors: {
    maxValueExceeded: 'Valor máximo permitido é R$ 100.000,00'
  }
}; 