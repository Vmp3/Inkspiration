import React, { useState, useEffect } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ScrollView, 
  SafeAreaView, 
  TouchableOpacity, 
  Image,
  Alert,
  ActivityIndicator
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import toastHelper from '../utils/toastHelper';
import ApiService from '../services/ApiService';
import Input from '../components/ui/Input';

const TwoFactorSetupScreen = () => {
  const navigation = useNavigation();
  const route = useRoute();
  const { action, onSuccess } = route.params; // 'enable' ou 'disable'
  
  const [step, setStep] = useState(1); // 1: instrucoes, 2: qrcode/codigo, 3: verificacao
  const [qrCode, setQrCode] = useState(null);
  const [secretKey, setSecretKey] = useState(null);
  const [issuer, setIssuer] = useState(null);
  const [accountName, setAccountName] = useState(null);
  const [otpAuthUrl, setOtpAuthUrl] = useState(null);
  const [showManualCode, setShowManualCode] = useState(false);
  const [verificationCode, setVerificationCode] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isGeneratingQR, setIsGeneratingQR] = useState(false);
  const [showRecoveryOption, setShowRecoveryOption] = useState(false);
  const [recoveryCode, setRecoveryCode] = useState('');
  const [isLoadingRecovery, setIsLoadingRecovery] = useState(false);

  useEffect(() => {
    if (action === 'enable' && step === 2) {
      generateQRCode();
    }
  }, [step, action]);

  const generateQRCode = async () => {
    try {
      setIsGeneratingQR(true);
      const response = await ApiService.post('/two-factor/generate-qr');
      
      if (response && response.success) {
        setQrCode(response.qrCode);
        setSecretKey(response.secretKey);
        setIssuer(response.issuer);
        setAccountName(response.accountName);
        setOtpAuthUrl(response.otpAuthUrl);
      } else {
        toastHelper.showError('Erro ao gerar instruções de configuração');
        navigation.goBack();
      }
    } catch (error) {
      toastHelper.showError('Erro ao gerar instruções de configuração');
      navigation.goBack();
    } finally {
      setIsGeneratingQR(false);
    }
  };

  const handleNextStep = () => {
    if (step < 3) {
      setStep(step + 1);
    }
  };

  const handlePrevStep = () => {
    if (step > 1) {
      setStep(step - 1);
    }
  };

  // Função para formatar o código como 000 - 000
  const formatCode = (value) => {
    const digits = value.replace(/\D/g, '').slice(0, 6);
    if (digits.length <= 3) return digits;
    return digits.slice(0, 3) + ' - ' + digits.slice(3);
  };

  // Função para remover a máscara
  const unmaskCode = (value) => value.replace(/\D/g, '').slice(0, 6);

  const handleVerifyCode = async () => {
    const code = unmaskCode(verificationCode);
    if (!code || code.length !== 6) {
      toastHelper.showError('Digite um código de 6 dígitos');
      return;
    }

    try {
      setIsLoading(true);
      
      const endpoint = action === 'enable' ? '/two-factor/enable' : '/two-factor/disable';
      const response = await ApiService.post(endpoint, {
        code: parseInt(code)
      });

      if (response && response.success) {
        toastHelper.showSuccess(response.message);
        navigation.goBack();
      } else {
        toastHelper.showError(response.message || 'Código inválido');
      }
    } catch (error) {
      toastHelper.showError('Erro ao verificar código');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSendRecoveryCode = async () => {
    try {
      setIsLoadingRecovery(true);
      const response = await ApiService.post('/two-factor/send-recovery-code');
      
      if (response && response.success) {
        toastHelper.showSuccess('Código de recuperação enviado para seu email');
        setShowRecoveryOption(true);
      } else {
        toastHelper.showError('Erro ao enviar código de recuperação');
      }
    } catch (error) {
      toastHelper.showError('Erro ao enviar código de recuperação');
    } finally {
      setIsLoadingRecovery(false);
    }
  };

  const handleRecoveryCodeSubmit = async () => {
    const code = unmaskCode(recoveryCode);
    if (!code || code.length !== 6) {
      toastHelper.showError('Digite um código de 6 dígitos');
      return;
    }

    try {
      setIsLoading(true);
      const response = await ApiService.post('/two-factor/disable-with-recovery', {
        recoveryCode: code
      });

      if (response && response.success) {
        toastHelper.showSuccess(response.message);
        if (onSuccess) {
          onSuccess();
        }
        navigation.goBack();
      } else {
        toastHelper.showError(response.message || 'Código de recuperação inválido');
      }
    } catch (error) {
      toastHelper.showError('Erro ao validar código de recuperação');
    } finally {
      setIsLoading(false);
    }
  };

  // Função para decodificar base64
  const decodeBase64 = (str) => {
    try {
      return decodeURIComponent(escape(atob(str)));
    } catch (e) {
      // Se atob não estiver disponível, usa Buffer (Node.js style)
      try {
        return Buffer.from(str, 'base64').toString('utf-8');
      } catch (e2) {
        return 'Erro ao decodificar instruções';
      }
    }
  };

  const renderStep1 = () => (
    <View style={styles.stepContainer}>
      <View style={styles.iconContainer}>
        <Text style={styles.stepIcon}>🔐</Text>
      </View>
      
      <Text style={styles.stepTitle}>
        {action === 'enable' ? 'Ativar' : 'Desativar'} Autenticação de Dois Fatores
      </Text>
      
      <Text style={styles.stepDescription}>
        {action === 'enable' 
          ? 'A autenticação de dois fatores adiciona uma camada extra de segurança à sua conta. Você precisará do Google Authenticator instalado no seu dispositivo móvel.'
          : 'Você está prestes a desativar a autenticação de dois fatores. Isso reduzirá a segurança da sua conta.'
        }
      </Text>

      {action === 'enable' && (
        <View style={styles.instructionsContainer}>
          <Text style={styles.instructionsTitle}>Você precisará:</Text>
          <Text style={styles.instructionItem}>📱 Google Authenticator instalado</Text>
          <Text style={styles.instructionItem}>📷 Câmera para escanear QR Code</Text>
          <Text style={styles.instructionItem}>⏱️ Acesso ao código gerado pelo app</Text>
        </View>
      )}

      <View style={styles.navigationButtons}>
        <TouchableOpacity 
          style={styles.secondaryButton} 
          onPress={() => navigation.goBack()}
        >
          <Text style={styles.secondaryButtonText}>Cancelar</Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={[styles.primaryButton, styles.nextButton]} 
          onPress={handleNextStep}
        >
          <Text style={styles.primaryButtonText}>
            {action === 'enable' ? 'Começar Configuração' : 'Continuar'}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  const renderStep2 = () => (
    <View style={styles.stepContainer}>
      {action === 'enable' ? (
        <>
          <View style={styles.iconContainer}>
            <Text style={styles.stepIcon}>📋</Text>
          </View>
          
          <Text style={styles.stepTitle}>Instruções de Configuração</Text>
          
          <Text style={styles.stepDescription}>
            Configure o Google Authenticator usando as instruções abaixo ou escaneie um QR Code se disponível.
          </Text>

          {isGeneratingQR ? (
            <View style={styles.loadingContainer}>
              <ActivityIndicator size="large" color="#111" />
              <Text style={styles.loadingText}>Gerando QR Code...</Text>
            </View>
          ) : qrCode ? (
            <View style={styles.qrCodeContainer}>
              {qrCode.startsWith('data:image/png;base64,') ? (
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
              ) : qrCode.startsWith('data:image/') ? (
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
              ) : (
                <View style={styles.instructionsContainer}>
                  <ScrollView style={styles.instructionsScrollView}>
                    <Text style={styles.instructionsText}>
                      {qrCode.startsWith('data:text/plain;base64,') 
                        ? decodeBase64(qrCode.replace('data:text/plain;base64,', ''))
                        : qrCode
                      }
                    </Text>
                  </ScrollView>
                </View>
              )}

              {/* Opção para mostrar código manual */}
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
          ) : (
            <Text style={styles.errorText}>Erro ao gerar QR Code</Text>
          )}

          <View style={styles.navigationButtons}>
            <TouchableOpacity 
              style={styles.secondaryButton} 
              onPress={handlePrevStep}
            >
              <Text style={styles.secondaryButtonText}>Voltar</Text>
            </TouchableOpacity>

            <TouchableOpacity 
              style={[styles.primaryButton, styles.nextButton]} 
              onPress={handleNextStep}
              disabled={isGeneratingQR || !qrCode}
            >
              <Text style={styles.primaryButtonText}>Próximo</Text>
            </TouchableOpacity>
          </View>
        </>
      ) : (
        <>
          <View style={styles.iconContainer}>
            <Text style={styles.stepIcon}>🔓</Text>
          </View>
          
          <Text style={styles.stepTitle}>Confirmar Desativação</Text>
          
          <Text style={styles.stepDescription}>
            Para desativar a autenticação de dois fatores, digite o código atual do seu Google Authenticator.
          </Text>

          <View style={styles.navigationButtons}>
            <TouchableOpacity 
              style={styles.secondaryButton} 
              onPress={handlePrevStep}
            >
              <Text style={styles.secondaryButtonText}>Voltar</Text>
            </TouchableOpacity>

            <TouchableOpacity 
              style={[styles.primaryButton, styles.nextButton]} 
              onPress={handleNextStep}
            >
              <Text style={styles.primaryButtonText}>Próximo</Text>
            </TouchableOpacity>
          </View>
        </>
      )}
    </View>
  );

  const renderStep3 = () => (
    <View style={styles.stepContainer}>
      <View style={styles.iconContainer}>
        <Text style={styles.stepIcon}>🔢</Text>
      </View>
      
      <Text style={styles.stepTitle}>Digite o Código de Verificação</Text>
      
      <Text style={styles.stepDescription}>
        {showRecoveryOption 
          ? 'Digite o código de 6 dígitos enviado para seu email.'
          : 'Digite o código de 6 dígitos gerado pelo Google Authenticator.'
        }
      </Text>

      <View style={styles.codeInputContainer}>
        <Input
          placeholder="000 - 000"
          value={formatCode(showRecoveryOption ? recoveryCode : verificationCode)}
          onChangeText={text => {
            const clean = unmaskCode(text);
            if (showRecoveryOption) setRecoveryCode(clean);
            else setVerificationCode(clean);
          }}
          keyboardType="numeric"
          maxLength={9} // 6 dígitos + 3 (espaço e hífen)
          style={styles.codeInput}
          textAlign="center"
        />
      </View>

      {/* Opção de recuperação por email para desativação */}
      {action === 'disable' && !showRecoveryOption && (
        <View style={styles.recoverySection}>
          <Text style={styles.recoveryText}>
            Perdeu o acesso ao seu celular?
          </Text>
          <TouchableOpacity 
            style={styles.recoveryButton}
            onPress={handleSendRecoveryCode}
            disabled={isLoadingRecovery}
          >
            {isLoadingRecovery ? (
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
      )}

      <View style={styles.navigationButtons}>
        <TouchableOpacity 
          style={styles.secondaryButton} 
          onPress={showRecoveryOption ? () => setShowRecoveryOption(false) : handlePrevStep}
        >
          <Text style={styles.secondaryButtonText}>
            {showRecoveryOption ? 'Voltar ao código do app' : 'Voltar'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={[
            styles.primaryButton, 
            styles.nextButton,
            action === 'disable' && styles.dangerButton
          ]} 
          onPress={showRecoveryOption ? handleRecoveryCodeSubmit : handleVerifyCode}
          disabled={isLoading || (showRecoveryOption ? recoveryCode.length !== 6 : verificationCode.length !== 6)}
        >
          {isLoading ? (
            <ActivityIndicator size="small" color="#fff" />
          ) : (
            <Text style={styles.primaryButtonText}>
              {action === 'enable' ? 'Ativar 2FA' : 'Desativar 2FA'}
            </Text>
          )}
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContainer}>
        <View style={styles.header}>
          <TouchableOpacity 
            style={styles.backButton} 
            onPress={() => navigation.goBack()}
          >
            <Text style={styles.backButtonText}>← Voltar</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.content}>
          <View style={styles.progressContainer}>
            <View style={styles.progressBar}>
              <View style={[
                styles.progressFill, 
                { width: `${(step / 3) * 100}%` }
              ]} />
            </View>
            <Text style={styles.progressText}>Passo {step} de 3</Text>
          </View>

          {step === 1 && renderStep1()}
          {step === 2 && renderStep2()}
          {step === 3 && renderStep3()}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollContainer: {
    flexGrow: 1,
    paddingHorizontal: 16,
  },
  header: {
    position: 'absolute',
    top: 20,
    left: 16,
    zIndex: 10,
  },
  backButton: {
    padding: 8,
  },
  backButtonText: {
    fontSize: 14,
    color: '#111',
    fontWeight: '500',
  },
  content: {
    width: '100%',
    maxWidth: 450,
    alignSelf: 'center',
    marginTop: 80,
  },
  progressContainer: {
    marginBottom: 28,
  },
  progressBar: {
    width: '100%',
    height: 2,
    backgroundColor: '#e2e2e2',
    borderRadius: 1,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#111',
    borderRadius: 1,
  },
  progressText: {
    textAlign: 'center',
    marginTop: 4,
    fontSize: 10,
    color: '#666',
  },
  stepContainer: {
    alignItems: 'center',
  },
  iconContainer: {
    marginBottom: 16,
  },
  stepIcon: {
    fontSize: 36,
  },
  stepTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 10,
    color: '#111',
  },
  stepDescription: {
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 24,
    color: '#666',
    lineHeight: 20,
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
  instructionsTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#111',
  },
  instructionItem: {
    fontSize: 13,
    marginBottom: 6,
    color: '#444',
  },
  loadingContainer: {
    alignItems: 'center',
    marginVertical: 28,
  },
  loadingText: {
    marginTop: 10,
    fontSize: 13,
    color: '#666',
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
  codeInputContainer: {
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
  navigationButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
    marginTop: 24,
    gap: 12,
  },
  primaryButton: {
    backgroundColor: '#111',
    paddingVertical: 10,
    paddingHorizontal: 18,
    borderRadius: 6,
    minWidth: 100,
    alignItems: 'center',
  },
  nextButton: {
    flex: 1,
  },
  dangerButton: {
    backgroundColor: '#ef5350',
  },
  primaryButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
  secondaryButton: {
    backgroundColor: 'transparent',
    paddingVertical: 10,
    paddingHorizontal: 18,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: '#e2e2e2',
    minWidth: 100,
    alignItems: 'center',
  },
  secondaryButtonText: {
    color: '#111',
    fontSize: 14,
    fontWeight: '500',
  },
  errorText: {
    color: '#ef5350',
    fontSize: 12,
    textAlign: 'center',
    marginVertical: 12,
  },
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
  },
  recoveryButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
  qrCodeContainer: {
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

export default TwoFactorSetupScreen; 