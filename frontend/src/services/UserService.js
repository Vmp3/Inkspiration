import AuthService from './AuthService';

const API_URL = 'http://localhost:8080';

class UserService {
  async getAllUsers(page = 0) {
    try {
      const headers = await AuthService.getAuthHeaders();
      const response = await fetch(`${API_URL}/usuario?page=${page}`, {
        method: 'GET',
        headers,
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Erro ao buscar usuários');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar usuários:', error);
      throw error;
    }
  }

  async getUserById(id) {
    try {
      const headers = await AuthService.getAuthHeaders();
      const response = await fetch(`${API_URL}/usuario/${id}`, {
        method: 'GET',
        headers,
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Erro ao buscar usuário');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar usuário:', error);
      throw error;
    }
  }

  async updateUser(id, userData) {
    try {
      const headers = await AuthService.getAuthHeaders();
      const response = await fetch(`${API_URL}/usuario/atualizar/${id}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify(userData),
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Erro ao atualizar usuário');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao atualizar usuário:', error);
      throw error;
    }
  }

  async deactivateUser(id) {
    try {
      const headers = await AuthService.getAuthHeaders();
      const response = await fetch(`${API_URL}/usuario/inativar/${id}`, {
        method: 'POST',
        headers,
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Erro ao inativar usuário');
      }

      return await response.text();
    } catch (error) {
      console.error('Erro ao inativar usuário:', error);
      throw error;
    }
  }

  async reactivateUser(id) {
    try {
      const headers = await AuthService.getAuthHeaders();
      const response = await fetch(`${API_URL}/usuario/reativar/${id}`, {
        method: 'POST',
        headers,
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Erro ao reativar usuário');
      }

      return await response.text();
    } catch (error) {
      console.error('Erro ao reativar usuário:', error);
      throw error;
    }
  }

  async deleteUser(id) {
    try {
      const headers = await AuthService.getAuthHeaders();
      const response = await fetch(`${API_URL}/usuario/deletar/${id}`, {
        method: 'DELETE',
        headers,
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Erro ao excluir usuário');
      }

      return await response.text();
    } catch (error) {
      console.error('Erro ao excluir usuário:', error);
      throw error;
    }
  }
}

export default new UserService(); 