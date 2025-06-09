import ApiService from './ApiService';

class PortifolioService {
  async deletePortifolio(id) {
    try {
      const response = await ApiService.delete(`/portifolio/deletar/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao deletar portfólio:', error);
      throw error;
    }
  }

  async getPortifolio(id) {
    try {
      const response = await ApiService.get(`/portifolio/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar portfólio:', error);
      throw error;
    }
  }

  async getAllPortifolios(page = 0) {
    try {
      const response = await ApiService.get(`/portifolio?page=${page}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar portfólios:', error);
      throw error;
    }
  }
}

export default new PortifolioService(); 