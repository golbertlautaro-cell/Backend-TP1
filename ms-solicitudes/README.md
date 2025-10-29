# ms-solicitudes

Microservicio Spring Boot 3.3.x (Java 21) para gestión de solicitudes.

## Requisitos
- Java 21
- Maven 3.9+
- PostgreSQL (opcional para correr local con BD)

## Configuración
Edita `src/main/resources/application.yml` y coloca tus credenciales de PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/REEMPLAZAR_DB
    username: REEMPLAZAR_USUARIO
    password: REEMPLAZAR_PASSWORD
```

> Nota: `ddl-auto` está en `none`. Cambia a `update` o `validate` si lo necesitas para desarrollo.

## Compilar
```powershell
mvn -DskipTests package
```

## Ejecutar
```powershell
mvn spring-boot:run
```

- Endpoint de prueba: `GET http://localhost:8081/ping` (requiere autenticación por Spring Security si no se desactiva)

## Dependencias principales
- Spring Web
- Spring Data JPA
- Spring Security (sin config inicial)
- Lombok
- PostgreSQL Driver
