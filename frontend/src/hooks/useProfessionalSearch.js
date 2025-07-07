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

  const filters = useMemo(() => ({
    searchTerm: searchTerm.trim() || null,
    locationTerm: locationTerm.trim() || null,
    minRating,
    selectedSpecialties,
    sortBy
  }), [searchTerm, locationTerm, minRating, selectedSpecialties, sortBy]);

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

  const loadProfessionals = useCallback(async (forceRefresh = false) => {
    if (isRequestInProgress.current && !forceRefresh) {
      return;
    }
    
    if (isDebouncing.current && !forceRefresh) {
      return;
    }
    
    try {
      isRequestInProgress.current = true;
      setIsLoading(true);
      const startTime = performance.now();
      
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
      
      const professionals = response.content || [];
      setDisplayedArtists(limitResults ? professionals.slice(0, resultLimit) : professionals);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setHasNext(response.hasNext);
      setHasPrevious(response.hasPrevious);
      updateActiveFilters();
    } catch (error) {
      // console.error('Erro ao carregar profissionais:', error);
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

  useEffect(() => {
    loadProfessionalsRef.current = loadProfessionals;
  }, [loadProfessionals]);

  const handleSearch = useCallback(() => {
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current);
    }
    
    isDebouncing.current = true;
    debounceTimerRef.current = setTimeout(() => {
      // Sempre resetar para a página 0 quando buscar
      setCurrentPage(0);
      
      // Forçar uma nova busca com os filtros atualizados
      if (loadProfessionalsRef.current) {
        loadProfessionalsRef.current(true);
      }
      isDebouncing.current = false;
    }, 300);
  }, []);

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
    handleSearch();
  }, [handleSearch]);

  useEffect(() => {
    if (!hasLoadedInitialData.current) {
      if (loadProfessionalsRef.current) {
        loadProfessionalsRef.current(true);
      } else {
        setTimeout(() => {
          if (loadProfessionalsRef.current) {
            loadProfessionalsRef.current(true);
          }
        }, 0);
      }
      hasLoadedInitialData.current = true;
    }
  }, []);

  useEffect(() => {
    if (isDebouncing.current) {
      return;
    }
    
    const shouldLoadInitialData = !hasLoadedInitialData.current;
    const hasDependencyChanged = 
      lastDependencies.current.currentPage !== currentPage || 
      lastDependencies.current.sortBy !== sortBy;
    
    lastDependencies.current = { currentPage, sortBy };
    
    if (shouldLoadInitialData || (hasLoadedInitialData.current && hasDependencyChanged)) {
      if (loadProfessionalsRef.current) {
        setTimeout(() => {
          if (loadProfessionalsRef.current) {
            loadProfessionalsRef.current();
          }
        }, 0);
      } else {
        setTimeout(() => {
          if (loadProfessionalsRef.current) {
            loadProfessionalsRef.current();
          }
        }, 0);
      }
      
      if (!hasLoadedInitialData.current) {
        hasLoadedInitialData.current = true;
      }
    }
  }, [currentPage, sortBy]);

  // useEffect específico para mudanças no sortBy - aplica filtro automaticamente
  useEffect(() => {
    if (hasLoadedInitialData.current) {
      // Resetar para página 0 quando sortBy muda
      if (currentPage !== 0) {
        setCurrentPage(0);
      } else {
        // Se já está na página 0, forçar reload
        if (loadProfessionalsRef.current) {
          loadProfessionalsRef.current(true);
        }
      }
    }
  }, [sortBy]);

  useEffect(() => {
    if (!hasLoadedInitialData.current || isDebouncing.current) {
      return;
    }
    
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current);
    }
    
    isDebouncing.current = true;
    
    debounceTimerRef.current = setTimeout(() => {
      if (currentPage === 0) {
        if (loadProfessionalsRef.current) {
          loadProfessionalsRef.current();
        }
      } else {
        setCurrentPage(0);
      }
      
      isDebouncing.current = false;
    }, 300);
    
    return () => {
      if (debounceTimerRef.current) {
        clearTimeout(debounceTimerRef.current);
      }
    };
  }, [searchTerm, locationTerm, minRating, selectedSpecialties, sortBy]);

  return {
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