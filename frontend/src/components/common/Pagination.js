import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

const Pagination = ({ currentPage, setCurrentPage, totalPages = 10 }) => {
  return (
    <View style={styles.paginationContainer}>
      <TouchableOpacity 
        style={[styles.paginationButton, currentPage === 1 && styles.paginationButtonDisabled]}
        onPress={() => currentPage > 1 && setCurrentPage(currentPage - 1)}
        disabled={currentPage === 1}
      >
        <Text style={[
          styles.paginationButtonText, 
          currentPage === 1 && styles.paginationButtonTextDisabled
        ]}>Anterior</Text>
      </TouchableOpacity>
      
      {[1, 2, 3].map((page) => (
        <TouchableOpacity 
          key={page}
          style={[
            styles.paginationButton, 
            currentPage === page && styles.paginationButtonActive
          ]}
          onPress={() => setCurrentPage(page)}
        >
          <Text style={[
            styles.paginationButtonText, 
            currentPage === page && styles.paginationButtonTextActive
          ]}>{page}</Text>
        </TouchableOpacity>
      ))}
      
      <Text style={styles.paginationEllipsis}>...</Text>
      
      <TouchableOpacity 
        style={styles.paginationButton}
        onPress={() => setCurrentPage(totalPages)}
      >
        <Text style={styles.paginationButtonText}>{totalPages}</Text>
      </TouchableOpacity>
      
      <TouchableOpacity 
        style={styles.paginationButton}
        onPress={() => currentPage < totalPages && setCurrentPage(currentPage + 1)}
      >
        <Text style={styles.paginationButtonText}>Próxima</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  paginationContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 16,
    marginBottom: 32,
    flexWrap: 'wrap',
  },
  paginationButton: {
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 6,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginHorizontal: 4,
    marginBottom: 8,
  },
  paginationButtonActive: {
    backgroundColor: '#000000',
    borderColor: '#000000',
  },
  paginationButtonDisabled: {
    borderColor: '#E5E7EB',
    opacity: 0.5,
  },
  paginationButtonText: {
    fontSize: 14,
    color: '#111827',
  },
  paginationButtonTextActive: {
    color: '#FFFFFF',
  },
  paginationButtonTextDisabled: {
    color: '#9CA3AF',
  },
  paginationEllipsis: {
    marginHorizontal: 4,
    fontSize: 16,
    color: '#111827',
  },
});

export default Pagination; 