import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Dimensions } from 'react-native';
import Input from '../ui/Input';
import SearchInput from '../ui/SearchInput';
import RatingFilter from './RatingFilter';
import SpecialtiesFilter from './SpecialtiesFilter';
import FilterSection from './FilterSection';
import Button from '../ui/Button';

const FiltersPanel = ({
  searchTerm,
  setSearchTerm,
  locationTerm,
  setLocationTerm,
  minRating,
  setMinRating,
  selectedSpecialties,
  toggleSpecialty,
  handleSearch,
  resetFilters,
  updateActiveFilters,
}) => {
  const screenWidth = Dimensions.get('window').width;
  const isTablet = screenWidth >= 768 && screenWidth < 1024;
  
  return (
    <View style={styles.filtersColumn}>
      {isTablet ? (
        <View style={styles.searchColumn}>
          <View style={styles.searchInputContainer}>
            <SearchInput
              icon="search"
              placeholder="Buscar artistas"
              value={searchTerm}
              onChangeText={setSearchTerm}
            />
          </View>
          <Button
            variant="primary"
            label="Buscar"
            onPress={handleSearch}
            style={styles.searchButton}
            size="search"
            fullWidth={true}
          />
        </View>
      ) : (
        <View style={styles.searchRow}>
          <View style={styles.searchInputContainer}>
            <SearchInput
              icon="search"
              placeholder="Buscar artistas"
              value={searchTerm}
              onChangeText={setSearchTerm}
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

      <View style={styles.filtersHeader}>
        <Text style={styles.filtersTitle}>Filtros</Text>
        <TouchableOpacity onPress={resetFilters}>
          <Text style={styles.clearFiltersText}>Limpar todos</Text>
        </TouchableOpacity>
      </View>
      
      <FilterSection title="Localização">
        <SearchInput
          icon="location-on"
          placeholder="Sua localização"
          value={locationTerm}
          onChangeText={setLocationTerm}
        />
      </FilterSection>
      <RatingFilter minRating={minRating} setMinRating={setMinRating} />
      
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
  searchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    marginBottom: 16,
  },
  searchColumn: {
    flexDirection: 'column',
    marginBottom: 16,
    gap: 8,
    width: '100%',
  },
  searchInputContainer: {
    flex: 1,
    width: '100%',
  },
  searchButton: {
    paddingHorizontal: 16,
    minWidth: 0,
    height: 40,
    alignSelf: 'stretch',
  },
  filtersHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
    paddingBottom: 16,
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