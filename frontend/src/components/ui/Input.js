import React, { useState } from 'react';
import { TextInput, StyleSheet, View, TouchableOpacity } from 'react-native';
import Icon from 'react-native-vector-icons/Feather'; // Changed to react-native-vector-icons

const Input = ({ 
  placeholder, 
  value, 
  onChangeText, 
  secureTextEntry, 
  keyboardType,
  multiline,
  numberOfLines,
  style,
  ...props 
}) => {
  const [passwordVisible, setPasswordVisible] = useState(false);

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

  return (
    <View style={styles.inputContainer}>
      <TextInput
        style={[
          styles.input, 
          style, 
          multiline && { textAlignVertical: 'top', minHeight: 100 },
          secureTextEntry && { paddingRight: 40 }
        ]}
        placeholder={placeholder}
        value={value}
        onChangeText={onChangeText}
        secureTextEntry={secureTextEntry && !passwordVisible}
        keyboardType={keyboardType || 'default'}
        multiline={multiline}
        numberOfLines={multiline ? (numberOfLines || 4) : 1}
        placeholderTextColor="#999"
        {...props}
      />
      {renderPasswordToggle()}
    </View>
  );
};

const styles = StyleSheet.create({
  inputContainer: {
    position: 'relative',
    width: '100%',
  },
  input: {
    width: '100%',
    height: 40,
    borderWidth: 1,
    borderColor: '#e2e2e2',
    borderRadius: 4,
    paddingHorizontal: 12,
    fontSize: 14,
    backgroundColor: '#fff',
  },
  eyeIcon: {
    position: 'absolute',
    right: 12,
    top: 10,
    zIndex: 1,
  },
});

export default Input; 