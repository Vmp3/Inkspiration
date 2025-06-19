export const attendancesMessages = {
  // Mensagens de carregamento
  loading: {
    loadingAttendances: 'Carregando atendimentos...',
    loadingFutureAttendances: 'Carregando atendimentos futuros...',
    loadingPastAttendances: 'Carregando atendimentos passados...',
    canceling: 'Cancelando agendamento...'
  },

  // Mensagens de erro
  errors: {
    loadFutureAttendances: 'Erro ao carregar atendimentos futuros',
    loadPastAttendances: 'Erro ao carregar atendimentos passados',
    identifyAppointment: 'Erro ao identificar o agendamento',
    onlyScheduledCanCancel: 'Apenas agendamentos com status "Agendado" podem ser cancelados',
    cancelAppointment: 'Erro ao cancelar agendamento',
    cancelAppointmentGeneric: 'Erro ao cancelar agendamento. Tente novamente.',
    networkError: 'Erro de conexão. Verifique sua internet.',
    genericError: 'Ocorreu um erro inesperado'
  },

  // Mensagens de sucesso
  success: {
    appointmentCanceled: 'Agendamento cancelado com sucesso'
  },

  // Mensagens informativas
  info: {
    noFutureAttendances: 'Você não possui atendimentos futuros.',
    noPastAttendances: 'Você não possui histórico de atendimentos.',
    noAttendances: 'Nenhum atendimento encontrado',
    confirmCancel: 'Tem certeza que deseja cancelar este agendamento?'
  }
}; 