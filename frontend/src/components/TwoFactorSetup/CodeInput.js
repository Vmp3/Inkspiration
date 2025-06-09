import React from 'react';
import { View, StyleSheet } from 'react-native';
import Input from '../ui/Input';

const CodeInput = ({ value, onChangeText, placeholder = "000 - 000" }) => {
  const formatCode = (value) => {
    const digits = value.replace(/\D/g, '').slice(0, 6);
    if (digits.length <= 3) return digits;
    return digits.slice(0, 3) + ' - ' + digits.slice(3);
  };

  // Função para remover a máscara
  const unmaskCode = (value) => value.replace(/\D/g, '').slice(0, 6);

  const handleChangeText = (text) => {
    const clean = unmaskCode(text);
    onChangeText(clean);
  };

  return (
    <View style={styles.container}>
      <Input
        placeholder={placeholder}
        value={formatCode(value)}
        onChangeText={handleChangeText}
        keyboardType="numeric"
        maxLength={9}
        style={styles.codeInput}
        textAlign="center"
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    marginVertical: 16,
  },
  codeInput: {
    fontSize: 20,
    fontWeight: 'bold',
    letterSpacing: 5,
    height: 48,
    textAlign: 'center',
  },
});

export default CodeInput; 