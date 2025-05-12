import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const FilterSection = ({ title, children, style }) => {
  return (
    <View style={[styles.filterSection, style]}>
      <Text style={styles.filterSectionTitle}>{title}</Text>
      {children}
    </View>
  );
};

const styles = StyleSheet.create({
  filterSection: {
    marginBottom: 24,
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
    paddingTop: 16,
  },
  filterSectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 12,
  },
});

export default FilterSection; 