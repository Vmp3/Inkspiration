import ApiService from './ApiService';

class AgendamentoService {
  
  async buscarHorariosDisponiveis(idProfissional, data, tipoServico) {
    try {
      const tipoServicoFormatado = this.formatarTipoServico(tipoServico);
      
      const params = new URLSearchParams({
        idProfissional: idProfissional.toString(),
        data: data,
        tipoServico: tipoServicoFormatado
      });
      
      const endpoint = `/disponibilidades/verificar?${params.toString()}`;
      const result = await ApiService.get(endpoint);
      
      if (result === null) {
        return [];
      }
      
      return result;
    } catch (error) {
      if (!error.message || !error.message.includes('204')) {
        // console.error('Erro ao buscar horários disponíveis:', error);
      }
      throw error;
    }
  }

  formatarTipoServico(tipoServico) {
    const mapeamento = {
      'TATUAGEM_PEQUENA': 'pequena',
      'TATUAGEM_MEDIA': 'media',
      'TATUAGEM_GRANDE': 'grande',
      'SESSAO': 'sessao'
    };
    
    return mapeamento[tipoServico] || tipoServico;
  }

  async criarAgendamento(dadosAgendamento) {
    try {
      const tipoServicoFormatado = this.formatarTipoServico(dadosAgendamento.tipoServico);
      
      const dadosComStatus = {
        ...dadosAgendamento,
        tipoServico: tipoServicoFormatado,
        status: "AGENDADO"
      };
      return await ApiService.post('/agendamentos', dadosComStatus);
    } catch (error) {
      // console.error('Erro ao criar agendamento:', error);
      throw error;
    }
  }

  async buscarTiposServicoPorProfissional(idProfissional) {
    try {
      return await ApiService.get(`/tipos-servico/${idProfissional}`);
    } catch (error) {
      // console.error('Erro ao buscar tipos de serviço do profissional:', error);
      throw error;
    }
  }

  async buscarAgendamentosPorUsuario(idUsuario, page = 0) {
    try {
      return await ApiService.get(`/agendamentos/usuario/${idUsuario}?page=${page}`);
    } catch (error) {
      // console.error('Erro ao buscar agendamentos do usuário:', error);
      throw error;
    }
  }

  async buscarAgendamentosPorProfissional(idProfissional, page = 0) {
    try {
      return await ApiService.get(`/agendamentos/profissional/${idProfissional}?page=${page}`);
    } catch (error) {
      // console.error('Erro ao buscar agendamentos do profissional:', error);
      throw error;
    }
  }

  async atualizarAgendamento(id, dadosAgendamento) {
    try {
      const tipoServicoFormatado = this.formatarTipoServico(dadosAgendamento.tipoServico);
      
      const dadosParaEnvio = {
        tipoServico: tipoServicoFormatado,
        descricao: dadosAgendamento.descricao,
        dtInicio: dadosAgendamento.dtInicio
      };
      
      return await ApiService.put(`/agendamentos/${id}`, dadosParaEnvio);
    } catch (error) {
      // console.error('Erro ao atualizar agendamento:', error);
      throw error;
    }
  }

  async excluirAgendamento(id) {
    try {
      return await ApiService.delete(`/agendamentos/${id}`);
    } catch (error) {
      // console.error('Erro ao excluir agendamento:', error);
      throw error;
    }
  }

  async listarMeusAgendamentos(page = 0, size = 5) {
    try {
      const response = await ApiService.get(`/agendamentos/meus-agendamentos?page=${page}&size=${size}`);
      return response;
    } catch (error) {
      // console.error('Erro ao buscar meus agendamentos:', error);
      throw error;
    }
  }

  async atualizarStatusAgendamento(id, novoStatus) {
    try {
      const statusMap = {
        'Agendado': 'AGENDADO',
        'Cancelado': 'CANCELADO',
        'Concluído': 'CONCLUIDO',
        'Concluido': 'CONCLUIDO'
      };

      const statusFormatado = statusMap[novoStatus] || novoStatus.toUpperCase();
      return await ApiService.put(`/agendamentos/${id}/status?status=${statusFormatado}`);
    } catch (error) {
      // console.error('Erro ao atualizar status do agendamento:', error);
      throw error;
    }
  }

  async listarMeusAgendamentosFuturos(page = 0, size = 5) {
    try {
      const response = await ApiService.get(`/agendamentos/meus-agendamentos/futuros?page=${page}&size=${size}&sort=dtInicio,asc`);
      return response;
    } catch (error) {
      // console.error('Erro ao listar agendamentos futuros:', error);
      throw error;
    }
  }

  async listarMeusAgendamentosPassados(page = 0, size = 5) {
    try {
      const response = await ApiService.get(`/agendamentos/meus-agendamentos/passados?page=${page}&size=${size}&sort=dtInicio,desc`);
      return response;
    } catch (error) {
      // console.error('Erro ao listar agendamentos passados:', error);
      throw error;
    }
  }

  async exportarAgendamentosPDF(ano) {
    try {
      const response = await ApiService.get(`/agendamentos/relatorios/exportar-pdf?ano=${ano}`, {
        responseType: 'blob',
        headers: {
          'Accept': 'application/pdf'
        }
      });
      
      if (response.data instanceof Blob) {
        const base64Data = await this.blobToBase64(response.data);
        return {
          ...response,
          data: base64Data
        };
      }
      
      return response;
    } catch (error) {
      // console.error('Erro ao exportar agendamentos para PDF:', error);
      throw error;
    }
  }

  blobToBase64(blob) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const base64 = reader.result.split(',')[1];
        resolve(base64);
      };
      reader.onerror = reject;
      reader.readAsDataURL(blob);
    });
  }

  async listarMeusAtendimentosFuturos(page = 0, size = 5) {
    try {
      const response = await ApiService.get(`/agendamentos/profissional/meus-atendimentos/futuros?page=${page}&size=${size}&sort=dtInicio,asc`);
      return response;
    } catch (error) {
      // console.error('Erro ao listar atendimentos futuros:', error);
      throw error;
    }
  }

  async listarMeusAtendimentosPassados(page = 0, size = 5) {
    try {
      const response = await ApiService.get(`/agendamentos/profissional/meus-atendimentos/passados?page=${page}&size=${size}&sort=dtInicio,desc`);
      return response;
    } catch (error) {
      // console.error('Erro ao listar atendimentos passados:', error);
      throw error;
    }
  }

  async exportarAtendimentosPDF(ano, mes) {
    try {
      const response = await ApiService.get(`/agendamentos/profissional/relatorios/exportar-pdf?ano=${ano}&mes=${mes}`, {
        responseType: 'blob',
        headers: {
          'Accept': 'application/pdf'
        }
      });
      
      if (response.data instanceof Blob) {
        const base64Data = await this.blobToBase64(response.data);
        return {
          ...response,
          data: base64Data
        };
      }
      
      return response;
    } catch (error) {
      // console.error('Erro ao exportar atendimentos para PDF:', error);
      throw error;
    }
  }
}

export default new AgendamentoService(); 