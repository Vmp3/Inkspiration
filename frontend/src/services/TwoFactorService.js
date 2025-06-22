import ApiService from './ApiService';

class TwoFactorService {
  // Método para lidar com erros de forma consistente
  handleError(error, defaultMessage) {
    // console.error('Erro no TwoFactorService:', error);
    
    if (error.response) {
      // Erro de resposta do servidor
      const status = error.response.status;
      const data = error.response.data;
      
      if (status === 400 && data && data.message) {
        throw new Error(data.message);
      } else if (status === 401) {
        throw new Error('Não autorizado. Faça login novamente');
      } else if (status === 403) {
        throw new Error('Acesso negado');
      } else if (status >= 500) {
        throw new Error('Erro interno do servidor. Tente novamente mais tarde');
      }
    }
    
    throw new Error(defaultMessage);
  }

  async generateQRCode() {
    try {
      const response = await ApiService.post('/two-factor/generate-qr');
      return response;
    } catch (error) {
      this.handleError(error, 'Erro ao gerar código QR');
    }
  }

  async verifyCode(code) {
    try {
      const response = await ApiService.post('/two-factor/validate', { code });
      return response;
    } catch (error) {
      this.handleError(error, 'Código inválido ou erro de verificação');
    }
  }

  async sendRecoveryCode() {
    try {
      const response = await ApiService.post('/two-factor/send-recovery-code');
      return response;
    } catch (error) {
      this.handleError(error, 'Erro ao enviar código de recuperação');
    }
  }

  async verifyRecoveryCode(recoveryCode) {
    try {
      const response = await ApiService.post('/two-factor/disable-with-recovery', { recoveryCode });
      return response;
    } catch (error) {
      this.handleError(error, 'Código de recuperação inválido');
    }
  }

  async getStatus() {
    try {
      const response = await ApiService.get('/two-factor/status');
      return response;
    } catch (error) {
      this.handleError(error, 'Erro ao carregar status da autenticação de dois fatores');
    }
  }

  async enable(code) {
    try {
      const response = await ApiService.post('/two-factor/enable', { code });
      return response;
    } catch (error) {
      this.handleError(error, 'Erro ao ativar autenticação de dois fatores');
    }
  }

  async disable(code) {
    try {
      const response = await ApiService.post('/two-factor/disable', { code });
      return response;
    } catch (error) {
      this.handleError(error, 'Erro ao desativar autenticação de dois fatores');
    }
  }

  async toggle(code, enabled) {
    if (enabled) {
      return await this.disable(code);
    } else {
      return await this.enable(code);
    }
  }
}

export default new TwoFactorService(); 