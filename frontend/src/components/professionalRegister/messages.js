export const professionalRegisterMessages = {
  // Mensagens de carregamento
  loading: {
    uploadingImages: 'Enviando imagens...',
    creatingProfessional: 'Cadastrando profissional...',
    processingData: 'Processando dados...'
  },

  // Mensagens de erro
  errors: {
    imageSelectionFailed: 'Falha ao selecionar imagem. Tente novamente.',
    portfolioFieldsRequired: 'Preencha todos os campos obrigatórios do portfólio',
    serviceTypeRequired: 'Selecione pelo menos um tipo de serviço',
    biographyMinLength: 'A biografia deve conter pelo menos 20 caracteres',
    scheduleRequired: 'Defina pelo menos um horário de disponibilidade',
    userNotIdentified: 'Não foi possível identificar seu usuário. Faça login novamente.',
    addressRequired: 'Seu cadastro não possui um endereço. Atualize seu perfil antes de continuar.',
    genericError: 'Ocorreu um erro ao processar sua solicitação. Tente novamente.',
    registrationFailed: 'Ocorreu um erro ao tentar cadastrar. Tente novamente.',
    basicInfoRequired: 'Complete todas as informações básicas obrigatórias',
    workScheduleRequired: 'Selecione pelo menos um horário de trabalho',
    invalidWorkHours: 'Verifique se os horários estão corretos',
    socialMediaTooLong: 'Redes sociais devem ter no máximo 50 caracteres',
    websiteTooLong: 'Website deve ter no máximo 255 caracteres'
  },

  // Mensagens de sucesso
  success: {
    professionalRegistered: 'Cadastro de profissional realizado com sucesso!'
  },

  // Mensagens de aviso
  warnings: {
    imageUploadPartialFailure: 'Profissional cadastrado, mas houve um problema ao enviar as imagens.',
    loginAgainForPermissions: 'Por favor, faça login novamente para atualizar suas permissões',
    completeBasicInfoFirst: 'Complete todas as informações básicas primeiro',
    selectWorkScheduleFirst: 'Selecione pelo menos um horário de trabalho primeiro'
  },

  // Mensagens informativas
  info: {
    uploadingImages: 'Enviando imagens...'
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