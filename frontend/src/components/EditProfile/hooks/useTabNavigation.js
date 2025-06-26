import { useState } from 'react';
import useFormValidation from '../utils/formValidation';
import toastHelper from '../../../utils/toastHelper';
import { editProfileMessages } from '../messages';

const useTabNavigation = (isArtist, formData, professionalFormData, addressValidationErrors = {}) => {
  const [activeTab, setActiveTab] = useState('personal');
  const validation = useFormValidation();
  
  // Destructure address validation errors
  const { cepError, estadoError, cidadeError, bairroError, enderecoValidationError } = addressValidationErrors;

  const getAvailableTabs = () => {
    const availableTabs = ['personal'];
    
    if (validation.isPersonalTabValid(formData)) {
      availableTabs.push('address');
    }
    
    if (validation.isPersonalTabValid(formData) && validation.isAddressTabValid(formData, cepError, estadoError, cidadeError, bairroError, enderecoValidationError)) {
      if (isArtist) {
        availableTabs.push('basic-info');
        
        if (validation.isBasicInfoTabValid(professionalFormData)) {
          availableTabs.push('hours');
          
          if (validation.isWorkHoursTabValid(professionalFormData)) {
            availableTabs.push('portfolio');
            
            if (validation.isPortfolioTabValid(professionalFormData)) {
              availableTabs.push('security');
            }
          }
        }
      } else {
        availableTabs.push('security');
      }
    }
    
    return availableTabs;
  };

  const handleTabPress = (tabId) => {
    const availableTabs = getAvailableTabs();
    
    if (availableTabs.includes(tabId)) {
      setActiveTab(tabId);
    } else {
      if (tabId === 'address' && !validation.isPersonalTabValid(formData)) {
        toastHelper.showWarning(editProfileMessages.warnings.completePersonalDataFirst);
      } else if (tabId === 'basic-info' && (!validation.isPersonalTabValid(formData) || !validation.isAddressTabValid(formData, cepError, estadoError, cidadeError, bairroError, enderecoValidationError))) {
        if (!validation.isPersonalTabValid(formData)) {
          toastHelper.showWarning(editProfileMessages.warnings.completePersonalDataFirst);
        } else {
          toastHelper.showWarning(editProfileMessages.warnings.completeAddressDataFirst);
        }
      } else if (tabId === 'hours' && (!validation.isPersonalTabValid(formData) || !validation.isAddressTabValid(formData, cepError, estadoError, cidadeError, bairroError, enderecoValidationError) || !validation.isBasicInfoTabValid(professionalFormData))) {
        if (!validation.isPersonalTabValid(formData)) {
          toastHelper.showWarning(editProfileMessages.warnings.completePersonalDataFirst);
        } else if (!validation.isAddressTabValid(formData, cepError, estadoError, cidadeError, bairroError, enderecoValidationError)) {
          toastHelper.showWarning(editProfileMessages.warnings.completeAddressDataFirst);
        } else {
          toastHelper.showWarning(editProfileMessages.warnings.completeBasicInfoFirst);
        }
      } else if (tabId === 'portfolio' && !validation.isWorkHoursTabValid(professionalFormData)) {
        toastHelper.showWarning(editProfileMessages.warnings.selectWorkScheduleFirst);
      } else if (tabId === 'security') {
        if (isArtist) {
          if (!validation.isPortfolioTabValid(professionalFormData)) {
            toastHelper.showWarning(editProfileMessages.warnings.completePortfolioFirst);
          }
        } else {
          if (!validation.isAddressTabValid(formData, cepError, estadoError, cidadeError, bairroError, enderecoValidationError)) {
            toastHelper.showWarning(editProfileMessages.warnings.completeAddressDataFirst);
          }
        }
      }
    }
  };

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

  const canChangeTab = (currentTab, targetTab) => {
    if (currentTab === 'hours') {
      const isValid = validation.validateWorkHoursTab(professionalFormData);
      if (!isValid) {
        toastHelper.showError(editProfileMessages.validations.fixInvalidSchedules);
        return false;
      }
    }
    
    if (currentTab === 'basic-info') {
      const isValid = validation.validateBasicInfoTab(professionalFormData);
      if (!isValid) {
        return false;
      }
    }
    
    return true;
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
        } else {
          // Forçar validação de endereço quando há erro, similar ao RegisterScreen
          if (addressValidationErrors.forceAddressValidation) {
            addressValidationErrors.forceAddressValidation();
          }
        }
        break;
      case 'basic-info':
        isValid = validation.validateBasicInfoTab(professionalFormData);
        if (isValid) setActiveTab('hours');
        break;
      case 'hours':
        if (!validation.hasWorkSchedule(professionalFormData)) {
          toastHelper.showError(editProfileMessages.validations.scheduleRequired);
        } else if (!validation.isWorkHoursTabValid(professionalFormData)) {
          return;
        } else {
          setActiveTab('portfolio');
        }
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

  const setActiveTabWithValidation = (newTab) => {
    if (activeTab === 'hours' && !canChangeTab(activeTab, newTab)) {
      return;
    }
    setActiveTab(newTab);
  };

  return {
    activeTab,
    setActiveTab: setActiveTabWithValidation,
    getTabs,
    getAvailableTabs,
    handleTabPress,
    handleNextTab,
    handlePrevTab,
    validateCurrentTab,
    isHoursValid: (() => {
      if (activeTab !== 'hours' || !professionalFormData || !professionalFormData.workHours) {
        return true;
      }
      
      return validation.isWorkHoursTabValid(professionalFormData);
    })()
  };
};

export default useTabNavigation; 