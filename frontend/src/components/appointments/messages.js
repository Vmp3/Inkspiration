export const appointmentsMessages = {
  // Mensagens de carregamento
  loading: {
    loadingAppointments: 'Carregando agendamentos...',
    canceling: 'Cancelando agendamento...',
    updating: 'Atualizando agendamento...',
    exporting: 'Exportando dados...'
  },

  // Mensagens de erro
  errors: {
    loadAppointments: 'Erro ao carregar seus agendamentos',
    cancelAppointment: 'Erro ao cancelar agendamento',
    updateAppointment: 'Erro ao atualizar agendamento',
    exportFailed: 'Erro ao exportar dados',
    networkError: 'Erro de conexão. Verifique sua internet.',
    genericError: 'Ocorreu um erro inesperado',
    editTimeLimit: 'A edição só é permitida com no mínimo 3 dias de antecedência.',
    cancelTimeLimit: 'O cancelamento só é permitido com no mínimo 3 dias de antecedência.'
  },

  // Mensagens de sucesso
  success: {
    appointmentCanceled: 'Agendamento cancelado com sucesso!',
    appointmentUpdated: 'Agendamento atualizado com sucesso!',
    exportSuccess: 'Dados exportados com sucesso!',
    emailSent: 'Email enviado com sucesso!'
  },

  // Mensagens informativas
  info: {
    noAppointments: 'Nenhum agendamento encontrado',
    noAppointmentsDescription: 'Você ainda não possui agendamentos. Explore nossos profissionais e faça seu primeiro agendamento!',
    confirmCancel: 'Tem certeza que deseja cancelar este agendamento?',
    selectDateRange: 'Selecione o período para exportar',
    fillEmail: 'Digite seu email para receber o arquivo'
  },

  // Mensagens de filtros
  filters: {
    all: 'Todos',
    scheduled: 'Agendados',
    completed: 'Concluídos', 
    canceled: 'Cancelados',
    filterApplied: (filter) => `Filtro "${filter}" aplicado`,
    filterCleared: 'Filtros removidos'
  }
}; 