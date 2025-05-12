import React from 'react';
import { TouchableOpacity, Text, StyleSheet, View } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

const FilterButton = ({ onPress, filterCount = 0 }) => {
  return (
    <TouchableOpacity style={styles.button} onPress={onPress}>
      <MaterialIcons name="tune" size={18} color="#111827" />
      <Text style={styles.buttonText}>Filtros</Text>
      {filterCount > 0 && (
        <View style={styles.badge}>
          <Text style={styles.badgeText}>{filterCount}</Text>
        </View>
      )}
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F3F4F6',
    borderRadius: 4,
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  buttonText: {
    marginLeft: 6,
    fontSize: 14,
    fontWeight: '500',
    color: '#111827',
  },
  badge: {
    backgroundColor: '#E5E7EB',
    width: 18,
    height: 18,
    borderRadius: 9,
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: 6,
  },
  badgeText: {
    fontSize: 10,
    fontWeight: 'bold',
    color: '#4B5563',
  },
});

export default FilterButton; 