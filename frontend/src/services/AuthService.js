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
        return null;
      }
      
      const tokenData = this.parseJwt(token);
      
      if (!tokenData) {
        return null;
      }
      
      const userId = tokenData.userId;
      
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
      
      if (!userId) {
        console.error('ID do usuário não encontrado no token');
        return { nome: 'Usuário', role: role };
      }
      
      try {
        const userResponse = await fetch(`${API_URL}/usuario/${userId}`, {
          method: 'GET',
          headers: await this.getAuthHeaders()
        });
        
        if (!userResponse.ok) {
          console.error('Erro ao buscar dados do usuário');
          return { nome: 'Usuário', role: role };
        }
        
        const userData = await userResponse.json();
        
        userData.role = role;
        
        return userData;
      } catch (error) {
        console.error('Erro ao buscar dados do usuário:', error);
        return { nome: 'Usuário', role: role };
      }
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