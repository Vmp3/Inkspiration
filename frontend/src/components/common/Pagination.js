import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

const Pagination = ({ currentPage, setCurrentPage, totalPages = 1 }) => {
  if (totalPages <= 1) return null;
  
  const displayCurrentPage = currentPage + 1;
  
  const getPageNumbers = () => {
    const pages = [];
    const maxVisiblePages = 5;
    
    if (totalPages <= maxVisiblePages) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (displayCurrentPage <= 3) {
        pages.push(1, 2, 3, 4, 5);
      } else if (displayCurrentPage >= totalPages - 2) {
        for (let i = totalPages - 4; i <= totalPages; i++) {
          pages.push(i);
        }
      } else {
        for (let i = displayCurrentPage - 2; i <= displayCurrentPage + 2; i++) {
          pages.push(i);
        }
      }
    }
    
    return pages;
  };

  const pageNumbers = getPageNumbers();
  const showFirstPage = pageNumbers[0] > 1;
  const showLastPage = pageNumbers[pageNumbers.length - 1] < totalPages;

  // A paginação é exibida somente se tiver mais de uma página
  return (
    <View style={styles.paginationContainer}>
      {/* Botão Anterior */}
      <TouchableOpacity 
        style={[styles.paginationButton, currentPage === 0 && styles.paginationButtonDisabled]}
        onPress={() => currentPage > 0 && setCurrentPage(currentPage - 1)}
        disabled={currentPage === 0}
      >
        <Text style={[
          styles.paginationButtonText, 
          currentPage === 0 && styles.paginationButtonTextDisabled
        ]}>Anterior</Text>
      </TouchableOpacity>
      
      {/* Primeira página se necessário */}
      {showFirstPage && (
        <>
          <TouchableOpacity 
            style={styles.paginationButton}
            onPress={() => setCurrentPage(0)}
          >
            <Text style={styles.paginationButtonText}>1</Text>
          </TouchableOpacity>
          {pageNumbers[0] > 2 && (
            <Text style={styles.paginationEllipsis}>...</Text>
          )}
        </>
      )}
      
      {/* Páginas do meio */}
      {pageNumbers.map((pageNum) => (
        <TouchableOpacity 
          key={pageNum}
          style={[
            styles.paginationButton, 
            displayCurrentPage === pageNum && styles.paginationButtonActive
          ]}
          onPress={() => setCurrentPage(pageNum - 1)}
        >
          <Text style={[
            styles.paginationButtonText, 
            displayCurrentPage === pageNum && styles.paginationButtonTextActive
          ]}>{pageNum}</Text>
        </TouchableOpacity>
      ))}
      
      {/* Última página se necessário */}
      {showLastPage && (
        <>
          {pageNumbers[pageNumbers.length - 1] < totalPages - 1 && (
            <Text style={styles.paginationEllipsis}>...</Text>
          )}
          <TouchableOpacity 
            style={styles.paginationButton}
            onPress={() => setCurrentPage(totalPages - 1)}
          >
            <Text style={styles.paginationButtonText}>{totalPages}</Text>
          </TouchableOpacity>
        </>
      )}
      
      {/* Botão Próxima */}
      <TouchableOpacity 
        style={[styles.paginationButton, currentPage >= totalPages - 1 && styles.paginationButtonDisabled]}
        onPress={() => currentPage < totalPages - 1 && setCurrentPage(currentPage + 1)}
        disabled={currentPage >= totalPages - 1}
      >
        <Text style={[
          styles.paginationButtonText,
          currentPage >= totalPages - 1 && styles.paginationButtonTextDisabled
        ]}>Próxima</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  paginationContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 24,
    marginBottom: 32,
    flexWrap: 'wrap',
    gap: 8,
  },
  paginationButton: {
    borderWidth: 2,
    borderColor: '#D1D5DB',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 10,
    marginHorizontal: 2,
    marginBottom: 8,
    backgroundColor: '#FFFFFF',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 1,
    },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 1,
  },
  paginationButtonActive: {
    backgroundColor: '#000000',
    borderColor: '#000000',
  },
  paginationButtonDisabled: {
    borderColor: '#E5E7EB',
    backgroundColor: '#F9FAFB',
    opacity: 0.6,
  },
  paginationButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#374151',
  },
  paginationButtonTextActive: {
    color: '#FFFFFF',
  },
  paginationButtonTextDisabled: {
    color: '#9CA3AF',
  },
  paginationEllipsis: {
    marginHorizontal: 8,
    fontSize: 16,
    fontWeight: '600',
    color: '#6B7280',
  },
});

export default Pagination; 