import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import Input from '../ui/Input';
import Checkbox from '../ui/Checkbox';

const ProfessionalForm = ({ 
  formData, 
  handleChange,
}) => {
  const allSpecialties = [
    "Tradicional",
    "Realista",
    "Aquarela",
    "Blackwork",
    "Geométrico",
    "Japonês",
    "Neo-Tradicional",
    "Minimalista",
    "Old School",
    "Fineline",
  ];

  // Opções de experiência
  const experienceOptions = [
    { value: '<1', label: 'Menos de 1 ano' },
    { value: '1-3', label: '1-3 anos' },
    { value: '3-5', label: '3-5 anos' },
    { value: '5-10', label: '5-10 anos' },
    { value: '>10', label: 'Mais de 10 anos' },
  ];

  const toggleSpecialty = (specialty) => {
    handleChange('especialidades', specialty);
  };

  const updateSocialNetwork = (network, value) => {
    handleChange('redesSociais', { [network]: value });
  };

  return (
    <View style={styles.tabContent}>
      <View style={styles.formSection}>
        <Text style={styles.sectionTitle}>Informações Profissionais</Text>
        
        <View style={styles.formFullWidth}>
          <Text style={styles.formLabel}>Biografia</Text>
          <Input
            placeholder="Conte sobre sua experiência, estilo e trajetória como tatuador"
            value={formData.bio}
            onChangeText={(text) => handleChange('bio', text)}
            multiline={true}
            numberOfLines={4}
            style={[styles.inputField, styles.textAreaInput]}
          />
        </View>

        <View style={styles.formFullWidth}>
          <Text style={styles.formLabel}>Anos de Experiência</Text>
          <View style={styles.experienceOptions}>
            {experienceOptions.map((option) => (
              <View key={option.value} style={styles.experienceOption}>
                <Checkbox
                  checked={formData.experiencia === option.value}
                  onPress={() => handleChange('experiencia', option.value)}
                />
                <Text style={styles.experienceLabel}>{option.label}</Text>
              </View>
            ))}
          </View>
        </View>
      </View>

      <View style={styles.formSection}>
        <Text style={styles.sectionTitle}>Especialidades</Text>
        <Text style={styles.sectionSubtitle}>Selecione os estilos que você domina</Text>
        
        <View style={styles.specialtiesContainer}>
          {allSpecialties.map((specialty) => (
            <View key={specialty} style={styles.specialtyItem}>
              <Checkbox
                checked={formData.especialidades.includes(specialty)}
                onPress={() => toggleSpecialty(specialty)}
              />
              <Text style={styles.specialtyLabel}>{specialty}</Text>
            </View>
          ))}
        </View>
      </View>

      <View style={styles.formSection}>
        <Text style={styles.sectionTitle}>Redes Sociais</Text>
        <Text style={styles.sectionSubtitle}>Conecte-se com potenciais clientes</Text>
        
        <View style={styles.formFullWidth}>
          <Text style={styles.formLabel}>Instagram</Text>
          <Input
            placeholder="@seu_instagram"
            value={formData.redesSociais.instagram}
            onChangeText={(text) => updateSocialNetwork('instagram', text)}
            style={styles.inputField}
          />
        </View>
        
        <View style={styles.formFullWidth}>
          <Text style={styles.formLabel}>TikTok</Text>
          <Input
            placeholder="@seu_tiktok"
            value={formData.redesSociais.tiktok}
            onChangeText={(text) => updateSocialNetwork('tiktok', text)}
            style={styles.inputField}
          />
        </View>
        
        <View style={styles.formFullWidth}>
          <Text style={styles.formLabel}>Facebook</Text>
          <Input
            placeholder="facebook.com/seuperfil"
            value={formData.redesSociais.facebook}
            onChangeText={(text) => updateSocialNetwork('facebook', text)}
            style={styles.inputField}
          />
        </View>
        
        <View style={styles.formFullWidth}>
          <Text style={styles.formLabel}>Twitter</Text>
          <Input
            placeholder="@seu_twitter"
            value={formData.redesSociais.twitter}
            onChangeText={(text) => updateSocialNetwork('twitter', text)}
            style={styles.inputField}
          />
        </View>
        
        <View style={styles.formFullWidth}>
          <Text style={styles.formLabel}>Website</Text>
          <Input
            placeholder="seusite.com"
            value={formData.redesSociais.website}
            onChangeText={(text) => updateSocialNetwork('website', text)}
            style={styles.inputField}
          />
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  tabContent: {
    flex: 1,
  },
  formSection: {
    marginBottom: 30,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#111',
    marginBottom: 8,
  },
  sectionSubtitle: {
    fontSize: 14,
    color: '#666',
    marginBottom: 16,
  },
  formFullWidth: {
    marginBottom: 20,
  },
  formLabel: {
    marginBottom: 8,
    fontSize: 14,
    fontWeight: '500',
    color: '#111',
  },
  inputField: {
    borderWidth: 1,
    borderColor: '#e2e2e2',
    borderRadius: 4,
    paddingHorizontal: 12,
    fontSize: 14,
    backgroundColor: '#fff',
  },
  textAreaInput: {
    height: 120,
    textAlignVertical: 'top',
    paddingTop: 12,
  },
  specialtiesContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  specialtyItem: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '50%',
    marginBottom: 12,
  },
  specialtyLabel: {
    marginLeft: 8,
    fontSize: 14,
    color: '#333',
  },
  experienceOptions: {
    marginTop: 8,
  },
  experienceOption: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  experienceLabel: {
    marginLeft: 8,
    fontSize: 14,
    color: '#333',
  },
});

export default ProfessionalForm; 