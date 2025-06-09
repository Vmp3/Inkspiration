import ApiService from './ApiService';

class PublicAuthService {
  async forgotPassword(cpf) {
    try {
      const response = await ApiService.post('/auth/forgot-password', { cpf });
      return response;
    } catch (error) {
      if (error.response) {
        const status = error.response.status;
        const data = error.response.data;
        
        if (status === 404) {
          throw new Error('CPF não encontrado em nosso sistema');
        } else if (status === 429) {
          throw new Error('Muitas tentativas. Aguarde 15 minutos para tentar novamente');
        } else if (typeof data === 'string' && data.includes('email')) {
          throw new Error('Erro ao enviar email. Verifique sua conexão e tente novamente');
        } else {
          throw new Error(data || 'Erro ao processar solicitação');
        }
      }
      throw new Error('Erro de conexão. Tente novamente.');
    }
  }

  async resetPassword(cpf, code, newPassword) {
    try {
      const response = await ApiService.post('/auth/reset-password', {
        cpf,
        code,
        newPassword,
      });
      return response;
    } catch (error) {
      if (error.response) {
        const status = error.response.status;
        const data = error.response.data;
        
        if (status === 400) {
          if (typeof data === 'string' && (data.includes('inválido') || data.includes('expirado'))) {
            throw new Error('Código inválido ou expirado. Solicite um novo código');
          } else {
            throw new Error(data || 'Dados inválidos');
          }
        } else {
          throw new Error(data || 'Erro ao redefinir senha');
        }
      }
      throw new Error('Erro de conexão. Tente novamente.');
    }
  }

  async register(userData) {
    try {
      const response = await ApiService.post('/auth/register', userData);
      return response;
    } catch (error) {
      if (error.response) {
        const data = error.response.data;
        
        // Verificar se há erros específicos por campo
        if (typeof data === 'object') {
          const errorMessages = [];
          Object.entries(data).forEach(([field, message]) => {
            if (typeof message === 'string') {
              errorMessages.push(message);
            }
          });
          
          if (errorMessages.length > 0) {
            throw new Error(errorMessages.join(', '));
          }
        }
        
        if (data.message) {
          throw new Error(data.message);
        }
        
        throw new Error(data || 'Ocorreu um erro ao cadastrar. Tente novamente.');
      }
      throw new Error('Ocorreu um erro ao cadastrar. Tente novamente.');
    }
  }
}

export default new PublicAuthService(); 