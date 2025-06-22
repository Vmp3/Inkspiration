import React, { useState, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  Dimensions,
  RefreshControl,

} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../context/AuthContext';
import UserService from '../services/UserService';
import PortfolioService from '../services/PortfolioService';
import toastHelper from '../utils/toastHelper';
import textUtils from '../utils/textUtils';
import { adminMessages } from '../components/admin/messages';

import Input from '../components/ui/Input';
import Button from '../components/ui/Button';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Avatar from '../components/ui/Avatar';
import Modal from '../components/ui/Modal';
import Pagination from '../components/common/Pagination';

const AdminUsersScreen = () => {
  const navigation = useNavigation();
  const { userData } = useAuth();
  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [isConfirmModalVisible, setIsConfirmModalVisible] = useState(false);
  const [modalAction, setModalAction] = useState(null);
  
  // Estados para paginação
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize] = useState(10);

  const isMobile = screenWidth < 768;

  useEffect(() => {
    if (!userData || userData.role !== 'ROLE_ADMIN') {
      navigation.navigate('Home');
      return;
    }

    loadUsers();
  }, [userData, navigation]);

  useEffect(() => {
    loadUsers();
  }, [currentPage]);

  // Debounce para busca
  useEffect(() => {
    const delayedSearch = setTimeout(() => {
      if (currentPage === 0) {
        loadUsers();
      } else {
        setCurrentPage(0); // Resetar para primeira página ao buscar
      }
    }, 500);

    return () => clearTimeout(delayedSearch);
  }, [searchTerm]);

  useEffect(() => {
    const updateLayout = () => {
      setScreenWidth(Dimensions.get('window').width);
    };

    const dimensionsHandler = Dimensions.addEventListener('change', updateLayout);
    
    return () => {
      if (dimensionsHandler?.remove) {
        dimensionsHandler.remove();
      }
    };
  }, []);

  const loadUsers = async () => {
    try {
      setIsLoading(true);
      const response = await UserService.getAllUsers(currentPage, pageSize, searchTerm);
      
      if (response.usuarios) {
        setUsers(response.usuarios);
        setTotalPages(response.totalPages || 1);
        setTotalElements(response.totalElements || 0);
      } else {
        // Fallback para resposta no formato antigo
        setUsers(response);
        setTotalPages(1);
        setTotalElements(response.length);
      }
    } catch (error) {
      console.error('Erro ao carregar usuários:', error);
      toastHelper.showError(adminMessages.errors.loadUsers);
      setUsers([]);
      setTotalPages(1);
      setTotalElements(0);
    } finally {
      setIsLoading(false);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await loadUsers();
    setRefreshing(false);
  };

  const handlePageChange = useCallback((newPage) => {
    setCurrentPage(newPage);
  }, []);

  const toggleUserStatus = (user) => {
    setSelectedUser(user);
    setModalAction('toggle');
    setIsConfirmModalVisible(true);
  };

  const deletePortfolio = (user) => {
    setSelectedUser(user);
    setModalAction('deletePortfolio');
    setIsConfirmModalVisible(true);
  };

  const confirmAction = async () => {
    if (!selectedUser) return;

    try {
      if (modalAction === 'toggle') {
        if (selectedUser.role === 'ROLE_DELETED') {
          await UserService.reactivateUser(selectedUser.idUsuario);
          toastHelper.showSuccess(adminMessages.success.userActivated(selectedUser.nome));
        } else {
          await UserService.deactivateUser(selectedUser.idUsuario);
          toastHelper.showSuccess(adminMessages.success.userDeactivated(selectedUser.nome));
        }
      } else if (modalAction === 'deletePortfolio') {
        try {
          await PortfolioService.deletePortfolio(selectedUser.idUsuario);
          toastHelper.showSuccess(adminMessages.success.portfolioDeleted(selectedUser.nome));
        } catch (error) {
          if (error.message.includes('404')) {
            toastHelper.showInfo(adminMessages.info.noPortfolio(selectedUser.nome));
          } else {
            throw error;
          }
        }
      }
      
      await loadUsers();
    } catch (error) {
      console.error('Erro na ação:', error);
      toastHelper.showError(adminMessages.errors.userAction);
    } finally {
      setIsConfirmModalVisible(false);
      setSelectedUser(null);
      setModalAction(null);
    }
  };

  const getBadgeVariant = (role) => {
    switch (role) {
      case 'ROLE_ADMIN':
        return 'admin';
      case 'ROLE_PROF':
        return 'professional';
      case 'ROLE_DELETED':
        return 'inactive';
      default:
        return 'client';
    }
  };

  const getBadgeText = (role) => {
    switch (role) {
      case 'ROLE_ADMIN':
        return 'Admin';
      case 'ROLE_PROF':
        return 'Profissional';
      case 'ROLE_DELETED':
        return 'Desativado';
      default:
        return 'Cliente';
    }
  };

  const getInitials = (name) => {
    return textUtils.getInitials(name);
  };

  const renderUserItem = (user) => {
    const isInactive = user.role === 'ROLE_DELETED';
    const isAdmin = user.role === 'ROLE_ADMIN';
    const isProfessional = user.role === 'ROLE_PROF';

    return (
      <Card key={user.idUsuario} style={[styles.userCard, isInactive && styles.inactiveCard]}>
        <View style={styles.userContent}>
          <View style={styles.userInfo}>
            <Avatar
              source={user.imagemPerfil}
              fallback={getInitials(user.nome)}
              size={48}
              style={styles.avatar}
            />
            <View style={styles.userDetails}>
              <View style={styles.userHeader}>
                <Text style={styles.userName} numberOfLines={2} ellipsizeMode="tail">
                  {textUtils.truncateName(user.nome, 30)}
                </Text>
                <View style={styles.badges}>
                  <Badge variant={getBadgeVariant(user.role)}>
                    {getBadgeText(user.role)}
                  </Badge>
                </View>
              </View>
              <Text style={styles.userCpf}>CPF: {user.cpf}</Text>
              <Text style={styles.userEmail} numberOfLines={1} ellipsizeMode="tail">
                {textUtils.truncateText(user.email, 35)}
              </Text>
            </View>
          </View>

          {!isAdmin && (
            <View style={[styles.actions, isMobile && styles.mobileActions]}>
              <Button
                variant="secondary"
                size="sm"
                label={isInactive ? "Ativar" : "Desativar"}
                onPress={() => toggleUserStatus(user)}
                style={[
                  styles.actionButton,
                  isInactive ? styles.activateButton : styles.deactivateButton
                ]}
                labelStyle={[
                  isInactive ? styles.activateText : styles.deactivateText
                ]}
              />
              {isProfessional && (
                <Button
                  variant="secondary"
                  size="sm"
                  label="Excluir Portfólio"
                  onPress={() => deletePortfolio(user)}
                  style={[styles.actionButton, styles.deletePortfolioButton]}
                  labelStyle={styles.deletePortfolioText}
                />
              )}
            </View>
          )}
        </View>
      </Card>
    );
  };

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
      >
        <View style={[styles.content, isMobile && styles.mobileContent]}>
          <Text style={styles.title}>Gerenciamento de Usuários</Text>

          {/* Barra de busca */}
          <View style={styles.searchContainer}>
            <View style={styles.searchInputContainer}>
              <Input
                placeholder="Buscar usuários por nome"
                value={searchTerm}
                onChangeText={setSearchTerm}
                icon="search"
                returnKeyType="search"
              />
            </View>
          </View>

          {/* Informações da busca */}
          {totalElements > 0 && (
            <View style={styles.searchInfo}>
              <Text style={styles.searchInfoText}>
                {searchTerm ? 
                  `${totalElements} usuário${totalElements !== 1 ? 's' : ''} encontrado${totalElements !== 1 ? 's' : ''} para "${searchTerm}"` :
                  `${totalElements} usuário${totalElements !== 1 ? 's' : ''} total${totalElements !== 1 ? 'is' : ''}`
                }
              </Text>
            </View>
          )}

          {/* Lista de usuários */}
          {isLoading ? (
            <View style={styles.loadingContainer}>
              <Text style={styles.loadingText}>Carregando usuários...</Text>
            </View>
          ) : users.length === 0 ? (
            <View style={styles.emptyContainer}>
              <Text style={styles.emptyTitle}>
                {searchTerm ? 'Nenhum usuário encontrado' : 'Nenhum usuário cadastrado'}
              </Text>
              <Text style={styles.emptyDescription}>
                {searchTerm ? 
                  'Tente usar outros termos de busca ou verifique a ortografia.' :
                  'Ainda não há usuários cadastrados no sistema.'
                }
              </Text>
            </View>
          ) : (
            <View style={styles.usersList}>
              {users.map(renderUserItem)}
            </View>
          )}

          {/* Paginação */}
          {totalPages > 1 && (
            <Pagination
              currentPage={currentPage}
              setCurrentPage={handlePageChange}
              totalPages={totalPages}
            />
          )}
        </View>
      </ScrollView>

      {/* Modal de confirmação */}
      <Modal
        visible={isConfirmModalVisible}
        onClose={() => setIsConfirmModalVisible(false)}
        title={
          modalAction === 'toggle'
            ? selectedUser?.role === 'ROLE_DELETED'
              ? 'Ativar usuário'
              : 'Desativar usuário'
            : 'Excluir portfólio'
        }
        description={
          modalAction === 'toggle'
            ? selectedUser?.role === 'ROLE_DELETED'
              ? `Tem certeza que deseja ativar o usuário ${selectedUser?.nome}? O usuário poderá acessar o sistema novamente.`
              : `Tem certeza que deseja desativar o usuário ${selectedUser?.nome}? O usuário não poderá mais acessar o sistema.`
            : `Tem certeza que deseja excluir o portfólio do usuário ${selectedUser?.nome}? Esta ação não pode ser desfeita.`
        }
        confirmText={
          modalAction === 'toggle'
            ? selectedUser?.role === 'ROLE_DELETED'
              ? 'Ativar'
              : 'Desativar'
            : 'Excluir Portfólio'
        }
        confirmVariant={
          modalAction === 'deletePortfolio' ? 'primary' : 'primary'
        }
        onConfirm={confirmAction}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
  },
  content: {
    maxWidth: 1200,
    alignSelf: 'center',
    width: '100%',
    padding: 32,
  },
  mobileContent: {
    padding: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 24,
  },
  searchContainer: {
    marginBottom: 16,
  },
  searchInputContainer: {
    flex: 1,
  },
  searchInfo: {
    marginBottom: 24,
  },
  searchInfoText: {
    fontSize: 14,
    color: '#6B7280',
    fontStyle: 'italic',
  },
  loadingContainer: {
    padding: 32,
    alignItems: 'center',
  },
  loadingText: {
    fontSize: 16,
    color: '#6B7280',
  },
  emptyContainer: {
    padding: 48,
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
    borderRadius: 8,
  },
  emptyTitle: {
    fontSize: 20,
    fontWeight: '500',
    color: '#111827',
    marginBottom: 8,
  },
  emptyDescription: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
  },
  usersList: {
    gap: 16,
  },
  userCard: {
    marginBottom: 0,
  },
  inactiveCard: {
    opacity: 0.7,
  },
  userContent: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  userInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  avatar: {
    marginRight: 16,
  },
  userDetails: {
    flex: 1,
  },
  userHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 4,
  },
  userName: {
    fontSize: 16,
    fontWeight: '500',
    color: '#111827',
    marginRight: 8,
  },
  badges: {
    flexDirection: 'row',
    gap: 8,
  },
  userCpf: {
    fontSize: 14,
    color: '#6B7280',
    marginBottom: 2,
  },
  userEmail: {
    fontSize: 14,
    color: '#6B7280',
  },
  actions: {
    flexDirection: 'row',
    gap: 8,
    alignItems: 'center',
  },
  mobileActions: {
    flexDirection: 'column',
    alignItems: 'stretch',
    minWidth: 100,
    gap: 8,
  },
  actionButton: {
    minWidth: 80,
    height: 32,
  },
  deactivateButton: {
    borderColor: '#EF4444',
  },
  deactivateText: {
    color: '#EF4444',
  },
  activateButton: {
    borderColor: '#10B981',
  },
  activateText: {
    color: '#10B981',
  },
  deletePortfolioButton: {
    borderColor: '#F59E0B',
  },
  deletePortfolioText: {
    color: '#F59E0B',
  },
});

export default AdminUsersScreen; 