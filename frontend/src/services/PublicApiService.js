import axios from 'axios';
import { API_CONFIG } from '../config/apiConfig';

class PublicApiService {
  constructor() {
    this.api = axios.create({
      baseURL: API_CONFIG.BASE_URL,
      timeout: API_CONFIG.TIMEOUT,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  async get(endpoint, options = {}) {
    try {
      const response = await this.api.get(endpoint, options);
      return response.data;
    } catch (error) {
      console.error(`Erro na requisição GET pública para ${endpoint}:`, error);
      throw error;
    }
  }

  async post(endpoint, data, options = {}) {
    try {
      const response = await this.api.post(endpoint, data, options);
      return response.data;
    } catch (error) {
      console.error(`Erro na requisição POST pública para ${endpoint}:`, error);
      throw error;
    }
  }
}

export default new PublicApiService(); 