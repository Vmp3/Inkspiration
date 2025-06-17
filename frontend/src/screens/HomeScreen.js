import React, { useState, useEffect, useRef, useMemo } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ScrollView, 
  TouchableOpacity,
  FlatList,
  Dimensions
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { StatusBar } from 'expo-status-bar';
import { MaterialIcons } from '@expo/vector-icons';
import Input from '../components/ui/Input';
import FilterButton from '../components/FilterButton';
import FilterDropdown from '../components/common/FilterDropdown';
import ArtistCard from '../components/ArtistCard';
import Footer from '../components/Footer';
import ActiveFilters from '../components/common/ActiveFilters';
import MobileFiltersModal from '../components/common/MobileFiltersModal';
import ProfessionalService from '../services/ProfessionalService';
import toastHelper from '../utils/toastHelper';
import { homeMessages } from '../components/home/messages';
import Button from '../components/ui/Button';

const HomeScreen = ({ navigation }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [locationTerm, setLocationTerm] = useState('');
  const [minRating, setMinRating] = useState(0);
  const [selectedSpecialties, setSelectedSpecialties] = useState([]);
  const [isFilterDropdownVisible, setIsFilterDropdownVisible] = useState(false);
  const [filterButtonPosition, setFilterButtonPosition] = useState({ top: 0, left: 0 });
  const filterButtonRef = useRef(null);
  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  const [activeFilters, setActiveFilters] = useState([]);
  const [showFiltersModal, setShowFiltersModal] = useState(false);
  
  // Estado para resultados
  const [displayedArtists, setDisplayedArtists] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // Detectar tamanho da tela para responsividade
  const updateLayout = () => {
    const { width } = Dimensions.get('window');
    setScreenWidth(width);
  };
  
  // Valores derivados baseados na largura da tela
  const isMobile = screenWidth < 768;
  const isSmallMobile = screenWidth < 480;
  
  // Determina o número de colunas com base na largura da tela
  const numColumns = useMemo(() => {
    if (screenWidth >= 768) return 3;
    if (screenWidth >= 480) return 2;
    return 1;
  }, [screenWidth]);
  
  // Key única para o FlatList quando numColumns muda
  const flatListKey = useMemo(() => `flatlist-${numColumns}-columns`, [numColumns]);
  
  useEffect(() => {
    updateLayout();
    // Listener para mudanças no tamanho da tela
    const dimensionsHandler = Dimensions.addEventListener('change', updateLayout);
    
    return () => {
      // Cleanup listener
      if (dimensionsHandler?.remove) {
        dimensionsHandler.remove();
      }
    };
  }, []);

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
        sortBy: 'relevancia'
      };
      
      const response = await ProfessionalService.getTransformedCompleteProfessionals(0, filters);
      const professionals = response.content || [];
      setDisplayedArtists(professionals.slice(0, 6));
      updateActiveFilters();
    } catch (error) {
      console.error('Erro ao carregar profissionais:', error);
      toastHelper.showError('Erro ao carregar profissionais');
      setDisplayedArtists([]);
    } finally {
      setIsLoading(false);
    }
  };

  // Todas as especialidades disponíveis
  const allSpecialties = [
    "Tradicional",
    "Japonês",
    "Aquarela",
    "Minimalista",
    "Blackwork",
    "Geométrico",
    "Realista",
    "Retratos",
    "Neo-Tradicional",
    "Old School",
  ];

  // Função de busca
  const handleSearch = () => {
    loadProfessionals();
  };

  // Alternar seleção de especialidade
  const toggleSpecialty = (specialty) => {
    setSelectedSpecialties((prev) =>
      prev.includes(specialty) ? prev.filter((s) => s !== specialty) : [...prev, specialty]
    );
  };

  // Resetar filtros
  const resetFilters = () => {
    setSearchTerm('');
    setLocationTerm('');
    setMinRating(0);
    setSelectedSpecialties([]);
    setActiveFilters([]);
    // Recarrega dados após resetar todos os filtros
    setTimeout(() => loadProfessionals(), 0);
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

  // Renderização dos itens de artista
  const renderArtistItem = ({ item }) => {
    const cardStyle = [
      styles.artistCard,
      numColumns === 3 ? styles.artistCardThreeCol : 
      numColumns === 2 ? styles.artistCardTwoCol : 
      styles.artistCardOneCol
    ];
    
    return (
      <TouchableOpacity 
        style={cardStyle}
        onPress={() => navigation.navigate('Artist', { artistId: item.id })}
      >
        <ArtistCard artist={item} />
      </TouchableOpacity>
    );
  };

  // Renderizar estrelas de avaliação
  const renderStars = (rating) => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <MaterialIcons
          key={i}
          name="star"
          size={20}
          color={i <= rating ? "#FFD700" : "#E5E7EB"}
        />
      );
    }
    return stars;
  };

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

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />

      <View style={styles.pageWrapper}>
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <View style={styles.main}>
            <View style={styles.heroSection}>
              <Text style={[styles.heroTitle, isMobile && styles.heroTitleMobile]}>Encontre o Tatuador Perfeito Perto de Você</Text>
              <Text style={styles.heroSubtitle}>
                Descubra tatuadores talentosos na sua região. Navegue por portfólios, leia avaliações e agende horários.
              </Text>

              <View style={styles.searchContainer}>
                {isMobile ? (
                  // Layout para dispositivos móveis
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
                ) : (
                  // Layout para desktop/tablet
                  <View style={styles.searchInputRow}>
                    <View style={styles.searchInputContainer}>
                      <Input
                        icon="search"
                        placeholder="Buscar artistas"
                        value={searchTerm}
                        onChangeText={setSearchTerm}
                        onSubmitEditing={handleSearch}
                        returnKeyType="search"
                      />
                    </View>
                    
                    <View style={styles.searchInputContainer}>
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
                      style={styles.searchButton}
                      size="search"
                    />
                  </View>
                )}

                <View style={styles.filterRow}>
                  <View ref={filterButtonRef}>
                    <FilterButton 
                      onPress={handleFilterPress} 
                      filterCount={activeFilters.length} 
                    />
                  </View>
                  
                  <View style={styles.activeFiltersContainer}>
                    <ActiveFilters 
                      activeFilters={activeFilters}
                      removeFilter={removeFilter}
                      resetFilters={resetFilters}
                    />
                  </View>
                </View>
              </View>
            </View>

            {isLoading ? (
              <View style={styles.loadingContainer}>
                <Text style={styles.loadingText}>Carregando profissionais...</Text>
              </View>
            ) : displayedArtists.length > 0 ? (
              <View style={styles.artistsSection}>
                <View style={styles.sectionHeader}>
                  <Text style={styles.sectionTitle}>Artistas em Destaque</Text>
                  <TouchableOpacity onPress={() => navigation.navigate('Explore')}>
                    <Text style={styles.seeAllButton}>Ver todos</Text>
                  </TouchableOpacity>
                </View>

                <FlatList
                  key={flatListKey}
                  data={displayedArtists}
                  renderItem={renderArtistItem}
                  keyExtractor={item => item.id}
                  numColumns={numColumns}
                  scrollEnabled={false}
                  columnWrapperStyle={numColumns > 1 ? styles.artistRow : null}
                  contentContainerStyle={styles.artistGrid}
                />
              </View>
            ) : (
              <View style={styles.noResultsContainer}>
                <Text style={styles.noResultsTitle}>Nenhum resultado encontrado</Text>
                <Text style={styles.noResultsSubtitle}>
                  Tente ajustar seus filtros ou termos de busca para encontrar mais resultados.
                </Text>
              </View>
            )}
          </View>
          
          {/* Espaço para garantir que o footer fique no final */}
          <View style={styles.footerSpacer} />
          
          <Footer />
        </ScrollView>
      </View>

      {/* FilterDropdown para desktop/tablet */}
      <FilterDropdown
        visible={isFilterDropdownVisible}
        onClose={() => setIsFilterDropdownVisible(false)}
        minRating={minRating}
        setMinRating={setMinRating}
        selectedSpecialties={selectedSpecialties}
        toggleSpecialty={toggleSpecialty}
        resetFilters={resetFilters}
        applyFilters={handleSearch}
        anchorPosition={filterButtonPosition}
      />

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
        applyFilters={handleSearch}
        updateActiveFilters={updateActiveFilters}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
  },
  pageWrapper: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    flexDirection: 'column',
  },
  main: {
    paddingHorizontal: 16,
    paddingVertical: 24,
    maxWidth: 1200,
    alignSelf: 'center',
    width: '100%',
  },
  heroSection: {
    marginBottom: 48,
    alignItems: 'center',
  },
  heroTitle: {
    fontSize: 38,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 12,
    textAlign: 'center',
  },
  heroTitleMobile: {
    fontSize: 28,
  },
  heroSubtitle: {
    fontSize: 16,
    color: '#6B7280',
    textAlign: 'center',
    marginBottom: 32,
    maxWidth: 600,
  },
  searchContainer: {
    width: '100%',
    maxWidth: 768,
    paddingHorizontal: 4,
  },
  searchInputRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
    gap: 8,
  },
  searchInputContainer: {
    flex: 1,
    minWidth: 150,
  },
  searchButton: {
    paddingHorizontal: 16,
    minWidth: 120,
  },
  filterRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
    gap: 8,
    width: '100%',
    marginTop: 8,
  },
  activeFiltersContainer: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  artistsSection: {
    marginBottom: 48,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#111827',
  },
  seeAllButton: {
    fontSize: 16,
    color: '#000000',
    fontWeight: '500',
  },
  artistRow: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    flexWrap: 'wrap',
    gap: 16,
  },
  artistGrid: {
    paddingBottom: 24,
  },
  artistCard: {
    flex: 1,
    marginBottom: 24,
  },
  artistCardThreeCol: {
    maxWidth: '32%',
  },
  artistCardTwoCol: {
    maxWidth: '48%',
  },
  artistCardOneCol: {
    maxWidth: '100%',
  },
  noResultsContainer: {
    padding: 48,
    alignItems: 'center',
    justifyContent: 'center',
  },
  noResultsTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
    textAlign: 'center',
  },
  noResultsSubtitle: {
    fontSize: 16,
    color: '#6B7280',
    textAlign: 'center',
    maxWidth: 400,
  },
  footerSpacer: {
    flex: 1,
    minHeight: 20,
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
});

export default HomeScreen; 