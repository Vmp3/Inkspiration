import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  TouchableWithoutFeedback,
  TextInput,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import Toast from 'react-native-toast-message';
import AvaliacaoService from '../services/AvaliacaoService';
import toastHelper from '../utils/toastHelper';
import toastConfig from '../config/toastConfig';

const RatingModal = ({ 
  visible, 
  appointment, 
  onClose, 
  onSuccess
}) => {
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    // Reset form quando modal abre
    if (visible) {
      setRating(0);
      setComment('');
    }
  }, [visible]);

  const handleStarPress = (selectedRating) => {
    setRating(selectedRating);
  };

  const handleSubmit = async () => {
    if (rating === 0) {
      toastHelper.showError('Por favor, selecione uma avaliação');
      return;
    }

    // Validar comentário obrigatório (mínimo 20 caracteres)
    if (!comment.trim()) {
      toastHelper.showError('Por favor, adicione um comentário');
      return;
    }

    if (comment.trim().length < 20) {
      toastHelper.showError('O comentário deve ter no mínimo 20 caracteres');
      return;
    }

    try {
      setIsSubmitting(true);

      const avaliacaoData = {
        rating: rating,
        descricao: comment.trim(),
        idAgendamento: appointment.idAgendamento
      };

      // Criar nova avaliação
      await AvaliacaoService.criarAvaliacao(avaliacaoData);
      
      onSuccess?.();
      onClose();
      
      // Mostrar toast após fechar o modal para garantir visibilidade
      setTimeout(() => {
        toastHelper.showSuccess('Avaliação enviada com sucesso!');
      }, 100);
    } catch (error) {
      console.error('Erro ao enviar avaliação:', error);
      
      let errorMessage = 'Erro ao enviar avaliação';
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }
      
      toastHelper.showError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderStars = () => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <TouchableOpacity
          key={i}
          onPress={() => handleStarPress(i)}
          style={styles.starButton}
        >
          <MaterialIcons
            name={i <= rating ? 'star' : 'star-border'}
            size={32}
            color={i <= rating ? '#FFD700' : '#CBD5E1'}
          />
        </TouchableOpacity>
      );
    }
    return stars;
  };

  if (!appointment) return null;

  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="fade"
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={onClose}>
        <View style={styles.modalBackdrop}>
          <TouchableWithoutFeedback onPress={(e) => e.stopPropagation()}>
            <View style={styles.modalContainer}>
              <View style={styles.modalHeader}>
                <Text style={styles.modalTitle}>
                  Avaliar Artista
                </Text>
                <Text style={styles.modalSubtitle}>
                  Compartilhe sua experiência com {appointment.nomeProfissional}
                </Text>
              </View>

              <ScrollView style={styles.modalContent}>
                <View style={styles.ratingSection}>
                  <Text style={styles.sectionTitle}>Sua nota</Text>
                  <View style={styles.starsContainer}>
                    {renderStars()}
                  </View>
                  {rating > 0 && (
                    <Text style={styles.ratingText}>
                      {rating} de 5 estrelas
                    </Text>
                  )}
                </View>

                <View style={styles.commentSection}>
                  <Text style={styles.sectionTitle}>
                    Seu comentário
                  </Text>
                  <TextInput
                    style={[
                      styles.commentInput,
                      comment.length > 0 && comment.length < 20 && styles.commentInputError
                    ]}
                    placeholder="Conte como foi sua experiência... (mínimo 20 caracteres)"
                    multiline
                    numberOfLines={4}
                    value={comment}
                    onChangeText={setComment}
                    maxLength={500}
                    textAlignVertical="top"
                  />
                  <View style={styles.characterCountContainer}>
                    <Text style={[
                      styles.characterCount,
                      comment.length < 20 && styles.characterCountError
                    ]}>
                      {comment.length}/500 caracteres
                    </Text>
                    {comment.length < 20 && (
                      <Text style={styles.minCharactersText}>
                        Mínimo: 20 caracteres
                      </Text>
                    )}
                  </View>
                </View>
              </ScrollView>

              <View style={styles.buttonContainer}>
                <TouchableOpacity
                  style={styles.cancelButton}
                  onPress={onClose}
                  disabled={isSubmitting}
                >
                  <Text style={styles.cancelButtonText}>Cancelar</Text>
                </TouchableOpacity>

                <TouchableOpacity
                  style={[
                    styles.submitButton, 
                    (isSubmitting || rating === 0 || comment.trim().length < 20) && styles.submitButtonDisabled
                  ]}
                  onPress={handleSubmit}
                  disabled={isSubmitting || rating === 0 || comment.trim().length < 20}
                >
                  {isSubmitting ? (
                    <ActivityIndicator size="small" color="#fff" />
                  ) : (
                    <Text style={styles.submitButtonText}>
                      Enviar Avaliação
                    </Text>
                  )}
                </TouchableOpacity>
              </View>
            </View>
          </TouchableWithoutFeedback>
        </View>
      </TouchableWithoutFeedback>
      
      <Toast 
        config={toastConfig} 
        style={{ 
          zIndex: 999999, 
          elevation: 999999,
          position: 'absolute',
          bottom: 50,
          left: 0,
          right: 0
        }} 
      />
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalBackdrop: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContainer: {
    width: '90%',
    maxWidth: 400,
    backgroundColor: '#fff',
    borderRadius: 12,
    overflow: 'hidden',
    maxHeight: '80%',
  },
  modalHeader: {
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#E2E8F0',
    backgroundColor: '#F8FAFC',
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#111',
    textAlign: 'center',
    marginBottom: 4,
  },
  modalSubtitle: {
    fontSize: 14,
    color: '#64748B',
    textAlign: 'center',
  },
  modalContent: {
    padding: 20,
  },
  ratingSection: {
    alignItems: 'center',
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111',
    marginBottom: 12,
  },
  starsContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 8,
  },
  starButton: {
    padding: 4,
    marginHorizontal: 2,
  },
  ratingText: {
    fontSize: 14,
    color: '#64748B',
    marginTop: 8,
  },
  commentSection: {
    marginBottom: 16,
  },
  commentInput: {
    borderWidth: 1,
    borderColor: '#E2E8F0',
    borderRadius: 8,
    padding: 12,
    fontSize: 14,
    color: '#111',
    minHeight: 100,
    backgroundColor: '#F8FAFC',
  },
  commentInputError: {
    borderColor: '#E11D48',
    backgroundColor: '#FEF2F2',
  },
  characterCountContainer: {
    marginTop: 4,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  characterCount: {
    fontSize: 12,
    color: '#64748B',
  },
  characterCountError: {
    color: '#E11D48',
  },
  minCharactersText: {
    fontSize: 12,
    color: '#E11D48',
    fontWeight: '500',
  },
  buttonContainer: {
    flexDirection: 'row',
    borderTopWidth: 1,
    borderTopColor: '#E2E8F0',
    padding: 16,
    gap: 12,
  },
  cancelButton: {
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#F1F5F9',
    alignItems: 'center',
    justifyContent: 'center',
  },
  cancelButtonText: {
    fontSize: 16,
    fontWeight: '500',
    color: '#64748B',
  },
  submitButton: {
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#111',
    alignItems: 'center',
    justifyContent: 'center',
  },
  submitButtonDisabled: {
    opacity: 0.6,
  },
  submitButtonText: {
    fontSize: 16,
    fontWeight: '500',
    color: '#fff',
  },
});

export default RatingModal; 