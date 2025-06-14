import { useState, useCallback } from 'react';
import toastHelper from '../../utils/toastHelper';

export const EMAIL_TIMEOUT_CONFIG = {
  DEFAULT_TIMEOUT: 15000, // 15 segundos
  RESEND_TIMEOUT: 20000,   // 20 segundos para reenvio
  RECOVERY_TIMEOUT: 25000, // 25 segundos para códigos de recuperação 
};

export const useEmailTimeout = (timeoutDuration = EMAIL_TIMEOUT_CONFIG.DEFAULT_TIMEOUT) => {
  const [isLoading, setIsLoading] = useState(false);
  const [timeoutId, setTimeoutId] = useState(null);

  const executeWithTimeout = useCallback(async (emailOperation, options = {}) => {
    const {
      successMessage = 'Email enviado com sucesso!',
      timeoutMessage = 'Tempo limite esgotado. Verifique sua conexão e tente novamente.',
      errorMessage = 'Erro ao enviar email. Tente novamente.',
      onSuccess,
      onError,
      onTimeout,
      customTimeout = timeoutDuration
    } = options;

    setIsLoading(true);

    const timeoutPromise = new Promise((_, reject) => {
      const id = setTimeout(() => {
        reject(new Error('EMAIL_TIMEOUT'));
      }, customTimeout);
      setTimeoutId(id);
    });

    try {
      const result = await Promise.race([
        emailOperation(),
        timeoutPromise
      ]);

      toastHelper.showSuccess(successMessage);
      if (onSuccess) {
        onSuccess(result);
      }
      return result;

    } catch (error) {
      if (error.message === 'EMAIL_TIMEOUT') {
        toastHelper.showError(timeoutMessage);
        if (onTimeout) {
          onTimeout();
        }
      } else {
        toastHelper.showError(error.message || errorMessage);
        if (onError) {
          onError(error);
        }
      }
      throw error;

    } finally {
      if (timeoutId) {
        clearTimeout(timeoutId);
        setTimeoutId(null);
      }
      setIsLoading(false);
    }
  }, [timeoutDuration, timeoutId]);

  const cancelTimeout = useCallback(() => {
    if (timeoutId) {
      clearTimeout(timeoutId);
      setTimeoutId(null);
    }
    setIsLoading(false);
  }, [timeoutId]);

  return {
    isLoading,
    executeWithTimeout,
    cancelTimeout
  };
};

export const EmailTimeoutProvider = ({ children, defaultTimeout = EMAIL_TIMEOUT_CONFIG.DEFAULT_TIMEOUT }) => {
  return children;
};

export default {
  useEmailTimeout,
  EmailTimeoutProvider,
  EMAIL_TIMEOUT_CONFIG
}; 