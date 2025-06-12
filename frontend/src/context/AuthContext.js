import React, { createContext, useState, useEffect, useContext, useCallback } from 'react';
import AuthService from '../services/AuthService';
import useTokenMonitor from '../hooks/useTokenMonitor';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);

  // Callback para quando o token for inválido
  const handleTokenInvalid = useCallback(async () => {
    console.log('Token inválido detectado, fazendo logout automático');
    setIsAuthenticated(false);
    setUserData(null);
    try {
      await AuthService.logout();
    } catch (error) {
      console.error('Erro ao fazer logout automático:', error);
    }
  }, []);

  // Usar o hook de monitoramento de token
  useTokenMonitor(isAuthenticated, handleTokenInvalid);

  const checkAuthStatus = useCallback(async () => {
    try {
      setLoading(true);
      const token = await AuthService.getToken();
      
      if (!token) {
        setIsAuthenticated(false);
        setLoading(false);
        return;
      }
      
      const isAuth = await AuthService.isAuthenticated();
      setIsAuthenticated(isAuth);
      
      if (isAuth) {
        const userInfo = await AuthService.getUserData();
        if (userInfo) {
          setUserData(userInfo);
        } else {
          console.error('Usuário autenticado, mas não foi possível obter os dados');
        }
      }
    } catch (error) {
      console.error('Erro ao verificar autenticação:', error);
      setIsAuthenticated(false);
    } finally {
      setLoading(false);
    }
  }, []);

  // Verificar autenticação ao montar o componente
  useEffect(() => {
    checkAuthStatus();
  }, [checkAuthStatus]);

  // Verificar periodicamente a integridade do token (a cada 30 segundos)
  useEffect(() => {
    if (!isAuthenticated) return;
    
    const interval = setInterval(() => {
      const verifyTokenIntegrity = async () => {
        try {
          // Usar o método isAuthenticated que já faz todas as verificações
          const isStillAuth = await AuthService.isAuthenticated();
          if (!isStillAuth) {
            console.log('Token inválido detectado na verificação periódica, fazendo logout');
            setIsAuthenticated(false);
            setUserData(null);
          }
        } catch (error) {
          console.error('Erro na verificação periódica do token:', error);
          setIsAuthenticated(false);
          setUserData(null);
        }
      };
      
      verifyTokenIntegrity();
    }, 30000); // 30 segundos
    
    return () => clearInterval(interval);
  }, [isAuthenticated]);

  const login = async (cpf, senha) => {
    try {
      setLoading(true);
      const result = await AuthService.login(cpf, senha);
      
      // Verificar se o token está realmente disponível
      const token = await AuthService.getToken();
      if (!token) {
        console.error('Token não foi armazenado corretamente após login');
        return { 
          success: false, 
          error: 'Falha ao armazenar token. Por favor, tente novamente.'
        };
      }
      
      setIsAuthenticated(true);
      
      const userInfo = await AuthService.getUserData();
      if (userInfo) {
        setUserData(userInfo);
      } else {
        console.error('Não foi possível obter os dados do usuário após o login');
      }
      
      return { success: true };
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      return { 
        success: false, 
        error: error.message || 'Falha ao fazer login. Verifique suas credenciais.'
      };
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      setLoading(true);
      await AuthService.logout();
      
      setIsAuthenticated(false);
      setUserData(null);
    } catch (error) {
      console.error('Erro ao fazer logout:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateUserData = async () => {
    if (isAuthenticated) {
      try {
        setLoading(true);
        const userInfo = await AuthService.getUserData();
        
        if (userInfo) {
          // Verificar se a role mudou
          if (userData && userData.role !== userInfo.role) {
            // Usar apenas o método de reautenticação para atualizar o token
            try {
              const tokenUpdated = await AuthService.reautenticar(userInfo.idUsuario);
              if (tokenUpdated) {
                // Obter dados novamente após atualizar o token
                const refreshedUserInfo = await AuthService.getUserData();
                if (refreshedUserInfo) {
                  setUserData(refreshedUserInfo);
                }
              } else {
                // Forçar logout em caso de falha
                await logout();
                return;
              }
            } catch (tokenError) {
              console.error('Erro ao atualizar token após mudança de role:', tokenError);
              await logout();
              return;
            }
          } else {
            setUserData(userInfo);
          }
        }
      } catch (error) {
        console.error('Erro ao atualizar dados do usuário:', error);
      } finally {
        setLoading(false);
      }
    }
  };

  const value = {
    isAuthenticated,
    userData,
    loading,
    login,
    logout,
    updateUserData
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  
  return context;
};

export default AuthContext; 