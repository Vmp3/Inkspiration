import React, { useState } from 'react';
import { View, Text, StyleSheet, Image, TouchableOpacity, ScrollView } from 'react-native';

const QRCodeDisplay = ({ qrCode, secretKey, issuer, accountName, otpAuthUrl }) => {
  const [showManualCode, setShowManualCode] = useState(false);

  return (
    <View style={styles.container}>
      {qrCode && qrCode.startsWith('data:image/') ? (
        <>
          <Text style={styles.qrCodeTitle}>Escaneie o QR Code:</Text>
          <Image 
            source={{ uri: qrCode }} 
            style={styles.qrCodeImage}
            resizeMode="contain"
            onError={(error) => {}}
            onLoad={() => {}}
          />
          <Text style={styles.qrCodeInstructions}>
            Abra o Google Authenticator e escaneie o código acima, ou adicione manualmente.
          </Text>
        </>
      ) : qrCode ? (
        <View style={styles.instructionsContainer}>
          <ScrollView style={styles.instructionsScrollView}>
            <Text style={styles.instructionsText}>{qrCode}</Text>
          </ScrollView>
        </View>
      ) : null}

      <View style={styles.manualCodeSection}>
        <TouchableOpacity 
          style={styles.manualCodeButton}
          onPress={() => setShowManualCode(!showManualCode)}
        >
          <Text style={styles.manualCodeButtonText}>
            {showManualCode ? '🔼 Ocultar código manual' : '🔽 Mostrar código manual'}
          </Text>
        </TouchableOpacity>

        {showManualCode && secretKey && (
          <View style={styles.manualCodeContainer}>
            <Text style={styles.manualCodeTitle}>Configuração Manual:</Text>
            
            <Text style={styles.manualCodeLabel}>Conta:</Text>
            <View style={styles.manualCodeBox}>
              <Text style={styles.manualCodeText}>{accountName}</Text>
            </View>
            
            <Text style={styles.manualCodeLabel}>Chave:</Text>
            <View style={styles.manualCodeBox}>
              <Text style={styles.manualCodeText}>{secretKey}</Text>
            </View>

            <Text style={styles.manualCodeLabel}>Emissor:</Text>
            <View style={styles.manualCodeBox}>
              <Text style={styles.manualCodeText}>{issuer}</Text>
            </View>

            <Text style={styles.manualCodeLabel}>URL completa (alternativa):</Text>
            <View style={styles.manualCodeBox}>
              <Text style={styles.manualCodeText}>{otpAuthUrl}</Text>
            </View>

            <Text style={styles.manualCodeInstructions}>
              No Google Authenticator: Adicionar conta → Inserir chave de configuração → 
              Cole os dados acima nos campos correspondentes. Ou use a URL completa diretamente.
            </Text>
          </View>
        )}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    marginVertical: 16,
  },
  qrCodeTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#111',
  },
  qrCodeImage: {
    width: 200,
    height: 200,
    marginBottom: 10,
  },
  qrCodeInstructions: {
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
  },
  instructionsContainer: {
    backgroundColor: '#f8f8f8',
    padding: 16,
    borderRadius: 6,
    marginVertical: 16,
    width: '100%',
    borderWidth: 1,
    borderColor: '#e2e2e2',
  },
  instructionsScrollView: {
    maxHeight: 120,
  },
  instructionsText: {
    fontSize: 11,
    color: '#444',
    fontFamily: 'monospace',
    lineHeight: 14,
  },
  manualCodeSection: {
    marginTop: 16,
    alignItems: 'center',
  },
  manualCodeButton: {
    backgroundColor: '#111',
    paddingVertical: 10,
    paddingHorizontal: 18,
    borderRadius: 6,
    minWidth: 100,
    alignItems: 'center',
  },
  manualCodeButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
  manualCodeContainer: {
    backgroundColor: '#f8f8f8',
    padding: 16,
    borderRadius: 6,
    marginVertical: 16,
    width: '100%',
    borderWidth: 1,
    borderColor: '#e2e2e2',
  },
  manualCodeTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#111',
  },
  manualCodeLabel: {
    fontSize: 13,
    fontWeight: 'bold',
    marginBottom: 6,
    color: '#111',
  },
  manualCodeBox: {
    backgroundColor: '#fff',
    padding: 10,
    borderRadius: 4,
    marginBottom: 10,
  },
  manualCodeText: {
    fontSize: 12,
    color: '#444',
  },
  manualCodeInstructions: {
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
  },
});

export default QRCodeDisplay; 