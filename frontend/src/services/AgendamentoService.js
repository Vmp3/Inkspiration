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
      const dadosComStatus = {
        ...dadosAgendamento,
        status: "Agendado"
      };
      return await ApiService.post('/agendamentos', dadosComStatus);
    } catch (error) {
      console.error('Erro ao criar agendamento:', error);
      throw error;
    }
  }

  async buscarTiposServicoPorProfissional(idProfissional) {
    try {
      return await ApiService.get(`/tipos-servico/${idProfissional}`);
    } catch (error) {
      console.error('Erro ao buscar tipos de serviço do profissional:', error);
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

  async listarMeusAgendamentos(page = 0, size = 5) {
    try {
      const response = await ApiService.get(`/agendamentos/meus-agendamentos?page=${page}&size=${size}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar meus agendamentos:', error);
      throw error;
    }
  }

  async atualizarStatusAgendamento(id, novoStatus) {
    try {
      return await ApiService.put(`/agendamentos/${id}/status?status=${novoStatus}`);
    } catch (error) {
      console.error('Erro ao atualizar status do agendamento:', error);
      throw error;
    }
  }
}

export default new AgendamentoService(); 