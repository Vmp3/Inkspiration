import React, { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ScrollView, 
  TouchableOpacity,
  Dimensions,
  Platform,
  ActivityIndicator
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { StatusBar } from 'expo-status-bar';
import Footer from '../components/Footer';
import Input from '../components/ui/Input';
import FilterButton from '../components/FilterButton';
import Button from '../components/ui/Button';
import { exploreMessages } from '../components/explore/messages';
import useProfessionalSearch from '../hooks/useProfessionalSearch';

// Componentes de exploração
import FiltersPanel from '../components/explore/FiltersPanel';
import ActiveFilters from '../components/common/ActiveFilters';
import SortByDropdown from '../components/explore/SortByDropdown';
import ArtistsGrid from '../components/explore/ArtistsGrid';
import Pagination from '../components/common/Pagination';
import MobileFiltersModal from '../components/common/MobileFiltersModal';
import FilterDropdown from '../components/common/FilterDropdown';

const ExploreScreen = ({ navigation }) => {
  const {
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
    handleSearch,
    toggleSpecialty,
    resetFilters,
    removeFilter,
    updateActiveFilters,
    loadProfessionals
  } = useProfessionalSearch({
    initialSortBy: 'melhorAvaliacao',
    limitResults: false
  });

  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  const [showFiltersModal, setShowFiltersModal] = useState(false);
  const [isFilterDropdownVisible, setIsFilterDropdownVisible] = useState(false);
  const [filterButtonPosition, setFilterButtonPosition] = useState({ top: 0, left: 0 });
  const filterButtonRef = useRef(null);
  const scrollViewRef = useRef(null);

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

  const isMobile = screenWidth < 768;

  const numColumns = useMemo(() => {
    if (screenWidth < 768) {
      return 1;
    }
    return 3;
  }, [screenWidth]);

  const handleFilterPress = useCallback(() => {
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
  }, [isMobile]);

  // Função para mudança de página, responsável por scrollar para o topo da página
  const handlePageChange = useCallback((newPage) => {
    setCurrentPage(newPage);
    if (scrollViewRef.current) {
      scrollViewRef.current.scrollTo({ y: 0, animated: true });
    }
  }, [setCurrentPage]);

  // Renderizar o componente de carregamento
  const renderLoading = useMemo(() => (
    <View style={styles.loadingContainer}>
      <MaterialIcons name="hourglass-top" size={32} color="#6B7280" />
      <Text style={styles.loadingText}>Carregando profissionais...</Text>
      <Text style={styles.loadingSubtext}>Aguarde enquanto buscamos os melhores profissionais para você</Text>
    </View>
  ), []);

  return (
    <View style={styles.container}>
      <StatusBar style="dark" />
      
      <ScrollView style={styles.scrollView} ref={scrollViewRef}>
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
                  renderLoading
                ) : displayedArtists.length > 0 ? (
                  <>
                    {loadingTime > 0 && (
                      <Text style={styles.loadingTimeText}>
                        Carregado em {loadingTime} segundos
                      </Text>
                    )}
                    <ArtistsGrid 
                      artists={displayedArtists} 
                      numColumns={numColumns} 
                      navigation={navigation} 
                    />
                  </>
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
        applyFilters={() => loadProfessionals(true)}
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
        applyFilters={() => loadProfessionals(true)}
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
  
  mainContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  mainContainerMobile: {
    flexDirection: 'column',
  },
  
  artistsColumn: {
    width: '75%',
    paddingLeft: 16,
  },
  artistsColumnMobile: {
    width: '100%',
    paddingLeft: 0,
  },
  
  separator: {
    height: 1,
    backgroundColor: '#E5E7EB',
    marginBottom: 16,
  },
  
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
    marginTop: 16,
    marginBottom: 8,
  },
  loadingSubtext: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
    opacity: 0.8,
  },
  loadingTimeText: {
    fontSize: 12,
    color: '#6B7280',
    textAlign: 'right',
    marginBottom: 8,
    fontStyle: 'italic',
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