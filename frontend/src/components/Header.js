import React, { useState, useEffect, useRef } from 'react';
import { 
  View, 
  Text, 
  TouchableOpacity, 
  StyleSheet, 
  StatusBar, 
  Dimensions, 
  Platform,
  Animated,
  Pressable,
  Image
} from 'react-native';
import { useNavigation, useNavigationState } from '@react-navigation/native';
import { MaterialIcons, Feather } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';
import toastHelper from '../utils/toastHelper';
import textUtils from '../utils/textUtils';
import { headerMessages } from './header/messages';

const Header = () => {
  const navigation = useNavigation();
  const { isAuthenticated, userData, logout } = useAuth();
  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  const [screenHeight, setScreenHeight] = useState(Dimensions.get('window').height);
  const [menuOpen, setMenuOpen] = useState(false);
  const [userDropdownOpen, setUserDropdownOpen] = useState(false);
  
  const dropdownRef = useRef(null);
  const dropdownButtonRef = useRef(null);
  
  const slideAnim = new Animated.Value(-300); 
  
  // Obtém a rota atual usando useNavigationState
  const currentRouteName = useNavigationState(state => {
    if (!state || !state.routes || state.routes.length === 0) {
      return 'Home';
    }
    const currentRoute = state.routes[state.index];
    return currentRoute?.name || 'Home';
  });
  
  const updateLayout = () => {
    const { width, height } = Dimensions.get('window');
    setScreenWidth(width);
    setScreenHeight(height);
  };
  
  const isMobile = screenWidth < 768;
  
  useEffect(() => {
    updateLayout();
    
    const dimensionsHandler = Dimensions.addEventListener('change', updateLayout);
    
    return () => {
      
      if (dimensionsHandler?.remove) {
        dimensionsHandler.remove();
      }
    };
  }, []);
  
  useEffect(() => {
    if (userData) {
      const availableRoles = ['ROLE_USER', 'ROLE_ADMIN', 'ROLE_PROF', 'ROLE_DELETED'];
    }
  }, [userData]);
  
  
  useEffect(() => {
    const handleClickOutside = (event) => {
      
      if (Platform.OS === 'web' && userDropdownOpen) {
        
        const isOutsideDropdown = dropdownRef.current && !dropdownRef.current.contains(event.target);
        const isOutsideButton = dropdownButtonRef.current && !dropdownButtonRef.current.contains(event.target);
        
        if (isOutsideDropdown && isOutsideButton) {
          setUserDropdownOpen(false);
        }
      }
    };

    
    if (Platform.OS === 'web') {
      document.addEventListener('mousedown', handleClickOutside);
    }
    
    
    return () => {
      if (Platform.OS === 'web') {
        document.removeEventListener('mousedown', handleClickOutside);
      }
    };
  }, [userDropdownOpen]);
  
  
  useEffect(() => {
    Animated.timing(slideAnim, {
      toValue: menuOpen ? 0 : -300,
      duration: 300,
      useNativeDriver: false
    }).start();
  }, [menuOpen]);
  
  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };
  
  const toggleUserDropdown = () => {
    setUserDropdownOpen(!userDropdownOpen);
  };
  
  const handleLogout = async () => {
    try {
      await logout();
      setUserDropdownOpen(false);
      toastHelper.showSuccess(headerMessages.success.logoutSuccess);
      navigation.navigate('Home');
    } catch (error) {
      // console.error('Erro ao fazer logout:', error);
      toastHelper.showError(headerMessages.errors.logoutError);
    }
  };

  
  const isActive = (routeName) => {
    const isRouteActive = currentRouteName === routeName;
    return isRouteActive;
  };

  
  const getInitial = (name) => {
    return textUtils.getInitials(name);
  };

  const dynamicOverlayStyles = {
    height: Platform.OS === 'web' ? '100vh' : screenHeight,
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    zIndex: 9998,
  };

  const dynamicMenuStyles = {
    height: Platform.OS === 'web' ? '100vh' : screenHeight,
    position: 'absolute',
    top: 0,
    left: 0,
    width: 280,
    backgroundColor: '#fff',
    zIndex: 9999,
    elevation: 24,
    boxShadow: '2px 0px 8px rgba(0, 0, 0, 0.25)',
  };

  return (
    <View style={styles.headerContainer}>
      <StatusBar backgroundColor="white" barStyle="dark-content" />
      
      {menuOpen && (
        <Pressable onPress={() => setMenuOpen(false)}>
          <View style={dynamicOverlayStyles} />
        </Pressable>
      )}
      
      <Animated.View 
        style={[
          dynamicMenuStyles,
          { transform: [{ translateX: slideAnim }] }
        ]}
      >
        <View style={styles.menuHeader}>
          <Text style={styles.menuTitle}>Menu</Text>
          <TouchableOpacity onPress={() => setMenuOpen(false)}>
            <Feather name="x" size={24} color="#000" />
          </TouchableOpacity>
        </View>
        
        <View style={styles.menuContent}>
          <TouchableOpacity 
            style={styles.menuItem}
            onPress={() => {
              navigation.navigate('Home');
              setMenuOpen(false);
            }}
          >
            <Text style={[styles.menuText, isActive('Home') && styles.activeMenuText]}>Início</Text>
          </TouchableOpacity>
          
          <TouchableOpacity 
            style={styles.menuItem}
            onPress={() => {
              navigation.navigate('Explore');
              setMenuOpen(false);
            }}
          >
            <Text style={[styles.menuText, isActive('Explore') && styles.activeMenuText]}>Explorar</Text>
          </TouchableOpacity>
          
          <TouchableOpacity 
            style={styles.menuItem}
            onPress={() => {
              navigation.navigate('About');
              setMenuOpen(false);
            }}
          >
            <Text style={[styles.menuText, isActive('About') && styles.activeMenuText]}>Sobre</Text>
          </TouchableOpacity>
        </View>
      </Animated.View>
      
      <View style={styles.header}>
        <View style={styles.container}>
          <View style={styles.leftSection}>
            {isMobile && (
              <TouchableOpacity 
                style={styles.menuButton}
                onPress={toggleMenu}
              >
                <Feather name="menu" size={24} color="#000" />
              </TouchableOpacity>
            )}

            <TouchableOpacity 
              style={styles.logoContainer}
              onPress={() => navigation.navigate('Home')}
            >
              <Text style={styles.logoText}>Inkspiration</Text>
            </TouchableOpacity>
          </View>
          
          {!isMobile && (
            <View style={styles.navContainer}>
              <TouchableOpacity 
                style={styles.navItem}
                onPress={() => navigation.navigate('Home')}
              >
                <Text style={[
                  styles.navText, 
                  isActive('Home') ? styles.activeNavText : styles.inactiveNavText
                ]}>Início</Text>
              </TouchableOpacity>
              <TouchableOpacity 
                style={styles.navItem}
                onPress={() => navigation.navigate('Explore')}
              >
                <Text style={[
                  styles.navText, 
                  isActive('Explore') ? styles.activeNavText : styles.inactiveNavText
                ]}>Explorar</Text>
              </TouchableOpacity>
              <TouchableOpacity 
                style={styles.navItem}
                onPress={() => navigation.navigate('About')}
              >
                <Text style={[
                  styles.navText, 
                  isActive('About') ? styles.activeNavText : styles.inactiveNavText
                ]}>Sobre</Text>
              </TouchableOpacity>
            </View>
          )}

          <View style={styles.authContainer}>
            {!isAuthenticated ? (
              <View style={styles.authButtons}>
                <TouchableOpacity 
                  style={styles.loginButton}
                  onPress={() => navigation.navigate('Login')}
                >
                  <Text style={styles.loginText}>Entrar</Text>
                </TouchableOpacity>
                
                <TouchableOpacity 
                  style={styles.registerButton}
                  onPress={() => navigation.navigate('Register')}
                >
                  <Text style={styles.registerText}>Registrar</Text>
                </TouchableOpacity>
              </View>
            ) : (
              <View style={styles.userDropdownContainer}>
                <TouchableOpacity 
                  style={styles.userButton}
                  onPress={toggleUserDropdown}
                  ref={dropdownButtonRef}
                >
                  <View style={styles.avatar}>
                    {userData?.imagemPerfil ? (
                      <Image 
                        source={{ uri: userData.imagemPerfil }} 
                        style={styles.avatarImage} 
                      />
                    ) : (
                      <View style={styles.avatarFallback}>
                        <Text style={styles.avatarText}>
                          {getInitial(userData?.nome)}
                        </Text>
                      </View>
                    )}
                  </View>
                  
                  {!isMobile && (
                    <Text style={styles.userName} numberOfLines={1} ellipsizeMode="tail">
                      {userData?.nome ? textUtils.truncateName(userData.nome.split(' ')[0], 15) : 'Carregando...'}
                    </Text>
                  )}
                </TouchableOpacity>
                
                {userDropdownOpen && (
                  <View 
                    style={styles.userDropdown}
                    ref={dropdownRef}
                  >
                    <View style={styles.dropdownHeader}>
                      <Text style={styles.dropdownLabel}>Minha Conta</Text>
                    </View>
                    
                    <View style={styles.dropdownDivider} />
                    
                    <TouchableOpacity 
                      style={styles.dropdownItem}
                      onPress={() => {
                        navigation.navigate('Profile');
                        setUserDropdownOpen(false);
                      }}
                    >
                      <Feather name="user" size={16} color="#666" style={styles.dropdownIcon} />
                      <Text style={styles.dropdownText}>Perfil</Text>
                    </TouchableOpacity>
                    
                    {userData?.role === 'ROLE_ADMIN' ? (
                      
                      <TouchableOpacity 
                        style={styles.dropdownItem}
                        onPress={() => {
                          navigation.navigate('AdminUsers');
                          setUserDropdownOpen(false);
                        }}
                      >
                        <Feather name="users" size={16} color="#666" style={styles.dropdownIcon} />
                        <Text style={styles.dropdownText}>Gerenciar Usuários</Text>
                      </TouchableOpacity>
                    ) : userData?.role === 'ROLE_PROF' ? (
                      
                      <>
                        <TouchableOpacity 
                          style={styles.dropdownItem}
                          onPress={() => {
                            navigation.navigate('MyAppointments');
                            setUserDropdownOpen(false);
                          }}
                        >
                          <Feather name="calendar" size={16} color="#666" style={styles.dropdownIcon} />
                          <Text style={styles.dropdownText}>Meus agendamentos</Text>
                        </TouchableOpacity>
                        
                        <TouchableOpacity 
                          style={styles.dropdownItem}
                          onPress={() => {
                            navigation.navigate('MyAttendances');
                            setUserDropdownOpen(false);
                          }}
                        >
                          <Feather name="clock" size={16} color="#666" style={styles.dropdownIcon} />
                          <Text style={styles.dropdownText}>Meus atendimentos</Text>
                        </TouchableOpacity>
                      </>
                    ) : userData?.role === 'ROLE_USER' ? (
                      <>
                        <TouchableOpacity 
                          style={styles.dropdownItem}
                          onPress={() => {
                            navigation.navigate('MyAppointments');
                            setUserDropdownOpen(false);
                          }}
                        >
                          <Feather name="calendar" size={16} color="#666" style={styles.dropdownIcon} />
                          <Text style={styles.dropdownText}>Meus agendamentos</Text>
                        </TouchableOpacity>
                        
                        <TouchableOpacity 
                          style={styles.dropdownItem}
                          onPress={() => {
                            navigation.navigate('ProfessionalRegister');
                            setUserDropdownOpen(false);
                          }}
                        >
                          <Feather name="edit-3" size={16} color="#666" style={styles.dropdownIcon} />
                          <Text style={styles.dropdownText}>Tornar-se profissional</Text>
                        </TouchableOpacity>
                      </>
                    ) : null}
                    
                    <View style={styles.dropdownDivider} />
                    
                    <TouchableOpacity 
                      style={styles.dropdownItem}
                      onPress={handleLogout}
                    >
                      <Feather name="log-out" size={16} color="#666" style={styles.dropdownIcon} />
                      <Text style={styles.dropdownText}>Sair</Text>
                    </TouchableOpacity>
                  </View>
                )}
              </View>
            )}
          </View>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  headerContainer: {
    position: 'relative',
    zIndex: 1000,
    width: '100%',
  },
  header: {
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#e5e5e5',
    width: '100%',
  },
  container: {
    position: 'relative',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    maxWidth: 1200,
    width: '100%',
    height: 64, 
    alignSelf: 'center',
  },
  leftSection: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  menuButton: {
    padding: 8,
    marginRight: 8,
  },
  logoContainer: {
    paddingVertical: 4,
  },
  logoText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  navContainer: {
    position: 'absolute',
    left: '50%',
    top: '50%',
    transform: [{ translateX: '-50%' }, { translateY: '-50%' }],
    flexDirection: 'row',
    justifyContent: 'center',
  },
  navItem: {
    marginHorizontal: 16,
    paddingVertical: 8,
  },
  navText: {
    fontSize: 16,
  },
  activeNavText: {
    fontWeight: '500',
    color: '#000',
  },
  inactiveNavText: {
    color: '#666',
  },
  authContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  authButtons: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  loginButton: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    marginRight: 8,
    borderWidth: 1,
    borderColor: '#e5e5e5',
    borderRadius: 4,
  },
  loginText: {
    fontSize: 16,
    color: '#333',
  },
  registerButton: {
    backgroundColor: '#000',
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 4,
  },
  registerText: {
    fontSize: 16,
    color: '#fff',
    fontWeight: '500',
  },
  
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0, 0, 0, 0.4)',
    zIndex: 100,
  },
  offcanvasMenu: {
    position: 'absolute',
    left: 0,
    top: 0,
    bottom: 0,
    width: 280,
    backgroundColor: '#fff',
    zIndex: 101,
    boxShadow: '2px 0px 8px rgba(0, 0, 0, 0.2)',
    elevation: 5,
    height: '100vh',
  },
  menuHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#e5e5e5',
  },
  menuTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#000',
  },
  menuContent: {
    padding: 16,
  },
  menuItem: {
    paddingVertical: 12,
  },
  menuText: {
    fontSize: 18,
    color: '#666',
  },
  activeMenuText: {
    fontWeight: '500',
    color: '#000',
  },

  userDropdownContainer: {
    position: 'relative',
  },
  userButton: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 8,
    borderRadius: 4,
  },
  avatar: {
    width: 32,
    height: 32,
    borderRadius: 16,
    overflow: 'hidden',
    backgroundColor: '#e5e5e5',
    justifyContent: 'center',
    alignItems: 'center',
  },
  avatarImage: {
    width: '100%',
    height: '100%',
  },
  avatarFallback: {
    width: '100%',
    height: '100%',
    backgroundColor: '#e5e5e5',
    justifyContent: 'center',
    alignItems: 'center',
  },
  avatarText: {
    fontSize: 16,
    fontWeight: '500',
    color: '#000',
  },
  userName: {
    marginLeft: 8,
    fontSize: 14,
    color: '#000',
  },
  userDropdown: {
    position: 'absolute',
    top: 45,
    right: 0,
    backgroundColor: '#fff',
    borderRadius: 4,
    boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)',
    elevation: 4,
    width: 220,
    zIndex: 102,
    borderWidth: 1,
    borderColor: '#e5e5e5',
    padding: 4,
  },
  dropdownHeader: {
    paddingHorizontal: 8,
    paddingVertical: 4,
  },
  dropdownLabel: {
    fontSize: 14,
    fontWeight: '500',
    color: '#666',
  },
  dropdownDivider: {
    height: 1,
    backgroundColor: '#e5e5e5',
    marginVertical: 4,
  },
  dropdownItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 8,
    paddingHorizontal: 8,
    borderRadius: 2,
  },
  dropdownIcon: {
    marginRight: 8,
  },
  dropdownText: {
    fontSize: 14,
    color: '#333',
  },
});

export default Header;