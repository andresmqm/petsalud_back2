# Clínica Veterinaria PetSalud - Backend-II

Backend de gestión veterinaria desarrollado con **Java 17, Spring Boot 3.3.6 y MySQL**.

## Descripción del proyecto

PetSalud es un sistema backend diseñado para gestionar las operaciones de una clínica veterinaria. Permite administrar usuarios, mascotas, citas veterinarias, expedientes médicos, productos, ventas, recompensas y notificaciones.

El proyecto incorpora medidas de seguridad para proteger los datos de los clientes y controlar el acceso a los recursos según los roles de cada usuario.

## Funcionalidades principales

- Gestión de usuarios y perfiles.
- Registro y administración de mascotas.
- Agendamiento de citas veterinarias.
- Gestión de expedientes médicos.
- Administración de productos e inventario.
- Gestión de ventas.
- Sistema de recompensas y notificaciones.
- Autenticación mediante JWT.
- Autorización basada en roles.
- Protección contra inyección SQL mediante Spring Data JPA.
- Sanitización de entradas mediante Jsoup.
- Monitoreo de intentos fallidos de autenticación.

## Tecnologías utilizadas

| Tecnología        | Descripción                       |
|-------------------|-----------------------------------|
| Java 17           | Lenguaje de programación          |
| Spring Boot 3.3.6 | Framework principal               |
| Spring Security   | Autenticación y autorización      |
| Spring Data JPA   | Persistencia de datos             |
| Hibernate         | ORM                               |
| MySQL             | Base de datos                     |
| JWT (JJWT)        | Generación y validación de tokens |
| BCrypt            | Codificación de contraseñas       |
| Jsoup             | Sanitización de entradas HTML     |
| Maven             | Gestión de dependencias           |
| Postman           | Pruebas de endpoints y seguridad  |

## Estructura del proyecto

```text
backendpetsalud/
├── src/
│   ├── main/
│   │   ├── java/com/petsalud/backendpetsalud/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── security/
│   │   │   ├── filter/
│   │   │   └── util/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Instalación y configuración

### 1. Requisitos previos

Para ejecutar el proyecto se necesita:

- Java JDK 17.
- MySQL.
- Maven Wrapper (incluido en el proyecto; no requiere instalación manual de Maven).
- Visual Studio Code, IntelliJ IDEA u otro IDE.
- Postman para realizar pruebas.

### 2. Clonar el repositorio

```bash
git clone https://github.com/Milencpa/backend-petsalud.git
cd backend-petsalud
```

### 3. Configurar la base de datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS petsalud_db;
```

Revisar el archivo:

```text
src/main/resources/application.properties
```

Configurar la conexión a MySQL con las credenciales correspondientes al entorno local.

### 4. Configurar JWT

El proyecto utiliza una clave secreta para generar y validar tokens JWT.

En macOS o Linux:

```bash
export JWT_SECRET="$(openssl rand -hex 32)"
```

La configuración de Spring debe utilizar la variable de entorno `JWT_SECRET`.

Cambiar la clave invalida los tokens generados anteriormente.

### 5. Compilar el proyecto

```bash
./mvnw clean compile
```

### 6. Ejecutar el backend

```bash
./mvnw spring-boot:run
```

La API estará disponible en:

```text
http://localhost:8080
```

En Windows se utiliza `mvnw.cmd` en lugar de `./mvnw`.


## Autorización basada en roles y Endpoints principales

### Roles

| Rol      | Permisos generales                                        |
|----------|-----------------------------------------------------------|
| CLIENTE  | Acceso a funcionalidades autorizadas para clientes        |
| EMPLEADO | Gestión de operaciones del personal veterinario           |
| GERENTE  | Acceso a operaciones del personal y funciones gerenciales |

### Autenticación

| Método | Endpoint             | Descripción                  |
|--------|----------------------|------------------------------|
| POST   | `/api/auth/login`    | Iniciar sesión y obtener JWT |
| POST   | `/api/auth/registro` | Registrar usuario            |

### Productos

| Método | Endpoint              | Descripción         |
|--------|-----------------------|---------------------|
| GET    | `/api/productos`      | Listar productos    |
| POST   | `/api/productos`      | Registrar producto  |
| GET    | `/api/productos/{id}` | Consultar producto  |
| PUT    | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto   |

### Citas veterinarias

| Método | Endpoint                                 | Descripción                         |
|--------|------------------------------------------|-------------------------------------|
| POST   | `/api/citas`                             | Agendar cita                        |
| GET    | `/api/citas/{id}`                        | Consultar cita                      |
| GET    | `/api/citas/mascota/{mascotaId}`         | Consultar citas de una mascota      |
| GET    | `/api/citas/veterinario/{veterinarioId}` | Consultar citas de un veterinario   |
| PUT    | `/api/citas/{id}/estado`                 | Actualizar estado                   |
| DELETE | `/api/citas/{id}`                        | Cancelar cita                       |
| GET    | `/api/citas/entre-fechas`                | Consultar citas por rango de fechas |


---

**Clínica PetSalud — Desarrollo Backend II**