import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import FilterSection from './FilterSection';

const SpecialtiesFilter = ({ 
  selectedSpecialties, 
  toggleSpecialty, 
  specialties = [
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
  ]
}) => {
  return (
    <FilterSection title="Especialidades">
      {specialties.map((specialty) => (
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
    </FilterSection>
  );
};

const styles = StyleSheet.create({
  specialtyItem: {
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
  specialtyText: {
    fontSize: 14,
    color: '#111827',
  },
});

export default SpecialtiesFilter; 