import React, { useState, useEffect, useRef } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ScrollView, 
  TouchableOpacity,
  FlatList,
  Dimensions,
  TextInput,
  PanResponder,
  Modal,
  TouchableWithoutFeedback,
  Pressable,
  Platform
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { SafeAreaView } from 'react-native-safe-area-context';
import { StatusBar } from 'expo-status-bar';
import ArtistCard from '../components/ArtistCard';
import Header from '../components/Header';
import Footer from '../components/Footer';
import Input from '../components/ui/Input';
import { artists as originalArtists } from '../data/artists';

const ExploreScreen = ({ navigation }) => {
  // Estados
  const [searchTerm, setSearchTerm] = useState('');
  const [locationTerm, setLocationTerm] = useState('');
  const [minRating, setMinRating] = useState(0);
  const [maxDistance, setMaxDistance] = useState(15);
  const [selectedSpecialties, setSelectedSpecialties] = useState([]);
  const [sortBy, setSortBy] = useState('relevancia');
  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  const [activeFilters, setActiveFilters] = useState([]);
  const [showFiltersModal, setShowFiltersModal] = useState(false);
  const [showRelevanceDropdown, setShowRelevanceDropdown] = useState(false);

  // Estado para resultados filtrados
  const [filteredArtists, setFilteredArtists] = useState(originalArtists);
  const [displayedArtists, setDisplayedArtists] = useState(originalArtists);
  const [currentPage, setCurrentPage] = useState(1);
  const [isSliderActive, setIsSliderActive] = useState(false);

  // Referência para controle do slider
  const sliderRef = useRef(null);
  const relevanceDropdownRef = useRef(null);

  // Configuração do Pan Responder para o slider
  const panResponder = PanResponder.create({
    onStartShouldSetPanResponder: () => true,
    onStartShouldSetPanResponderCapture: () => true,
    onMoveShouldSetPanResponder: () => true,
    onMoveShouldSetPanResponderCapture: () => true,
    onPanResponderGrant: () => {
      setIsSliderActive(true);
    },
    onPanResponderMove: (evt, gestureState) => {
      if (sliderRef.current) {
        sliderRef.current.measure((fx, fy, width, height, px, py) => {
          // Calculamos a porcentagem baseada na posição do dedo
          let percentage = Math.max(0, Math.min(100, ((gestureState.moveX - px) / width) * 100));
          
          // Convertemos a porcentagem para o valor de distância (0-50km)
          const newDistance = Math.round((percentage / 100) * 50);
          
          // Atualizamos o valor do estado
          setMaxDistance(newDistance);
        });
      }
    },
    onPanResponderRelease: () => {
      setIsSliderActive(false);
      updateActiveFilters();
    },
    onPanResponderTerminate: () => {
      setIsSliderActive(false);
    },
  });

  // Detectar tamanho da tela para responsividade
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
  
  // Valores derivados baseados na largura da tela
  const isMobile = screenWidth < 768;
  
  // Determina o número de colunas com base na largura da tela
  const numColumns = screenWidth >= 768 ? 3 : (screenWidth >= 480 ? 2 : 1);
  
  // Key única para o FlatList quando numColumns muda
  const flatListKey = `flatlist-${numColumns}-columns`;

  // Todas as especialidades disponíveis
  const allSpecialties = [
    "Tradicional",
    "Japonês",
    "Aquarela",
    "Minimalista",
    "Blackwork",
    "Geométrico",
    "Piercing",
    "Realista",
    "Retratos",
    "Neo-Tradicional",
    "Old School",
    "Fineline",
  ];

  // Função de busca
  const handleSearch = () => {
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
    applyFilters(artistResults);
    updateActiveFilters();
    setCurrentPage(1);
  };

  // Aplicar filtros
  const applyFilters = (artists = filteredArtists) => {
    const artistResults = artists.filter((artist) => {
      const matchesRating = artist.rating >= minRating;
      const matchesSpecialties =
        selectedSpecialties.length === 0 ||
        selectedSpecialties.some((specialty) => artist.specialties.includes(specialty));

      return matchesRating && matchesSpecialties;
    });

    setDisplayedArtists(artistResults);
    updateActiveFilters();
  };

  // Atualizar filtros ativos
  const updateActiveFilters = () => {
    const filters = [];
    
    if (minRating > 0) {
      filters.push({ type: 'rating', value: `${minRating}★` });
    }
    
    if (maxDistance !== 15) {
      filters.push({ type: 'distance', value: `Distância: ${maxDistance}km` });
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
    setMaxDistance(15);
    setSelectedSpecialties([]);
    setActiveFilters([]);
  };
  
  // Remover filtro específico
  const removeFilter = (filter) => {
    if (filter.type === 'rating') {
      setMinRating(0);
    } else if (filter.type === 'distance') {
      setMaxDistance(15);
    } else if (filter.type === 'specialty') {
      setSelectedSpecialties(prev => prev.filter(s => s !== filter.value));
    }
    updateActiveFilters();
  };

  // Efeito para aplicar filtros quando eles mudam
  useEffect(() => {
    applyFilters();
  }, [minRating, selectedSpecialties]);

  // Efeito para fechar o dropdown quando o usuário clicar fora dele
  useEffect(() => {
    const handlePressOutside = () => {
      if (showRelevanceDropdown) {
        setShowRelevanceDropdown(false);
      }
    };

    // Em React Native para Web poderia usar esse approach
    if (Platform?.OS === 'web' && typeof document !== 'undefined') {
      if (showRelevanceDropdown) {
        document.addEventListener('mousedown', handlePressOutside);
      }
      return () => {
        document.removeEventListener('mousedown', handlePressOutside);
      };
    }
    
    // No React Native nativo, usamos o componente Pressable
    return () => {};
  }, [showRelevanceDropdown]);

  return (
    <View style={styles.container}>
      <Header />
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
              <View style={styles.filtersColumn}>
                {/* Campo de busca dentro da coluna de filtros */}
                <Input
                  icon="search"
                  placeholder="Buscar artistas"
                  value={searchTerm}
                  onChangeText={setSearchTerm}
                  showSearchButton={true}
                  onSearch={handleSearch}
                />

                <View style={styles.filtersHeader}>
                  <Text style={styles.filtersTitle}>Filtros</Text>
                  <TouchableOpacity onPress={resetFilters}>
                    <Text style={styles.clearFiltersText}>Limpar todos</Text>
                  </TouchableOpacity>
                </View>
                
                {/* Localização */}
                <View style={styles.filterSection}>
                  <Text style={styles.filterSectionTitle}>Localização</Text>
                  <Input
                    icon="location-on"
                    placeholder="Sua localização"
                    value={locationTerm}
                    onChangeText={setLocationTerm}
                  />
                </View>
                
                {/* Distância */}
                <View style={styles.filterSection}>
                  <View style={styles.distanceHeader}>
                    <Text style={styles.filterSectionTitle}>Distância</Text>
                    <Text style={styles.distanceValue}>{maxDistance} km</Text>
                  </View>
                  <View 
                    ref={sliderRef}
                    style={styles.slider}
                    {...panResponder.panHandlers}
                  >
                    <View style={styles.sliderTrack} />
                    <View 
                      style={[
                        styles.sliderFill, 
                        { width: `${(maxDistance/50)*100}%` }
                      ]} 
                    />
                    <View 
                      style={[
                        styles.sliderThumb, 
                        { left: `${(maxDistance/50)*100}%` },
                        isSliderActive && styles.sliderThumbActive
                      ]} 
                    />
                    <View style={styles.sliderLabels}>
                      <Text style={styles.sliderLabelText}>0 km</Text>
                      <Text style={styles.sliderLabelText}>50 km</Text>
                    </View>
                  </View>
                </View>
                
                {/* Avaliação */}
                <View style={styles.filterSection}>
                  <Text style={styles.filterSectionTitle}>Avaliação</Text>
                  {[5, 4, 3, 2, 1].map((rating) => (
                    <TouchableOpacity 
                      key={rating}
                      style={styles.ratingItem}
                      onPress={() => setMinRating(minRating === rating ? 0 : rating)}
                    >
                      <View style={[
                        styles.checkbox,
                        minRating === rating && styles.checkboxChecked
                      ]}>
                        {minRating === rating && (
                          <MaterialIcons name="check" size={16} color="#fff" />
                        )}
                      </View>
                      
                      <View style={styles.starsContainer}>
                        {[1, 2, 3, 4, 5].map((star) => (
                          <MaterialIcons 
                            key={star}
                            name="star" 
                            size={16} 
                            color={star <= rating ? "#FFD700" : "#E5E7EB"} 
                          />
                        ))}
                        <Text style={styles.ratingText}> e acima</Text>
                      </View>
                    </TouchableOpacity>
                  ))}
                </View>
                
                {/* Especialidades */}
                <View style={styles.filterSection}>
                  <Text style={styles.filterSectionTitle}>Especialidades</Text>
                  {allSpecialties.map((specialty) => (
                    <TouchableOpacity 
                      key={specialty}
                      style={styles.specialtyItem}
                      onPress={() => toggleSpecialty(specialty)}
                    >
                      <View style={[
                        styles.checkbox,
                        selectedSpecialties.includes(specialty) && styles.checkboxChecked
                      ]}>
                        {selectedSpecialties.includes(specialty) && (
                          <MaterialIcons name="check" size={16} color="#fff" />
                        )}
                      </View>
                      <Text style={styles.specialtyText}>{specialty}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              </View>
            )}
            
            {/* Coluna de artistas (direita) */}
            <View style={[styles.artistsColumn, isMobile && styles.artistsColumnMobile]}>
              {/* Linha separadora para dispositivos móveis */}
              {isMobile && <View style={styles.separator} />}
              
              {/* Filtros móveis (só aparece em dispositivos móveis) */}
              {isMobile && (
                <TouchableOpacity 
                  style={styles.mobileFiltersButton}
                  onPress={() => setShowFiltersModal(true)}
                >
                  <MaterialIcons name="filter-list" size={20} color="#FFFFFF" />
                  <Text style={styles.mobileFiltersButtonText}>
                    Filtros {activeFilters.length > 0 && `(${activeFilters.length})`}
                  </Text>
                </TouchableOpacity>
              )}

              {/* Filtros ativos */}
              {activeFilters.length > 0 && (
                <View style={styles.activeFiltersBar}>
                  {activeFilters.map((filter, index) => (
                    <View key={index} style={styles.filterBadge}>
                      <Text style={styles.filterBadgeText}>{filter.value}</Text>
                      <TouchableOpacity 
                        style={styles.filterBadgeCloseButton}
                        onPress={() => removeFilter(filter)}
                      >
                        <MaterialIcons name="close" size={16} color="#6B7280" />
                      </TouchableOpacity>
                    </View>
                  ))}
                  <TouchableOpacity 
                    style={styles.clearAllButton}
                    onPress={resetFilters}
                  >
                    <Text style={styles.clearAllText}>Limpar todos</Text>
                  </TouchableOpacity>
                </View>
              )}

              {/* Dropdown de relevância para a lista de artistas */}
              <View style={[styles.artistsRelevanceContainer]} ref={relevanceDropdownRef}>
                <TouchableOpacity 
                  style={styles.relevanceButton}
                  onPress={() => setShowRelevanceDropdown(!showRelevanceDropdown)}
                  activeOpacity={0.7}
                >
                  <Text style={styles.relevanceText}>
                    {sortBy === 'relevancia' ? 'Relevância' : 
                     sortBy === 'melhorAvaliacao' ? 'Melhor Avaliação' : 
                     'Mais Recente'}
                  </Text>
                  <MaterialIcons 
                    name={showRelevanceDropdown ? "arrow-drop-up" : "arrow-drop-down"} 
                    size={24} 
                    color="#000000" 
                  />
                </TouchableOpacity>
                
                {/* Menu dropdown de relevância */}
                {showRelevanceDropdown && (
                  <View style={styles.relevanceDropdownContainer}>
                    <TouchableWithoutFeedback onPress={() => setShowRelevanceDropdown(false)}>
                      <View style={styles.relevanceDropdownOverlay} />
                    </TouchableWithoutFeedback>
                    <View style={styles.relevanceDropdown}>
                      <TouchableOpacity
                        style={[
                          styles.relevanceDropdownItem,
                          sortBy === 'relevancia' && styles.relevanceDropdownItemActive
                        ]}
                        onPress={() => {
                          setSortBy('relevancia');
                          setShowRelevanceDropdown(false);
                        }}
                        activeOpacity={0.7}
                      >
                        {sortBy === 'relevancia' && (
                          <MaterialIcons
                            name="check"
                            size={18}
                            color="#000000"
                            style={styles.relevanceDropdownIcon}
                          />
                        )}
                        <Text style={[
                          styles.relevanceDropdownText,
                          sortBy === 'relevancia' && styles.relevanceDropdownTextActive
                        ]}>Relevância</Text>
                      </TouchableOpacity>
                      <TouchableOpacity
                        style={[
                          styles.relevanceDropdownItem,
                          sortBy === 'melhorAvaliacao' && styles.relevanceDropdownItemActive
                        ]}
                        onPress={() => {
                          setSortBy('melhorAvaliacao');
                          setShowRelevanceDropdown(false);
                        }}
                        activeOpacity={0.7}
                      >
                        {sortBy === 'melhorAvaliacao' && (
                          <MaterialIcons
                            name="check"
                            size={18}
                            color="#000000"
                            style={styles.relevanceDropdownIcon}
                          />
                        )}
                        <Text style={[
                          styles.relevanceDropdownText,
                          sortBy === 'melhorAvaliacao' && styles.relevanceDropdownTextActive
                        ]}>Melhor Avaliação</Text>
                      </TouchableOpacity>
                      <TouchableOpacity
                        style={[
                          styles.relevanceDropdownItem,
                          sortBy === 'maisRecente' && styles.relevanceDropdownItemActive
                        ]}
                        onPress={() => {
                          setSortBy('maisRecente');
                          setShowRelevanceDropdown(false);
                        }}
                        activeOpacity={0.7}
                      >
                        {sortBy === 'maisRecente' && (
                          <MaterialIcons
                            name="check"
                            size={18}
                            color="#000000"
                            style={styles.relevanceDropdownIcon}
                          />
                        )}
                        <Text style={[
                          styles.relevanceDropdownText,
                          sortBy === 'maisRecente' && styles.relevanceDropdownTextActive
                        ]}>Mais Recente</Text>
                      </TouchableOpacity>
                    </View>
                  </View>
                )}
              </View>
              
              {/* Grid de artistas */}
              <View style={styles.artistsGrid}>
                {displayedArtists.length > 0 ? (
                  displayedArtists.map((artist) => (
                    <TouchableOpacity 
                      key={artist.id}
                      style={[
                        styles.artistCard,
                        numColumns === 3 ? { width: '32%', marginHorizontal: '0.66%' } :
                        numColumns === 2 ? { width: '48%', marginHorizontal: '1%' } :
                        { width: '100%' }
                      ]}
                      onPress={() => navigation.navigate('ArtistDetail', { artistId: artist.id })}
                    >
                      <ArtistCard artist={artist} />
                    </TouchableOpacity>
                  ))
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
                <View style={styles.paginationContainer}>
                  <TouchableOpacity 
                    style={[styles.paginationButton, currentPage === 1 && styles.paginationButtonDisabled]}
                    onPress={() => currentPage > 1 && setCurrentPage(currentPage - 1)}
                    disabled={currentPage === 1}
                  >
                    <Text style={[
                      styles.paginationButtonText, 
                      currentPage === 1 && styles.paginationButtonTextDisabled
                    ]}>Anterior</Text>
                  </TouchableOpacity>
                  
                  {[1, 2, 3].map((page) => (
                    <TouchableOpacity 
                      key={page}
                      style={[
                        styles.paginationButton, 
                        currentPage === page && styles.paginationButtonActive
                      ]}
                      onPress={() => setCurrentPage(page)}
                    >
                      <Text style={[
                        styles.paginationButtonText, 
                        currentPage === page && styles.paginationButtonTextActive
                      ]}>{page}</Text>
                    </TouchableOpacity>
                  ))}
                  
                  <Text style={styles.paginationEllipsis}>...</Text>
                  
                  <TouchableOpacity 
                    style={styles.paginationButton}
                    onPress={() => setCurrentPage(10)}
                  >
                    <Text style={styles.paginationButtonText}>10</Text>
                  </TouchableOpacity>
                  
                  <TouchableOpacity 
                    style={styles.paginationButton}
                    onPress={() => currentPage < 10 && setCurrentPage(currentPage + 1)}
                  >
                    <Text style={styles.paginationButtonText}>Próxima</Text>
                  </TouchableOpacity>
                </View>
              )}
            </View>
          </View>
        </View>
        
        <Footer />
      </ScrollView>
      
      {/* Modal de filtros para dispositivos móveis */}
      <Modal
        visible={showFiltersModal}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setShowFiltersModal(false)}
        statusBarTranslucent={true}
      >
        <TouchableWithoutFeedback onPress={() => setShowFiltersModal(false)}>
          <View style={styles.modalOverlay} />
        </TouchableWithoutFeedback>
        
        <View style={styles.modalContainer}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Filtros</Text>
              <TouchableOpacity onPress={() => setShowFiltersModal(false)}>
                <MaterialIcons name="close" size={24} color="#000000" />
              </TouchableOpacity>
            </View>
            
            <ScrollView style={styles.modalScrollView}>
              {/* Campo de busca no modal */}
              <Input
                icon="search"
                placeholder="Buscar artistas"
                value={searchTerm}
                onChangeText={setSearchTerm}
                showSearchButton={true}
                onSearch={() => {
                  handleSearch();
                  setShowFiltersModal(false);
                }}
              />
              
              {/* Localização */}
              <View style={styles.filterSection}>
                <Text style={styles.filterSectionTitle}>Localização</Text>
                <Input
                  icon="location-on"
                  placeholder="Sua localização"
                  value={locationTerm}
                  onChangeText={setLocationTerm}
                />
              </View>
              
              {/* Distância */}
              <View style={styles.filterSection}>
                <View style={styles.distanceHeader}>
                  <Text style={styles.filterSectionTitle}>Distância</Text>
                  <Text style={styles.distanceValue}>{maxDistance} km</Text>
                </View>
                <View 
                  ref={sliderRef}
                  style={styles.slider}
                  {...panResponder.panHandlers}
                >
                  <View style={styles.sliderTrack} />
                  <View 
                    style={[
                      styles.sliderFill, 
                      { width: `${(maxDistance/50)*100}%` }
                    ]} 
                  />
                  <View 
                    style={[
                      styles.sliderThumb, 
                      { left: `${(maxDistance/50)*100}%` },
                      isSliderActive && styles.sliderThumbActive
                    ]} 
                  />
                  <View style={styles.sliderLabels}>
                    <Text style={styles.sliderLabelText}>0 km</Text>
                    <Text style={styles.sliderLabelText}>50 km</Text>
                  </View>
                </View>
              </View>
              
              {/* Avaliação */}
              <View style={styles.filterSection}>
                <Text style={styles.filterSectionTitle}>Avaliação</Text>
                {[5, 4, 3, 2, 1].map((rating) => (
                  <TouchableOpacity 
                    key={rating}
                    style={styles.ratingItem}
                    onPress={() => setMinRating(minRating === rating ? 0 : rating)}
                  >
                    <View style={[
                      styles.checkbox,
                      minRating === rating && styles.checkboxChecked
                    ]}>
                      {minRating === rating && (
                        <MaterialIcons name="check" size={16} color="#fff" />
                      )}
                    </View>
                    
                    <View style={styles.starsContainer}>
                      {[1, 2, 3, 4, 5].map((star) => (
                        <MaterialIcons 
                          key={star}
                          name="star" 
                          size={16} 
                          color={star <= rating ? "#FFD700" : "#E5E7EB"} 
                        />
                      ))}
                      <Text style={styles.ratingText}> e acima</Text>
                    </View>
                  </TouchableOpacity>
                ))}
              </View>
              
              {/* Especialidades */}
              <View style={styles.filterSection}>
                <Text style={styles.filterSectionTitle}>Especialidades</Text>
                {allSpecialties.map((specialty) => (
                  <TouchableOpacity 
                    key={specialty}
                    style={styles.specialtyItem}
                    onPress={() => toggleSpecialty(specialty)}
                  >
                    <View style={[
                      styles.checkbox,
                      selectedSpecialties.includes(specialty) && styles.checkboxChecked
                    ]}>
                      {selectedSpecialties.includes(specialty) && (
                        <MaterialIcons name="check" size={16} color="#fff" />
                      )}
                    </View>
                    <Text style={styles.specialtyText}>{specialty}</Text>
                  </TouchableOpacity>
                ))}
              </View>
            </ScrollView>
            
            <View style={styles.modalFooter}>
              <TouchableOpacity 
                style={styles.modalCancelButton}
                onPress={() => {
                  resetFilters();
                  setShowFiltersModal(false);
                }}
              >
                <Text style={styles.modalCancelButtonText}>Limpar todos</Text>
              </TouchableOpacity>
              <TouchableOpacity 
                style={styles.modalApplyButton}
                onPress={() => {
                  applyFilters();
                  setShowFiltersModal(false);
                }}
              >
                <Text style={styles.modalApplyButtonText}>Aplicar filtros</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
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
    alignItems: 'flex-start',
  },
  
  // Layout principal
  mainContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  mainContainerMobile: {
    flexDirection: 'column',
  },
  
  // Coluna de filtros
  filtersColumn: {
    width: '25%',
    paddingRight: 16,
  },
  filtersHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
    paddingBottom: 16,
    marginTop: 16,
  },
  filtersTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
  },
  clearFiltersText: {
    fontSize: 14,
    color: '#000000',
    fontWeight: '500',
  },
  
  // Seção de filtros
  filterSection: {
    marginBottom: 24,
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
    paddingTop: 16,
  },
  filterSectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 12,
  },
  
  // Distância
  distanceHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  distanceValue: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111827',
  },
  slider: {
    height: 4,
    backgroundColor: '#E5E7EB',
    borderRadius: 2,
    marginVertical: 12,
    position: 'relative',
    marginBottom: 24,
  },
  sliderTrack: {
    height: 4,
    width: '100%',
    backgroundColor: '#E5E7EB',
    borderRadius: 2,
    position: 'absolute',
  },
  sliderFill: {
    height: 4,
    backgroundColor: '#000000',
    borderRadius: 2,
    position: 'absolute',
  },
  sliderThumb: {
    width: 16,
    height: 16,
    backgroundColor: '#000000',
    borderRadius: 8,
    position: 'absolute',
    top: -6,
    marginLeft: -8,
  },
  sliderThumbActive: {
    transform: [{ scale: 1.2 }],
  },
  sliderLabels: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    position: 'absolute',
    width: '100%',
    top: 10,
  },
  sliderLabelText: {
    fontSize: 12,
    color: '#6B7280',
  },
  
  // Avaliação
  ratingItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  checkbox: {
    width: 20,
    height: 20,
    borderRadius: 4,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    backgroundColor: '#FFFFFF',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 8,
  },
  checkboxChecked: {
    backgroundColor: '#000000',
    borderColor: '#000000',
  },
  starsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  ratingText: {
    fontSize: 14,
    color: '#000000',
  },
  
  // Especialidades
  specialtyItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  specialtyText: {
    fontSize: 14,
    color: '#111827',
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
  
  // Dropdown de relevância no contexto dos artistas
  artistsRelevanceContainer: {
    marginBottom: 16,
    zIndex: 100,
    alignSelf: 'flex-end',
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
  
  // Grid de artistas
  artistsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 24,
    marginTop: 8,
  },
  artistCard: {
    marginBottom: 16,
  },
  
  // Nenhum resultado
  noResultsContainer: {
    padding: 48,
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  noResultsTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
    textAlign: 'center',
    color: '#111827',
  },
  noResultsSubtitle: {
    fontSize: 16,
    color: '#6B7280',
    textAlign: 'center',
  },
  
  // Paginação
  paginationContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 16,
    marginBottom: 32,
    flexWrap: 'wrap',
  },
  paginationButton: {
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 6,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginHorizontal: 4,
    marginBottom: 8,
  },
  paginationButtonActive: {
    backgroundColor: '#000000',
    borderColor: '#000000',
  },
  paginationButtonDisabled: {
    borderColor: '#E5E7EB',
    opacity: 0.5,
  },
  paginationButtonText: {
    fontSize: 14,
    color: '#111827',
  },
  paginationButtonTextActive: {
    color: '#FFFFFF',
  },
  paginationButtonTextDisabled: {
    color: '#9CA3AF',
  },
  paginationEllipsis: {
    marginHorizontal: 4,
    fontSize: 16,
    color: '#111827',
  },
  
  // Filtros ativos
  activeFiltersBar: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
    marginBottom: 16,
  },
  filterBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F3F4F6',
    borderRadius: 20,
    paddingVertical: 6,
    paddingHorizontal: 12,
    marginRight: 8,
    marginBottom: 8,
  },
  filterBadgeText: {
    fontSize: 14,
    color: '#111827',
    marginRight: 8,
  },
  filterBadgeCloseButton: {
    width: 16,
    height: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  clearAllButton: {
    marginLeft: 4,
    marginBottom: 8,
  },
  clearAllText: {
    fontSize: 14,
    color: '#000000',
    fontWeight: '500',
  },
  
  // Relevância
  relevanceContainer: {
    position: 'relative',
    zIndex: 100,
  },
  relevanceContainerMobile: {
    alignItems: 'flex-start',
    marginTop: 12,
    width: '100%',
    marginBottom: 16,
  },
  relevanceButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 6,
    paddingHorizontal: 12,
    paddingVertical: 8,
    height: 40,
    minWidth: 140,
  },
  relevanceText: {
    fontSize: 14,
    color: '#111827',
    marginRight: 4,
  },
  relevanceDropdownContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    zIndex: 10,
  },
  relevanceDropdownOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'transparent',
  },
  relevanceDropdown: {
    position: 'absolute',
    top: 40,
    right: 0,
    width: 180,
    backgroundColor: '#FFFFFF',
    borderRadius: 6,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    zIndex: 20,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    overflow: 'hidden',
  },
  relevanceDropdownItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 10,
    paddingHorizontal: 16,
    backgroundColor: '#FFFFFF',
  },
  relevanceDropdownItemActive: {
    backgroundColor: '#F9FAFB',
  },
  relevanceDropdownIcon: {
    marginRight: 8,
    width: 18,
  },
  relevanceDropdownText: {
    fontSize: 14,
    color: '#111827',
  },
  relevanceDropdownTextActive: {
    fontWeight: '500',
  },
  
  // Estilos para o modal
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    maxHeight: '80%',
  },
  modalContent: {
    flex: 1,
  },
  modalHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
  },
  modalScrollView: {
    padding: 16,
  },
  modalFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 16,
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
  },
  modalCancelButton: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 6,
    paddingVertical: 12,
    marginRight: 8,
    alignItems: 'center',
  },
  modalCancelButtonText: {
    fontSize: 16,
    color: '#111827',
    fontWeight: '500',
  },
  modalApplyButton: {
    flex: 1,
    backgroundColor: '#000000',
    borderRadius: 6,
    paddingVertical: 12,
    marginLeft: 8,
    alignItems: 'center',
  },
  modalApplyButtonText: {
    fontSize: 16,
    color: '#FFFFFF',
    fontWeight: '500',
  },
});

export default ExploreScreen;