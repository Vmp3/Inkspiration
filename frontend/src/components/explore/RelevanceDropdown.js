import React, { useRef } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, TouchableWithoutFeedback } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

const RelevanceDropdown = ({ 
  sortBy, 
  setSortBy, 
  showDropdown, 
  setShowDropdown 
}) => {
  const dropdownRef = useRef(null);

  return (
    <View style={styles.relevanceContainer} ref={dropdownRef}>
      <TouchableOpacity 
        style={styles.relevanceButton}
        onPress={() => setShowDropdown(!showDropdown)}
        activeOpacity={0.7}
      >
        <Text style={styles.relevanceText}>
          {sortBy === 'relevancia' ? 'Relevância' : 
           sortBy === 'melhorAvaliacao' ? 'Melhor Avaliação' : 
           'Mais Recente'}
        </Text>
        <MaterialIcons 
          name={showDropdown ? "arrow-drop-up" : "arrow-drop-down"} 
          size={24} 
          color="#000000" 
        />
      </TouchableOpacity>
      
      {/* Menu dropdown de relevância */}
      {showDropdown && (
        <View style={styles.relevanceDropdownContainer}>
          <TouchableWithoutFeedback onPress={() => setShowDropdown(false)}>
            <View style={styles.relevanceDropdownOverlay} />
          </TouchableWithoutFeedback>
          <View style={styles.relevanceDropdown}>
            <TouchableOpacity
              style={[
                styles.relevanceDropdownItem,
                sortBy === 'relevancia' && styles.relevanceDropdownItemActive
              ]}
              onPress={() => {
                setSortBy('relevancia');
                setShowDropdown(false);
              }}
              activeOpacity={0.7}
            >
              {sortBy === 'relevancia' && (
                <MaterialIcons
                  name="check"
                  size={18}
                  color="#000000"
                  style={styles.relevanceDropdownIcon}
                />
              )}
              <Text style={[
                styles.relevanceDropdownText,
                sortBy === 'relevancia' && styles.relevanceDropdownTextActive
              ]}>Relevância</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[
                styles.relevanceDropdownItem,
                sortBy === 'melhorAvaliacao' && styles.relevanceDropdownItemActive
              ]}
              onPress={() => {
                setSortBy('melhorAvaliacao');
                setShowDropdown(false);
              }}
              activeOpacity={0.7}
            >
              {sortBy === 'melhorAvaliacao' && (
                <MaterialIcons
                  name="check"
                  size={18}
                  color="#000000"
                  style={styles.relevanceDropdownIcon}
                />
              )}
              <Text style={[
                styles.relevanceDropdownText,
                sortBy === 'melhorAvaliacao' && styles.relevanceDropdownTextActive
              ]}>Melhor Avaliação</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[
                styles.relevanceDropdownItem,
                sortBy === 'maisRecente' && styles.relevanceDropdownItemActive
              ]}
              onPress={() => {
                setSortBy('maisRecente');
                setShowDropdown(false);
              }}
              activeOpacity={0.7}
            >
              {sortBy === 'maisRecente' && (
                <MaterialIcons
                  name="check"
                  size={18}
                  color="#000000"
                  style={styles.relevanceDropdownIcon}
                />
              )}
              <Text style={[
                styles.relevanceDropdownText,
                sortBy === 'maisRecente' && styles.relevanceDropdownTextActive
              ]}>Mais Recente</Text>
            </TouchableOpacity>
          </View>
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  relevanceContainer: {
    position: 'relative',
    zIndex: 100,
    alignSelf: 'flex-end',
    marginBottom: 16,
    width: 140,
  },
  relevanceButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 6,
    paddingHorizontal: 12,
    paddingVertical: 8,
    height: 40,
    minWidth: 140,
  },
  relevanceText: {
    fontSize: 14,
    color: '#111827',
    marginRight: 4,
  },
  relevanceDropdownContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    zIndex: 1000,
  },
  relevanceDropdownOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'transparent',
  },
  relevanceDropdown: {
    position: 'absolute',
    top: 40,
    right: 0,
    width: 180,
    backgroundColor: '#FFFFFF',
    borderRadius: 6,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    zIndex: 2000,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    overflow: 'hidden',
  },
  relevanceDropdownItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 10,
    paddingHorizontal: 16,
    backgroundColor: '#FFFFFF',
  },
  relevanceDropdownItemActive: {
    backgroundColor: '#F9FAFB',
  },
  relevanceDropdownIcon: {
    marginRight: 8,
    width: 18,
  },
  relevanceDropdownText: {
    fontSize: 14,
    color: '#111827',
  },
  relevanceDropdownTextActive: {
    fontWeight: '500',
  },
});

export default RelevanceDropdown; 