export const professionalMessages = {
  // Mensagens de carregamento
  loading: {
    profile: 'Carregando perfil...',
    register: 'Finalizando cadastro...',
    uploadingImages: 'Enviando imagens...',
    generatingQR: 'Gerando código QR...',
    verifyingCode: 'Verificando código...',
    sendingRecovery: 'Enviando código de recuperação...'
  },

  // Mensagens de erro - Profile
  profileErrors: {
    noId: 'ID do profissional não fornecido',
    loadProfile: 'Não foi possível carregar o perfil do profissional',
    notFound: 'Profissional não encontrado',
    generic: 'Ocorreu um erro inesperado'
  },

  // Mensagens de erro - Register
  registerErrors: {
    noSpecialties: 'Selecione pelo menos uma especialidade',
    minBiography: 'A biografia deve conter pelo menos 20 caracteres',
    noWorkHours: 'Defina pelo menos um horário de disponibilidade',
    noUser: 'Não foi possível identificar seu usuário. Faça login novamente.',
    noAddress: 'Seu cadastro não possui um endereço. Atualize seu perfil antes de continuar.',
    uploadImages: 'Profissional cadastrado, mas houve um problema ao enviar as imagens.',
    serverError: 'Ocorreu um erro ao tentar cadastrar. Tente novamente.',
    imageSelection: 'Falha ao selecionar imagem. Tente novamente.',
    updateToken: 'Por favor, faça login novamente para atualizar suas permissões'
  },

  // Mensagens de erro - Two Factor
  twoFactorErrors: {
    generateQR: 'Erro ao gerar código QR',
    verifyCode: 'Código inválido ou erro de verificação',
    invalidCode: 'Código deve conter 6 dígitos',
    sendRecovery: 'Erro ao enviar código de recuperação',
    verifyRecovery: 'Código de recuperação inválido',
    generic: 'Erro na autenticação de dois fatores'
  },

  // Mensagens de sucesso
  success: {
    profileLoaded: 'Perfil carregado com sucesso',
    registerSuccess: 'Cadastro de profissional realizado com sucesso!',
    imagesUploaded: 'Imagens enviadas com sucesso',
    qrGenerated: 'Código QR gerado com sucesso',
    codeVerified: 'Código verificado com sucesso',
    recoveryCodeSent: 'Código de recuperação enviado para seu email',
    twoFactorEnabled: 'Autenticação de dois fatores ativada com sucesso'
  },

  // Mensagens informativas
  info: {
    uploadingImages: 'Enviando imagens...',
    noImages: 'Nenhuma imagem no portfólio',
    noSocialMedia: 'Nenhuma rede social cadastrada',
    scanQR: 'Escaneie o código QR com seu aplicativo autenticador',
    enterCode: 'Digite o código de 6 dígitos do seu aplicativo',
    qrInstructions: 'Use um aplicativo como Google Authenticator ou Authy para escanear o código',
    recoveryInstructions: 'Enviamos um código para seu email. Digite-o abaixo para recuperar o acesso.',
    stepProgress: (current, total) => `Passo ${current} de ${total}`
  },

  // Validações
  validations: {
    minBiography: 'A biografia deve conter pelo menos 20 caracteres',
    selectWorkHours: 'Defina pelo menos um horário de disponibilidade',
    invalidSocialMedia: 'Link de rede social inválido'
  }
}; 