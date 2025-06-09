import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const API_URL = 'http://localhost:8080';

const TOKEN_KEY = 'jwtToken';

class AuthService {
  constructor() {
    this.api = axios.create({
      baseURL: API_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  async login(cpf, senha) {
    try {
      // Limpar qualquer token anterior
      await this.logout();
      
      const response = await this.api.post('/auth/login', { cpf, senha });
      const token = response.data;

      if (!token) {
        throw new Error('Servidor retornou um token vazio');
      }
      
      // Verificar se o token é válido
      try {
        const tokenData = this.parseJwt(token);
        if (!tokenData) {
          throw new Error('Token inválido retornado pelo servidor');
        }
      } catch (tokenError) {
        console.error('Erro ao validar token recebido:', tokenError);
        throw new Error('Erro ao validar token recebido do servidor');
      }
      
      // Armazenar o token e verificar se foi armazenado corretamente
      await this.setToken(token);
      
      // Verificar se o token foi realmente armazenado
      const storedToken = await this.getToken();
      if (!storedToken) {
        console.error('Falha ao armazenar token após login');
        throw new Error('Falha ao armazenar token de autenticação');
      }
      
      return { token };
    } catch (error) {
      console.error('Erro no login:', error);
      throw error;
    }
  }

  async logout() {
    try {
      // Remover do AsyncStorage
      await AsyncStorage.removeItem(TOKEN_KEY);
      
      // Remover de cookie (web)
      if (typeof document !== 'undefined') {
        document.cookie = `${TOKEN_KEY}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
      }
    } catch (error) {
      console.error('Erro ao fazer logout:', error);
    }
  }

  async setToken(token) {
    try {
      // Salvar no AsyncStorage (para mobile)
      await AsyncStorage.setItem(TOKEN_KEY, token);
      
      // Salvar em cookie (para web)
      if (typeof document !== 'undefined') {
        const expirationDate = new Date();
        expirationDate.setDate(expirationDate.getDate() + 30); // Cookie expira em 30 dias
        document.cookie = `${TOKEN_KEY}=${token}; expires=${expirationDate.toUTCString()}; path=/; SameSite=Strict`;
      }
    } catch (error) {
      console.error('Erro ao armazenar token:', error);
      throw error;
    }
  }

  async getToken() {
    try {
      // Tentar obter do AsyncStorage (mobile)
      const tokenFromStorage = await AsyncStorage.getItem(TOKEN_KEY);
      if (tokenFromStorage) {
        return tokenFromStorage;
      }
      
      // Tentar obter de cookie (web)
      if (typeof document !== 'undefined') {
        const cookies = document.cookie.split(';');
        for (const cookie of cookies) {
          const [name, value] = cookie.trim().split('=');
          if (name === TOKEN_KEY) {
            return value;
          }
        }
      }
      
      return null;
    } catch (error) {
      console.error('Erro ao recuperar token:', error);
      return null;
    }
  }

  parseJwt(token) {
    try {
      // Verificar se o token existe e tem o formato básico correto
      if (!token || typeof token !== 'string') {
        console.error('Token inválido: não é uma string válida');
        return null;
      }
      
      const parts = token.split('.');
      if (parts.length !== 3) {
        console.error('Token inválido: não possui 3 partes separadas por ponto');
        return null;
      }
      
      const base64Url = parts[1];
      if (!base64Url) {
        console.error('Token inválido: payload vazio');
        return null;
      }
      
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      
      const parsedPayload = JSON.parse(jsonPayload);
      
      // Verificar se o payload tem os campos essenciais
      if (!parsedPayload || typeof parsedPayload !== 'object') {
        console.error('Token inválido: payload não é um objeto válido');
        return null;
      }
      
      return parsedPayload;
    } catch (error) {
      console.error('Erro ao decodificar token:', error);
      return null;
    }
  }

  async isAuthenticated() {
    try {
      const token = await this.getToken();
      
      if (!token) {
        return false;
      }
      
      // Verificar se o token tem formato válido
      const tokenData = this.parseJwt(token);
      if (!tokenData) {
        console.log('Token com formato inválido detectado, fazendo logout automático');
        await this.logout();
        return false;
      }
      
      // Verificar se o token está expirado
      if (!tokenData.exp || tokenData.exp * 1000 <= Date.now()) {
        console.log('Token expirado, fazendo logout automático');
        await this.logout();
        return false;
      }
      
      // Verificar se o token está revogado ou inválido no servidor
      if (tokenData.userId) {
        try {
          const validationResult = await this.validateToken(token, tokenData.userId);
          if (!validationResult.valid) {
            console.log('Token inválido no servidor:', validationResult.reason);
            await this.logout();
            return false;
          }
          
          // Se há um novo token, atualizá-lo
          if (validationResult.newToken) {
            console.log('Recebido novo token do servidor');
            await this.setToken(validationResult.newToken);
          }
        } catch (error) {
          console.error('Erro ao validar token com servidor:', error);
        }
      }
      
      return true;
    } catch (error) {
      console.error('Erro ao verificar autenticação:', error);
      // Em caso de erro crítico, fazer logout por segurança
      await this.logout();
      return false;
    }
  }

  async getUserData() {
    try {
      const token = await this.getToken();
      
      if (!token) {
        console.warn('Token não encontrado ao buscar dados do usuário');
        return null;
      }
      
      const tokenData = this.parseJwt(token);
      
      if (!tokenData) {
        console.error('Token com formato inválido detectado ao buscar dados do usuário, fazendo logout');
        await this.logout();
        return null;
      }
      
      // Verificar role do token
      const scope = tokenData.scope || '';
      let role = 'ROLE_USER';
      
      if (scope.includes('ROLE_ADMIN')) {
        role = 'ROLE_ADMIN';
      } else if (scope.includes('ROLE_DELETED')) {
        role = 'ROLE_DELETED';
      } else if (scope.includes('ROLE_PROF')) {
        role = 'ROLE_PROF';
      } else if (scope.includes('ROLE_USER')) {
        role = 'ROLE_USER';
      }
      
      // Extrair userId do token
      const userId = tokenData.userId;
      
      if (!userId) {
        console.error('ID do usuário não encontrado no token');
        return { nome: 'Usuário', role: role, idUsuario: null };
      }
      
      // Buscar dados completos do usuário da API usando axios
      try {
        const headers = await this.getAuthHeaders();
        const response = await this.api.get(`/usuario/detalhes/${userId}`, { headers });
        const userData = response.data;
        
        // Se o usuário for um profissional, buscar dados profissionais adicionais
        let dadosProfissionais = {};
        if (role === 'ROLE_PROF') {
          dadosProfissionais = await this.getDadosProfissionais(userId);
        }
        
        // Garantir que os dados estejam formatados corretamente para o EditProfileScreen
        const endereco = userData.endereco || {
          cep: '',
          rua: '',
          numero: '',
          complemento: '',
          bairro: '',
          cidade: '',
          estado: ''
        };
        
        return {
          idUsuario: userData.idUsuario,
          nome: userData.nome,
          cpf: userData.cpf ? userData.cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') : '',
          email: userData.email || '',
          telefone: userData.telefone || '',
          dataNascimento: userData.dataNascimento || '',
          role: role,
          cpfMascarado: tokenData.sub ? `***.***.***-${tokenData.sub.substring(tokenData.sub.length - 2)}` : null,
          imagemPerfil: userData.imagemPerfil || null,
          endereco: endereco,
          ...dadosProfissionais
        };
      } catch (error) {
        console.error('Erro ao buscar dados do usuário da API:', error);
        throw error;
      }
    } catch (error) {
      console.error('Erro ao obter dados do usuário:', error);
      return null;
    }
  }

  async getDadosProfissionais(userId) {
    try {
      const headers = await this.getAuthHeaders();
      const response = await this.api.get(`/profissional/usuario/${userId}`, { headers });
      const dadosProfissionais = response.data;

      return {
        especialidades: dadosProfissionais.especialidades || [],
        bio: dadosProfissionais.bio || '',
        experiencia: dadosProfissionais.experiencia || '',
        redesSociais: dadosProfissionais.redesSociais || {
          instagram: '',
          tiktok: '',
          facebook: '',
          twitter: '',
          website: ''
        }
      };
    } catch (error) {
      console.error('Erro ao buscar dados profissionais:', error);
      return {
        especialidades: [],
        bio: '',
        experiencia: '',
        redesSociais: {
          instagram: '',
          tiktok: '',
          facebook: '',
          twitter: '',
          website: ''
        }
      };
    }
  }

  async getAuthHeaders() {
    const token = await this.getToken();
    return {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : '',
    };
  }

  async validateTokenBeforeRequest() {
    try {
      // Usar o método isAuthenticated que já faz todas as validações necessárias
      const isAuth = await this.isAuthenticated();
      
      if (!isAuth) {
        console.warn('Token inválido ou usuário não autenticado');
        return false;
      }

      return true;
    } catch (error) {
      console.error('Erro ao validar token antes da requisição:', error);
      await this.logout();
      return false;
    }
  }

  // Método para fazer fetch com validação de token
  async fetchWithAuth(url, options = {}) {
    const isTokenValid = await this.validateTokenBeforeRequest();
    
    if (!isTokenValid) {
      throw new Error('Token inválido ou expirado');
    }
    
    const headers = await this.getAuthHeaders();
    const mergedOptions = {
      ...options,
      headers: {
        ...headers,
        ...(options.headers || {})
      }
    };
    
    const response = await fetch(url, mergedOptions);
    
    // Verificar se a resposta contém um novo token no header
    const newToken = response.headers.get('New-Auth-Token');
    if (newToken) {
      console.log('Recebido novo token do servidor na resposta');
      await this.setToken(newToken);
    }
    
    return response;
  }

  async validateToken(token, userId = null) {
    try {
      // Primeiro, extrair o userId do token se não foi fornecido
      if (!userId) {
        const tokenData = this.parseJwt(token);
        if (!tokenData || !tokenData.userId) {
          return { valid: false, reason: 'Token inválido ou userId não encontrado' };
        }
        userId = tokenData.userId;
      }
      
      const response = await this.api.post(`/usuario/${userId}/validate-token`, 
        { token },
        {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        }
      );

      const result = response.data;
      return {
        valid: result.valid === true,
        reason: result.message || '',
        newToken: result.newToken || null
      };
    } catch (error) {
      console.error('Erro ao validar token:', error);
      return { valid: false, reason: 'Erro de conexão com servidor' };
    }
  }

  async reautenticar(userId) {
    try {
      // Limpar tokens anteriores
      await this.logout();
      
      const response = await this.api.post(`/auth/reauth/${userId}`);
      const token = response.data;
      
      // Salvar o novo token
      await this.setToken(token);
      
      // Parse o token para verificar a role
      const tokenData = this.parseJwt(token);
      if (!tokenData) {
        throw new Error('Token inválido recebido na reautenticação');
      }
      
      return tokenData.scope.includes('ROLE_PROF');
    } catch (error) {
      console.error('Erro na reautenticação:', error);
      return false;
    }
  }

  async refreshToken(userId) {
    // Delegando para o método reautenticar
    return await this.reautenticar(userId);
  }

  // Método para detectar se o token foi modificado externamente
  async detectTokenTampering() {
    try {
      const token = await this.getToken();
      if (!token) {
        return false; // Sem token, não há tampering
      }

      // Verificar se o token tem formato válido
      const tokenData = this.parseJwt(token);
      if (!tokenData) {
        console.warn('Token com formato inválido detectado');
        return true; // Token foi modificado/corrompido
      }

      // Verificar se o token está expirado
      if (!tokenData.exp || tokenData.exp * 1000 <= Date.now()) {
        console.warn('Token expirado detectado');
        return true;
      }

      // Verificar com o servidor se o token é válido usando o novo método
      if (tokenData.userId) {
        const validationResult = await this.validateToken(token, tokenData.userId);
        if (!validationResult.valid) {
          console.warn('Token não corresponde ao armazenado no servidor:', validationResult.reason);
          return true;
        }
      }

      return false; // Token está íntegro
    } catch (error) {
      console.error('Erro ao detectar tampering do token:', error);
      return true; // Em caso de erro, assumir que há problema
    }
  }
}

export default new AuthService(); 