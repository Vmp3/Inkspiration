import React from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, Image, ScrollView } from 'react-native';
import { Feather } from '@expo/vector-icons';

const PortfolioForm = ({ 
  biography, 
  setBiography, 
  portfolioImages, 
  profileImage, 
  handleAddPortfolioImage,
  handleRemovePortfolioImage,
  pickImage
}) => {
  return (
    <View style={styles.tabContent}>
      <View style={styles.formGroup}>
        <Text style={styles.label}>Biografia</Text>
        <TextInput
          style={styles.biographyInput}
          placeholder="Conte sobre sua experiência, estilo e trajetória como tatuador"
          multiline={true}
          numberOfLines={6}
          value={biography}
          onChangeText={setBiography}
        />
      </View>
      
      <View style={styles.formGroup}>
        <View style={styles.portfolioHeader}>
          <Text style={styles.label}>Portfólio de Trabalhos</Text>
          <TouchableOpacity 
            style={styles.addButton}
            onPress={handleAddPortfolioImage}
          >
            <Feather name="plus" size={16} color="#000" style={styles.addButtonIcon} />
            <Text style={styles.addButtonText}>Adicionar Trabalho</Text>
          </TouchableOpacity>
        </View>
        
        <Text style={styles.portfolioHelpText}>
          Adicione fotos dos seus melhores trabalhos. Clique nos quadrados ou no botão acima para selecionar imagens.
        </Text>
        
        <View style={styles.portfolioGrid}>
          {portfolioImages.filter(image => image !== null).map((image, index) => (
            <View key={index} style={styles.portfolioItem}>
              <TouchableOpacity
                style={styles.portfolioImageContainer}
                onPress={() => pickImage('portfolio', index)}
              >
                <Image
                  source={{ uri: image.uri }}
                  style={styles.portfolioImage}
                  resizeMode="cover"
                />
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.removeImageButton}
                onPress={() => handleRemovePortfolioImage(index)}
              >
                <Feather name="trash-2" size={18} color="#ff4444" />
              </TouchableOpacity>
            </View>
          ))}
        </View>
      </View>
      
      <View style={styles.formGroup}>
        <Text style={styles.label}>Foto de Perfil</Text>
        <TouchableOpacity 
          style={styles.profileImageContainer}
          onPress={() => pickImage('profile')}
        >
          {profileImage ? (
            <Image 
              source={{ uri: profileImage.uri }}
              style={styles.profileImage}
              resizeMode="cover"
            />
          ) : (
            <View style={styles.profileImagePlaceholder}>
              <Feather name="upload" size={24} color="#666" />
              <Text style={styles.profileImageText}>
                Arraste e solte uma imagem aqui, ou clique para selecionar
              </Text>
              <Text style={styles.profileImageSubtext}>
                Recomendado: formato quadrado, máximo 5MB
              </Text>
            </View>
          )}
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  tabContent: {
    padding: 16,
    position: 'relative',
  },
  formGroup: {
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  biographyInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    padding: 12,
    height: 120,
    textAlignVertical: 'top',
  },
  portfolioHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  addButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#000',
    borderRadius: 4,
    paddingHorizontal: 10,
    paddingVertical: 6,
  },
  addButtonIcon: {
    marginRight: 5,
  },
  addButtonText: {
    color: '#000',
    fontWeight: '500',
    fontSize: 14,
  },
  portfolioGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -8,
  },
  portfolioItem: {
    width: '50%',
    padding: 8,
  },
  portfolioImageContainer: {
    aspectRatio: 1,
    backgroundColor: '#f1f1f1',
    borderRadius: 4,
    overflow: 'hidden',
  },
  portfolioImage: {
    width: '100%',
    height: '100%',
  },
  profileImageContainer: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderStyle: 'dashed',
    borderRadius: 8,
    overflow: 'hidden',
  },
  profileImage: {
    width: '100%',
    height: 200,
  },
  profileImagePlaceholder: {
    height: 200,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  profileImageText: {
    textAlign: 'center',
    marginVertical: 8,
    color: '#666',
    fontSize: 14,
  },
  profileImageSubtext: {
    textAlign: 'center',
    color: '#999',
    fontSize: 12,
    marginBottom: 16,
  },
  removeImageButton: {
    position: 'absolute',
    top: 12,
    right: 12,
    backgroundColor: 'rgba(255,255,255,0.9)',
    borderRadius: 15,
    width: 30,
    height: 30,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 2,
    elevation: 2,
    zIndex: 1,
  },
  portfolioHelpText: {
    fontSize: 14,
    color: '#666',
    marginBottom: 16,
    fontStyle: 'italic',
  },
});

export default PortfolioForm;