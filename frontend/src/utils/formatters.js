const formatCPF = (value) => {
  const numbers = value.replace(/\D/g, '');
  
  // Format: 000.000.000-00
  if (numbers.length <= 3) return numbers;
  if (numbers.length <= 6) return `${numbers.slice(0, 3)}.${numbers.slice(3)}`;
  if (numbers.length <= 9) return `${numbers.slice(0, 3)}.${numbers.slice(3, 6)}.${numbers.slice(6)}`;
  return `${numbers.slice(0, 3)}.${numbers.slice(3, 6)}.${numbers.slice(6, 9)}-${numbers.slice(9, 11)}`;
};

const formatCEP = (value) => {
  const numbers = value.replace(/\D/g, '');
  
  // Format: 00000-000
  if (numbers.length <= 5) return numbers;
  return `${numbers.slice(0, 5)}-${numbers.slice(5, 8)}`;
};

const formatPhone = (value) => {
  const numbers = value.replace(/\D/g, '');
  
  // Format: (00) 00000-0000
  if (numbers.length <= 2) return numbers;
  if (numbers.length <= 7) return `(${numbers.slice(0, 2)}) ${numbers.slice(2)}`;
  return `(${numbers.slice(0, 2)}) ${numbers.slice(2, 7)}-${numbers.slice(7, 11)}`;
};

const formatBirthDate = (value) => {
  const numbers = value.replace(/\D/g, '');
  
  // Format: DD/MM/AAAA
  if (numbers.length <= 2) return numbers;
  if (numbers.length <= 4) {
    const day = numbers.slice(0, 2);
    const month = numbers.slice(2);
    // Validate day (1-31)
    if (parseInt(day) > 31) return `${31}/${month}`;
    return `${day}/${month}`;
  }
  
  const day = numbers.slice(0, 2);
  const month = numbers.slice(2, 4);
  const year = numbers.slice(4, 8);
  
  // Validate day (1-31) and month (1-12)
  const validDay = Math.min(parseInt(day), 31);
  const validMonth = Math.min(parseInt(month), 12);
  
  return `${validDay.toString().padStart(2, '0')}/${validMonth.toString().padStart(2, '0')}/${year}`;
};

const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

const validateCPF = (cpf) => {
  const numbers = cpf.replace(/\D/g, '');

  if (numbers.length !== 11) return false;
  if (/^(\d)\1{10}$/.test(numbers)) return false;
  
  let sum = 0;
  for (let i = 0; i < 9; i++) {
    sum += parseInt(numbers.charAt(i)) * (10 - i);
  }
  let remainder = 11 - (sum % 11);
  let digit = remainder > 9 ? 0 : remainder;
  if (digit !== parseInt(numbers.charAt(9))) return false;
  
  sum = 0;
  for (let i = 0; i < 10; i++) {
    sum += parseInt(numbers.charAt(i)) * (11 - i);
  }
  remainder = 11 - (sum % 11);
  digit = remainder > 9 ? 0 : remainder;
  if (digit !== parseInt(numbers.charAt(10))) return false;
  
  return true;
};

const validatePhone = (phone) => {
  const numbers = phone.replace(/\D/g, '');
  
  return numbers.length === 11;
};

const validateBirthDate = (birthDate) => {
  if (!/^\d{2}\/\d{2}\/\d{4}$/.test(birthDate)) {
    return false;
  }
  
  const [day, month, year] = birthDate.split('/').map(Number);
  
  const birthDateObj = new Date(year, month - 1, day);
  
  if (
    birthDateObj.getFullYear() !== year ||
    birthDateObj.getMonth() !== month - 1 ||
    birthDateObj.getDate() !== day
  ) {
    return false;
  }
  
  const today = new Date();
  let age = today.getFullYear() - birthDateObj.getFullYear();
  const m = today.getMonth() - birthDateObj.getMonth();
  
  if (m < 0 || (m === 0 && today.getDate() < birthDateObj.getDate())) {
    age--;
  }
  
  return age >= 18;
};

const validateFirstName = (firstName) => {
  if (!firstName) return false;
  
  if (firstName.trim().length < 2) return false;
  
  if (firstName.trim().includes(' ')) return false;
  
  return true;
};

const validateSurname = (surname) => {
  if (!surname) return false;
  
  if (surname.trim().length < 2) return false;
  
  return true;
};

const validateFullNameLength = (firstName, surname) => {
  const fullName = `${firstName} ${surname}`.trim();
  return fullName.length <= 255;
};

const validateSocialMedia = (value) => {
  if (!value || value.trim() === '') return true;
  
  return value.trim().length <= 50;
};

const validateWebsite = (value) => {
  if (!value || value.trim() === '') return true;
  
  return value.trim().length <= 255;
};

const validatePassword = (password) => {
  if (!password) return false;
  
  // Mínimo 8 caracteres
  if (password.length < 8) return false;
  
  // Pelo menos uma letra maiúscula
  if (!/[A-Z]/.test(password)) return false;
  
  // Pelo menos um número
  if (!/[0-9]/.test(password)) return false;
  
  // Pelo menos um caractere especial
  if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) return false;
  
  return true;
};

const formatCurrency = (value) => {
  if (!value && value !== 0) return '';
  
  const numericValue = typeof value === 'string' ? parseFloat(value) : value;
  
  if (isNaN(numericValue)) return '';
  
  return numericValue.toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

export {
  formatCPF,
  formatCEP,
  formatPhone,
  formatBirthDate,
  validateEmail,
  validateCPF,
  validatePhone,
  validateBirthDate,
  validateFirstName,
  validateSurname,
  validateFullNameLength,
  validateSocialMedia,
  validateWebsite,
  validatePassword,
  formatCurrency
}; 