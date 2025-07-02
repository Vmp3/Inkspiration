import React from 'react';
import { View, Text, Modal, TouchableOpacity, Pressable, ScrollView, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import Input from '../ui/Input';
import FilterSection from '../explore/FilterSection';
import RatingFilter from '../explore/RatingFilter';
import SpecialtiesFilter from '../explore/SpecialtiesFilter';
import Button from '../ui/Button';

const MobileFiltersModal = ({
  visible,
  onClose,
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
  applyFilters,
  updateActiveFilters,
}) => {
  const applyAndClose = () => {
    applyFilters();
    onClose();
  };

  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="slide"
      onRequestClose={onClose}
      statusBarTranslucent={false}
    >
      <View style={styles.modalOverlay}>
        <Pressable style={styles.overlay} onPress={onClose} />
        
        <View style={styles.modalContainer}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Filtros</Text>
              <TouchableOpacity onPress={onClose} style={styles.closeButton}>
                <MaterialIcons name="close" size={24} color="#000000" />
              </TouchableOpacity>
            </View>
            
            <ScrollView 
              style={styles.modalScrollView}
              contentContainerStyle={styles.scrollContent}
              showsVerticalScrollIndicator={true}
              nestedScrollEnabled={true}
            >
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
              
              {/* Avaliação mínima */}
              <RatingFilter minRating={minRating} setMinRating={setMinRating} />
              
              {/* Especialidades */}
              <SpecialtiesFilter 
                selectedSpecialties={selectedSpecialties} 
                toggleSpecialty={toggleSpecialty} 
              />
            </ScrollView>
            
            <View style={styles.modalFooter}>
              <Button
                variant="outline"
                label="Limpar todos"
                onPress={resetFilters}
                style={styles.clearButton}
              />
              <Button
                variant="primary"
                label="Aplicar filtros"
                onPress={applyAndClose}
                style={styles.applyButton}
              />
            </View>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalOverlay: {
    flex: 1,
    justifyContent: 'flex-end',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  overlay: {
    flex: 1,
  },
  modalContainer: {
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    maxHeight: '90%',
    minHeight: '70%',
  },
  modalContent: {
    flex: 1,
    flexDirection: 'column',
  },
  modalHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
  },
  closeButton: {
    padding: 4,
  },
  modalScrollView: {
    flex: 1,
    paddingHorizontal: 16,
  },
  scrollContent: {
    paddingTop: 16,
    paddingBottom: 20,
  },
  modalFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 16,
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
    gap: 12,
  },
  clearButton: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 6,
    paddingVertical: 12,
    alignItems: 'center',
  },
  applyButton: {
    flex: 1,
    backgroundColor: '#000000',
    borderRadius: 6,
    paddingVertical: 12,
    alignItems: 'center',
  },
});

export default MobileFiltersModal; 