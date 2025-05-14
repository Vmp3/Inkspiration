import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, SafeAreaView, KeyboardAvoidingView, Platform } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import * as formatters from '../utils/formatters';
import { useAuth } from '../context/AuthContext';
import toastHelper from '../utils/toastHelper';

import Header from '../components/Header';
import LoginForm from '../components/forms/LoginForm';

const LoginScreen = () => {
  const navigation = useNavigation();
  const { login, loading: authLoading } = useAuth();
  
  const [formData, setFormData] = useState({
    cpf: '',
    password: '',
  });
  const [rememberMe, setRememberMe] = useState(false);
  const [loading, setLoading] = useState(false);
  const [cpfError, setCpfError] = useState('');

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
    if (!formData.cpf || !formData.password) {
      toastHelper.showError('Por favor, preencha todos os campos');
      return;
    }

    if (!formatters.validateCPF(formData.cpf)) {
      toastHelper.showError('CPF inválido');
      return;
    }

    setLoading(true);
    try {
      const result = await login(
        formData.cpf.replace(/\D/g, ''),
        formData.password
      );

      if (!result.success) {
        toastHelper.showError('Falha ao fazer login. Verifique suas credenciais.');
        return;
      }

      toastHelper.showSuccess('Login realizado com sucesso!');

      // Navegar para a tela principal
      navigation.reset({
        index: 0,
        routes: [{ name: 'Home' }],
      });
    } catch (error) {
      toastHelper.showError('Ocorreu um erro ao fazer login. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView 
        style={styles.container}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 64 : 0}
      >
        <Header />
        
        <ScrollView contentContainerStyle={styles.scrollContainer}>
          <View style={styles.formContainer}>
            <View style={styles.titleContainer}>
              <Text style={styles.title}>Entrar</Text>
              <Text style={styles.subtitle}>Entre na sua conta para continuar</Text>
            </View>

            <LoginForm
              formData={formData}
              handleChange={handleChange}
              handleBlur={handleBlur}
              handleSubmit={handleSubmit}
              cpfError={cpfError}
              rememberMe={rememberMe}
              setRememberMe={setRememberMe}
              loading={loading || authLoading}
            />

            <View style={styles.registerContainer}>
              <Text style={styles.registerText}>
                Não tem uma conta?{' '}
                <Text 
                  style={styles.registerLink}
                  onPress={() => navigation.navigate('Register')}
                >
                  Registre-se
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
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
  },
  registerContainer: {
    alignItems: 'center',
  },
  registerText: {
    fontSize: 14,
    color: '#666',
  },
  registerLink: {
    color: '#000',
    fontWeight: '500',
    textDecorationLine: 'underline',
  },
});

export default LoginScreen; 