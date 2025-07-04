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
import { useNavigation, useRoute } from '@react-navigation/native';
import { Feather } from '@expo/vector-icons';
import toastHelper from '../utils/toastHelper';
import Input from '../components/ui/Input';
import Button from '../components/ui/Button';
import PublicAuthService from '../services/PublicAuthService';
import { authMessages } from '../components/auth/messages';
import { useEmailTimeout, EMAIL_TIMEOUT_CONFIG } from '../components/ui/EmailTimeout';

const ResetPasswordScreen = () => {
  const navigation = useNavigation();
  const route = useRoute();
  const { cpf } = route.params || {};
  
  const [formData, setFormData] = useState({
    code: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [loading, setLoading] = useState(false);
  const [codeError, setCodeError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  
  // Hook para timeout de email
  const resendTimeout = useEmailTimeout(EMAIL_TIMEOUT_CONFIG.RESEND_TIMEOUT);

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    if (field === 'code') setCodeError('');
    if (field === 'newPassword') setPasswordError('');
    if (field === 'confirmPassword') setConfirmPasswordError('');
  };

  const handleBlur = (field) => {
    if (field === 'code' && formData.code) {
      if (formData.code.length !== 6) {
        setCodeError(authMessages.resetPasswordErrors.invalidCode);
      } else {
        setCodeError('');
      }
    }

    if (field === 'newPassword' && formData.newPassword) {
      if (formData.newPassword.length < 6) {
        setPasswordError(authMessages.resetPasswordErrors.invalidPassword);
      } else {
        setPasswordError('');
      }
    }

    if (field === 'confirmPassword' && formData.confirmPassword) {
      if (formData.newPassword !== formData.confirmPassword) {
        setConfirmPasswordError(authMessages.resetPasswordErrors.passwordMismatch);
      } else {
        setConfirmPasswordError('');
      }
    }
  };

  const validateForm = () => {
    let isValid = true;

    if (!formData.code) {
      setCodeError(authMessages.resetPasswordErrors.requiredFields);
      isValid = false;
    } else if (formData.code.length !== 6) {
      setCodeError(authMessages.resetPasswordErrors.invalidCode);
      isValid = false;
    }

    if (!formData.newPassword) {
      setPasswordError(authMessages.resetPasswordErrors.requiredFields);
      isValid = false;
    } else if (formData.newPassword.length < 6) {
      setPasswordError(authMessages.resetPasswordErrors.invalidPassword);
      isValid = false;
    }

    if (!formData.confirmPassword) {
      setConfirmPasswordError(authMessages.resetPasswordErrors.requiredFields);
      isValid = false;
    } else if (formData.newPassword !== formData.confirmPassword) {
      setConfirmPasswordError(authMessages.resetPasswordErrors.passwordMismatch);
      isValid = false;
    }

    return isValid;
  };

  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      await PublicAuthService.resetPassword(cpf, formData.code, formData.newPassword);
      toastHelper.showSuccess(authMessages.success.resetPasswordSuccess);
      setFormData({
        code: '',
        newPassword: '',
        confirmPassword: '',
      });
      navigation.navigate('Login');
    } catch (error) {
      toastHelper.showError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleResendCode = async () => {
    try {
      await resendTimeout.executeWithTimeout(
        () => PublicAuthService.forgotPassword(cpf),
        {
          successMessage: authMessages.success.codeResent,
          timeoutMessage: 'Tempo limite para reenvio do código esgotado. Tente novamente.',
          errorMessage: 'Erro ao reenviar código.',
        }
      );
    } catch (error) {
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView 
        style={styles.container}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 64 : 0}
      >
        <ScrollView contentContainerStyle={styles.scrollContainer}>
          <View style={styles.formContainer}>
            <View style={styles.titleContainer}>
              <Text style={styles.title}>Redefinir Senha</Text>
              <Text style={styles.subtitle}>
                Digite o código de 6 dígitos enviado para seu email e sua nova senha
              </Text>
            </View>

            <View style={styles.form}>
              <View style={styles.formFieldGroup}>
                <Text style={styles.formLabel}>Código de verificação</Text>
                <Input
                  placeholder="000000"
                  value={formData.code}
                  onChangeText={(text) => handleChange('code', text.replace(/\D/g, '').slice(0, 6))}
                  onBlur={() => handleBlur('code')}
                  keyboardType="numeric"
                  maxLength={6}
                  style={[styles.inputField, codeError && styles.inputError]}
                />
                {codeError ? <Text style={styles.errorText}>{codeError}</Text> : null}
              </View>

              <View style={styles.formFieldGroup}>
                <Text style={styles.formLabel}>Nova senha</Text>
                <Input
                  placeholder="••••••••"
                  value={formData.newPassword}
                  onChangeText={(text) => handleChange('newPassword', text)}
                  onBlur={() => handleBlur('newPassword')}
                  secureTextEntry
                  style={[styles.inputField, passwordError && styles.inputError]}
                />
                {passwordError ? <Text style={styles.errorText}>{passwordError}</Text> : null}
              </View>

              <View style={styles.formFieldGroup}>
                <Text style={styles.formLabel}>Confirmar senha</Text>
                <Input
                  placeholder="••••••••"
                  value={formData.confirmPassword}
                  onChangeText={(text) => handleChange('confirmPassword', text)}
                  onBlur={() => handleBlur('confirmPassword')}
                  secureTextEntry
                  style={[styles.inputField, confirmPasswordError && styles.inputError]}
                />
                {confirmPasswordError ? <Text style={styles.errorText}>{confirmPasswordError}</Text> : null}
              </View>

              <Button
                label={loading ? "Redefinindo..." : "Redefinir senha"}
                onPress={handleSubmit}
                loading={loading}
                disabled={loading}
                style={styles.submitButton}
              />

              <TouchableOpacity 
                style={[styles.resendButton, resendTimeout.isLoading && styles.resendButtonDisabled]}
                onPress={handleResendCode}
                disabled={resendTimeout.isLoading}
              >
                <Text style={[styles.resendText, resendTimeout.isLoading && styles.resendTextDisabled]}>
                  {resendTimeout.isLoading ? "Reenviando..." : "Não recebeu o código? Reenviar"}
                </Text>
              </TouchableOpacity>
            </View>

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
    marginBottom: 16,
    width: '100%',
  },
  resendButton: {
    alignItems: 'center',
    paddingVertical: 8,
  },
  resendText: {
    fontSize: 16,
    color: '#000',
    textDecorationLine: 'underline',
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
  resendButtonDisabled: {
    opacity: 0.5,
  },
  resendTextDisabled: {
    textDecorationLine: 'none',
  },
});

export default ResetPasswordScreen; 