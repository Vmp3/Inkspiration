import ApiService from './ApiService';

class AgendamentoService {
  
  async buscarHorariosDisponiveis(idProfissional, data, tipoServico) {
    try {
      const params = new URLSearchParams({
        idProfissional: idProfissional.toString(),
        data: data,
        tipoServico: tipoServico
      });
      
      const endpoint = `/disponibilidades/verificar?${params.toString()}`;
      const result = await ApiService.get(endpoint);
      
      if (result === null) {
        return [];
      }
      
      return result;
    } catch (error) {
      if (!error.message || !error.message.includes('204')) {
        console.error('Erro ao buscar horários disponíveis:', error);
      }
      throw error;
    }
  }

  async criarAgendamento(dadosAgendamento) {
    try {
      return await ApiService.post('/agendamentos', dadosAgendamento);
    } catch (error) {
      console.error('Erro ao criar agendamento:', error);
      throw error;
    }
  }

  async buscarTiposServico() {
    try {
      return await ApiService.get('/agendamentos/tipos-servico');
    } catch (error) {
      console.error('Erro ao buscar tipos de serviço:', error);
      throw error;
    }
  }

  async buscarAgendamentosPorUsuario(idUsuario, page = 0) {
    try {
      return await ApiService.get(`/agendamentos/usuario/${idUsuario}?page=${page}`);
    } catch (error) {
      console.error('Erro ao buscar agendamentos do usuário:', error);
      throw error;
    }
  }

  async buscarAgendamentosPorProfissional(idProfissional, page = 0) {
    try {
      return await ApiService.get(`/agendamentos/profissional/${idProfissional}?page=${page}`);
    } catch (error) {
      console.error('Erro ao buscar agendamentos do profissional:', error);
      throw error;
    }
  }

  async atualizarAgendamento(id, dadosAgendamento) {
    try {
      return await ApiService.put(`/agendamentos/${id}`, dadosAgendamento);
    } catch (error) {
      console.error('Erro ao atualizar agendamento:', error);
      throw error;
    }
  }

  async excluirAgendamento(id) {
    try {
      return await ApiService.delete(`/agendamentos/${id}`);
    } catch (error) {
      console.error('Erro ao excluir agendamento:', error);
      throw error;
    }
  }
}

export default new AgendamentoService(); 