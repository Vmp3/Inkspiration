import React, { useState, useEffect, useCallback } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ScrollView, 
  SafeAreaView, 
  TouchableOpacity, 
  Alert,
  ActivityIndicator
} from 'react-native';
import { useRoute } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import toastHelper from '../utils/toastHelper';
import ApiService from '../services/ApiService';
import TwoFactorService from '../services/TwoFactorService';
import { professionalMessages } from '../components/professional/messages';
import { useEmailTimeout, EMAIL_TIMEOUT_CONFIG } from '../components/ui/EmailTimeout';
import useNavigationHelper from '../hooks/useNavigationHelper';

// Componentes
import StepIndicator from '../components/TwoFactorSetup/StepIndicator';
import StepHeader from '../components/TwoFactorSetup/StepHeader';
import QRCodeDisplay from '../components/TwoFactorSetup/QRCodeDisplay';
import CodeInput from '../components/TwoFactorSetup/CodeInput';
import NavigationButtons from '../components/TwoFactorSetup/NavigationButtons';
import RecoverySection from '../components/TwoFactorSetup/RecoverySection';

const TwoFactorSetupScreen = () => {
  const { safeGoBackToProfile } = useNavigationHelper();
  const route = useRoute();
  const { action } = route.params;
  
  const [step, setStep] = useState(1);
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
  const [qrCodeData, setQrCodeData] = useState('');
  const [error, setError] = useState('');
  
  // Hook para timeout de email
  const recoveryTimeout = useEmailTimeout(EMAIL_TIMEOUT_CONFIG.RECOVERY_TIMEOUT);

  useEffect(() => {
    if (action === 'enable' && step === 2) {
      generateQRCode();
    }
  }, [step, action]);

  const generateQRCode = useCallback(async () => {
    try {
      setIsGeneratingQR(true);
      const response = await TwoFactorService.generateQRCode();
      setQrCodeData(response.qrCodeUrl);
      setSecretKey(response.secretKey);
      setIssuer(response.issuer);
      setAccountName(response.accountName);
      setOtpAuthUrl(response.otpAuthUrl);
      setQrCode(response.qrCode);
    } catch (error) {
      // console.error('Error generating QR code:', error);
      setError(error.message || professionalMessages.twoFactorErrors.generateQR);
      safeGoBackToProfile();
    } finally {
      setIsGeneratingQR(false);
    }
  }, [safeGoBackToProfile]);

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

  const verifyCode = async (code) => {
    try {
      setIsLoading(true);
      await TwoFactorService.verifyCode(code);
      setStep(3);
      toastHelper.showSuccess(professionalMessages.success.twoFactorEnabled);
    } catch (error) {
      setError(error.message || professionalMessages.twoFactorErrors.verifyCode);
    } finally {
      setIsLoading(false);
    }
  };

  const sendRecoveryCode = async () => {
    try {
      setIsSendingRecovery(true);
      await TwoFactorService.sendRecoveryCode();
      toastHelper.showSuccess(professionalMessages.success.recoverySent);
    } catch (error) {
      setError(error.message || professionalMessages.twoFactorErrors.sendRecovery);
    } finally {
      setIsSendingRecovery(false);
    }
  };

  const verifyRecoveryCode = async (code) => {
    try {
      setIsSendingRecovery(true);
      await TwoFactorService.verifyRecoveryCode(code);
      toastHelper.showSuccess(professionalMessages.success.recoveryVerified);
    } catch (error) {
      setError(error.message || professionalMessages.twoFactorErrors.verifyRecovery);
    } finally {
      setIsSendingRecovery(false);
    }
  };

  const handleVerifyCode = async () => {
    const code = unmaskCode(verificationCode);
    if (!code || code.length !== 6) {
      toastHelper.showError(professionalMessages.twoFactorErrors.invalidCode);
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
        safeGoBackToProfile();
      } else {
        toastHelper.showError(response.message || professionalMessages.twoFactorErrors.verifyCode);
      }
    } catch (error) {
      toastHelper.showError(professionalMessages.twoFactorErrors.verifyCode);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSendRecoveryCode = async () => {
    try {
      await recoveryTimeout.executeWithTimeout(
        () => ApiService.post('/two-factor/send-recovery-code'),
        {
          successMessage: professionalMessages.success.recoveryCodeSent,
          timeoutMessage: 'Tempo limite para envio do código de recuperação esgotado. Tente novamente.',
          errorMessage: professionalMessages.twoFactorErrors.sendRecovery,
          onSuccess: (response) => {
            if (response && response.success) {
              setShowRecoveryOption(true);
            }
          }
        }
      );
    } catch (error) {
    }
  };

  const handleRecoveryCodeSubmit = async () => {
    const code = unmaskCode(recoveryCode);
    if (!code || code.length !== 6) {
      toastHelper.showError(professionalMessages.twoFactorErrors.invalidCode);
      return;
    }

    try {
      setIsLoading(true);
      const response = await ApiService.post('/two-factor/disable-with-recovery', {
        recoveryCode: code
      });

      if (response && response.success) {
        toastHelper.showSuccess(response.message);
        safeGoBackToProfile();
      } else {
        toastHelper.showError(response.message || professionalMessages.twoFactorErrors.verifyRecovery);
      }
    } catch (error) {
      toastHelper.showError(professionalMessages.twoFactorErrors.verifyRecovery);
    } finally {
      setIsLoading(false);
    }
  };

  const decodeBase64 = (str) => {
    try {
      return decodeURIComponent(escape(atob(str)));
    } catch (e) {
      try {
        return Buffer.from(str, 'base64').toString('utf-8');
      } catch (e2) {
        return 'Erro ao decodificar instruções';
      }
    }
  };

  const renderStep1 = () => (
    <View style={styles.stepContainer}>
      <StepHeader
        icon="🔐"
        title={`${action === 'enable' ? 'Ativar' : 'Desativar'} Autenticação de Dois Fatores`}
        description={
          action === 'enable' 
            ? 'A autenticação de dois fatores adiciona uma camada extra de segurança à sua conta. Você precisará do Google Authenticator instalado no seu dispositivo móvel.'
            : 'Você está prestes a desativar a autenticação de dois fatores. Isso reduzirá a segurança da sua conta.'
        }
      />

      {action === 'enable' && (
        <View style={styles.instructionsContainer}>
          <Text style={styles.instructionsTitle}>Você precisará:</Text>
          <Text style={styles.instructionItem}>📱 Google Authenticator instalado</Text>
          <Text style={styles.instructionItem}>📷 Câmera para escanear QR Code</Text>
          <Text style={styles.instructionItem}>⏱️ Acesso ao código gerado pelo app</Text>
        </View>
      )}

      <NavigationButtons
        onPrev={safeGoBackToProfile}
        onNext={handleNextStep}
        prevText="Cancelar"
        nextText={action === 'enable' ? 'Começar Configuração' : 'Continuar'}
      />
    </View>
  );

  const renderStep2 = () => (
    <View style={styles.stepContainer}>
      {action === 'enable' ? (
        <>
          <StepHeader
            icon="📋"
            title="Instruções de Configuração"
            description="Configure o Google Authenticator usando as instruções abaixo ou escaneie um QR Code se disponível."
          />

          {isGeneratingQR ? (
            <View style={styles.loadingContainer}>
              <ActivityIndicator size="large" color="#111" />
              <Text style={styles.loadingText}>{professionalMessages.loading.generatingQR}</Text>
            </View>
          ) : qrCode ? (
            <QRCodeDisplay
              qrCode={qrCode}
              secretKey={secretKey}
              issuer={issuer}
              accountName={accountName}
              otpAuthUrl={otpAuthUrl}
            />
          ) : (
            <Text style={styles.errorText}>{professionalMessages.twoFactorErrors.generateQR}</Text>
          )}

          <NavigationButtons
            onPrev={handlePrevStep}
            onNext={handleNextStep}
            disabled={isGeneratingQR || !qrCode}
          />
        </>
      ) : (
        <>
          <StepHeader
            icon="🔓"
            title="Confirmar Desativação"
            description="Para desativar a autenticação de dois fatores, digite o código atual do seu Google Authenticator."
          />

          <NavigationButtons
            onPrev={handlePrevStep}
            onNext={handleNextStep}
          />
        </>
      )}
    </View>
  );

  const renderStep3 = () => (
    <View style={styles.stepContainer}>
      <StepHeader
        icon="🔢"
        title="Digite o Código de Verificação"
        description={
          showRecoveryOption 
            ? 'Digite o código de 6 dígitos enviado para seu email.'
            : 'Digite o código de 6 dígitos gerado pelo Google Authenticator.'
        }
      />

      <CodeInput
        value={showRecoveryOption ? recoveryCode : verificationCode}
        onChangeText={showRecoveryOption ? setRecoveryCode : setVerificationCode}
      />

      {/* Opção de recuperação por email para desativação */}
      {action === 'disable' && !showRecoveryOption && (
              <RecoverySection
        onSendRecoveryCode={handleSendRecoveryCode}
        isLoading={recoveryTimeout.isLoading}
      />
      )}

      <NavigationButtons
        onPrev={showRecoveryOption ? () => setShowRecoveryOption(false) : handlePrevStep}
        onNext={showRecoveryOption ? handleRecoveryCodeSubmit : handleVerifyCode}
        prevText={showRecoveryOption ? 'Voltar ao código do app' : 'Voltar'}
        nextText={action === 'enable' ? 'Ativar 2FA' : 'Desativar 2FA'}
        isLoading={isLoading}
        disabled={showRecoveryOption ? recoveryCode.length !== 6 : verificationCode.length !== 6}
        isDanger={action === 'disable'}
      />
    </View>
  );

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContainer}>
        <View style={styles.header}>
          <TouchableOpacity 
            style={styles.backButton} 
            onPress={safeGoBackToProfile}
          >
            <Text style={styles.backButtonText}>← Voltar</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.content}>
          <StepIndicator currentStep={step} totalSteps={3} />

          <View style={styles.stepContent}>
            {step === 1 && renderStep1()}
            {step === 2 && renderStep2()}
            {step === 3 && renderStep3()}
          </View>
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
    paddingBottom: 30,
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
    flex: 1,
    width: '100%',
    maxWidth: 450,
    alignSelf: 'center',
    marginTop: 80,
    justifyContent: 'space-between',
    minHeight: 400,
  },
  stepContent: {
    flex: 1,
    justifyContent: 'center',
    paddingVertical: 20,
  },
  stepContainer: {
    alignItems: 'center',
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
  errorText: {
    color: '#ef5350',
    fontSize: 12,
    textAlign: 'center',
    marginVertical: 12,
  },
});

export default TwoFactorSetupScreen; 