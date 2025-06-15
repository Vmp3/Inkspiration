import React, { useState, useEffect, useRef } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ScrollView, 
  TouchableOpacity,
  Dimensions,
  Platform
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { StatusBar } from 'expo-status-bar';
import Footer from '../components/Footer';
import Input from '../components/ui/Input';
import FilterButton from '../components/FilterButton';
import ProfessionalService from '../services/ProfessionalService';
import toastHelper from '../utils/toastHelper';
import { exploreMessages } from '../components/explore/messages';
import Button from '../components/ui/Button';

// Componentes de exploração
import FiltersPanel from '../components/explore/FiltersPanel';
import ActiveFilters from '../components/common/ActiveFilters';
import SortByDropdown from '../components/explore/SortByDropdown';
import ArtistsGrid from '../components/explore/ArtistsGrid';
import Pagination from '../components/common/Pagination';
import MobileFiltersModal from '../components/common/MobileFiltersModal';
import FilterDropdown from '../components/common/FilterDropdown';

const ExploreScreen = ({ navigation }) => {
  // Estados
  const [searchTerm, setSearchTerm] = useState('');
  const [locationTerm, setLocationTerm] = useState('');
  const [minRating, setMinRating] = useState(0);
  const [selectedSpecialties, setSelectedSpecialties] = useState([]);
  const [sortBy, setSortBy] = useState('melhorAvaliacao');
  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  const [activeFilters, setActiveFilters] = useState([]);
  const [showFiltersModal, setShowFiltersModal] = useState(false);

  const [isFilterDropdownVisible, setIsFilterDropdownVisible] = useState(false);
  const [filterButtonPosition, setFilterButtonPosition] = useState({ top: 0, left: 0 });
  const filterButtonRef = useRef(null);
  
  // Estado para resultados paginados
  const [displayedArtists, setDisplayedArtists] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [hasPrevious, setHasPrevious] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const updateLayout = () => {
      const { width } = Dimensions.get('window');
      setScreenWidth(width);
    };
    
    const dimensionsHandler = Dimensions.addEventListener('change', updateLayout);
    
    return () => {
      if (dimensionsHandler?.remove) {
        dimensionsHandler.remove();
      }
    };
  }, []);
  useEffect(() => {
    loadProfessionals();
  }, [currentPage, sortBy]);

  // Carregar profissionais na primeira renderização
  useEffect(() => {
    loadProfessionals();
  }, []);

  const loadProfessionals = async () => {
    try {
      setIsLoading(true);
      const filters = {
        searchTerm: searchTerm.trim() || null,
        locationTerm: locationTerm.trim() || null,
        minRating,
        selectedSpecialties,
        sortBy
      };
      
      const response = await ProfessionalService.getTransformedCompleteProfessionals(currentPage, filters);
      setDisplayedArtists(response.content);
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
    }
  };
  
  const isMobile = screenWidth < 768;
  
  const numColumns = (() => {
    if (screenWidth < 768) {
      return 1;
    }
    return 3;
  })();

  // Função de busca
  const handleSearch = () => {
    setCurrentPage(0);
    loadProfessionals();
  };

  // Atualizar filtros ativos
  const updateActiveFilters = () => {
    const filters = [];
    
    if (minRating > 0) {
      filters.push({ type: 'rating', value: `${minRating}★` });
    }
    
    selectedSpecialties.forEach(specialty => {
      filters.push({ type: 'specialty', value: specialty });
    });
    
    setActiveFilters(filters);
  };

  // Alternar seleção de especialidade
  const toggleSpecialty = (specialty) => {
    setSelectedSpecialties(prev =>
      prev.includes(specialty) ? prev.filter(s => s !== specialty) : [...prev, specialty]
    );
  };

  // Resetar filtros
  const resetFilters = () => {
    setMinRating(0);
    setSelectedSpecialties([]);
    setActiveFilters([]);
    handleSearch();
  };
  
  // Remover filtro específico
  const removeFilter = (filter) => {
    if (filter.type === 'rating') {
      setMinRating(0);
    } else if (filter.type === 'specialty') {
      setSelectedSpecialties(prev => prev.filter(s => s !== filter.value));
    }
    // Fazer busca após remover filtro
    handleSearch();
  };

  // Função para lidar com o clique no botão de filtros
  const handleFilterPress = () => {
    if (isMobile) {
      setShowFiltersModal(true);
    } else {
      if (filterButtonRef.current) {
        filterButtonRef.current.measureInWindow((x, y, width, height) => {
          setFilterButtonPosition({ top: y, left: x });
          setIsFilterDropdownVisible(true);
        });
      } else {
        setIsFilterDropdownVisible(true);
      }
    }
  };

  // Função para mudança de página, responsável por scrollar para o topo da página
  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    if (typeof window !== 'undefined') {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar style="dark" />
      
      <ScrollView style={styles.scrollView}>
        <View style={styles.content}>
          {/* Título da página */}
          <View style={[styles.pageHeader, isMobile && styles.pageHeaderMobile]}>
            <Text style={styles.title}>Explorar Artistas</Text>
          </View>
          
          {/* Layout principal */}
          <View style={[styles.mainContainer, isMobile && styles.mainContainerMobile]}>
            {/* Coluna de filtros (esquerda) */}
            {!isMobile && (
              <FiltersPanel
                searchTerm={searchTerm}
                setSearchTerm={setSearchTerm}
                locationTerm={locationTerm}
                setLocationTerm={setLocationTerm}
                minRating={minRating}
                setMinRating={setMinRating}
                selectedSpecialties={selectedSpecialties}
                toggleSpecialty={toggleSpecialty}
                handleSearch={handleSearch}
                resetFilters={resetFilters}
                updateActiveFilters={updateActiveFilters}
              />
            )}
            
            {/* Coluna de artistas (direita) */}
            <View style={[styles.artistsColumn, isMobile && styles.artistsColumnMobile]}>
              {/* Linha separadora para dispositivos móveis */}
              {isMobile && <View style={styles.separator} />}
              
              {/* Adicionar inputs de busca para dispositivos móveis */}
              {isMobile && (
                <View style={styles.mobileSearchContainer}>
                  <View style={styles.mobileInputWrapper}>
                    <Input
                      icon="search"
                      placeholder="Buscar artistas"
                      value={searchTerm}
                      onChangeText={setSearchTerm}
                      onSubmitEditing={handleSearch}
                      returnKeyType="search"
                    />
                  </View>
                  
                  <View style={styles.mobileInputWrapper}>
                    <Input
                      icon="location-on"
                      placeholder="Sua localização"
                      value={locationTerm}
                      onChangeText={setLocationTerm}
                      onSubmitEditing={handleSearch}
                      returnKeyType="search"
                    />
                  </View>
                  
                  <Button 
                    variant="primary"
                    label="Buscar"
                    onPress={handleSearch}
                    style={styles.mobileSearchButton}
                    size="search"
                    fullWidth={true}
                  />
                </View>
              )}
              
              {/* Filtros e dropdown de relevância */}
              <View style={[styles.filtersAndRelevanceRow, isMobile && styles.filtersAndRelevanceRowMobile]}>
                {/* Filtros */}
                <View style={[styles.filtersContainer, isMobile && styles.filtersContainerMobile]}>
                  {isMobile && (
                    <View ref={filterButtonRef} style={styles.filterButton}>
                      <FilterButton 
                        onPress={handleFilterPress} 
                        filterCount={activeFilters.length} 
                      />
                    </View>
                  )}
                  
                  {/* Filtros ativos */}
                  <View style={[styles.activeFiltersContainer, isMobile && styles.activeFiltersContainerMobile]}>
                    <ActiveFilters 
                      activeFilters={activeFilters} 
                      removeFilter={removeFilter} 
                      resetFilters={resetFilters} 
                    />
                  </View>
                </View>
                
                {/* Dropdown de relevância */}
                <View style={[styles.relevanceContainer, isMobile && styles.relevanceContainerMobile]}>
                  <SortByDropdown 
                    sortBy={sortBy} 
                    setSortBy={setSortBy} 
                  />
                </View>
              </View>
              
              {/* Grid de artistas */}
              <View style={styles.artistsGridContainer}>
                {isLoading ? (
                  <View style={styles.loadingContainer}>
                    <Text style={styles.loadingText}>Carregando profissionais...</Text>
                  </View>
                ) : displayedArtists.length > 0 ? (
                  <ArtistsGrid 
                    artists={displayedArtists} 
                    numColumns={numColumns} 
                    navigation={navigation} 
                  />
                ) : (
                  <View style={styles.noResultsContainer}>
                    <Text style={styles.noResultsTitle}>Nenhum resultado encontrado</Text>
                    <Text style={styles.noResultsSubtitle}>
                      Tente ajustar seus filtros ou termos de busca para encontrar mais resultados.
                    </Text>
                  </View>
                )}
              </View>
              
              {/* Paginação */}
              {displayedArtists.length > 0 && (
                <Pagination 
                  currentPage={currentPage} 
                  setCurrentPage={handlePageChange} 
                  totalPages={totalPages} 
                />
              )}
            </View>
          </View>
        </View>
        
        <Footer />
      </ScrollView>
      
      {/* Modal de filtros para dispositivos móveis */}
      <MobileFiltersModal
        visible={showFiltersModal}
        onClose={() => setShowFiltersModal(false)}
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        locationTerm={locationTerm}
        setLocationTerm={setLocationTerm}
        minRating={minRating}
        setMinRating={setMinRating}
        selectedSpecialties={selectedSpecialties}
        toggleSpecialty={toggleSpecialty}
        handleSearch={handleSearch}
        resetFilters={resetFilters}
        applyFilters={loadProfessionals}
        updateActiveFilters={updateActiveFilters}
      />

      {/* FilterDropdown para desktop/tablet */}
      <FilterDropdown
        visible={isFilterDropdownVisible}
        onClose={() => setIsFilterDropdownVisible(false)}
        minRating={minRating}
        setMinRating={setMinRating}
        selectedSpecialties={selectedSpecialties}
        toggleSpecialty={toggleSpecialty}
        resetFilters={resetFilters}
        applyFilters={loadProfessionals}
        anchorPosition={filterButtonPosition}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  scrollView: {
    flex: 1,
  },
  content: {
    padding: 16,
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111827',
  },
  
  // Cabeçalho da página
  pageHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
  },
  pageHeaderMobile: {
    flexDirection: 'column',
    alignItems: 'center',
    width: '100%',
  },
  
  // Layout principal
  mainContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  mainContainerMobile: {
    flexDirection: 'column',
  },
  
  // Coluna de artistas
  artistsColumn: {
    width: '75%',
    paddingLeft: 16,
  },
  artistsColumnMobile: {
    width: '100%',
    paddingLeft: 0,
  },
  
  // Separador
  separator: {
    height: 1,
    backgroundColor: '#E5E7EB',
    marginBottom: 16,
  },
  
  // Filtros móveis
  mobileFiltersButton: {
    backgroundColor: '#000000',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 10,
    borderRadius: 6,
    marginBottom: 16,
  },
  mobileFiltersButtonText: {
    color: '#FFFFFF',
    fontWeight: '600',
    marginLeft: 8,
  },
  
  // Adicionar inputs de busca para dispositivos móveis
  mobileSearchContainer: {
    flexDirection: 'column',
    width: '100%',
    gap: 16,
    marginBottom: 16,
  },
  mobileInputWrapper: {
    flex: 1,
    width: '100%',
  },
  mobileSearchButton: {
    paddingHorizontal: 16,
    alignSelf: 'stretch',
  },
  
  // Filtros e dropdown de relevância
  filtersAndRelevanceRow: {
    flexDirection: 'column',
    width: '100%',
    marginBottom: 16,
    zIndex: 100,
    position: 'relative',
  },
  filtersContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
    width: '100%',
    zIndex: 101,
    marginBottom: 8,
    gap: 8,
  },
  relevanceContainer: {
    alignItems: 'flex-end',
    zIndex: 101,
    alignSelf: 'flex-end',
    width: '100%',
  },
  activeFiltersContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    width: '100%',
    maxWidth: '100%',
  },
  artistsGridContainer: {
    width: '100%',
    position: 'relative',
    zIndex: 1,
  },
  loadingContainer: {
    padding: 48,
    alignItems: 'center',
    justifyContent: 'center',
  },
  loadingText: {
    fontSize: 16,
    color: '#6B7280',
    textAlign: 'center',
  },
  noResultsContainer: {
    padding: 48,
    alignItems: 'center',
    justifyContent: 'center',
  },
  noResultsTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 8,
    textAlign: 'center',
  },
  noResultsSubtitle: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
    lineHeight: 20,
  },
  filtersAndRelevanceRowMobile: {
    flexDirection: 'column',
    width: '100%',
    marginBottom: 16,
    zIndex: 100,
    position: 'relative',
  },
  filtersContainerMobile: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
    width: '100%',
    zIndex: 101,
    marginBottom: 8,
    gap: 8,
  },
  relevanceContainerMobile: {
    alignItems: 'flex-end',
    zIndex: 101,
    alignSelf: 'flex-end',
    width: '100%',
  },
  activeFiltersContainerMobile: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    width: '100%',
    maxWidth: '100%',
  },
  filterButton: {
    marginBottom: 8,
  },
});

export default ExploreScreen;