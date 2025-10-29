# Keycloak + Microservicios con OAuth2

Este proyecto incluye integración con Keycloak para autenticación y autorización basada en JWT con roles.

## Inicio rápido

### 1. Levantar Keycloak
```powershell
docker-compose -f docker-compose.keycloak.yml up -d
```

Espera ~60 segundos para que Keycloak inicie completamente.

**Accesos:**
- Keycloak Admin Console: http://localhost:8090
  - Usuario: `admin`
  - Contraseña: `admin`
- Realm: `tpi-realm`

### 2. Usuarios pre-configurados

| Usuario         | Contraseña    | Rol(es)                           | Email                      |
|-----------------|---------------|-----------------------------------|----------------------------|
| `cliente1`      | `password123` | CLIENTE                          | cliente1@example.com       |
| `operador1`     | `password123` | OPERADOR                         | operador1@example.com      |
| `transportista1`| `password123` | TRANSPORTISTA                    | transportista1@example.com |
| `admin-tpi`     | `admin123`    | CLIENTE, OPERADOR, TRANSPORTISTA | admin@example.com          |

### 3. Obtener token JWT

```powershell
# Para CLIENTE
curl -X POST http://localhost:8090/realms/tpi-realm/protocol/openid-connect/token `
  -d "client_id=tpi-backend" `
  -d "username=cliente1" `
  -d "password=password123" `
  -d "grant_type=password"

# Para OPERADOR
curl -X POST http://localhost:8090/realms/tpi-realm/protocol/openid-connect/token `
  -d "client_id=tpi-backend" `
  -d "username=operador1" `
  -d "password=password123" `
  -d "grant_type=password"

# Para TRANSPORTISTA
curl -X POST http://localhost:8090/realms/tpi-realm/protocol/openid-connect/token `
  -d "client_id=tpi-backend" `
  -d "username=transportista1" `
  -d "password=password123" `
  -d "grant_type=password"
```

Respuesta incluirá `access_token` (JWT válido por 30 min).

### 4. Probar endpoints protegidos

**Crear solicitud (requiere rol CLIENTE):**
```powershell
$TOKEN = "tu_access_token_aqui"

curl -X POST http://localhost:8080/api/solicitudes `
  -H "Authorization: Bearer $TOKEN" `
  -H "Content-Type: application/json" `
  -d '{"idContenedor":"CONT123","idCliente":1}'
```

**Asignar camión (requiere rol OPERADOR):**
```powershell
curl -X PUT http://localhost:8080/api/tramos/1/asignar-camion `
  -H "Authorization: Bearer $TOKEN" `
  -H "Content-Type: application/json" `
  -d '{"dominioCamion":"ABC123"}'
```

**Iniciar tramo (requiere rol TRANSPORTISTA):**
```powershell
curl -X PUT http://localhost:8080/api/tramos/1/iniciar `
  -H "Authorization: Bearer $TOKEN"
```

### 5. Errores comunes

**403 Forbidden**: Token no tiene el rol requerido.
**401 Unauthorized**: Token inválido, expirado o faltante.

### 6. Verificar token

Decodifica el JWT en https://jwt.io para ver:
- `realm_access.roles`: debe contener el rol necesario
- `exp`: timestamp de expiración
- `iss`: debe ser `http://localhost:8090/realms/tpi-realm`

### 7. Detener Keycloak

```powershell
docker-compose -f docker-compose.keycloak.yml down
```

Para eliminar también los datos:
```powershell
docker-compose -f docker-compose.keycloak.yml down -v
```

## Arquitectura de seguridad

### Roles y permisos

| Endpoint                           | Método | Rol requerido   |
|------------------------------------|--------|-----------------|
| `POST /api/solicitudes`            | POST   | CLIENTE         |
| `PUT /api/tramos/{id}/asignar-camion` | PUT  | OPERADOR        |
| `PUT /api/tramos/{id}/iniciar`     | PUT    | TRANSPORTISTA   |
| Otros `/api/**`                    | *      | Autenticado     |
| `/swagger-ui/**`, `/ping`          | *      | Público         |

### Flujo de autenticación

1. Usuario obtiene token de Keycloak (POST `/realms/tpi-realm/protocol/openid-connect/token`)
2. Cliente incluye token en header `Authorization: Bearer <token>`
3. Gateway/Servicios validan el token contra Keycloak (`issuer-uri`)
4. Spring Security extrae roles de `realm_access.roles`
5. `@PreAuthorize` verifica permisos antes de ejecutar el endpoint

### Configuración por servicio

**ms-solicitudes (8082):**
- Valida JWT
- Requiere autenticación en `/api/**`
- Roles: CLIENTE (crear solicitud), OPERADOR (asignar), TRANSPORTISTA (iniciar)

**ms-logistica (8081):**
- Valida JWT
- Requiere autenticación en `/api/**`
- Sin roles específicos (validación futura)

**ms-gateway (8080):**
- Valida JWT (reactivo)
- Propaga token a servicios downstream
- Punto único de entrada

## Notas

- **Desarrollo**: Keycloak usa modo dev (`start-dev`) sin HTTPS
- **Producción**: Cambiar a `start` con certificados SSL y `KC_HOSTNAME`
- **Tokens**: Válidos 30 min; refresh tokens disponibles
- **Client**: `tpi-backend` es público (sin secret) para simplicidad en desarrollo

## Siguientes pasos

1. Agregar más roles si es necesario (ej: `ADMIN`)
2. Configurar refresh tokens en el frontend
3. Habilitar SSL en producción
4. Agregar custom mappers para claims adicionales
5. Configurar políticas de password más estrictas
