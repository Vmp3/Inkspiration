const API_URL = 'http://localhost:8080';

class PublicApiService {
  async get(endpoint, options = {}) {
    try {
      const url = `${API_URL}${endpoint}`;
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        ...options
      });
      
      if (!response.ok) {
        throw new Error(`Erro na requisição: ${response.status} ${response.statusText}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error(`Erro na requisição GET pública para ${endpoint}:`, error);
      throw error;
    }
  }

  async post(endpoint, data, options = {}) {
    try {
      const url = `${API_URL}${endpoint}`;
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        ...options
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Erro na requisição: ${response.status} ${response.statusText} - ${errorText}`);
      }
      
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }
      
      return await response.text();
    } catch (error) {
      console.error(`Erro na requisição POST pública para ${endpoint}:`, error);
      throw error;
    }
  }
}

export default new PublicApiService(); 