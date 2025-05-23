import AsyncStorage from '@react-native-async-storage/async-storage';

const API_URL = 'http://localhost:8080';

const TOKEN_KEY = 'jwtToken';


class AuthService {
  async login(cpf, senha) {
    try {
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
      
      if (token) {
        await this.setToken(token);
      }

      return { token };
    } catch (error) {
      console.error('Erro no login:', error);
      throw error;
    }
  }


  async logout() {
    try {
      await AsyncStorage.removeItem(TOKEN_KEY);
    } catch (error) {
      console.error('Erro ao fazer logout:', error);
      throw error;
    }
  }

  async setToken(token) {
    try {
      await AsyncStorage.setItem(TOKEN_KEY, token);
    } catch (error) {
      console.error('Erro ao armazenar token:', error);
      throw error;
    }
  }


  async getToken() {
    try {
      return await AsyncStorage.getItem(TOKEN_KEY);
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
      
      // Retornar objeto com informações mínimas necessárias
      // Inclui campos extras que podem ser necessários nas telas
      return {
        idUsuario: userId,
        nome: 'Usuário',
        email: 'usuario@exemplo.com',
        telefone: '',
        cpf: '',
        dataNascimento: '',
        role: role,
        cpfMascarado: tokenData.sub ? `***.***.***-${tokenData.sub.substring(tokenData.sub.length - 2)}` : null,
        imagemPerfil: 'https://via.placeholder.com/200'
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
}

export default new AuthService(); 