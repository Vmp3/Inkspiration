import ApiService from './ApiService';

class PortfolioService {
  async deletePortfolio(id) {
    try {
      const response = await ApiService.delete(`/portfolio/deletar/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao deletar portfólio:', error);
      throw error;
    }
  }

  async getPortfolio(id) {
    try {
      const response = await ApiService.get(`/portfolio/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar portfólio:', error);
      throw error;
    }
  }

  async getAllPortfolios(page = 0) {
    try {
      const response = await ApiService.get(`/portfolio?page=${page}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar portfólios:', error);
      throw error;
    }
  }
}

export default new PortfolioService(); 