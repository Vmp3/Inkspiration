import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import Input from '../ui/Input';
import RatingFilter from './RatingFilter';
import SpecialtiesFilter from './SpecialtiesFilter';
import DistanceSlider from './DistanceSlider';
import FilterSection from './FilterSection';

const FiltersPanel = ({
  searchTerm,
  setSearchTerm,
  locationTerm,
  setLocationTerm,
  minRating,
  setMinRating,
  maxDistance,
  setMaxDistance,
  selectedSpecialties,
  toggleSpecialty,
  handleSearch,
  resetFilters,
  updateActiveFilters,
}) => {
  return (
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
      <FilterSection title="Localização">
        <Input
          icon="location-on"
          placeholder="Sua localização"
          value={locationTerm}
          onChangeText={setLocationTerm}
        />
      </FilterSection>
      
      {/* Distância */}
      <DistanceSlider 
        maxDistance={maxDistance} 
        setMaxDistance={setMaxDistance}
        onSliderRelease={updateActiveFilters}
      />
      
      {/* Avaliação */}
      <RatingFilter minRating={minRating} setMinRating={setMinRating} />
      
      {/* Especialidades */}
      <SpecialtiesFilter 
        selectedSpecialties={selectedSpecialties} 
        toggleSpecialty={toggleSpecialty} 
      />
    </View>
  );
};

const styles = StyleSheet.create({
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
});

export default FiltersPanel; 