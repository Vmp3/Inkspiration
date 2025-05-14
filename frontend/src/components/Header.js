import React, { useState, useEffect } from 'react';
import { 
  View, 
  Text, 
  TouchableOpacity, 
  StyleSheet, 
  StatusBar, 
  Dimensions, 
  Platform,
  Animated,
  TouchableWithoutFeedback 
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { MaterialIcons } from '@expo/vector-icons';

const Header = () => {
  const navigation = useNavigation();
  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  const [screenHeight, setScreenHeight] = useState(Dimensions.get('window').height);
  const [menuOpen, setMenuOpen] = useState(false);
  
  // Valor para animação do menu offcanvas
  const slideAnim = new Animated.Value(-300); // Começa fora da tela
  
  // Detectar tamanho da tela para responsividade
  const updateLayout = () => {
    const { width, height } = Dimensions.get('window');
    setScreenWidth(width);
    setScreenHeight(height);
  };
  
  const isMobile = screenWidth < 768;
  
  useEffect(() => {
    updateLayout();
    // Listener para mudanças no tamanho da tela
    const dimensionsHandler = Dimensions.addEventListener('change', updateLayout);
    
    return () => {
      // Cleanup listener
      if (dimensionsHandler?.remove) {
        dimensionsHandler.remove();
      }
    };
  }, []);
  
  // Animar abertura/fechamento do menu
  useEffect(() => {
    Animated.timing(slideAnim, {
      toValue: menuOpen ? 0 : -300,
      duration: 300,
      useNativeDriver: true
    }).start();
  }, [menuOpen]);
  
  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  // Get dynamic styles for overlay and menu
  const dynamicOverlayStyles = {
    height: Platform.OS === 'web' ? '100vh' : screenHeight,
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    zIndex: 9998, // Very high z-index but below the menu
  };

  const dynamicMenuStyles = {
    height: Platform.OS === 'web' ? '100vh' : screenHeight,
    position: 'absolute',
    top: 0,
    left: 0,
    width: 280,
    backgroundColor: '#fff',
    zIndex: 9999, // Highest z-index for components (only Toast should be higher)
    elevation: 24, // Higher Android elevation
    shadowColor: '#000',
    shadowOffset: { width: 2, height: 0 },
    shadowOpacity: 0.25,
    shadowRadius: 8,
  };

  return (
    <View style={styles.headerContainer}>
      <StatusBar backgroundColor="white" barStyle="dark-content" />
      
      {/* Overlay para fechar o menu quando clicar fora */}
      {menuOpen && (
        <TouchableWithoutFeedback onPress={() => setMenuOpen(false)}>
          <View style={dynamicOverlayStyles} />
        </TouchableWithoutFeedback>
      )}
      
      {/* Menu Offcanvas */}
      <Animated.View 
        style={[
          dynamicMenuStyles,
          { transform: [{ translateX: slideAnim }] }
        ]}
      >
        <View style={styles.menuHeader}>
          <Text style={styles.menuTitle}>Menu</Text>
          <TouchableOpacity onPress={() => setMenuOpen(false)}>
            <MaterialIcons name="close" size={24} color="#111" />
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
            <MaterialIcons name="home" size={20} color="#666" style={styles.menuIcon} />
            <Text style={styles.menuText}>Início</Text>
          </TouchableOpacity>
          
          <TouchableOpacity 
            style={styles.menuItem}
            onPress={() => {
              navigation.navigate('Explore');
              setMenuOpen(false);
            }}
          >
            <MaterialIcons name="search" size={20} color="#666" style={styles.menuIcon} />
            <Text style={styles.menuText}>Explorar</Text>
          </TouchableOpacity>
          
          <TouchableOpacity 
            style={styles.menuItem}
            onPress={() => {
              navigation.navigate('About');
              setMenuOpen(false);
            }}
          >
            <MaterialIcons name="info" size={20} color="#666" style={styles.menuIcon} />
            <Text style={styles.menuText}>Sobre</Text>
          </TouchableOpacity>
          
          <View style={styles.menuDivider} />
          
          <TouchableOpacity 
            style={styles.menuItem}
            onPress={() => {
              navigation.navigate('Login');
              setMenuOpen(false);
            }}
          >
            <MaterialIcons name="login" size={20} color="#666" style={styles.menuIcon} />
            <Text style={styles.menuText}>Entrar</Text>
          </TouchableOpacity>
          
          <TouchableOpacity 
            style={styles.registerButton}
            onPress={() => {
              navigation.navigate('Register');
              setMenuOpen(false);
            }}
          >
            <Text style={styles.registerText}>Registrar</Text>
          </TouchableOpacity>
        </View>
      </Animated.View>
      
      {/* Header principal */}
      <View style={styles.header}>
        <View style={styles.container}>
          <View style={styles.leftSection}>
            {/* Menu Hambúrguer (visível apenas em dispositivos móveis) */}
            {isMobile && (
              <TouchableOpacity 
                style={styles.menuButton}
                onPress={toggleMenu}
              >
                <MaterialIcons name="menu" size={24} color="#111" />
              </TouchableOpacity>
            )}
            
            {/* Logo à esquerda */}
            <TouchableOpacity 
              style={styles.logoContainer}
              onPress={() => navigation.navigate('Home')}
            >
              <Text style={styles.logoText}>Inkspiration</Text>
            </TouchableOpacity>
          </View>
          
          {/* Navegação no centro (oculta em dispositivos móveis) */}
          {!isMobile && (
            <View style={styles.navContainer}>
              <TouchableOpacity 
                style={styles.navItem}
                onPress={() => navigation.navigate('Home')}
              >
                <Text style={styles.navText}>Início</Text>
              </TouchableOpacity>
              <TouchableOpacity 
                style={styles.navItem}
                onPress={() => navigation.navigate('Explore')}
              >
                <Text style={styles.navText}>Explorar</Text>
              </TouchableOpacity>
              <TouchableOpacity 
                style={styles.navItem}
                onPress={() => navigation.navigate('About')}
              >
                <Text style={styles.navText}>Sobre</Text>
              </TouchableOpacity>
            </View>
          )}

          {/* Botões à direita (sempre visíveis) */}
          <View style={styles.authContainer}>
            {isMobile ? (
              // Em mobile, não mostrar botões de autenticação
              <View />
            ) : (
              // Em desktop, mostrar ambos os botões
              <>
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
              </>
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
    borderBottomColor: '#f0f0f0',
    width: '100%',
  },
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    maxWidth: 1200,
    width: '100%',
    height: 60,
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
    flexDirection: 'row',
    justifyContent: 'center',
    flex: 1,
  },
  navItem: {
    marginHorizontal: 16,
    paddingVertical: 8,
  },
  navText: {
    fontSize: 16,
    color: '#333',
  },
  authContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  loginButton: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    marginRight: 8,
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
  
  // Offcanvas menu styles (static styles only - dynamic styles applied directly)
  menuHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  menuTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111',
  },
  menuContent: {
    padding: 16,
  },
  menuItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
  },
  menuIcon: {
    marginRight: 12,
  },
  menuText: {
    fontSize: 16,
    color: '#333',
  },
  menuDivider: {
    height: 1,
    backgroundColor: '#f0f0f0',
    marginVertical: 16,
  },
});

export default Header;