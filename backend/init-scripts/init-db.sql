-- Drop the database if it exists
DROP DATABASE IF EXISTS inkspiration;

-- Create the database
CREATE DATABASE inkspiration;

-- Connect to the inkspiration database
\c inkspiration;

-- Create extension for UUID if not exists
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Add any additional database initialization commands here
-- such as creating tables, indexes, etc. 