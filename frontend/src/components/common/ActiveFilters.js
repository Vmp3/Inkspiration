import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

const ActiveFilters = ({ activeFilters, removeFilter, resetFilters }) => {
  if (activeFilters.length === 0) return null;
  
  return (
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
  );
};

const styles = StyleSheet.create({
  activeFiltersBar: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
    width: '100%',
    maxWidth: '100%',
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
    maxWidth: 200,
  },
  filterBadgeText: {
    fontSize: 14,
    color: '#111827',
    marginRight: 8,
    flexShrink: 1,
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
});

export default ActiveFilters; 