import React, { useRef, useEffect, useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, Platform, ActivityIndicator } from 'react-native';
import { Feather } from '@expo/vector-icons';
import ApiService from '../../services/ApiService';

const BasicInfoForm = ({ 
  experience, 
  setExperience, 
  specialties, 
  handleSpecialtyChange, 
  socialMedia, 
  handleSocialMediaChange, 
  handleNextTab, 
  experienceDropdownOpen, 
  setExperienceDropdownOpen,
  tiposServico,
  setTiposServico,
  tipoServicoSelecionados,
  handleTipoServicoChange
}) => {
  const dropdownRef = useRef(null);
  const [isLoading, setIsLoading] = useState(false);
  
  const experienceOptions = [
    'Menos de 1 ano',
    '1-3 anos',
    '3-5 anos',
    '5-10 anos',
    'Mais de 10 anos'
  ];
  
  useEffect(() => {
    const fetchTiposServico = async () => {
      if (!tiposServico || tiposServico.length === 0) {
        setIsLoading(true);
        try {
          const response = await ApiService.get('/tipos-servico');
          if (response && Array.isArray(response)) {
            setTiposServico(response);
          }
        } catch (error) {
          console.error('Erro ao carregar tipos de serviço:', error);
        } finally {
          setIsLoading(false);
        }
      }
    };
    
    fetchTiposServico();
  }, []);
  
  useEffect(() => {
    if (Platform.OS === 'web') {
      const removeZIndexFromViews = () => {
        const cssViewElements = document.querySelectorAll('.css-view-175oi2r');
        cssViewElements.forEach(element => {
          if (element.style.zIndex === '0') {
            element.style.zIndex = 'auto';
          }
        });

        if (dropdownRef.current) {
          const parentElements = [];
          let currentParent = dropdownRef.current.parentElement;
          
          while (currentParent && currentParent !== document.body) {
            parentElements.push(currentParent);
            currentParent = currentParent.parentElement;
          }
          
          parentElements.forEach(el => {
            const currentZIndex = window.getComputedStyle(el).zIndex;
            if (currentZIndex === 'auto' || currentZIndex === '0') {
              el.style.zIndex = 'auto';
            }
          });
        }
      };
      
      removeZIndexFromViews();
      
      setTimeout(removeZIndexFromViews, 100);
      
      if (experienceDropdownOpen) {
        removeZIndexFromViews();
      }
    }
  }, [experienceDropdownOpen]);
  
  const handleExperienceSelect = (option) => {
    setExperience(option);
    setExperienceDropdownOpen(false);
  };

  const formatarNomeTipo = (nome) => {
    if (nome === 'SESSAO') return 'Sessão';
    if (nome.startsWith('TATUAGEM_')) {
      const tipo = nome.replace('TATUAGEM_', '').toLowerCase();
      return 'Tatuagem ' + tipo.charAt(0).toUpperCase() + tipo.slice(1);
    }
    return nome.charAt(0).toUpperCase() + nome.slice(1).toLowerCase();
  };

  return (
    <View style={styles.tabContent}>
      <View style={styles.formGroup}>
        <Text style={styles.label}>Anos de Experiência</Text>
        <View style={styles.dropdownContainer} ref={dropdownRef}>
          <TouchableOpacity 
            style={styles.selectField}
            onPress={() => setExperienceDropdownOpen(!experienceDropdownOpen)}
          >
            <Text>{experience}</Text>
            <Feather name={experienceDropdownOpen ? "chevron-up" : "chevron-down"} size={20} color="#666" />
          </TouchableOpacity>
          
          {experienceDropdownOpen && (
            <View style={styles.dropdownList}>
              {experienceOptions.map((option, index) => (
                <TouchableOpacity
                  key={index}
                  style={[
                    styles.dropdownItem,
                    option === experience && styles.dropdownItemSelected
                  ]}
                  onPress={() => handleExperienceSelect(option)}
                >
                  {option === experience && (
                    <View style={{width: 20}}>
                      <Feather name="check" size={16} color="#000" />
                    </View>
                  )}
                  {option !== experience && <View style={{width: 20}} />}
                  <Text 
                    style={option === experience ? styles.dropdownItemTextSelected : styles.dropdownItemText}
                  >
                    {option}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          )}
        </View>
      </View>
      
      <View style={styles.formGroup}>
        <Text style={styles.label}>Tipos de Serviço</Text>
        {isLoading ? (
          <ActivityIndicator size="small" color="#000" />
        ) : (
          <View style={styles.checkboxGrid}>
            {tiposServico && tiposServico.map((tipo, index) => (
              <View key={index} style={styles.checkboxTipoServico}>
                <TouchableOpacity 
                  style={[
                    styles.checkbox, 
                    tipoServicoSelecionados[tipo.nome] && styles.checkboxChecked
                  ]}
                  onPress={() => handleTipoServicoChange(tipo.nome)}
                >
                  {tipoServicoSelecionados[tipo.nome] && <Feather name="check" size={16} color="#fff" />}
                </TouchableOpacity>
                <View style={styles.tipoServicoTextContainer}>
                  <Text style={styles.checkboxLabel}>
                    {formatarNomeTipo(tipo.nome)} | Duração: {tipo.duracaoHoras} {tipo.duracaoHoras === 1 ? 'hora' : 'horas'}
                  </Text>
                </View>
              </View>
            ))}
          </View>
        )}
      </View>
      
      <View style={styles.formGroup}>
        <Text style={styles.label}>Especialidades</Text>
        <View style={styles.checkboxGrid}>
          {Object.entries(specialties).map(([name, checked], index) => (
            <View key={index} style={styles.checkboxItem}>
              <TouchableOpacity 
                style={[styles.checkbox, checked && styles.checkboxChecked]}
                onPress={() => handleSpecialtyChange(name)}
              >
                {checked && <Feather name="check" size={16} color="#fff" />}
              </TouchableOpacity>
              <Text style={styles.checkboxLabel}>{name}</Text>
            </View>
          ))}
        </View>
      </View>
      
      <View style={styles.formGroup}>
        <Text style={styles.label}>Redes Sociais</Text>
        
        <View style={styles.socialInputRow}>
          <Feather name="instagram" size={20} color="#666" />
          <TextInput
            style={styles.socialInput}
            placeholder="@seu_instagram"
            value={socialMedia.instagram}
            onChangeText={(text) => handleSocialMediaChange('instagram', text)}
          />
        </View>
        
        <View style={styles.socialInputRow}>
          <Feather name="music" size={20} color="#666" />
          <TextInput
            style={styles.socialInput}
            placeholder="@seu_tiktok"
            value={socialMedia.tiktok}
            onChangeText={(text) => handleSocialMediaChange('tiktok', text)}
          />
        </View>
        
        <View style={styles.socialInputRow}>
          <Feather name="facebook" size={20} color="#666" />
          <TextInput
            style={styles.socialInput}
            placeholder="facebook.com/seuperfil"
            value={socialMedia.facebook}
            onChangeText={(text) => handleSocialMediaChange('facebook', text)}
          />
        </View>
        
        <View style={styles.socialInputRow}>
          <Feather name="twitter" size={20} color="#666" />
          <TextInput
            style={styles.socialInput}
            placeholder="@seu_twitter"
            value={socialMedia.twitter}
            onChangeText={(text) => handleSocialMediaChange('twitter', text)}
          />
        </View>
        
        <View style={styles.socialInputRow}>
          <Feather name="globe" size={20} color="#666" />
          <TextInput
            style={styles.socialInput}
            placeholder="seusite.com"
            value={socialMedia.website}
            onChangeText={(text) => handleSocialMediaChange('website', text)}
          />
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  tabContent: {
    padding: 16,
  },
  formGroup: {
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  dropdownContainer: {
    position: 'relative',
    zIndex: 9999,
  },
  selectField: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    padding: 12,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#fff',
    zIndex: 9999,
  },
  dropdownList: {
    position: 'absolute',
    top: '100%',
    left: 0,
    right: 0,
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    marginTop: 2,
    maxHeight: 300,
    zIndex: 10000,
    elevation: 5,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    ...(Platform.OS === 'web' ? { 
      position: 'absolute', 
      overflow: 'auto',
      width: '100%'
    } : {}),
  },
  dropdownItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  dropdownItemSelected: {
    backgroundColor: '#f5f5f5',
  },
  dropdownItemText: {
    fontSize: 14,
    color: '#333',
  },
  dropdownItemTextSelected: {
    fontSize: 14,
    color: '#000',
    fontWeight: 'bold',
  },
  checkboxGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  checkboxItem: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '33.33%',
    marginBottom: 12,
  },
  checkboxTipoServico: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    width: '100%',
    marginBottom: 16,
    paddingRight: 8,
  },
  checkbox: {
    width: 20,
    height: 20,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 4,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 8,
    marginTop: 2,
  },
  checkboxChecked: {
    backgroundColor: '#000',
    borderColor: '#000',
  },
  checkboxLabel: {
    fontSize: 14,
  },
  checkboxSubLabel: {
    fontSize: 12,
    color: '#666',
    marginTop: 2,
  },
  socialInputRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  socialInput: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 4,
    padding: 10,
    marginLeft: 10,
  },
  tipoServicoTextContainer: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
});

export default BasicInfoForm;