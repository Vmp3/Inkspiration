import React, { useState } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  ScrollView, 
  SafeAreaView, 
  KeyboardAvoidingView, 
  Platform,
  TouchableOpacity 
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { Feather } from '@expo/vector-icons';
import * as formatters from '../utils/formatters';
import toastHelper from '../utils/toastHelper';
import Input from '../components/ui/Input';
import Button from '../components/ui/Button';
import PublicAuthService from '../services/PublicAuthService';
import { authMessages } from '../components/auth/messages';
import { useEmailTimeout, EMAIL_TIMEOUT_CONFIG } from '../components/ui/EmailTimeout';

const ForgotPasswordScreen = () => {
  const navigation = useNavigation();
  
  const [formData, setFormData] = useState({
    cpf: '',
  });
  const [cpfError, setCpfError] = useState('');
  const [emailSent, setEmailSent] = useState(false);
  
  // Hook para timeout de email
  const emailTimeout = useEmailTimeout(EMAIL_TIMEOUT_CONFIG.DEFAULT_TIMEOUT);

  const handleChange = (field, value) => {
    let formattedValue = value;
    
    // Apply CPF formatter
    if (field === 'cpf') {
      formattedValue = formatters.formatCPF(value);
      // Clear error when typing
      setCpfError('');
    }

    setFormData(prev => ({ ...prev, [field]: formattedValue }));
  };

  const handleBlur = (field) => {
    if (field === 'cpf' && formData.cpf) {
      if (!formatters.validateCPF(formData.cpf)) {
        setCpfError(authMessages.forgotPasswordErrors.invalidCpf);
      } else {
        setCpfError('');
      }
    }
  };

  const handleSubmit = async () => {
    if (!formData.cpf) {
      toastHelper.showError(authMessages.forgotPasswordErrors.requiredCpf);
      return;
    }

    if (!formatters.validateCPF(formData.cpf)) {
      toastHelper.showError(authMessages.forgotPasswordErrors.invalidCpf);
      return;
    }

    try {
      await emailTimeout.executeWithTimeout(
        () => PublicAuthService.forgotPassword(formData.cpf.replace(/\D/g, '')),
        {
          successMessage: authMessages.success.forgotPasswordSuccess,
          timeoutMessage: 'Tempo limite para envio do email esgotado. Verifique sua conexão e tente novamente.',
          errorMessage: 'Erro ao enviar email de recuperação.',
          onSuccess: () => {
            setEmailSent(true);
          }
        }
      );
    } catch (error) {
      // Erro já tratado pelo hook
    }
  };

  const handleGoToResetPassword = () => {
    navigation.navigate('ResetPassword', { cpf: formData.cpf.replace(/\D/g, '') });
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView 
        style={styles.container}
        behavior={Platform.OS === 'ios' ? 'height' : undefined}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 0 : 0}
      >
        <ScrollView contentContainerStyle={styles.scrollContainer}>
          <View style={styles.formContainer}>
            <View style={styles.titleContainer}>
              <Text style={styles.title}>Recuperar Senha</Text>
              <Text style={styles.subtitle}>
                {emailSent 
                  ? 'Um código foi enviado para seu email' 
                  : 'Informe seu CPF para receber um link de recuperação'
                }
              </Text>
            </View>

            {!emailSent ? (
              <View style={styles.form}>
                <View style={styles.formFieldGroup}>
                  <Text style={styles.formLabel}>CPF</Text>
                  <Input
                    placeholder="000.000.000-00"
                    value={formData.cpf}
                    onChangeText={(text) => handleChange('cpf', text)}
                    onBlur={() => handleBlur('cpf')}
                    keyboardType="numeric"
                    style={[styles.inputField, cpfError && styles.inputError]}
                  />
                  {cpfError ? <Text style={styles.errorText}>{cpfError}</Text> : null}
                </View>

                <Button
                  label={emailTimeout.isLoading ? authMessages.loading.forgotPassword : "Enviar email de recuperação"}
                  onPress={handleSubmit}
                  loading={emailTimeout.isLoading}
                  disabled={emailTimeout.isLoading}
                  style={styles.submitButton}
                />
              </View>
            ) : (
              <View style={styles.successContainer}>
                <View style={styles.successIcon}>
                  <Feather name="mail" size={64} color="#000" />
                </View>
                
                <Text style={styles.successTitle}>Email enviado!</Text>
                <Text style={styles.successMessage}>
                  Enviamos um código de 6 dígitos para seu email. Verifique sua caixa de entrada (e também a pasta de spam) e digite o código na próxima tela.
                </Text>

                <Button
                  label="Inserir código"
                  onPress={handleGoToResetPassword}
                  style={styles.codeButton}
                />

                <TouchableOpacity 
                  style={[styles.resendButton, emailTimeout.isLoading && styles.resendButtonDisabled]}
                  onPress={handleSubmit}
                  disabled={emailTimeout.isLoading}
                >
                  <Text style={[styles.resendText, emailTimeout.isLoading && styles.resendTextDisabled]}>
                    {emailTimeout.isLoading ? authMessages.loading.resendCode : "Reenviar código"}
                  </Text>
                </TouchableOpacity>
              </View>
            )}

            <View style={styles.loginContainer}>
              <Text style={styles.loginText}>
                Lembrou sua senha?{' '}
                <Text 
                  style={styles.loginLink}
                  onPress={() => navigation.navigate('Login')}
                >
                  Voltar ao login
                </Text>
              </Text>
            </View>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#fff',
  },
  container: {
    flex: 1,
  },
  scrollContainer: {
    flexGrow: 1,
    justifyContent: 'center',
    paddingHorizontal: 16,
    paddingBottom: 40,
  },
  formContainer: {
    width: '100%',
    maxWidth: 400,
    alignSelf: 'center',
  },
  titleContainer: {
    marginBottom: 40,
    alignItems: 'center',
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111',
    marginBottom: 8,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    lineHeight: 22,
  },
  form: {
    marginBottom: 24,
  },
  formFieldGroup: {
    marginBottom: 20,
  },
  formLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111',
    marginBottom: 8,
  },
  inputError: {
    borderColor: '#ef4444',
  },
  errorText: {
    color: '#ef4444',
    fontSize: 14,
    marginTop: 4,
  },
  submitButton: {
    marginTop: 24,
    width: '100%',
  },
  successContainer: {
    alignItems: 'center',
    paddingVertical: 24,
  },
  successIcon: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#f0f9ff',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 24,
  },
  successTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111',
    marginBottom: 16,
    textAlign: 'center',
  },
  successMessage: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    lineHeight: 22,
    marginBottom: 24,
  },
  codeButton: {
    marginBottom: 16,
    width: '100%',
  },
  resendButton: {
    paddingVertical: 8,
    alignItems: 'center',
  },
  resendText: {
    fontSize: 16,
    color: '#000',
    textDecorationLine: 'underline',
  },
  resendButtonDisabled: {
    opacity: 0.5,
  },
  resendTextDisabled: {
    textDecorationLine: 'none',
  },
  loginContainer: {
    alignItems: 'center',
    marginTop: 24,
  },
  loginText: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
  },
  loginLink: {
    color: '#000',
    fontWeight: '500',
    textDecorationLine: 'underline',
  },
});

export default ForgotPasswordScreen; 