import React, { useState } from 'react';
import { TextInput, StyleSheet, View, TouchableOpacity, Text, Platform } from 'react-native';
import Icon from 'react-native-vector-icons/Feather'; // Changed to react-native-vector-icons
import { MaterialIcons } from '@expo/vector-icons';

const Input = ({ 
  placeholder, 
  value, 
  onChangeText, 
  onSubmitEditing,
  secureTextEntry, 
  keyboardType,
  multiline,
  numberOfLines,
  style,
  icon,
  showSearchButton = false,
  onSearch,
  searchButtonText = "Buscar",
  ...props 
}) => {
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [isFocused, setIsFocused] = useState(false);

  const handleKeyPress = (event) => {
    const key = event.nativeEvent?.key || event.key;
    if (key === 'Enter' && onSubmitEditing) {
      onSubmitEditing();
    }
  };

  const renderPasswordToggle = () => {
    if (!secureTextEntry) return null;
    
    return (
      <TouchableOpacity 
        style={styles.eyeIcon} 
        onPress={() => setPasswordVisible(!passwordVisible)}
      >
        <Icon name={passwordVisible ? 'eye' : 'eye-off'} size={18} color="#555" />
      </TouchableOpacity>
    );
  };

  const renderIcon = () => {
    if (!icon) return null;

    return (
      <View style={styles.iconContainer}>
        <MaterialIcons name={icon} size={20} color="#9CA3AF" />
      </View>
    );
  };

  return (
    <View style={[styles.container, style]}>
      <View style={[
        styles.inputContainer,
        isFocused && styles.inputContainerFocused
      ]}>
        {renderIcon()}
        <TextInput
          style={[
            styles.input,
            isFocused && styles.inputFocused,
            { outline: 'none', borderWidth: 0, boxShadow: 'none' },
            icon && styles.inputWithIcon,
            multiline && { textAlignVertical: 'top', minHeight: 100 },
            secureTextEntry && { paddingRight: 40 },
            Platform.OS === 'web' ? { outlineStyle: 'none' } : {},
          ]}
          placeholder={placeholder}
          value={value}
          onChangeText={onChangeText}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          onSubmitEditing={onSubmitEditing}
          onKeyPress={handleKeyPress}
          secureTextEntry={secureTextEntry && !passwordVisible}
          keyboardType={keyboardType || 'default'}
          multiline={multiline}
          numberOfLines={multiline ? (numberOfLines || 4) : 1}
          placeholderTextColor="#9CA3AF"
          {...props}
        />
        {renderPasswordToggle()}
      </View>
      
      {showSearchButton && (
        <TouchableOpacity 
          style={styles.searchButton}
          onPress={onSearch}
        >
          <Text style={styles.searchButtonText}>{searchButtonText}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  inputContainer: {
    position: 'relative',
    flex: 1,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 4,
    backgroundColor: '#fff',
  },
  inputContainerFocused: {
    borderColor: '#000000',
    borderWidth: 1.5,
  },
  input: {
    width: '100%',
    height: 40,
    paddingHorizontal: 12,
    fontSize: 14,
    backgroundColor: 'transparent',
    borderWidth: 0,
    outline: 'none',
  },
  inputWithIcon: {
    paddingLeft: 36,
  },
  eyeIcon: {
    position: 'absolute',
    right: 12,
    top: 10,
    zIndex: 1,
  },
  iconContainer: {
    position: 'absolute',
    left: 10,
    top: 10,
    zIndex: 1,
  },
  searchButton: {
    backgroundColor: '#000000',
    borderRadius: 4,
    paddingHorizontal: 12,
    justifyContent: 'center',
    alignItems: 'center',
    height: 40,
    minWidth: 80,
  },
  searchButtonText: {
    color: '#FFFFFF',
    fontWeight: '600',
    fontSize: 14,
  },
  inputFocused: {
    borderColor: '#000000',
    borderWidth: 1.5,
  },
});

export default Input; 