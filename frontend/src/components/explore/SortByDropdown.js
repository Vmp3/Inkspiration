import React, { useState, useRef, useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Platform } from 'react-native';
import { Feather } from '@expo/vector-icons';

const SortByDropdown = ({ 
  sortBy, 
  setSortBy 
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);
  const buttonRef = useRef(null);

  const sortOptions = [
    { value: 'melhorAvaliacao', label: 'Melhor Avaliação' },
    { value: 'maisRecente', label: 'Mais Recente' },
    { value: 'maisAntigo', label: 'Mais Antigos' }
  ];

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (Platform.OS === 'web' && isOpen) {
        const isOutsideDropdown = dropdownRef.current && !dropdownRef.current.contains(event.target);
        const isOutsideButton = buttonRef.current && !buttonRef.current.contains(event.target);
        
        if (isOutsideDropdown && isOutsideButton) {
          setIsOpen(false);
        }
      }
    };

    if (Platform.OS === 'web') {
      document.addEventListener('mousedown', handleClickOutside);
      return () => {
        document.removeEventListener('mousedown', handleClickOutside);
      };
    }
  }, [isOpen]);

  const handleSelect = (value) => {
    console.log('Selecionando ordenação:', value);
    setSortBy(value);
    setIsOpen(false);
  };

  const getCurrentLabel = () => {
    const option = sortOptions.find(opt => opt.value === sortBy);
    return option ? option.label : 'Melhor Avaliação';
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity 
        style={styles.button}
        onPress={() => setIsOpen(!isOpen)}
        ref={buttonRef}
      >
        <Text style={styles.buttonText}>{getCurrentLabel()}</Text>
        <Feather 
          name={isOpen ? "chevron-up" : "chevron-down"} 
          size={16} 
          color="#666" 
        />
      </TouchableOpacity>
      
      {isOpen && (
        <View 
          style={styles.dropdown}
          ref={dropdownRef}
        >
          {sortOptions.map((option) => (
            <TouchableOpacity
              key={option.value}
              style={[
                styles.dropdownItem,
                sortBy === option.value && styles.activeItem
              ]}
              onPress={() => handleSelect(option.value)}
            >
              <Text style={[
                styles.dropdownText,
                sortBy === option.value && styles.activeText
              ]}>
                {option.label}
              </Text>
              {sortBy === option.value && (
                <Feather name="check" size={16} color="#000" />
              )}
            </TouchableOpacity>
          ))}
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    position: 'relative',
    alignSelf: 'flex-end',
    marginBottom: 16,
    width: 180,
  },
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 6,
    paddingHorizontal: 12,
    paddingVertical: 10,
    height: 40,
  },
  buttonText: {
    fontSize: 14,
    color: '#111827',
    fontWeight: '400',
  },
  dropdown: {
    position: 'absolute',
    top: 42,
    left: 0,
    right: 0,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 6,
    shadowColor: '#000000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 5,
    zIndex: 1000,
    minWidth: 180,
  },
  dropdownItem: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#F3F4F6',
  },
  activeItem: {
    backgroundColor: '#F9FAFB',
  },
  dropdownText: {
    fontSize: 14,
    color: '#111827',
  },
  activeText: {
    fontWeight: '500',
    color: '#000000',
  },
});

export default SortByDropdown; 