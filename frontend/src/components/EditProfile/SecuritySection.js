import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Switch } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import Input from '../ui/Input';
import TwoFactorService from '../../services/TwoFactorService';
import toastHelper from '../../utils/toastHelper';
import { editProfileMessages } from './messages';

const SecuritySection = ({ 
  formData, 
  handleChange, 
  handleBlur, 
  passwordError,
  confirmPasswordError 
}) => {
  const navigation = useNavigation();
  const [twoFactorEnabled, setTwoFactorEnabled] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const loadTwoFactorStatus = async () => {
    try {
      console.log('Carregando status do 2FA...');
      const status = await TwoFactorService.getStatus();
      console.log('Status do 2FA recebido:', status);
      setTwoFactorEnabled(status.enabled || false);
    } catch (error) {
      // console.error('Erro ao carregar status do 2FA:', error);
      toastHelper.showError(editProfileMessages.errors.twoFactorStatus);
    }
  };

  // Atualiza o status sempre que a tela recebe foco
  useFocusEffect(
    React.useCallback(() => {
      console.log('SecuritySection recebeu foco, atualizando status 2FA');
      loadTwoFactorStatus();
    }, [])
  );

  const handleTwoFactorToggle = async (newValue) => {
    try {
      setIsLoading(true);
      
      const action = newValue ? 'enable' : 'disable';
      console.log('Navegando para TwoFactorSetup com action:', action);
      
      // Navegar para a tela de configuração 2FA
      navigation.navigate('TwoFactorSetup', {
        action: action,
        onSuccess: () => {
          console.log('Callback onSuccess executado, recarregando status');
          // Callback chamado quando a configuração é bem-sucedida
          loadTwoFactorStatus();
        }
      });
      
    } catch (error) {
      // console.error('Erro ao alterar status do 2FA:', error);
      toastHelper.showError(editProfileMessages.errors.twoFactorToggle);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.sectionTitle}>Segurança</Text>
      
      {/* Seção de alteração de senha */}
      <View style={styles.section}>
        <Text style={styles.subsectionTitle}>Alterar Senha</Text>
        <Text style={styles.subsectionDescription}>
          Deixe em branco se não quiser alterar a senha
        </Text>
        
        <View style={styles.inputGroup}>
          <Text style={styles.label}>Senha Atual</Text>
          <Input
            placeholder="Digite sua senha atual"
            value={formData.senhaAtual}
            onChangeText={(value) => handleChange('senhaAtual', value)}
            onBlur={() => handleBlur('senhaAtual')}
            secureTextEntry
            style={[styles.input, passwordError && styles.inputError]}
          />
          {passwordError ? <Text style={styles.errorText}>{passwordError}</Text> : null}
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>Nova Senha</Text>
          <Input
            placeholder="Digite sua nova senha"
            value={formData.novaSenha}
            onChangeText={(value) => handleChange('novaSenha', value)}
            onBlur={() => handleBlur('novaSenha')}
            secureTextEntry
            style={styles.input}
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>Confirmar Nova Senha</Text>
          <Input
            placeholder="Confirme sua nova senha"
            value={formData.confirmarSenha}
            onChangeText={(value) => handleChange('confirmarSenha', value)}
            onBlur={() => handleBlur('confirmarSenha')}
            secureTextEntry
            style={[styles.input, confirmPasswordError && styles.inputError]}
          />
          {confirmPasswordError ? <Text style={styles.errorText}>{confirmPasswordError}</Text> : null}
        </View>
      </View>

      {/* Seção de autenticação de dois fatores */}
      <View style={styles.section}>
        <Text style={styles.subsectionTitle}>Autenticação de Dois Fatores</Text>
        <Text style={styles.subsectionDescription}>
          Adicione uma camada extra de segurança à sua conta
        </Text>
        
        <View style={styles.twoFactorContainer}>
          <View style={styles.twoFactorInfo}>
            <MaterialIcons 
              name="security" 
              size={24} 
              color={twoFactorEnabled ? "#10B981" : "#6B7280"} 
            />
            <View style={styles.twoFactorText}>
              <Text style={styles.twoFactorTitle}>
                Autenticação de Dois Fatores
              </Text>
              <Text style={styles.twoFactorStatus}>
                Status: {twoFactorEnabled ? 'Ativada' : 'Desativada'}
              </Text>
            </View>
          </View>
          
          <Switch
            value={twoFactorEnabled}
            onValueChange={handleTwoFactorToggle}
            disabled={isLoading}
            trackColor={{ false: "#D1D5DB", true: "#10B981" }}
            thumbColor={twoFactorEnabled ? "#FFFFFF" : "#F3F4F6"}
          />
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 24,
  },
  section: {
    marginBottom: 32,
  },
  subsectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 8,
  },
  subsectionDescription: {
    fontSize: 14,
    color: '#6B7280',
    marginBottom: 16,
  },
  inputGroup: {
    marginBottom: 16,
  },
  label: {
    fontSize: 14,
    fontWeight: '500',
    color: '#374151',
    marginBottom: 6,
  },
  inputField: {
    height: 40,
    borderWidth: 1,
    borderColor: '#e2e2e2',
    borderRadius: 4,
    paddingHorizontal: 12,
    fontSize: 14,
    backgroundColor: '#fff',
  },
  inputError: {
    borderColor: '#EF4444',
  },
  errorText: {
    color: '#EF4444',
    fontSize: 12,
    marginTop: 4,
  },
  twoFactorContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#F9FAFB',
    padding: 16,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  twoFactorInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  twoFactorText: {
    marginLeft: 12,
    flex: 1,
  },
  twoFactorTitle: {
    fontSize: 14,
    fontWeight: '500',
    color: '#111827',
  },
  twoFactorStatus: {
    fontSize: 12,
    color: '#6B7280',
    marginTop: 2,
  },
});

export default SecuritySection; 