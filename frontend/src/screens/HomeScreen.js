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
import SearchInput from '../components/SearchInput';
import FilterButton from '../components/FilterButton';
import FilterDropdown from '../components/FilterDropdown';
import ArtistCard from '../components/ArtistCard';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { artists as originalArtists } from '../data/artists';

const HomeScreen = ({ navigation }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [locationTerm, setLocationTerm] = useState('');
  const [viewType, setViewType] = useState('artists');
  const [minRating, setMinRating] = useState(0);
  const [selectedSpecialties, setSelectedSpecialties] = useState([]);
  const [isFilterDropdownVisible, setIsFilterDropdownVisible] = useState(false);
  const [filterButtonPosition, setFilterButtonPosition] = useState({ top: 0, left: 0 });
  const filterButtonRef = useRef(null);
  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  
  // Estado para resultados filtrados
  const [filteredArtists, setFilteredArtists] = useState(originalArtists);
  const [displayedArtists, setDisplayedArtists] = useState(originalArtists.slice(0, 6));

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
    if (screenWidth >= 768) return 3; // Desktop: 3 colunas
    if (screenWidth >= 480) return 2; // Tablet/Mobile médio: 2 colunas
    return 1; // Mobile pequeno: 1 coluna
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
    // Filtrar artistas
    const artistResults = originalArtists.filter((artist) => {
      const matchesSearch = 
        searchTerm === "" || 
        artist.name.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesLocation = 
        locationTerm === "" || 
        artist.location.toLowerCase().includes(locationTerm.toLowerCase());

      return matchesSearch && matchesLocation;
    });

    setFilteredArtists(artistResults);
    updateDisplayedResults(viewType, artistResults);
  };

  // Aplicar filtros
  const applyFilters = () => {
    // Filtrar artistas
    const artistResults = filteredArtists.filter((artist) => {
      const matchesRating = artist.rating >= minRating;

      const matchesSpecialties =
        selectedSpecialties.length === 0 ||
        selectedSpecialties.some((specialty) => artist.specialties.includes(specialty));

      return matchesRating && matchesSpecialties;
    });

    updateDisplayedResults(viewType, artistResults);
  };

  // Atualizar resultados exibidos com base no tipo de visualização
  const updateDisplayedResults = (type, artists) => {
    setDisplayedArtists(artists.slice(0, 6));
  };

  // Alternar seleção de especialidade
  const toggleSpecialty = (specialty) => {
    setSelectedSpecialties((prev) =>
      prev.includes(specialty) ? prev.filter((s) => s !== specialty) : [...prev, specialty]
    );
  };

  // Resetar filtros
  const resetFilters = () => {
    setMinRating(0);
    setSelectedSpecialties([]);
  };

  // Efeito para aplicar filtros quando eles mudam
  useEffect(() => {
    applyFilters();
  }, [minRating, selectedSpecialties]);

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
        onPress={() => navigation.navigate('ArtistDetail', { artistId: item.id })}
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
    if (filterButtonRef.current) {
      filterButtonRef.current.measureInWindow((x, y, width, height) => {
        setFilterButtonPosition({ top: y, left: x });
        setIsFilterDropdownVisible(true);
      });
    } else {
      setIsFilterDropdownVisible(true);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />
      <Header />

      <View style={styles.pageWrapper}>
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <View style={styles.main}>
            <View style={styles.heroSection}>
              <Text style={[styles.heroTitle, isMobile && styles.heroTitleMobile]}>Encontre o Tatuador Perfeito Perto de Você</Text>
              <Text style={styles.heroSubtitle}>
                Descubra tatuadores talentosos na sua região. Navegue por portfólios, leia avaliações e agende horários.
              </Text>

              <View style={styles.searchContainer}>
                <View style={styles.searchInputRow}>
                  <View style={styles.searchInputContainer}>
                    <SearchInput
                      icon="search"
                      placeholder="Buscar artistas"
                      value={searchTerm}
                      onChangeText={setSearchTerm}
                    />
                  </View>
                  
                  <View style={styles.searchInputContainer}>
                    <SearchInput
                      icon="location-on"
                      placeholder="Sua localização"
                      value={locationTerm}
                      onChangeText={setLocationTerm}
                    />
                  </View>
                  
                  <TouchableOpacity style={styles.searchButton} onPress={handleSearch}>
                    <Text style={styles.searchButtonText}>Buscar</Text>
                  </TouchableOpacity>
                </View>

                <View style={styles.filterRow}>
                  <View ref={filterButtonRef}>
                    <FilterButton 
                      onPress={handleFilterPress} 
                      filterCount={(minRating > 0 || selectedSpecialties.length > 0) ? 
                        (minRating > 0 && selectedSpecialties.length > 0 ? 2 : 1) : 0} 
                    />
                  </View>
                  
                  {minRating > 0 && (
                    <View style={styles.filterBadge}>
                      <Text style={styles.filterBadgeText}>{minRating}+ </Text>
                      <MaterialIcons name="star" size={12} color="#111" />
                    </View>
                  )}

                  {selectedSpecialties.length > 0 && (
                    <View style={styles.filterBadge}>
                      <Text style={styles.filterBadgeText}>
                        {selectedSpecialties.length} especialidade{selectedSpecialties.length !== 1 ? "s" : ""}
                      </Text>
                    </View>
                  )}
                </View>
              </View>
            </View>

            {displayedArtists.length > 0 && (
              <View style={styles.artistsSection}>
                <View style={styles.sectionHeader}>
                  <Text style={styles.sectionTitle}>Artistas em Destaque</Text>
                  <TouchableOpacity onPress={() => navigation.navigate('Explore')}>
                    <Text style={styles.seeAllButton}>Ver todos</Text>
                  </TouchableOpacity>
                </View>

                <FlatList
                  key={flatListKey} // Key única quando numColumns muda
                  data={displayedArtists}
                  renderItem={renderArtistItem}
                  keyExtractor={item => item.id}
                  numColumns={numColumns}
                  scrollEnabled={false}
                  columnWrapperStyle={numColumns > 1 ? styles.artistRow : null}
                  contentContainerStyle={styles.artistGrid}
                />
              </View>
            )}

            {displayedArtists.length === 0 && (
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

      <FilterDropdown
        visible={isFilterDropdownVisible}
        onClose={() => setIsFilterDropdownVisible(false)}
        minRating={minRating}
        setMinRating={setMinRating}
        selectedSpecialties={selectedSpecialties}
        toggleSpecialty={toggleSpecialty}
        resetFilters={resetFilters}
        applyFilters={applyFilters}
        anchorPosition={filterButtonPosition}
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
    flexGrow: 1, // Importante para que o conteúdo possa crescer e empurrar o footer para baixo
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
    fontSize: 28, // Tamanho menor para mobile
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
  },
  searchInputRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    marginBottom: 16,
    gap: 8,
  },
  searchInputContainer: {
    flex: 1,
    minWidth: 200,
  },
  searchButton: {
    backgroundColor: '#111827',
    borderRadius: 8,
    paddingVertical: 14,
    paddingHorizontal: 16,
    minWidth: 100,
    justifyContent: 'center',
    alignItems: 'center',
  },
  searchButtonText: {
    color: '#FFFFFF',
    fontWeight: '600',
    fontSize: 16,
  },
  filterRow: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: 8,
  },
  filterBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F3F4F6',
    borderRadius: 4,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    paddingHorizontal: 8,
    paddingVertical: 4,
  },
  filterBadgeText: {
    fontSize: 12,
    color: '#111827',
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
    flex: 1, // Isso irá empurrar o footer para baixo
    minHeight: 20, // Altura mínima para garantir algum espaço
  }
});

export default HomeScreen; 