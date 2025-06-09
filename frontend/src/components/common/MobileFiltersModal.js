import React from 'react';
import { View, Text, Modal, TouchableOpacity, Pressable, ScrollView, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import Input from '../ui/Input';
import FilterSection from '../explore/FilterSection';
import RatingFilter from '../explore/RatingFilter';
import SpecialtiesFilter from '../explore/SpecialtiesFilter';
import DistanceSlider from '../explore/DistanceSlider';

const MobileFiltersModal = ({
  visible,
  onClose,
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
  applyFilters,
  updateActiveFilters,
}) => {
  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="slide"
      onRequestClose={onClose}
      statusBarTranslucent={true}
    >
      <View style={styles.modalContainer}>
        <Pressable style={styles.overlay} onPress={onClose} />
        
        <View style={styles.modalContent}>
          <View style={styles.modalHeader}>
            <Text style={styles.modalTitle}>Filtros</Text>
            <TouchableOpacity onPress={onClose}>
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
            />
            
            {/* Localização */}
            <FilterSection title="Localização">
              <Input
                icon="location-on"
                placeholder="Sua localização"
                value={locationTerm}
                onChangeText={setLocationTerm}
              />
            </FilterSection>
            
            {/* Distância - exibir apenas quando o maxDistance não for 0 */}
            {maxDistance > 0 && (
              <DistanceSlider 
                maxDistance={maxDistance} 
                setMaxDistance={setMaxDistance}
                onSliderRelease={updateActiveFilters}
              />
            )}
            
            {/* Avaliação */}
            <RatingFilter minRating={minRating} setMinRating={setMinRating} />
            
            {/* Especialidades */}
            <SpecialtiesFilter 
              selectedSpecialties={selectedSpecialties} 
              toggleSpecialty={toggleSpecialty} 
            />
          </ScrollView>
          
          <View style={styles.modalFooter}>
            <TouchableOpacity 
              style={styles.modalCancelButton}
              onPress={() => {
                resetFilters();
                onClose();
              }}
            >
              <Text style={styles.modalCancelButtonText}>Limpar todos</Text>
            </TouchableOpacity>
            <TouchableOpacity 
              style={styles.modalApplyButton}
              onPress={() => {
                applyFilters();
                handleSearch();
                onClose();
              }}
            >
              <Text style={styles.modalApplyButtonText}>Aplicar filtros</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: {
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

export default MobileFiltersModal; 