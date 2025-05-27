import AuthService from './AuthService';

const API_URL = 'http://localhost:8080';

class ApiService {
  constructor() {
    this.authService = AuthService;
  }

  async get(endpoint, options = {}) {
    try {
      const url = `${API_URL}${endpoint}`;
      const response = await this.authService.fetchWithAuth(url, {
        method: 'GET',
        ...options
      });
      
      if (!response.ok) {
        throw new Error(`Erro na requisição: ${response.status} ${response.statusText}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error(`Erro na requisição GET para ${endpoint}:`, error);
      throw error;
    }
  }

  async post(endpoint, data, options = {}) {
    try {
      const url = `${API_URL}${endpoint}`;
      const response = await this.authService.fetchWithAuth(url, {
        method: 'POST',
        body: JSON.stringify(data),
        ...options
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Erro na requisição: ${response.status} ${response.statusText} - ${errorText}`);
      }
      
      // Verificar se a resposta está vazia
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }
      
      return await response.text();
    } catch (error) {
      console.error(`Erro na requisição POST para ${endpoint}:`, error);
      throw error;
    }
  }

  async put(endpoint, data, options = {}) {
    try {
      const url = `${API_URL}${endpoint}`;
      const response = await this.authService.fetchWithAuth(url, {
        method: 'PUT',
        body: JSON.stringify(data),
        ...options
      });
      
      if (!response.ok) {
        throw new Error(`Erro na requisição: ${response.status} ${response.statusText}`);
      }
      
      // Verificar se a resposta está vazia
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }
      
      return await response.text();
    } catch (error) {
      console.error(`Erro na requisição PUT para ${endpoint}:`, error);
      throw error;
    }
  }

  async delete(endpoint, options = {}) {
    try {
      const url = `${API_URL}${endpoint}`;
      const response = await this.authService.fetchWithAuth(url, {
        method: 'DELETE',
        ...options
      });
      
      if (!response.ok) {
        throw new Error(`Erro na requisição: ${response.status} ${response.statusText}`);
      }
      
      // Verificar se a resposta está vazia
      if (response.status === 204) {
        return null;
      }
      
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }
      
      return await response.text();
    } catch (error) {
      console.error(`Erro na requisição DELETE para ${endpoint}:`, error);
      throw error;
    }
  }
}

export default new ApiService(); 