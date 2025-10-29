-- Script de inicialización de bases de datos
-- Se ejecuta automáticamente cuando se crea el contenedor de PostgreSQL

-- Crear base de datos para ms-logistica
CREATE DATABASE logistica_db;

-- Crear base de datos para ms-solicitudes
CREATE DATABASE solicitudes_db;

-- Conceder permisos
GRANT ALL PRIVILEGES ON DATABASE logistica_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE solicitudes_db TO postgres;
