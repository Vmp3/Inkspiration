export const authMessages = {
  // Mensagens de carregamento
  loading: {
    login: 'Fazendo login...',
    register: 'Criando conta...',
    forgotPassword: 'Enviando código...',
    resetPassword: 'Redefinindo senha...',
    resendCode: 'Reenviando código...',
    verifying: 'Verificando dados...'
  },

  // Mensagens de erro - Login
  loginErrors: {
    invalidCredentials: 'CPF ou senha incorretos',
    requiredFields: 'Por favor, preencha todos os campos',
    invalidCpf: 'CPF inválido',
    loginFailed: 'Falha ao fazer login. Verifique suas credenciais.',
    serverError: 'Ocorreu um erro ao fazer login. Tente novamente.',
    tokenError: 'Erro ao processar autenticação',
    networkError: 'Erro de conexão. Verifique sua internet.'
  },

  // Mensagens de erro - Register
  registerErrors: {
    invalidName: 'Nome inválido',
    invalidSurname: 'Sobrenome inválido',
    invalidFullName: 'Nome e sobrenome não podem ultrapassar 255 caracteres',
    invalidCpf: 'CPF inválido',
    invalidEmail: 'Email inválido',
    invalidPhone: 'Telefone inválido',
    invalidBirthDate: 'Você deve ter pelo menos 18 anos para se registrar',
    invalidPassword: 'A senha deve ter pelo menos 6 caracteres',
    passwordMismatch: 'As senhas não coincidem',
    requiredTerms: 'Você precisa aceitar os termos de uso para continuar',
    requiredFields: 'Preencha todos os campos obrigatórios',
    serverError: 'Ocorreu um erro ao cadastrar. Tente novamente.',
    cpfExists: 'CPF já cadastrado',
    emailExists: 'Email já cadastrado'
  },

  // Mensagens de erro - Forgot Password
  forgotPasswordErrors: {
    requiredCpf: 'Por favor, informe seu CPF',
    invalidCpf: 'CPF inválido',
    cpfNotFound: 'CPF não encontrado em nosso sistema',
    tooManyAttempts: 'Muitas tentativas. Aguarde 15 minutos para tentar novamente',
    emailError: 'Erro ao enviar email. Verifique sua conexão e tente novamente',
    serverError: 'Erro ao processar solicitação',
    networkError: 'Erro de conexão. Tente novamente.'
  },

  // Mensagens de erro - Reset Password
  resetPasswordErrors: {
    requiredFields: 'Preencha todos os campos',
    invalidCode: 'Código deve ter 6 dígitos',
    invalidPassword: 'A senha deve ter pelo menos 6 caracteres',
    passwordMismatch: 'As senhas não coincidem',
    invalidOrExpiredCode: 'Código inválido ou expirado. Solicite um novo código',
    serverError: 'Erro ao redefinir senha',
    networkError: 'Erro de conexão. Tente novamente.'
  },

  // Mensagens de sucesso
  success: {
    loginSuccess: 'Login realizado com sucesso!',
    registerSuccess: 'Cadastro realizado com sucesso!',
    forgotPasswordSuccess: 'Código de recuperação enviado para seu email!',
    resetPasswordSuccess: 'Senha redefinida com sucesso!',
    codeResent: 'Novo código enviado para seu email!'
  },

  // Mensagens informativas
  info: {
    fillRequired: 'Preencha todos os campos obrigatórios',
    checkSpam: 'Verifique também a pasta de spam',
    codeExpires: 'O código expira em 15 minutos',
    passwordRequirements: 'A senha deve ter pelo menos 6 caracteres',
    termsRequired: 'É necessário aceitar os termos de uso'
  }
}; 