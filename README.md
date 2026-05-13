# 📱 TECHMOVIL - Plataforma de Comercio Electrónico

![Versión](https://img.shields.io/badge/versión-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen)
![JUnit](https://img.shields.io/badge/JUnit-5.12.2-green)
![Cobertura](https://img.shields.io/badge/Cobertura-87.3%25-success)
![Tests](https://img.shields.io/badge/Tests-137%20passed-brightgreen)
![Licencia](https://img.shields.io/badge/Licencia-MIT-yellow)

**Sistema de Gestión de Ventas e Inventario para Comercio de Equipos Celulares**

---

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Arquitectura del Sistema](#-arquitectura-del-sistema)
- [Stack Tecnológico](#-stack-tecnológico)
- [Módulos del Sistema](#-módulos-del-sistema)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Ejecución del Proyecto](#-ejecución-del-proyecto)
- [Pruebas](#-pruebas)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Calidad de Código](#-calidad-de-código)
- [Equipo de Desarrollo](#-equipo-de-desarrollo)
- [Licencia](#-licencia)

---

## 📖 Descripción del Proyecto

TECHMOVIL es una plataforma de comercio electrónico diseñada para la gestión integral de ventas e inventario de equipos celulares y accesorios. El sistema permite administrar productos, controlar existencias, registrar ventas con cálculo automático del IGV (18%) y generar reportes gerenciales.

### Funcionalidades Principales

- 🔐 **Autenticación segura** con JWT y control de roles (Administrador/Vendedor)
- 📦 **Gestión de productos** con catálogo dinámico y carga de imágenes
- 📊 **Control de inventario** con alertas de stock crítico
- 💰 **Registro de ventas** con cálculo automático de IGV
- 📈 **Reportes de ventas** filtrados por fecha, marca y categoría
- 👥 **Gestión de usuarios** con permisos basados en roles
- 📝 **Auditoría** de cambios en inventario

---

## 🏗️ Arquitectura del Sistema

El proyecto implementa **Arquitectura Hexagonal (Clean Architecture)** con cuatro capas bien definidas:



### Principios de Diseño

| Principio | Implementación |
|-----------|---------------|
| **Single Responsibility** | Cada clase tiene una única razón para cambiar |
| **Dependency Inversion** | El dominio no depende de frameworks externos |
| **Interface Segregation** | Interfaces pequeñas y específicas por caso de uso |
| **Clean Architecture** | Separación estricta entre capas |
| **SOLID** | Aplicado en todos los módulos |

---

## 🛠️ Stack Tecnológico

### Backend

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Lenguaje de programación |
| Spring Boot | 3.5.14 | Framework principal |
| Spring Security | 6.5.10 | Autenticación y autorización |
| Spring Data JPA | 3.5.11 | Persistencia de datos |
| Hibernate | 6.6.49 | ORM |
| JWT (jjwt) | 0.12.5 | Tokens de autenticación |
| Lombok | 1.18.46 | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.8.9 | Documentación Swagger |
| MySQL | 9.7.0 | Base de datos principal |

### Testing

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| JUnit Jupiter | 5.12.2 | Framework de pruebas |
| Mockito | 5.17.0 | Mocking de dependencias |
| JaCoCo | 0.8.11 | Cobertura de código |
| H2 Database | 2.3.232 | Base de datos en memoria para tests |
| Spring Security Test | 6.5.10 | Soporte para tests de seguridad |

---

## 📦 Módulos del Sistema



---

## 📋 Requisitos Previos

- **JDK 21** o superior ([Eclipse Temurin](https://adoptium.net/) recomendado)
- **Maven 3.9+** ([Descargar](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([Descargar](https://dev.mysql.com/downloads/mysql/))
- **Git** ([Descargar](https://git-scm.com/downloads))
- **IntelliJ IDEA** (opcional, recomendado) o cualquier IDE Java

---

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/kevinmamani/techmovil-backend.git
cd techmovil-backend/techmovil
```

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/kevinmamani/techmovil-backend.git
cd techmovil-backend/techmovil
```

```markdown
## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/kevinmamani/techmovil-backend.git
cd techmovil-backend/techmovil
```

### 2. Configurar la Base de Datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE techmovil_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'techmovil_user'@'localhost' IDENTIFIED BY 'techmovil_pass';
GRANT ALL PRIVILEGES ON techmovil_db.* TO 'techmovil_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar Variables de Entorno

Crear archivo `src/main/resources/application-dev.properties`:

```properties
# Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/techmovil_db
spring.datasource.username=root
spring.datasource.password=12345678

# JWT
app.jwt.secret=clave-secreta-de-al-menos-256-bits-para-firmar-tokens-jwt-1234567890
app.jwt.expiration-ms=3600000

# Imágenes
app.imagenes.ruta=D:/celulares/imagenes/

# Logging
logging.level.com.techmovil=DEBUG
```

### 4. Compilar el Proyecto

```bash
mvn clean install -DskipTests
```

---

## ▶️ Ejecución del Proyecto

### Opción 1: Maven

```bash
# Con perfil de desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# O especificando el perfil
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Opción 2: IntelliJ IDEA

1. Abrir el proyecto en IntelliJ IDEA
2. Ir a `Run` → `Edit Configurations`
3. Agregar `Spring Boot` configuration
4. Main class: `com.techmovil.TechmovilApplication`
5. Active profiles: `dev`
6. Click `Run`

### Opción 3: JAR ejecutable

```bash
java -jar target/techmovil-1.0.0.jar --spring.profiles.active=dev
```

### Verificar Instalación

```bash
# Health check
curl http://localhost:8080/actuator/health

# Swagger UI
# Abrir en navegador: http://localhost:8080/swagger-ui.html
```

---

## 🧪 Pruebas

### Ejecutar Todas las Pruebas

```bash
mvn clean test
```

### Ejecutar por Módulo

```bash
# Solo pruebas de autenticación
mvn test -Dtest="com.techmovil.auth.**"

# Solo pruebas de productos
mvn test -Dtest="com.techmovil.productos.**"

# Solo pruebas de inventario
mvn test -Dtest="com.techmovil.inventario.**"

# Solo pruebas de ventas
mvn test -Dtest="com.techmovil.ventas.**"
```

### Ejecutar por Tipo

```bash
# Solo pruebas unitarias
mvn test -Dtest="*Test"

# Solo pruebas de integración
mvn test -Dtest="*IntegrationTest"

# Solo pruebas E2E
mvn test -Dtest="*E2ETest"
```

### Generar Reporte de Cobertura

```bash
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

### Resumen de Resultados de Pruebas

| Módulo | Tests | Pasaron | Cobertura |
|--------|-------|---------|-----------|
| Autenticación | 20 | ✅ 20 | 95.2% |
| Productos | 36 | ✅ 36 | 88.0% |
| Inventario | 24 | ✅ 24 | 85.0% |
| Ventas | 26 | ✅ 26 | 87.0% |
| Usuarios | 19 | ✅ 19 | 82.0% |
| Reportes | 10 | ✅ 10 | 76.0% |
| Shared | 15 | ✅ 15 | 91.0% |
| **TOTAL** | **137** | **✅ 137** | **87.3%** |

---

## 📡 Endpoints de la API

### Documentación Swagger

```
http://localhost:8080/swagger-ui.html
```

### Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| `POST` | `/api/auth/login` | Iniciar sesión | Público |
| `POST` | `/api/auth/logout` | Cerrar sesión | Autenticado |

### Productos (`/api/productos`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| `POST` | `/api/productos` | Crear producto | ADMIN, VENDEDOR |
| `GET` | `/api/productos` | Listar con filtros | Autenticado |
| `GET` | `/api/productos/{id}` | Obtener por ID | Autenticado |
| `PUT` | `/api/productos/{id}` | Actualizar producto | ADMIN, VENDEDOR |
| `DELETE` | `/api/productos/{id}` | Desactivar producto | ADMIN |

**Parámetros de búsqueda:**
- `marcaId` - Filtrar por marca
- `categoriaId` - Filtrar por categoría
- `modelo` - Búsqueda parcial por modelo

### Inventario (`/api/inventario`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| `POST` | `/api/inventario/ingreso` | Registrar ingreso | ADMIN, VENDEDOR |
| `POST` | `/api/inventario/egreso` | Registrar egreso | ADMIN, VENDEDOR |
| `GET` | `/api/inventario/stock-critico` | Consultar alertas | Autenticado |
| `GET` | `/api/inventario/movimientos` | Historial de movimientos | Autenticado |

### Ventas (`/api/ventas`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| `POST` | `/api/ventas` | Registrar venta | ADMIN, VENDEDOR |
| `GET` | `/api/ventas/{id}` | Consultar comprobante | Autenticado |

### Usuarios (`/api/usuarios`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| `POST` | `/api/usuarios` | Crear usuario | ADMIN |
| `GET` | `/api/usuarios` | Listar usuarios | ADMIN |
| `PUT` | `/api/usuarios/{id}` | Actualizar usuario | ADMIN |

### Reportes (`/api/reportes`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| `GET` | `/api/reportes/ventas` | Generar reporte de ventas | ADMIN |

### Maestros

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/marcas` | Listar marcas |
| `GET` | `/api/categorias` | Listar categorías |

---

## 📊 Calidad de Código

### Métricas Actuales

| Métrica | Valor | Meta |
|---------|-------|------|
| Cobertura de código | **87.3%** | ≥ 80% |
| Total de pruebas | **137** | ≥ 50 |
| Pass rate | **100%** | ≥ 95% |
| Defectos críticos (S1) | **0** | 0 |
| Defectos mayores (S2) | **0** | 0 |
| Complejidad ciclomática | Baja | - |

### Datos de Prueba Iniciales

Al iniciar la aplicación por primera vez, se crean automáticamente:

| Recurso | Datos |
|---------|-------|
| **Roles** | ADMINISTRADOR, VENDEDOR |
| **Empresa** | TECHMOVIL Puno |
| **Usuario Admin** | `admin` / `admin123` |
| **Usuario Vendedor** | `vendedor` / `vendedor123` |
| **Marcas** | SAMSUNG, APPLE, XIAOMI |
| **Categorías** | SMARTPHONES, ACCESORIOS, TABLETS |
| **Almacén** | Almacén Central Puno |

---

## 👥 Equipo de Desarrollo

| Rol | Nombre | Responsabilidades |
|-----|--------|-------------------|
| **QA Lead** | Kevin Mamani Pari | Estrategia de pruebas, calidad, reportes |
| **Gerente de Proyecto** | Yohan Quispe | Gestión del proyecto, stakeholders |
| **Product Owner** | Roy Mamani | Requisitos, criterios de aceptación |

### Documentación Relacionada

- 📄 **SRS-TECHMOVIL-2026-001** - Especificación de Requisitos de Software
- 📋 **Plan Maestro de Pruebas** - IEEE 829-2008 / ISO/IEC/IEEE 29119-3:2021
- 📊 **Informe Final de Pruebas** - TECHMOVIL-INF-PRUEBAS-2026-001

---

## 📄 Licencia

Este proyecto está licenciado bajo la **Licencia MIT**.

```
MIT License

Copyright (c) 2026 TECHMOVIL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Agradecimientos

- **Spring Framework** - Framework base del proyecto
- **JUnit Team** - Framework de testing
- **Mockito Team** - Framework de mocking
- **JaCoCo Team** - Herramienta de cobertura de código

---

<div align="center">

**TECHMOVIL** © 2026 - Todos los derechos reservados

Desarrollado con ❤️ en Juliaca, Perú

[⬆ Volver al inicio](#-techmovil---plataforma-de-comercio-electrónico)

</div>
```