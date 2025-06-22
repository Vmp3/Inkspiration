import axios from 'axios';
import AuthService from './AuthService';
import { API_CONFIG } from '../config/apiConfig';

class ApiService {
  constructor() {
    this.authService = AuthService;
    this.api = axios.create({
      baseURL: API_CONFIG.BASE_URL,
      timeout: API_CONFIG.TIMEOUT,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Interceptor para adicionar token de autenticação
    this.api.interceptors.request.use(
      async (config) => {
        const token = await this.authService.getToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Interceptor para lidar com respostas
    this.api.interceptors.response.use(
      (response) => {
        // Verificar se há um novo token no header
        const newToken = response.headers['new-auth-token'];
        if (newToken) {
          console.log('Recebido novo token do servidor na resposta');
          this.authService.setToken(newToken);
        }
        return response;
      },
      async (error) => {
        if (error.response?.status === 401) {
          // Token expirado ou inválido
          await this.authService.logout();
        }
        return Promise.reject(error);
      }
    );
  }

  async get(endpoint, options = {}) {
    try {
      const response = await this.api.get(endpoint, options);
      
      if (options.responseType === 'blob') {
        return response;
      }
      
      return response.data;
    } catch (error) {
      // console.error(`Erro na requisição GET para ${endpoint}:`, error);
      throw error;
    }
  }

  async post(endpoint, data, options = {}) {
    try {
      const response = await this.api.post(endpoint, data, options);
      return response.data;
    } catch (error) {
      // console.error(`Erro na requisição POST para ${endpoint}:`, error);
      throw error;
    }
  }

  async put(endpoint, data, options = {}) {
    try {
      const response = await this.api.put(endpoint, data, options);
      return response.data;
    } catch (error) {
      // console.error(`Erro na requisição PUT para ${endpoint}:`, error);
      throw error;
    }
  }

  async delete(endpoint, options = {}) {
    try {
      const response = await this.api.delete(endpoint, options);
      return response.data;
    } catch (error) {
      // console.error(`Erro na requisição DELETE para ${endpoint}:`, error);
      throw error;
    }
  }
}

export default new ApiService(); 