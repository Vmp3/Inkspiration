export const homeMessages = {
  // Mensagens de carregamento
  loading: {
    professionals: 'Carregando profissionais...',
    search: 'Buscando resultados...',
    filters: 'Aplicando filtros...'
  },

  // Mensagens de erro
  errors: {
    loadProfessionals: 'Erro ao carregar profissionais',
    searchFailed: 'Erro ao realizar busca',
    filtersFailed: 'Erro ao aplicar filtros',
    noConnection: 'Erro de conexão. Verifique sua internet.',
    generic: 'Ocorreu um erro inesperado'
  },

  // Mensagens de sucesso
  success: {
    searchCompleted: 'Busca realizada com sucesso',
    filtersApplied: 'Filtros aplicados com sucesso',
    professionalsLoaded: 'Profissionais carregados com sucesso',
    navigatedToExplore: 'Redirecionando para explorar todos os artistas...'
  },

  // Mensagens informativas
  info: {
    noResults: 'Nenhum resultado encontrado',
    noResultsDescription: 'Tente ajustar seus filtros ou termos de busca para encontrar mais resultados.',
    searchHint: 'Digite o nome do artista ou especialidade',
    locationHint: 'Digite sua cidade ou região',
    welcome: 'Bem-vindo ao Inkspiration!',
    featuredArtists: 'Artistas em destaque na sua região',
    seeAll: 'Ver todos os profissionais disponíveis'
  },

  // Mensagens de filtros
  filters: {
    rating: (rating) => `Avaliação: ${rating}+ estrelas`,
    specialty: (specialty) => `Especialidade: ${specialty}`,
    cleared: 'Filtros removidos',
    applied: 'Filtros aplicados'
  }
}; 