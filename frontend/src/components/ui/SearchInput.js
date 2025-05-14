import React from 'react';
import { View, TextInput, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

/**
 * Input de busca com ícone
 * @param {Object} props Propriedades do componente
 * @param {string} props.placeholder Texto do placeholder
 * @param {string} props.value Valor do input
 * @param {function} props.onChangeText Função chamada quando o texto muda
 * @param {string} props.icon Nome do ícone do MaterialIcons
 * @param {Object} props.style Estilos adicionais para o container
 */
const SearchInput = ({ 
  placeholder, 
  value, 
  onChangeText, 
  icon,
  style,
  ...otherProps
}) => {
  return (
    <View style={[styles.container, style]}>
      <MaterialIcons name={icon} size={16} color="#6B7280" style={styles.icon} />
      <TextInput
        style={styles.input}
        placeholder={placeholder}
        value={value}
        onChangeText={onChangeText}
        placeholderTextColor="#9CA3AF"
        {...otherProps}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    borderRadius: 4,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    paddingHorizontal: 12,
    height: 40,
  },
  icon: {
    marginRight: 8,
  },
  input: {
    flex: 1,
    fontSize: 14,
    color: '#111827',
    padding: 0,
  },
});

export default SearchInput; 