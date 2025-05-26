-- Conecta ao banco de dados padrão 'postgres'
\c postgres;

-- Cria o banco de dados 'inkspiration' se não existir
SELECT 'CREATE DATABASE inkspiration'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'inkspiration');

-- Conecta ao banco de dados 'inkspiration'
\c inkspiration;

-- Adicione aqui comandos adicionais para inicializar o banco de dados, se necessário
-- como criar tabelas, índices, etc. 