import React from 'react';
import { View, Text, StyleSheet, ScrollView, SafeAreaView } from 'react-native';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';

const ProfileScreen = () => {
  const { userData } = useAuth();

  return (
    <SafeAreaView style={styles.container}>
      <Header />
      <ScrollView contentContainerStyle={styles.scrollContainer}>
        <View style={styles.profileContainer}>
          <Text style={styles.title}>Perfil do Usuário</Text>
          
          {userData ? (
            <View style={styles.infoContainer}>
              <View style={styles.infoRow}>
                <Text style={styles.label}>Nome:</Text>
                <Text style={styles.value}>{userData.nome || 'Não informado'}</Text>
              </View>
              
              <View style={styles.infoRow}>
                <Text style={styles.label}>E-mail:</Text>
                <Text style={styles.value}>{userData.email || 'Não informado'}</Text>
              </View>
              
              <View style={styles.infoRow}>
                <Text style={styles.label}>CPF:</Text>
                <Text style={styles.value}>{userData.cpf || 'Não informado'}</Text>
              </View>
              
              <View style={styles.infoRow}>
                <Text style={styles.label}>Telefone:</Text>
                <Text style={styles.value}>{userData.telefone || 'Não informado'}</Text>
              </View>
              
              {userData.endereco && (
                <>
                  <View style={styles.addressHeader}>
                    <Text style={styles.addressTitle}>Endereço</Text>
                  </View>
                  
                  <View style={styles.infoRow}>
                    <Text style={styles.label}>Rua:</Text>
                    <Text style={styles.value}>
                      {userData.endereco.rua} {userData.endereco.numero && `, ${userData.endereco.numero}`}
                    </Text>
                  </View>
                  
                  {userData.endereco.complemento && (
                    <View style={styles.infoRow}>
                      <Text style={styles.label}>Complemento:</Text>
                      <Text style={styles.value}>{userData.endereco.complemento}</Text>
                    </View>
                  )}
                  
                  <View style={styles.infoRow}>
                    <Text style={styles.label}>Bairro:</Text>
                    <Text style={styles.value}>{userData.endereco.bairro || 'Não informado'}</Text>
                  </View>
                  
                  <View style={styles.infoRow}>
                    <Text style={styles.label}>Cidade/UF:</Text>
                    <Text style={styles.value}>
                      {userData.endereco.cidade || 'Não informada'} 
                      {userData.endereco.estado && `/${userData.endereco.estado}`}
                    </Text>
                  </View>
                  
                  <View style={styles.infoRow}>
                    <Text style={styles.label}>CEP:</Text>
                    <Text style={styles.value}>{userData.endereco.cep || 'Não informado'}</Text>
                  </View>
                </>
              )}
            </View>
          ) : (
            <View style={styles.emptyState}>
              <Text style={styles.emptyText}>
                Não foi possível carregar os dados do usuário. Por favor, tente novamente mais tarde.
              </Text>
            </View>
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollContainer: {
    padding: 16,
    paddingBottom: 40,
  },
  profileContainer: {
    maxWidth: 800,
    width: '100%',
    alignSelf: 'center',
    marginTop: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 24,
    color: '#111',
  },
  infoContainer: {
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  infoRow: {
    flexDirection: 'row',
    marginBottom: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    paddingBottom: 12,
  },
  label: {
    width: 100,
    fontWeight: 'bold',
    color: '#555',
  },
  value: {
    flex: 1,
    color: '#333',
  },
  addressHeader: {
    marginTop: 12,
    marginBottom: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
    paddingBottom: 8,
  },
  addressTitle: {
    fontWeight: 'bold',
    fontSize: 16,
    color: '#333',
  },
  emptyState: {
    padding: 24,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
  },
  emptyText: {
    textAlign: 'center',
    color: '#666',
    lineHeight: 22,
  },
});

export default ProfileScreen; 