import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Header from '../components/Header';
import Footer from '../components/Footer';

const MainScreen = () => {
  return (
    <SafeAreaView style={styles.container}>
      <Header />
      <View style={styles.pageWrapper}>
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <View style={styles.content}>
            <Text style={styles.title}>Bem-vindo!</Text>
            <Text style={styles.subtitle}>Você está logado com sucesso.</Text>
          </View>
          
          {/* Espaço para empurrar o footer para o final */}
          <View style={styles.footerSpacer} />
          
          <Footer />
        </ScrollView>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  pageWrapper: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    flexDirection: 'column',
  },
  content: {
    flex: 0,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
    minHeight: 300,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#111827',
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#6b7280',
    textAlign: 'center',
    maxWidth: 600,
  },
  footerSpacer: {
    flex: 1,
    minHeight: 20,
  }
});

export default MainScreen; 