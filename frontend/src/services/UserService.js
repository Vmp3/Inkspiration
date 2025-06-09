import ApiService from './ApiService';

class UserService {
  async getAllUsers(page = 0) {
    try {
      const response = await ApiService.get(`/usuario?page=${page}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar usuários:', error);
      throw new Error(error.message || 'Erro ao buscar usuários');
    }
  }

  async getUserById(id) {
    try {
      const response = await ApiService.get(`/usuario/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar usuário:', error);
      throw new Error(error.message || 'Erro ao buscar usuário');
    }
  }

  async updateUser(id, userData) {
    try {
      const response = await ApiService.put(`/usuario/atualizar/${id}`, userData);
      return response;
    } catch (error) {
      console.error('Erro ao atualizar usuário:', error);
      throw new Error(error.message || 'Erro ao atualizar usuário');
    }
  }

  async deactivateUser(id) {
    try {
      const response = await ApiService.put(`/usuario/desativar/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao desativar usuário:', error);
      throw new Error(error.message || 'Erro ao desativar usuário');
    }
  }

  async reactivateUser(id) {
    try {
      const response = await ApiService.put(`/usuario/reativar/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao reativar usuário:', error);
      throw new Error(error.message || 'Erro ao reativar usuário');
    }
  }

  async deleteUser(id) {
    try {
      const response = await ApiService.delete(`/usuario/deletar/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao excluir usuário:', error);
      throw new Error(error.message || 'Erro ao excluir usuário');
    }
  }

  async updateProfileImage(id, imageBase64) {
    try {
      const response = await ApiService.put(`/usuario/${id}/foto-perfil`, { imagemBase64: imageBase64 });
      return response;
    } catch (error) {
      console.error('Erro ao atualizar foto do perfil:', error);
      throw new Error(error.message || 'Erro ao atualizar foto do perfil');
    }
  }
}

export default new UserService(); 