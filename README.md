# Navium - Microservicio de Usuarios (ms-usuarios)

Este microservicio es responsable de centralizar el control de acceso, la autenticación y la gestión de perfiles para todos los actores de la plataforma Navium (Centro de Mando, Operadores de Patio y Sucursal Virtual).

## Requerimientos de Seguridad
- Las contraseñas se almacenan encriptadas mediante Hash (BCrypt).
- La comunicación y autorización de los endpoints protegidos se realiza mediante tokens JWT.
- Borrado lógico implementado para mantener la integridad histórica y auditorías del puerto.

## Contrato de Interfaz (API Endpoints)

| Método | Endpoint | Acción | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/usuarios` | **Registro** | Creación de un nuevo usuario en el sistema. |
| `POST` | `/api/auth/login` | **Autenticación** | Valida credenciales y emite el token JWT. |
| `GET` | `/api/usuarios/{id}` | **Consulta** | Obtiene el perfil de un usuario específico. |
| `PUT` | `/api/usuarios/{id}` | **Actualización** | Modifica datos del perfil del usuario. |
| `DELETE` | `/api/usuarios/{id}` | **Baja Lógica** | Desactiva la cuenta del usuario sin borrar historial. |