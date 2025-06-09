import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ActivityIndicator } from 'react-native';

const RecoverySection = ({ onSendRecoveryCode, isLoading = false }) => {
  return (
    <View style={styles.recoverySection}>
      <Text style={styles.recoveryText}>
        Perdeu o acesso ao seu celular?
      </Text>
      <TouchableOpacity 
        style={styles.recoveryButton}
        onPress={onSendRecoveryCode}
        disabled={isLoading}
      >
        {isLoading ? (
          <>
            <ActivityIndicator size="small" color="#fff" style={{ marginRight: 8 }} />
            <Text style={styles.recoveryButtonText}>Enviando...</Text>
          </>
        ) : (
          <Text style={styles.recoveryButtonText}>
            Receber código por email
          </Text>
        )}
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  recoverySection: {
    marginTop: 16,
    alignItems: 'center',
  },
  recoveryText: {
    fontSize: 13,
    color: '#666',
    marginBottom: 8,
  },
  recoveryButton: {
    backgroundColor: '#111',
    paddingVertical: 10,
    paddingHorizontal: 18,
    borderRadius: 6,
    minWidth: 100,
    alignItems: 'center',
    flexDirection: 'row',
  },
  recoveryButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
});

export default RecoverySection; 