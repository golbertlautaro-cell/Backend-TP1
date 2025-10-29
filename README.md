# TPI Logística Backend

Sistema de microservicios para gestión logística con Spring Boot 3.3.5 y Java 21.

## 📋 Arquitectura

- **ms-solicitudes** (Puerto 8080): Gestión de solicitudes, tramos y clientes
- **ms-logistica** (Puerto 8081): Gestión de camiones y depósitos
- **PostgreSQL** (Puerto 5432): Base de datos compartida

## 🚀 Ejecución Local

### Requisitos
- Java 21
- Maven 3.8+
- PostgreSQL 16 (o Docker)

### Compilar ambos microservicios
```bash
# ms-solicitudes
cd ms-solicitudes
mvn clean package -DskipTests

# ms-logistica
cd ../ms-logistica
mvn clean package -DskipTests
```

### Ejecutar con base de datos local
```bash
# Terminal 1 - ms-logistica
cd ms-logistica
java -jar target/ms-logistica-0.0.1-SNAPSHOT.jar

# Terminal 2 - ms-solicitudes
cd ms-solicitudes
java -jar target/ms-solicitudes-0.0.1-SNAPSHOT.jar
```

## 🐳 Ejecución con Docker

### 1. Compilar los JARs
```bash
cd ms-solicitudes
mvn clean package -DskipTests
cd ../ms-logistica
mvn clean package -DskipTests
cd ..
```

### 2. Iniciar todos los servicios
```bash
docker-compose up --build
```

### 3. Detener los servicios
```bash
docker-compose down
```

### 4. Detener y eliminar volúmenes (resetear BD)
```bash
docker-compose down -v
```

## 📚 Documentación API

Una vez levantados los servicios, acceder a:

- **ms-solicitudes Swagger UI**: http://localhost:8080/swagger-ui.html
- **ms-logistica Swagger UI**: http://localhost:8081/swagger-ui.html

## 🔗 Endpoints Principales

### ms-solicitudes
- `GET /api/clientes` - Listar clientes
- `GET /api/tramos` - Listar tramos
- `POST /api/tramos/{id}/asignarACamion` - Asignar camión a tramo
- `POST /api/tramos/{id}/iniciar` - Iniciar tramo
- `PUT /api/tramos/{id}/finalizar` - Finalizar tramo
- `GET /api/integracion/camiones/estado` - Estado de camiones (vía ms-logistica)

### ms-logistica
- `GET /api/camiones` - Listar camiones (con filtros)
- `GET /api/camiones/estado` - Resumen de camiones libres/ocupados
- `POST /api/camiones/validar-capacidad` - Validar capacidad (RF11)
- `GET /api/depositos` - Listar depósitos (pendiente implementar CRUD)

## 🧪 Tests

```bash
# Ejecutar tests de ms-logistica
cd ms-logistica
mvn test

# Solo tests de CamionService
mvn test -Dtest=CamionServiceTest
```

## 📦 Estructura del Proyecto

```
backend1/
├── ms-solicitudes/
│   ├── src/main/java/com/tpi/solicitudes/
│   │   ├── domain/          # Entidades JPA
│   │   ├── repository/      # Repositorios Spring Data
│   │   ├── service/         # Lógica de negocio
│   │   ├── web/            # Controladores REST
│   │   │   └── dto/        # DTOs
│   │   ├── client/         # Clientes para otros microservicios
│   │   └── config/         # Configuración (Security, OpenAPI, WebClient)
│   ├── Dockerfile
│   └── pom.xml
├── ms-logistica/
│   ├── src/main/java/com/tpi/logistica/
│   │   ├── domain/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── web/
│   │   └── config/
│   ├── src/test/java/       # Tests unitarios
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
├── init-db.sql
└── README.md
```

## 🔧 Configuración

### Variables de Entorno

**ms-solicitudes**:
- `MS_LOGISTICA_URL`: URL del microservicio de logística (default: http://localhost:8081)
- `SPRING_DATASOURCE_URL`: URL de PostgreSQL
- `SPRING_DATASOURCE_USERNAME`: Usuario de BD
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de BD

**ms-logistica**:
- `SPRING_DATASOURCE_URL`: URL de PostgreSQL
- `SPRING_DATASOURCE_USERNAME`: Usuario de BD
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de BD

## ✨ Características Implementadas

- ✅ Entidades JPA con validación (Jakarta Validation)
- ✅ Repositorios Spring Data con queries derivadas
- ✅ Servicios con lógica de negocio (RF11: validación de capacidad)
- ✅ Controladores REST con paginación y filtros
- ✅ Manejo global de errores (GlobalExceptionHandler)
- ✅ Seguridad con Spring Security (config dev-permissive)
- ✅ Documentación automática con Springdoc OpenAPI (Swagger)
- ✅ Tests unitarios con JUnit 5 y Mockito
- ✅ Comunicación entre microservicios con WebClient
- ✅ Dockerización con Docker Compose

## 📝 Notas

- Los microservicios usan Spring Security en modo desarrollo (permitAll para endpoints de API)
- PostgreSQL se inicializa automáticamente con dos bases de datos separadas
- La comunicación entre microservicios usa WebClient (Spring WebFlux)
- Swagger UI está disponible en ambos microservicios sin autenticación
