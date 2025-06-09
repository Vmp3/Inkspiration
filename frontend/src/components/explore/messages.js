export const exploreMessages = {
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
    professionalsLoaded: 'Profissionais carregados com sucesso'
  },

  // Mensagens informativas
  info: {
    noResults: 'Nenhum resultado encontrado',
    noResultsDescription: 'Tente ajustar seus filtros ou termos de busca para encontrar mais resultados.',
    searchHint: 'Digite o nome do artista ou especialidade',
    locationHint: 'Digite sua cidade ou região',
    clearFilters: 'Limpar todos os filtros',
    filterCount: (count) => count === 1 ? '1 filtro ativo' : `${count} filtros ativos`,
    resultCount: (count) => count === 1 ? '1 resultado encontrado' : `${count} resultados encontrados`
  },

  // Mensagens de filtros
  filters: {
    rating: (rating) => `Avaliação: ${rating}+ estrelas`,
    distance: (distance) => `Distância: até ${distance}km`,
    specialty: (specialty) => `Especialidade: ${specialty}`,
    cleared: 'Filtros removidos',
    applied: 'Filtros aplicados'
  },

  // Mensagens de ordenação
  sorting: {
    relevance: 'Ordenado por relevância',
    rating: 'Ordenado por melhor avaliação',
    recent: 'Ordenado por mais recente'
  }
}; 