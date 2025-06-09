import { useState } from 'react';
import useFormValidation from '../utils/formValidation';

const useTabNavigation = (isArtist, formData, professionalFormData) => {
  const [activeTab, setActiveTab] = useState('personal');
  const validation = useFormValidation();

  const getTabs = () => {
    const tabs = [
      { id: 'personal', label: 'Dados Pessoais' },
      { id: 'address', label: 'Endereço' }
    ];

    if (isArtist) {
      tabs.push(
        { id: 'basic-info', label: 'Profissional' },
        { id: 'hours', label: 'Horário' },
        { id: 'portfolio', label: 'Portfólio' }
      );
    }

    tabs.push({ id: 'security', label: 'Segurança' });

    return tabs;
  };

  const handleNextTab = () => {
    let isValid = false;

    switch (activeTab) {
      case 'personal':
        isValid = validation.validatePersonalTab(formData);
        if (isValid) setActiveTab('address');
        break;
      case 'address':
        isValid = validation.validateAddressTab(formData);
        if (isValid) {
          if (isArtist) {
            setActiveTab('basic-info');
          } else {
            setActiveTab('security');
          }
        }
        break;
      case 'basic-info':
        isValid = validation.validateBasicInfoTab(professionalFormData);
        if (isValid) setActiveTab('hours');
        break;
      case 'hours':
        isValid = validation.validateWorkHoursTab(professionalFormData);
        if (isValid) setActiveTab('portfolio');
        break;
      case 'portfolio':
        isValid = validation.validatePortfolioTab(professionalFormData);
        if (isValid) setActiveTab('security');
        break;
      case 'professional':
        isValid = validation.validateProfessionalTab(formData, isArtist);
        if (isValid) setActiveTab('security');
        break;
    }
  };

  const handlePrevTab = () => {
    switch (activeTab) {
      case 'security':
        if (isArtist) {
          setActiveTab('portfolio');
        } else {
          setActiveTab('address');
        }
        break;
      case 'portfolio':
        setActiveTab('hours');
        break;
      case 'hours':
        setActiveTab('basic-info');
        break;
      case 'basic-info':
        setActiveTab('address');
        break;
      case 'professional':
        setActiveTab('address');
        break;
      case 'address':
        setActiveTab('personal');
        break;
    }
  };

  const validateCurrentTab = () => {
    switch (activeTab) {
      case 'personal':
        return validation.validatePersonalTab(formData);
      case 'address':
        return validation.validateAddressTab(formData);
      case 'security':
        return validation.validateSecurityTab(formData);
      case 'professional':
        return validation.validateProfessionalTab(formData, isArtist);
      case 'basic-info':
        return validation.validateBasicInfoTab(professionalFormData);
      case 'hours':
        return validation.validateWorkHoursTab(professionalFormData);
      case 'portfolio':
        return validation.validatePortfolioTab(professionalFormData);
      default:
        return true;
    }
  };

  return {
    activeTab,
    setActiveTab,
    getTabs,
    handleNextTab,
    handlePrevTab,
    validateCurrentTab
  };
};

export default useTabNavigation; 