import ApiService from './ApiService';
import PublicApiService from './PublicApiService';

class ProfessionalService {


  async getProfessionalById(id) {
    try {
      const response = await PublicApiService.get(`/profissional/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar profissional:', error);
      throw error;
    }
  }

  async buscarPorId(id) {
    try {
      const response = await ApiService.get(`/profissional/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar profissional:', error);
      throw error;
    }
  }

  async getProfessionalByUserId(userId) {
    try {
      const response = await ApiService.get(`/profissional/usuario/${userId}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar profissional por usuário:', error);
      throw error;
    }
  }

  async checkProfessionalProfile(userId) {
    try {
      const response = await ApiService.get(`/profissional/verificar/${userId}`);
      return response;
    } catch (error) {
      console.error('Erro ao verificar perfil profissional:', error);
      throw error;
    }
  }

  async getProfessionalImages(id) {
    try {
      // Usar o endpoint completo e extrair apenas as imagens
      const response = await PublicApiService.get(`/profissional/completo/${id}`);
      return response.imagens || [];
    } catch (error) {
      console.error('Erro ao buscar imagens do profissional:', error);
      throw error;
    }
  }

  async getAllProfessionalsComplete(page = 0, filters = {}) {
    try {
      const params = new URLSearchParams();
      params.append('page', page);
      params.append('size', 9);
      
      if (filters.searchTerm) {
        params.append('searchTerm', filters.searchTerm);
      }
      if (filters.locationTerm) {
        params.append('locationTerm', filters.locationTerm);
      }
      if (filters.minRating && filters.minRating > 0) {
        params.append('minRating', filters.minRating);
      }
      if (filters.selectedSpecialties && filters.selectedSpecialties.length > 0) {
        filters.selectedSpecialties.forEach(specialty => {
          params.append('selectedSpecialties', specialty);
        });
      }
      if (filters.sortBy) {
        params.append('sortBy', filters.sortBy);
      }
      
      const response = await PublicApiService.get(`/profissional/completo?${params.toString()}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar profissionais completos:', error);
      throw error;
    }
  }

  async getProfessionalCompleteById(id) {
    try {
      const response = await PublicApiService.get(`/profissional/completo/${id}`);
      return response;
    } catch (error) {
      console.error('Erro ao buscar profissional completo:', error);
      throw error;
    }
  }

  // Método para transformar dados do backend para o formato esperado pelo frontend
  transformProfessionalData(data) {
    // Verificar se é a nova estrutura (com objetos separados) ou a estrutura antiga
    if (data.endereco && data.portfolio && data.usuario && data.profissional) {
      // Nova estrutura
      const { endereco, portfolio, usuario, profissional } = data;
      
      return {
        id: profissional?.idProfissional?.toString() || profissional?.id?.toString(),
        name: usuario?.nome || usuario?.name,
        rating: profissional?.nota !== undefined && profissional?.nota !== null ? profissional.nota : 0,
        specialties: portfolio?.especialidade 
          ? portfolio.especialidade.split(',').map(s => s.trim())
          : ['Tatuagem'],
        location: endereco 
          ? `${endereco.cidade}, ${endereco.estado}`
          : 'Localização não informada',
        coverImage: usuario?.imagemPerfil || 
                   'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-VEjAdaIDHE3fmR3mSKry3Fh8WoF0J3.png',
        // Dados adicionais do backend
        experience: portfolio?.experiencia,
        description: portfolio?.descricao,
        instagram: portfolio?.instagram,
        tiktok: portfolio?.tiktok,
        facebook: portfolio?.facebook,
        twitter: portfolio?.twitter,
        website: portfolio?.website,
        email: usuario?.email,
        phone: usuario?.telefone,
      };
    } else {
      // Estrutura antiga (manter compatibilidade)
      const professional = data;
      return {
        id: professional.idProfissional?.toString() || professional.id?.toString(),
        name: professional.usuario?.nome || professional.name,
        rating: professional.nota !== undefined && professional.nota !== null ? professional.nota : 0,
        specialties: professional.portifolio?.especialidade 
          ? professional.portifolio.especialidade.split(',').map(s => s.trim())
          : ['Tatuagem'],
        location: professional.endereco 
          ? `${professional.endereco.cidade}, ${professional.endereco.estado}`
          : professional.usuario?.endereco
          ? `${professional.usuario.endereco.cidade}, ${professional.usuario.endereco.estado}`
          : 'Localização não informada',
        coverImage: professional.usuario?.imagemPerfil || 
                   'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-VEjAdaIDHE3fmR3mSKry3Fh8WoF0J3.png',
        // Dados adicionais do backend
        experience: professional.portifolio?.experiencia,
        description: professional.portifolio?.descricao,
        instagram: professional.portifolio?.instagram,
        tiktok: professional.portifolio?.tiktok,
        facebook: professional.portifolio?.facebook,
        twitter: professional.portifolio?.twitter,
        website: professional.portifolio?.website,
        email: professional.usuario?.email,
        phone: professional.usuario?.telefone,
      };
    }
  }



  // Método para transformar dados completos do backend para o formato esperado pelo frontend
  transformCompleteProfessionalData(data) {
    // Nova estrutura da API tem os dados organizados em objetos separados
    const { endereco, portfolio, usuario, profissional } = data;
    
    return {
      id: profissional?.idProfissional?.toString() || 'N/A',
      name: usuario?.nome || 'Nome não informado',
      rating: profissional?.nota !== undefined && profissional?.nota !== null ? profissional.nota : 0,
      specialties: portfolio?.especialidade 
        ? portfolio.especialidade.split(',').map(s => s.trim())
        : ['Tatuagem'],
      location: endereco 
        ? `${endereco.cidade}, ${endereco.estado}`
        : 'Localização não informada',
      coverImage: usuario?.imagemPerfil || 
                 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-VEjAdaIDHE3fmR3mSKry3Fh8WoF0J3.png',
      // Dados adicionais do backend
      experience: portfolio?.experiencia || 'Não informado',
      description: portfolio?.descricao || 'Descrição não disponível',
      instagram: portfolio?.instagram,
      tiktok: portfolio?.tiktok,
      facebook: portfolio?.facebook,
      twitter: portfolio?.twitter,
      website: portfolio?.website,
      email: usuario?.email,
      phone: usuario?.telefone,
      // Dados do endereço
      address: endereco ? {
        rua: endereco.rua,
        numero: endereco.numero,
        bairro: endereco.bairro,
        cidade: endereco.cidade,
        estado: endereco.estado,
        cep: endereco.cep,
        complemento: endereco.complemento,
        latitude: endereco.latitude,
        longitude: endereco.longitude
      } : null,
    };
  }

  // Método para buscar e transformar todos os profissionais completos
  async getTransformedCompleteProfessionals(page = 0, filters = {}) {
    try {
      const response = await this.getAllProfessionalsComplete(page, filters);
      const transformedProfessionals = response.content.map(professional => 
        this.transformCompleteProfessionalData(professional)
      );
      
      return {
        content: transformedProfessionals,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        currentPage: response.currentPage,
        size: response.size,
        hasNext: response.hasNext,
        hasPrevious: response.hasPrevious
      };
    } catch (error) {
      console.error('Erro ao buscar e transformar profissionais completos:', error);
      throw error;
    }
  }
}

export default new ProfessionalService(); 