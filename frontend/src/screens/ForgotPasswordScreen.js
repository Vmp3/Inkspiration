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

const ForgotPasswordScreen = () => {
  const navigation = useNavigation();
  
  const [formData, setFormData] = useState({
    cpf: '',
  });
  const [loading, setLoading] = useState(false);
  const [cpfError, setCpfError] = useState('');
  const [emailSent, setEmailSent] = useState(false);

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
        setCpfError('CPF inválido');
      } else {
        setCpfError('');
      }
    }
  };

  const handleSubmit = async () => {
    if (!formData.cpf) {
      toastHelper.showError('Por favor, informe seu CPF');
      return;
    }

    if (!formatters.validateCPF(formData.cpf)) {
      toastHelper.showError('CPF inválido');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/auth/forgot-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ 
          cpf: formData.cpf.replace(/\D/g, '') 
        }),
      });

      if (response.ok) {
        setEmailSent(true);
        toastHelper.showSuccess('Código de recuperação enviado para seu email!');
      } else {
        const errorData = await response.text();
        
        // Tratar diferentes tipos de erro
        if (response.status === 404) {
          toastHelper.showError('CPF não encontrado em nosso sistema');
        } else if (response.status === 429) {
          toastHelper.showError('Muitas tentativas. Aguarde 15 minutos para tentar novamente');
        } else if (errorData.includes('email')) {
          toastHelper.showError('Erro ao enviar email. Verifique sua conexão e tente novamente');
        } else {
          toastHelper.showError(errorData || 'Erro ao processar solicitação');
        }
      }
    } catch (error) {
      toastHelper.showError('Erro de conexão. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  const handleGoToResetPassword = () => {
    navigation.navigate('ResetPassword', { cpf: formData.cpf.replace(/\D/g, '') });
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
                  label={loading ? "Enviando..." : "Enviar link de recuperação"}
                  onPress={handleSubmit}
                  loading={loading}
                  disabled={loading}
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
                  style={[styles.resendButton, loading && styles.resendButtonDisabled]}
                  onPress={handleSubmit}
                  disabled={loading}
                >
                  <Text style={[styles.resendText, loading && styles.resendTextDisabled]}>
                    {loading ? "Reenviando..." : "Reenviar código"}
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