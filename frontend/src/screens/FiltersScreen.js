import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MaterialIcons } from '@expo/vector-icons';
import Header from '../components/Header';

const FiltersScreen = ({ navigation, route }) => {
  const [minRating, setMinRating] = useState(0);
  const [selectedSpecialties, setSelectedSpecialties] = useState([]);

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

  // Aplicar filtros e voltar para a tela anterior
  const applyFilters = () => {
    // Aqui você poderia passar os filtros de volta para a tela anterior
    navigation.goBack();
  };

  return (
    <SafeAreaView style={styles.container}>
      <Header />
      <ScrollView style={styles.scrollView}>
        <View style={styles.content}>
          <View style={styles.header}>
            <Text style={styles.title}>Filtros</Text>
            <TouchableOpacity onPress={resetFilters}>
              <Text style={styles.resetButton}>Limpar todos</Text>
            </TouchableOpacity>
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Avaliação Mínima</Text>
            <View style={styles.ratingContainer}>
              <View style={styles.stars}>
                {[1, 2, 3, 4, 5].map((rating) => (
                  <TouchableOpacity key={rating} onPress={() => setMinRating(rating)}>
                    <MaterialIcons
                      name="star"
                      size={30}
                      color={rating <= minRating ? "#FFD700" : "#E5E7EB"}
                      style={styles.star}
                    />
                  </TouchableOpacity>
                ))}
              </View>
              <Text style={styles.ratingText}>
                {minRating} estrela{minRating !== 1 ? "s" : ""}
              </Text>
            </View>
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Especialidades</Text>
            <View style={styles.specialtiesContainer}>
              {allSpecialties.map((specialty) => (
                <TouchableOpacity
                  key={specialty}
                  style={[
                    styles.specialtyButton,
                    selectedSpecialties.includes(specialty) && styles.specialtyButtonSelected,
                  ]}
                  onPress={() => toggleSpecialty(specialty)}
                >
                  <Text
                    style={[
                      styles.specialtyText,
                      selectedSpecialties.includes(specialty) && styles.specialtyTextSelected,
                    ]}
                  >
                    {specialty}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>

          <TouchableOpacity style={styles.applyButton} onPress={applyFilters}>
            <Text style={styles.applyButtonText}>Aplicar Filtros</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
  },
  scrollView: {
    flex: 1,
  },
  content: {
    padding: 16,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 24,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#111827',
  },
  resetButton: {
    color: '#6366F1',
    fontSize: 14,
    fontWeight: '500',
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 12,
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  stars: {
    flexDirection: 'row',
  },
  star: {
    marginRight: 8,
  },
  ratingText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111827',
  },
  specialtiesContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  specialtyButton: {
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 4,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginBottom: 8,
  },
  specialtyButtonSelected: {
    backgroundColor: '#F3F4F6',
    borderColor: '#D1D5DB',
  },
  specialtyText: {
    fontSize: 14,
    color: '#4B5563',
  },
  specialtyTextSelected: {
    fontWeight: '500',
    color: '#111827',
  },
  applyButton: {
    backgroundColor: '#111827',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 24,
  },
  applyButtonText: {
    color: '#FFFFFF',
    fontWeight: '600',
    fontSize: 16,
  },
});

export default FiltersScreen; 