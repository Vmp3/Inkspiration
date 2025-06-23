export const bookingMessages = {
  // Mensagens de carregamento
  loading: {
    loadingProfessional: 'Carregando dados do profissional...',
    loadingSchedules: 'Carregando horários disponíveis...',
    creatingBooking: 'Criando agendamento...'
  },

  // Mensagens de erro
  errors: {
    loadProfessional: 'Erro ao carregar dados do profissional',
    loadSchedules: 'Erro ao carregar horários disponíveis',
    loginRequired: 'Você precisa estar logado para fazer um agendamento',
    requiredFields: 'Por favor, preencha todos os campos obrigatórios',
    bookingFailed: 'Erro ao criar agendamento. Tente novamente.',
    networkError: 'Erro de conexão. Verifique sua internet.',
    genericError: 'Erro ao criar agendamento. Tente novamente.'
  },

  // Mensagens de sucesso
  success: {
    bookingCreated: 'Agendamento realizado com sucesso!'
  },

  // Mensagens informativas
  info: {
    selectDateTime: 'Selecione data e horário',
    noSchedulesAvailable: 'Nenhum horário disponível para esta data',
    fillDescription: 'Descreva detalhes sobre sua tatuagem'
  }
}; 