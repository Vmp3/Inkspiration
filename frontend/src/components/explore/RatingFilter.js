import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import FilterSection from './FilterSection';

const RatingFilter = ({ minRating, setMinRating }) => {
  return (
    <FilterSection title="Avaliação">
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
    </FilterSection>
  );
};

const styles = StyleSheet.create({
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
    color: '#6B7280',
  },
});

export default RatingFilter; 