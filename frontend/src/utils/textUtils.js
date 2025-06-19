const textUtils = {
  /**
   * Trunca texto se exceder o limite de caracteres e adiciona "..."
   * @param {string} text - Texto a ser truncado
   * @param {number} maxLength - Número máximo de caracteres
   * @param {string} suffix - Sufixo a adicionar (padrão: "...")
   * @returns {string} - Texto truncado
   */
  truncateText: (text, maxLength = 20, suffix = '...') => {
    if (!text || typeof text !== 'string') return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength - suffix.length).trim() + suffix;
  },

  /**
   * Trunca nome completo preservando primeiro e último nome quando possível
   * @param {string} fullName - Nome completo
   * @param {number} maxLength - Número máximo de caracteres
   * @returns {string} - Nome truncado
   */
  truncateName: (fullName, maxLength = 25) => {
    if (!fullName || typeof fullName !== 'string') return '';
    if (fullName.length <= maxLength) return fullName;
    
    const names = fullName.trim().split(' ');
    if (names.length === 1) {
      return textUtils.truncateText(names[0], maxLength);
    }
    
    // Tentar preservar primeiro e último nome
    if (names.length >= 2) {
      const firstName = names[0];
      const lastName = names[names.length - 1];
      const shortName = `${firstName} ${lastName}`;
      
      if (shortName.length <= maxLength) {
        return shortName;
      }
      
      // Se ainda for muito longo, truncar apenas o primeiro nome
      return textUtils.truncateText(firstName, maxLength);
    }
    
    return textUtils.truncateText(fullName, maxLength);
  },

  /**
   * Formata nome para exibição em avatares/initials
   * @param {string} name - Nome completo
   * @returns {string} - Iniciais do nome
   */
  getInitials: (name) => {
    if (!name || typeof name !== 'string') return '?';
    
    const names = name.trim().split(' ');
    if (names.length === 1) {
      return names[0].charAt(0).toUpperCase();
    }
    
    return (names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase();
  }
};

export default textUtils; 