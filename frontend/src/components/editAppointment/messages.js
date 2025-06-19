export const editAppointmentMessages = {
  // Mensagens de carregamento
  loading: {
    loadingAppointment: 'Carregando dados do agendamento...',
    loadingSchedules: 'Carregando horários disponíveis...',
    updating: 'Atualizando agendamento...'
  },

  // Mensagens de erro
  errors: {
    loadAppointment: 'Erro ao carregar dados do agendamento',
    loadSchedules: 'Erro ao carregar horários disponíveis',
    editTimeLimit: 'O agendamento só pode ser editado com no mínimo 3 dias de antecedência',
    requiredFields: 'Por favor, preencha todos os campos obrigatórios',
    updateFailed: 'Erro ao atualizar agendamento',
    networkError: 'Erro de conexão. Verifique sua internet.',
    genericError: 'Ocorreu um erro inesperado'
  },

  // Mensagens de sucesso
  success: {
    appointmentUpdated: 'Agendamento atualizado com sucesso!'
  },

  // Mensagens informativas
  info: {
    selectDateTime: 'Selecione nova data e horário',
    noSchedulesAvailable: 'Nenhum horário disponível para esta data',
    confirmUpdate: 'Tem certeza que deseja atualizar este agendamento?'
  }
}; 