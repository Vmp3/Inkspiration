import ApiService from './ApiService';

class AvaliacaoService {
  
  async criarAvaliacao(avaliacaoData) {
    try {
      const response = await ApiService.post('/avaliacoes', avaliacaoData);
      return response.data;
    } catch (error) {
      console.error('Erro ao criar avaliação:', error);
      throw error;
    }
  }



  async buscarAvaliacaoPorAgendamento(idAgendamento) {
    try {
      const response = await ApiService.get(`/avaliacoes/agendamento/${idAgendamento}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        return null;
      }
      console.error('Erro ao buscar avaliação:', error);
      throw error;
    }
  }

  async podeAvaliar(idAgendamento) {
    try {
      const response = await ApiService.get(`/avaliacoes/pode-avaliar/${idAgendamento}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao verificar se pode avaliar:', error);
      return false;
    }
  }


}

export default new AvaliacaoService(); 