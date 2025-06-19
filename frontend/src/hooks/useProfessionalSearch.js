import { useState, useEffect, useRef, useCallback, useMemo } from 'react';
import ProfessionalService from '../services/ProfessionalService';
import toastHelper from '../utils/toastHelper';

/**
 * Hook personalizado para gerenciar a busca de profissionais
 * @param {Object} options - Opções de configuração
 * @param {number} options.initialPage - Página inicial (padrão: 0)
 * @param {string} options.initialSortBy - Ordenação inicial (padrão: 'melhorAvaliacao')
 * @param {boolean} options.limitResults - Se deve limitar os resultados (padrão: false)
 * @param {number} options.resultLimit - Limite de resultados quando limitResults=true (padrão: 6)
 * @returns {Object} - Estados e funções para gerenciar a busca de profissionais
 */
const useProfessionalSearch = ({
  initialPage = 0,
  initialSortBy = 'melhorAvaliacao',
  limitResults = false,
  resultLimit = 6
} = {}) => {
  // Estados
  const [searchTerm, setSearchTerm] = useState('');
  const [locationTerm, setLocationTerm] = useState('');
  const [minRating, setMinRating] = useState(0);
  const [selectedSpecialties, setSelectedSpecialties] = useState([]);
  const [sortBy, setSortBy] = useState(initialSortBy);
  const [activeFilters, setActiveFilters] = useState([]);
  
  // Estado para resultados paginados
  const [displayedArtists, setDisplayedArtists] = useState([]);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [hasPrevious, setHasPrevious] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [loadingTime, setLoadingTime] = useState(0);

  // Referências
  const hasLoadedInitialData = useRef(false);
  const loadProfessionalsRef = useRef(null);
  const lastDependencies = useRef({ currentPage: initialPage, sortBy: initialSortBy });
  const debounceTimerRef = useRef(null);
  const isDebouncing = useRef(false);
  const isRequestInProgress = useRef(false);

  // Memoizar os filtros para evitar recriação desnecessária
  const filters = useMemo(() => ({
    searchTerm: searchTerm.trim() || null,
    locationTerm: locationTerm.trim() || null,
    minRating,
    selectedSpecialties,
    sortBy
  }), [searchTerm, locationTerm, minRating, selectedSpecialties, sortBy]);

  // Atualizar filtros ativos
  const updateActiveFilters = useCallback(() => {
    const filters = [];
    
    if (minRating > 0) {
      filters.push({ type: 'rating', value: `${minRating}★` });
    }
    
    selectedSpecialties.forEach(specialty => {
      filters.push({ type: 'specialty', value: specialty });
    });
    
    setActiveFilters(filters);
  }, [minRating, selectedSpecialties]);

  // Função principal para carregar profissionais
  const loadProfessionals = useCallback(async (forceRefresh = false) => {
    // Se já houver uma requisição em andamento e não for uma atualização forçada, não fazer nada
    if (isRequestInProgress.current && !forceRefresh) {
      return;
    }
    
    // Se estivermos no meio de um debounce e não for uma atualização forçada, não fazer nada
    if (isDebouncing.current && !forceRefresh) {
      return;
    }
    
    try {
      isRequestInProgress.current = true;
      setIsLoading(true);
      const startTime = performance.now();
      
      // Preparar os filtros para o formato que o backend espera
      const currentFilters = {
        searchTerm: searchTerm.trim() || null,
        locationTerm: locationTerm.trim() || null,
        minRating,
        selectedSpecialties: selectedSpecialties.length > 0 ? selectedSpecialties : null,
        sortBy
      };
      
      const response = await ProfessionalService.getTransformedCompleteProfessionals(currentPage, currentFilters);
      
      const endTime = performance.now();
      const timeElapsed = (endTime - startTime) / 1000;
      setLoadingTime(timeElapsed.toFixed(2));
      console.log(`%c[TEMPO DE RESPOSTA] ${timeElapsed.toFixed(2)} segundos para carregar ${response.content?.length || 0} profissionais`, 'background: #222; color: #bada55; font-size: 14px; padding: 5px;');
      
      // Se limitResults for true, limitar o número de resultados
      const professionals = response.content || [];
      setDisplayedArtists(limitResults ? professionals.slice(0, resultLimit) : professionals);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setHasNext(response.hasNext);
      setHasPrevious(response.hasPrevious);
      updateActiveFilters();
    } catch (error) {
      console.error('Erro ao carregar profissionais:', error);
      toastHelper.showError('Erro ao carregar profissionais');
      setDisplayedArtists([]);
      setTotalPages(0);
      setTotalElements(0);
      setHasNext(false);
      setHasPrevious(false);
    } finally {
      setIsLoading(false);
      isRequestInProgress.current = false;
    }
  }, [currentPage, searchTerm, locationTerm, minRating, selectedSpecialties, sortBy, limitResults, resultLimit, updateActiveFilters]);

  // Atualizar a referência quando loadProfessionals mudar
  useEffect(() => {
    loadProfessionalsRef.current = loadProfessionals;
  }, [loadProfessionals]);

  // Função de busca com debounce
  const handleSearch = useCallback(() => {
    // Limpar qualquer timer existente
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current);
    }
    
    // Marcar que estamos no meio de um debounce
    isDebouncing.current = true;
    
    // Configurar um novo timer
    debounceTimerRef.current = setTimeout(() => {
      // Sempre resetar para a página 0 quando buscar
      setCurrentPage(0);
      
      // Forçar uma nova busca com os filtros atualizados
      if (loadProfessionalsRef.current) {
        loadProfessionalsRef.current(true); // Forçar refresh
      }
      
      // Marcar que não estamos mais no meio de um debounce
      isDebouncing.current = false;
    }, 300); // 300ms de debounce
  }, []);

  // Alternar seleção de especialidade
  const toggleSpecialty = useCallback((specialty) => {
    setSelectedSpecialties(prev =>
      prev.includes(specialty) ? prev.filter(s => s !== specialty) : [...prev, specialty]
    );
  }, []);

  // Resetar filtros
  const resetFilters = useCallback(() => {
    setMinRating(0);
    setSelectedSpecialties([]);
    setActiveFilters([]);
    handleSearch();
  }, [handleSearch]);
  
  // Remover filtro específico
  const removeFilter = useCallback((filter) => {
    if (filter.type === 'rating') {
      setMinRating(0);
    } else if (filter.type === 'specialty') {
      setSelectedSpecialties(prev => prev.filter(s => s !== filter.value));
    }
    // Fazer busca após remover filtro
    handleSearch();
  }, [handleSearch]);

  // Carregar dados iniciais apenas uma vez
  useEffect(() => {
    if (!hasLoadedInitialData.current) {
      if (loadProfessionalsRef.current) {
        loadProfessionalsRef.current(true); // Forçar carregamento inicial
      } else {
        // Fallback para caso a referência ainda não esteja definida
        setTimeout(() => {
          if (loadProfessionalsRef.current) {
            loadProfessionalsRef.current(true);
          }
        }, 0);
      }
      hasLoadedInitialData.current = true;
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Unificar os useEffect para evitar requisições duplicadas
  useEffect(() => {
    // Se estivermos no meio de um debounce, não fazer nada
    if (isDebouncing.current) {
      return;
    }
    
    const shouldLoadInitialData = !hasLoadedInitialData.current;
    const hasDependencyChanged = 
      lastDependencies.current.currentPage !== currentPage || 
      lastDependencies.current.sortBy !== sortBy;
    
    // Atualizar as dependências atuais
    lastDependencies.current = { currentPage, sortBy };
    
    // Carregar dados apenas se for a carga inicial ou se as dependências mudaram
    if (shouldLoadInitialData || (hasLoadedInitialData.current && hasDependencyChanged)) {
      if (loadProfessionalsRef.current) {
        // Pequeno timeout para garantir que o estado foi atualizado
        setTimeout(() => {
          if (loadProfessionalsRef.current) {
            loadProfessionalsRef.current();
          }
        }, 0);
      } else {
        // Fallback para caso a referência ainda não esteja definida
        setTimeout(() => {
          if (loadProfessionalsRef.current) {
            loadProfessionalsRef.current();
          }
        }, 0);
      }
      
      // Marcar como carregado após a primeira carga
      if (!hasLoadedInitialData.current) {
        hasLoadedInitialData.current = true;
      }
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, sortBy]);

  // Efeito para lidar com mudanças nos filtros
  useEffect(() => {
    // Ignorar a primeira renderização ou se estivermos no meio de um debounce
    if (!hasLoadedInitialData.current || isDebouncing.current) {
      return;
    }
    
    // Usar debounce para evitar múltiplas chamadas
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current);
    }
    
    isDebouncing.current = true;
    
    debounceTimerRef.current = setTimeout(() => {
      if (currentPage === 0) {
        // Se a página já for 0, chamar loadProfessionals diretamente
        if (loadProfessionalsRef.current) {
          loadProfessionalsRef.current();
        }
      } else {
        // Se a página não for 0, resetar para 0
        // Isso vai disparar o outro useEffect
        setCurrentPage(0);
      }
      
      isDebouncing.current = false;
    }, 300);
    
    // Limpar o timeout quando o componente for desmontado
    return () => {
      if (debounceTimerRef.current) {
        clearTimeout(debounceTimerRef.current);
      }
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchTerm, locationTerm, minRating, selectedSpecialties, sortBy]);

  return {
    // Estados
    searchTerm,
    setSearchTerm,
    locationTerm,
    setLocationTerm,
    minRating,
    setMinRating,
    selectedSpecialties,
    setSelectedSpecialties,
    sortBy,
    setSortBy,
    activeFilters,
    displayedArtists,
    currentPage,
    setCurrentPage,
    totalPages,
    totalElements,
    hasNext,
    hasPrevious,
    isLoading,
    loadingTime,
    
    // Funções
    handleSearch,
    toggleSpecialty,
    resetFilters,
    removeFilter,
    updateActiveFilters,
    loadProfessionals
  };
};

export default useProfessionalSearch; 