export const adminMessages = {
  // Mensagens de carregamento
  loading: {
    users: 'Carregando usuários...',
    action: 'Processando ação...',
    search: 'Buscando usuários...'
  },

  // Mensagens de erro
  errors: {
    loadUsers: 'Erro ao carregar usuários',
    unauthorizedAccess: 'Acesso não autorizado',
    userAction: 'Erro ao executar ação',
    deletePortfolio: 'Erro ao excluir portfólio',
    toggleUserStatus: 'Erro ao alterar status do usuário',
    searchUsers: 'Erro ao buscar usuários',
    generic: 'Ocorreu um erro inesperado'
  },

  // Mensagens de sucesso
  success: {
    userActivated: (userName) => `Usuário ${userName} foi reativado com sucesso`,
    userDeactivated: (userName) => `Usuário ${userName} foi desativado com sucesso`,
    portfolioDeleted: (userName) => `Portfólio do usuário ${userName} foi excluído com sucesso`,
    searchCompleted: 'Busca realizada com sucesso'
  },

  // Mensagens informativas
  info: {
    noUsers: 'Nenhum usuário encontrado',
    noUsersDescription: 'Tente ajustar os termos de busca ou verifique se há usuários cadastrados.',
    noPortfolio: (userName) => `Usuário ${userName} não possui portfólio para excluir`,
    adminRestriction: 'Não é possível realizar ações em contas de administrador',
    refreshing: 'Atualizando lista de usuários...'
  },

  // Mensagens de confirmação
  confirmations: {
    activateUser: (userName) => `Tem certeza que deseja ativar o usuário ${userName}? O usuário poderá acessar o sistema novamente.`,
    deactivateUser: (userName) => `Tem certeza que deseja desativar o usuário ${userName}? O usuário não poderá mais acessar o sistema.`,
    deletePortfolio: (userName) => `Tem certeza que deseja excluir o portfólio do usuário ${userName}? Esta ação não pode ser desfeita.`
  }
}; 