import React from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  TouchableOpacity,
  TouchableWithoutFeedback,
  Dimensions,
  Modal
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import Checkbox from './ui/Checkbox';

const FilterDropdown = ({ 
  visible, 
  onClose, 
  minRating, 
  setMinRating, 
  selectedSpecialties, 
  toggleSpecialty, 
  resetFilters,
  applyFilters,
  anchorPosition = { top: 0, left: 0 }
}) => {
  if (!visible) return null;

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

  const windowWidth = Dimensions.get('window').width;

  return (
    <View style={styles.dropdownWrapper} pointerEvents="box-none">
      <TouchableWithoutFeedback onPress={onClose}>
        <View style={styles.touchableOverlay} />
      </TouchableWithoutFeedback>
      
      <View 
        style={[
          styles.container, 
          { 
            top: anchorPosition.top + 50, 
            left: windowWidth < 768 ? 16 : anchorPosition.left,
            right: windowWidth < 768 ? 16 : undefined,
          }
        ]}
      >
        <View style={styles.arrowUp} />
        <View style={styles.header}>
          <Text style={styles.heading}>Filtros</Text>
          <TouchableOpacity
            style={styles.resetButton}
            onPress={resetFilters}
          >
            <Text style={styles.resetButtonText}>Limpar todos</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Avaliação Mínima</Text>
          <View style={styles.ratingContainer}>
            <View style={styles.starsContainer}>
              {[1, 2, 3, 4, 5].map((rating) => (
                <TouchableOpacity
                  key={rating}
                  onPress={() => setMinRating(rating)}
                >
                  <MaterialIcons
                    name="star"
                    size={24}
                    color={rating <= minRating ? "#FFD700" : "#E5E7EB"}
                    style={rating <= minRating ? styles.starFilled : {}}
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
          <View style={styles.specialtiesGrid}>
            {allSpecialties.map((specialty) => (
              <View key={specialty} style={styles.checkboxContainer}>
                <Checkbox
                  checked={selectedSpecialties.includes(specialty)}
                  onPress={() => toggleSpecialty(specialty)}
                />
                <Text style={styles.checkboxLabel}>{specialty}</Text>
              </View>
            ))}
          </View>
        </View>

        <TouchableOpacity 
          style={styles.applyButton}
          onPress={() => {
            applyFilters();
            onClose();
          }}
        >
          <Text style={styles.applyButtonText}>Aplicar Filtros</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  dropdownWrapper: {
    ...StyleSheet.absoluteFillObject,
    pointerEvents: 'box-none',
    zIndex: 1000,
  },
  touchableOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  container: {
    position: 'absolute',
    backgroundColor: 'white',
    borderRadius: 12,
    width: '100%',
    maxWidth: 400,
    padding: 20,
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
    zIndex: 1001,
  },
  arrowUp: {
    position: 'absolute',
    top: -10,
    left: 20,
    width: 0,
    height: 0,
    backgroundColor: 'transparent',
    borderStyle: 'solid',
    borderLeftWidth: 10,
    borderRightWidth: 10,
    borderBottomWidth: 10,
    borderLeftColor: 'transparent',
    borderRightColor: 'transparent',
    borderBottomColor: 'white',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
  },
  heading: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
  },
  resetButton: {
    padding: 4,
  },
  resetButtonText: {
    fontSize: 14,
    color: '#6366F1',
    fontWeight: '500',
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 12,
  },
  ratingContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  starsContainer: {
    flexDirection: 'row',
  },
  starFilled: {
    textShadowColor: 'rgba(0, 0, 0, 0.1)',
    textShadowOffset: { width: 1, height: 1 },
    textShadowRadius: 1,
  },
  ratingText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111827',
  },
  specialtiesGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  checkboxContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '50%',
    marginBottom: 12,
  },
  checkboxLabel: {
    fontSize: 14,
    color: '#111827',
    marginLeft: 8,
  },
  applyButton: {
    backgroundColor: '#111827',
    borderRadius: 6,
    padding: 12,
    alignItems: 'center',
    marginTop: 8,
  },
  applyButtonText: {
    color: 'white',
    fontSize: 14,
    fontWeight: '600',
  },
});

export default FilterDropdown; 