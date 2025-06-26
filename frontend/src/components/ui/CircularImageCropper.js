import React, { useState } from 'react';
import { View, Modal, TouchableOpacity, Text, StyleSheet, Dimensions } from 'react-native';
import { Feather } from '@expo/vector-icons';
import ImageWithAlt from './ImageWithAlt';

const { width: screenWidth, height: screenHeight } = Dimensions.get('window');

const CircularImageCropper = ({ visible, imageUri, onCrop, onCancel }) => {
  const [cropPosition, setCropPosition] = useState({ x: 0, y: 0 });
  const [imageSize, setImageSize] = useState({ width: 0, height: 0 });
  const cropSize = 200; // Tamanho do círculo de crop

  const handleImageLayout = (event) => {
    const { width, height } = event.nativeEvent.layout;
    setImageSize({ width, height });
    // Centralizar o crop inicialmente
    setCropPosition({
      x: (width - cropSize) / 2,
      y: (height - cropSize) / 2
    });
  };

  const handlePanGesture = (event) => {
    const { locationX, locationY } = event.nativeEvent;
    
    // Limitar o movimento do crop dentro dos limites da imagem
    const newX = Math.max(0, Math.min(locationX - cropSize / 2, imageSize.width - cropSize));
    const newY = Math.max(0, Math.min(locationY - cropSize / 2, imageSize.height - cropSize));
    
    setCropPosition({ x: newX, y: newY });
  };

  const handleCrop = () => {
    // Calcular as coordenadas relativas para o crop
    const cropData = {
      x: cropPosition.x / imageSize.width,
      y: cropPosition.y / imageSize.height,
      size: cropSize / Math.min(imageSize.width, imageSize.height)
    };
    
    onCrop(cropData);
  };

  if (!imageUri) return null;

  return (
    <Modal visible={visible} transparent animationType="fade">
      <View style={styles.modalContainer}>
        <View style={styles.header}>
          <Text style={styles.title}>Ajustar Foto de Perfil</Text>
          <TouchableOpacity onPress={onCancel} style={styles.cancelButton}>
            <Feather name="x" size={24} color="#FFFFFF" />
          </TouchableOpacity>
        </View>

        <View style={styles.imageContainer}>
          <ImageWithAlt
            source={{ uri: imageUri }}
            alt="Imagem para recorte de foto de perfil"
            style={styles.image}
            resizeMode="contain"
            onLayout={handleImageLayout}
            accessibilityLabel="Imagem para recorte de foto de perfil"
          />
          
          {/* Overlay escuro */}
          <View style={styles.overlay} />
          
          {/* Círculo de crop */}
          <TouchableOpacity
            style={[
              styles.cropCircle,
              {
                left: cropPosition.x,
                top: cropPosition.y,
                width: cropSize,
                height: cropSize,
                borderRadius: cropSize / 2
              }
            ]}
            onPress={handlePanGesture}
            activeOpacity={1}
          >
            <View style={styles.cropBorder} />
          </TouchableOpacity>
        </View>

        <View style={styles.footer}>
          <Text style={styles.instructions}>
            Toque e arraste para posicionar sua foto
          </Text>
          
          <View style={styles.buttonContainer}>
            <TouchableOpacity onPress={onCancel} style={styles.cancelButtonFooter}>
              <Text style={styles.cancelButtonText}>Cancelar</Text>
            </TouchableOpacity>
            
            <TouchableOpacity onPress={handleCrop} style={styles.cropButton}>
              <Text style={styles.cropButtonText}>Usar Foto</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalContainer: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.9)',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingTop: 50,
    paddingBottom: 20,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    color: '#FFFFFF',
  },
  cancelButton: {
    padding: 8,
  },
  imageContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    position: 'relative',
  },
  image: {
    width: screenWidth - 40,
    height: screenWidth - 40,
    maxHeight: screenHeight * 0.6,
  },
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  cropCircle: {
    position: 'absolute',
    borderWidth: 3,
    borderColor: '#FFFFFF',
    backgroundColor: 'transparent',
  },
  cropBorder: {
    flex: 1,
    borderRadius: 100,
    borderWidth: 1,
    borderColor: '#FFFFFF',
    borderStyle: 'dashed',
  },
  footer: {
    paddingHorizontal: 20,
    paddingBottom: 40,
  },
  instructions: {
    fontSize: 16,
    color: '#FFFFFF',
    textAlign: 'center',
    marginBottom: 20,
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  cancelButtonFooter: {
    flex: 1,
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: '#FFFFFF',
    borderRadius: 8,
    paddingVertical: 12,
    marginRight: 10,
  },
  cancelButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '500',
    textAlign: 'center',
  },
  cropButton: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    paddingVertical: 12,
    marginLeft: 10,
  },
  cropButtonText: {
    color: '#000000',
    fontSize: 16,
    fontWeight: '600',
    textAlign: 'center',
  },
});

export default CircularImageCropper; 