import React, { createContext, useState, useEffect, useContext } from 'react';
import AuthService from '../services/AuthService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuthStatus = async () => {
      try {
        const isAuth = await AuthService.isAuthenticated();
        setIsAuthenticated(isAuth);
        
        if (isAuth) {
          const userInfo = await AuthService.getUserData();
          setUserData(userInfo);
        }
      } catch (error) {
        console.error('Erro ao verificar autenticação:', error);
      } finally {
        setLoading(false);
      }
    };

    checkAuthStatus();
  }, []);

  const login = async (cpf, senha) => {
    try {
      setLoading(true);
      await AuthService.login(cpf, senha);
      
      setIsAuthenticated(true);
      const userInfo = await AuthService.getUserData();
      setUserData(userInfo);
      
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
        const userInfo = await AuthService.getUserData();
        setUserData(userInfo);
      } catch (error) {
        console.error('Erro ao atualizar dados do usuário:', error);
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