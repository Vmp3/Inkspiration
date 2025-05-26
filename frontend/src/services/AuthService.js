import AsyncStorage from '@react-native-async-storage/async-storage';

const API_URL = 'http://localhost:8080';

const TOKEN_KEY = 'jwtToken';


class AuthService {
  async login(cpf, senha) {
    try {
      // Limpar qualquer token anterior
      await this.logout();
      
      const response = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ cpf, senha }),
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Erro ao realizar login');
      }

      const token = await response.text();
      
      if (!token) {
        throw new Error('Servidor retornou um token vazio');
      }
      
      // Verificar se o token é válido
      try {
        const tokenData = this.parseJwt(token);
        if (!tokenData) {
          throw new Error('Token inválido retornado pelo servidor');
        }
        console.log('Login bem-sucedido, role no token:', tokenData.scope);
      } catch (tokenError) {
        console.error('Erro ao validar token recebido:', tokenError);
        throw new Error('Erro ao validar token recebido do servidor');
      }
      
      // Armazenar o token e verificar se foi armazenado corretamente
      await this.setToken(token);
      
      // Verificar se o token foi realmente armazenado
      const storedToken = await this.getToken();
      if (!storedToken) {
        console.error('Falha ao armazenar token após login');
        throw new Error('Falha ao armazenar token de autenticação');
      }
      
      console.log('Token armazenado com sucesso após login');
      return { token };
    } catch (error) {
      console.error('Erro no login:', error);
      throw error;
    }
  }


  async logout() {
    try {
      // Remover do AsyncStorage
      await AsyncStorage.removeItem(TOKEN_KEY);
      
      // Remover do cookie (para web)
      if (typeof document !== 'undefined') {
        document.cookie = `${TOKEN_KEY}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/; SameSite=Strict`;
        
        // Tenta remover sem o path e SameSite também (caso tenha sido salvo de forma diferente)
        document.cookie = `${TOKEN_KEY}=; expires=Thu, 01 Jan 1970 00:00:00 UTC;`;
        
        console.log('Token removido dos cookies e AsyncStorage');
      }
    } catch (error) {
      console.error('Erro ao fazer logout:', error);
      throw error;
    }
  }

  async setToken(token) {
    try {
      // Salvar no AsyncStorage (para mobile)
      await AsyncStorage.setItem(TOKEN_KEY, token);
      
      // Salvar em cookie (para web)
      if (typeof document !== 'undefined') {
        const expirationDate = new Date();
        expirationDate.setDate(expirationDate.getDate() + 30); // Cookie expira em 30 dias
        document.cookie = `${TOKEN_KEY}=${token}; expires=${expirationDate.toUTCString()}; path=/; SameSite=Strict`;
      }
    } catch (error) {
      console.error('Erro ao armazenar token:', error);
      throw error;
    }
  }


  async getToken() {
    try {
      // Tentar obter do AsyncStorage (mobile)
      const tokenFromStorage = await AsyncStorage.getItem(TOKEN_KEY);
      if (tokenFromStorage) {
        return tokenFromStorage;
      }
      
      // Tentar obter de cookie (web)
      if (typeof document !== 'undefined') {
        const cookies = document.cookie.split(';');
        for (const cookie of cookies) {
          const [name, value] = cookie.trim().split('=');
          if (name === TOKEN_KEY) {
            return value;
          }
        }
      }
      
      return null;
    } catch (error) {
      console.error('Erro ao recuperar token:', error);
      return null;
    }
  }


  parseJwt(token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Erro ao decodificar token:', error);
      return null;
    }
  }

  async isAuthenticated() {
    try {
      const token = await this.getToken();
      
      if (!token) {
        return false;
      }
      
      // Verificar se o token está revogado
      const isRevoked = await this.isTokenRevoked(token);
      if (isRevoked) {
        console.log('Token revogado, fazendo logout automático');
        await this.logout();
        return false;
      }
      
      const tokenData = this.parseJwt(token);
      return tokenData && tokenData.exp * 1000 > Date.now();
    } catch (error) {
      console.error('Erro ao verificar autenticação:', error);
      return false;
    }
  }

  async getUserData() {
    try {
      const token = await this.getToken();
      
      if (!token) {
        console.warn('Token não encontrado ao buscar dados do usuário');
        return null;
      }
      
      const tokenData = this.parseJwt(token);
      
      if (!tokenData) {
        console.error('Erro ao decodificar token para obter dados do usuário');
        return null;
      }
      
      // Verificar role do token
      const scope = tokenData.scope || '';
      let role = 'ROLE_USER';
      
      if (scope.includes('ROLE_ADMIN')) {
        role = 'ROLE_ADMIN';
      } else if (scope.includes('ROLE_DELETED')) {
        role = 'ROLE_DELETED';
      } else if (scope.includes('ROLE_PROF')) {
        role = 'ROLE_PROF';
      } else if (scope.includes('ROLE_USER')) {
        role = 'ROLE_USER';
      }
      
      // Extrair userId do token
      const userId = tokenData.userId;
      
      if (!userId) {
        console.error('ID do usuário não encontrado no token');
        return { nome: 'Usuário', role: role, idUsuario: null };
      }
      
      // Buscar dados reais do usuário da API
      try {
        const headers = await this.getAuthHeaders();
        const response = await fetch(`${API_URL}/usuario/${userId}`, {
          method: 'GET',
          headers
        });
        
        if (response.ok) {
          const userData = await response.json();
          return {
            ...userData,
            role: role,
            cpfMascarado: tokenData.sub ? `***.***.***-${tokenData.sub.substring(tokenData.sub.length - 2)}` : null,
          };
        }
      } catch (error) {
        console.error('Erro ao buscar dados do usuário da API:', error);
      }
      
      // Fallback para dados básicos se a API falhar
      return {
        idUsuario: userId,
        nome: 'Usuário',
        email: 'usuario@exemplo.com',
        telefone: '',
        cpf: '',
        dataNascimento: '',
        role: role,
        cpfMascarado: tokenData.sub ? `***.***.***-${tokenData.sub.substring(tokenData.sub.length - 2)}` : null,
        imagemPerfil: null
      };
    } catch (error) {
      console.error('Erro ao obter dados do usuário:', error);
      return null;
    }
  }

  async getAuthHeaders() {
    const token = await this.getToken();
    return {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : '',
    };
  }

  async validateTokenBeforeRequest() {
    try {
      // Verificar se existe um token
      const token = await this.getToken();
      if (!token) {
        console.warn('Nenhum token disponível para validação');
        return false;
      }

      // Verificar se o token está expirado
      const tokenData = this.parseJwt(token);
      if (!tokenData || !tokenData.exp) {
        console.warn('Token inválido ou sem data de expiração');
        return false;
      }

      const isExpired = tokenData.exp * 1000 < Date.now();
      if (isExpired) {
        console.warn('Token expirado, necessário fazer login novamente');
        await this.logout();
        return false;
      }

      // Verificar se o token foi revogado no servidor
      const isRevoked = await this.isTokenRevoked(token);
      if (isRevoked) {
        console.warn('Token foi revogado no servidor');
        await this.logout();
        return false;
      }

      // Verificar se o token no cliente corresponde ao token_atual no servidor
      // para o usuário logado
      if (tokenData.userId) {
        try {
          const response = await fetch(`${API_URL}/usuario/${tokenData.userId}/validate-token`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ token })
          });

          if (response.ok) {
            const result = await response.json();
            if (!result.valid) {
              console.warn('Token diferente do armazenado no servidor:', result.message);
              
              if (result.newToken) {
                console.log('Recebendo novo token do servidor');
                await this.setToken(result.newToken);
                return true;
              }
              
              await this.logout();
              return false;
            }
            return true;
          }
        } catch (error) {
          console.error('Erro ao validar token com servidor:', error);
          // Continua mesmo com erro de validação no servidor
        }
      }

      return true;
    } catch (error) {
      console.error('Erro ao validar token:', error);
      return false;
    }
  }

  // Método para fazer fetch com validação de token
  async fetchWithAuth(url, options = {}) {
    const isTokenValid = await this.validateTokenBeforeRequest();
    
    if (!isTokenValid) {
      throw new Error('Token inválido ou expirado');
    }
    
    const headers = await this.getAuthHeaders();
    const mergedOptions = {
      ...options,
      headers: {
        ...headers,
        ...(options.headers || {})
      }
    };
    
    const response = await fetch(url, mergedOptions);
    
    // Verificar se a resposta contém um novo token no header
    const newToken = response.headers.get('New-Auth-Token');
    if (newToken) {
      console.log('Recebido novo token do servidor na resposta');
      await this.setToken(newToken);
    }
    
    return response;
  }

  async isTokenRevoked(token) {
    try {
      const response = await fetch(`${API_URL}/auth/verify-token`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ token })
      });
      
      if (response.ok) {
        return await response.json();
      }
      
      return false;
    } catch (error) {
      console.error('Erro ao verificar token revogado:', error);
      return false;
    }
  }

  async reautenticar(userId) {
    try {
      console.log('Reautenticando usuário:', userId);
      
      // Limpar tokens anteriores
      await this.logout();
      
      const response = await fetch(`${API_URL}/auth/reauth/${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        console.error('Erro na reautenticação:', response.status, response.statusText);
        throw new Error('Falha na reautenticação');
      }
      
      const token = await response.text();
      console.log('Novo token recebido da reautenticação');
      
      // Salvar o novo token
      await this.setToken(token);
      
      // Verificar se o token tem a role correta
      const tokenData = this.parseJwt(token);
      console.log('Token de reautenticação com role:', tokenData.scope);
      
      return tokenData.scope.includes('ROLE_PROF');
    } catch (error) {
      console.error('Erro na reautenticação:', error);
      return false;
    }
  }

  async refreshToken(userId) {
    // Delegando para o método reautenticar
    return await this.reautenticar(userId);
  }
}

export default new AuthService(); 