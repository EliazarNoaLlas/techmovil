# =============================================================================
# TECHMOVIL BACKEND - Generador de Estructura Clean Architecture
# Ejecutar desde: C:\Users\infoe\IdeaProjects\techmovil_backend\techmovil\src
# Comando: powershell -ExecutionPolicy Bypass -File crear_estructura_techmovil.ps1
# =============================================================================

$base = "main\java\com\techmovil"
$test = "test\java\com\techmovil"
$res  = "main\resources"

Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  TECHMOVIL - Generando estructura Clean Architecture   " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# ------------------------------------------------------------------------------
# Funcion auxiliar: crea carpeta + archivo placeholder si no existe
# ------------------------------------------------------------------------------
function New-Dir($path) {
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
    }
}

function New-File($path, $content) {
    if (-not (Test-Path $path)) {
        New-Item -ItemType File -Path $path -Force | Out-Null
        Set-Content -Path $path -Value $content -Encoding UTF8
    }
}

# ==============================================================================
# RESOURCES
# ==============================================================================
New-Dir "$res\static"
New-Dir "$res\templates"

New-File "$res\application.properties" @"
# ============================================================
# TECHMOVIL - Configuracion base (sobreescrita por profiles)
# ============================================================
spring.application.name=techmovil
spring.profiles.active=dev
"@

New-File "$res\application-dev.properties" @"
# ============================================================
# PERFIL: Desarrollo local
# ============================================================
spring.datasource.url=jdbc:mysql://localhost:3306/techmovil_db?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT
app.jwt.secret=techmovil-dev-secret-key-2026-muy-segura
app.jwt.expiration-ms=86400000

# Imagenes
app.imagenes.ruta=D:/celulares/imagenes/

# IGV por defecto
app.igv.porcentaje=18.00

logging.level.com.techmovil=DEBUG
"@

New-File "$res\application-test.properties" @"
# ============================================================
# PERFIL: Pruebas automatizadas (H2 en memoria)
# ============================================================
spring.datasource.url=jdbc:h2:mem:techmovil_test;DB_CLOSE_DELAY=-1;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

app.jwt.secret=techmovil-test-secret-key
app.jwt.expiration-ms=3600000
app.igv.porcentaje=18.00
"@

New-File "$res\application-prod.properties" @"
# ============================================================
# PERFIL: Produccion (valores desde variables de entorno)
# ============================================================
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=86400000
app.igv.porcentaje=18.00
"@

# ==============================================================================
# SHARED  (excepciones, config, seguridad, utilidades transversales)
# ==============================================================================
Write-Host "  [+] Creando modulo: shared" -ForegroundColor Yellow

$modulos_shared = @(
    "$base\shared\config",
    "$base\shared\exception",
    "$base\shared\exception\handler",
    "$base\shared\security\jwt",
    "$base\shared\security\filter",
    "$base\shared\util",
    "$base\shared\response",
    "$base\shared\audit"
)
foreach ($d in $modulos_shared) { New-Dir $d }

New-File "$base\shared\config\SecurityConfig.java" @"
package com.techmovil.shared.config;

// TODO: Configuracion de Spring Security + JWT
// - permitAll: /api/auth/**
// - authenticated: el resto
// - Filtro: JwtAuthenticationFilter
public class SecurityConfig { }
"@

New-File "$base\shared\config\CorsConfig.java" @"
package com.techmovil.shared.config;

// TODO: Configuracion de CORS para el frontend
public class CorsConfig { }
"@

New-File "$base\shared\config\OpenApiConfig.java" @"
package com.techmovil.shared.config;

// TODO: Configuracion de Swagger / OpenAPI 3
// Acceso: http://localhost:8080/swagger-ui.html
public class OpenApiConfig { }
"@

New-File "$base\shared\exception\TechmovilException.java" @"
package com.techmovil.shared.exception;

/** Excepcion base del dominio TECHMOVIL */
public class TechmovilException extends RuntimeException {
    private final String codigo;
    public TechmovilException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
    public String getCodigo() { return codigo; }
}
"@

New-File "$base\shared\exception\RecursoNoEncontradoException.java" @"
package com.techmovil.shared.exception;

public class RecursoNoEncontradoException extends TechmovilException {
    public RecursoNoEncontradoException(String recurso, Object id) {
        super("RECURSO_NO_ENCONTRADO", recurso + " con id " + id + " no encontrado");
    }
}
"@

New-File "$base\shared\exception\StockInsuficienteException.java" @"
package com.techmovil.shared.exception;

public class StockInsuficienteException extends TechmovilException {
    public StockInsuficienteException(String producto, double disponible) {
        super("STOCK_INSUFICIENTE",
              "Stock insuficiente para '" + producto + "'. Disponible: " + disponible);
    }
}
"@

New-File "$base\shared\exception\ReglaDeNegocioException.java" @"
package com.techmovil.shared.exception;

public class ReglaDeNegocioException extends TechmovilException {
    public ReglaDeNegocioException(String mensaje) {
        super("REGLA_NEGOCIO", mensaje);
    }
}
"@

New-File "$base\shared\exception\handler\GlobalExceptionHandler.java" @"
package com.techmovil.shared.exception.handler;

// TODO: @RestControllerAdvice
// Captura TechmovilException, MethodArgumentNotValidException, etc.
// Devuelve ApiResponse estandarizado
public class GlobalExceptionHandler { }
"@

New-File "$base\shared\response\ApiResponse.java" @"
package com.techmovil.shared.response;

/** Respuesta estandarizada para todos los endpoints */
public record ApiResponse<T>(
    boolean exito,
    String  mensaje,
    T       datos,
    Object  errores
) {
    public static <T> ApiResponse<T> ok(T datos) {
        return new ApiResponse<>(true, "OK", datos, null);
    }
    public static <T> ApiResponse<T> ok(String mensaje, T datos) {
        return new ApiResponse<>(true, mensaje, datos, null);
    }
    public static <T> ApiResponse<T> error(String mensaje, Object errores) {
        return new ApiResponse<>(false, mensaje, null, errores);
    }
}
"@

New-File "$base\shared\response\PaginaResponse.java" @"
package com.techmovil.shared.response;

import java.util.List;

/** Wrapper para respuestas paginadas */
public record PaginaResponse<T>(
    List<T> contenido,
    int     paginaActual,
    int     totalPaginas,
    long    totalElementos,
    boolean esUltima
) { }
"@

New-File "$base\shared\security\jwt\JwtService.java" @"
package com.techmovil.shared.security.jwt;

// TODO: Generacion y validacion de tokens JWT
// - generarToken(UserDetails)
// - validarToken(token, UserDetails)
// - extraerUsername(token)
public class JwtService { }
"@

New-File "$base\shared\security\filter\JwtAuthenticationFilter.java" @"
package com.techmovil.shared.security.filter;

// TODO: OncePerRequestFilter
// - Extrae token del header Authorization: Bearer <token>
// - Valida y setea SecurityContext
public class JwtAuthenticationFilter { }
"@

New-File "$base\shared\util\IgvCalculadora.java" @"
package com.techmovil.shared.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculadora de IGV centralizada.
 * RF-06: El sistema debe calcular el 18% de IGV automaticamente.
 */
public final class IgvCalculadora {

    private IgvCalculadora() {}

    public static final BigDecimal TASA_IGV = new BigDecimal("0.18");

    /** Calcula el monto de IGV dado un subtotal sin IGV */
    public static BigDecimal calcularIgv(BigDecimal subtotal) {
        return subtotal.multiply(TASA_IGV).setScale(2, RoundingMode.HALF_UP);
    }

    /** Calcula el total incluyendo IGV */
    public static BigDecimal calcularTotal(BigDecimal subtotal) {
        return subtotal.add(calcularIgv(subtotal));
    }

    /** Extrae la base imponible de un precio que YA incluye IGV */
    public static BigDecimal extraerBase(BigDecimal precioConIgv) {
        return precioConIgv.divide(BigDecimal.ONE.add(TASA_IGV), 2, RoundingMode.HALF_UP);
    }
}
"@

New-File "$base\shared\audit\AuditoriaServicio.java" @"
package com.techmovil.shared.audit;

// TODO: Registra en tabla 'auditoria' los cambios criticos
// RF-12: fecha, hora y usuario de cambios en inventario
public class AuditoriaServicio { }
"@

# ==============================================================================
# MODULO: auth
# ==============================================================================
Write-Host "  [+] Creando modulo: auth" -ForegroundColor Yellow

$dirs_auth = @(
    "$base\auth\domain",
    "$base\auth\application\dto",
    "$base\auth\infrastructure\persistence",
    "$base\auth\presentation"
)
foreach ($d in $dirs_auth) { New-Dir $d }

New-File "$base\auth\application\dto\LoginRequestDto.java" @"
package com.techmovil.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO de entrada para el endpoint POST /api/auth/login (RF-01) */
public record LoginRequestDto(
    @NotBlank String username,
    @NotBlank String password
) { }
"@

New-File "$base\auth\application\dto\LoginResponseDto.java" @"
package com.techmovil.auth.application.dto;

/** DTO de respuesta con el token JWT generado */
public record LoginResponseDto(
    String token,
    String tipo,
    String username,
    String nombreCompleto,
    java.util.List<String> roles
) { }
"@

New-File "$base\auth\application\LoginUseCase.java" @"
package com.techmovil.auth.application;

import com.techmovil.auth.application.dto.LoginRequestDto;
import com.techmovil.auth.application.dto.LoginResponseDto;

/**
 * Caso de uso: Autenticacion de usuario.
 * RF-01: login segun usuario y contrasena para roles Administrador y Vendedor.
 */
public interface LoginUseCase {
    LoginResponseDto ejecutar(LoginRequestDto request);
}
"@

New-File "$base\auth\presentation\AuthController.java" @"
package com.techmovil.auth.presentation;

// TODO: @RestController @RequestMapping("/api/auth")
// POST /login   -> LoginUseCase
// POST /logout  -> invalida token / auditoria
public class AuthController { }
"@

# Tests auth
New-Dir "$test\auth\application"
New-File "$test\auth\application\LoginUseCaseTest.java" @"
package com.techmovil.auth.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD - Pruebas unitarias del caso de uso de Login (RF-01)
 */
@DisplayName("Login - Casos de prueba")
class LoginUseCaseTest {

    @Test
    @DisplayName("Debe retornar token JWT cuando credenciales son correctas")
    void debeRetornarTokenCuandoCredencialesCorrectas() {
        // GIVEN - usuario y contrasena validos
        // WHEN  - se ejecuta LoginUseCase
        // THEN  - se retorna LoginResponseDto con token no nulo
        // TODO: implementar con Mockito
        assertTrue(true, "Prueba placeholder - implementar");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando credenciales son incorrectas")
    void debeLanzarExcepcionCuandoCredencialesIncorrectas() {
        // TODO: verificar que lanza BadCredentialsException
        assertTrue(true, "Prueba placeholder - implementar");
    }

    @Test
    @DisplayName("Debe bloquear usuario tras 3 intentos fallidos")
    void debeBloquerUsuarioTrasTresIntentosFallidos() {
        // RF-01: protocolo de bloqueo de seguridad
        // TODO: implementar
        assertTrue(true, "Prueba placeholder - implementar");
    }
}
"@

# ==============================================================================
# MODULO: usuarios
# ==============================================================================
Write-Host "  [+] Creando modulo: usuarios" -ForegroundColor Yellow

$dirs_users = @(
    "$base\usuarios\domain",
    "$base\usuarios\application\dto",
    "$base\usuarios\application\usecase",
    "$base\usuarios\infrastructure\persistence",
    "$base\usuarios\presentation"
)
foreach ($d in $dirs_users) { New-Dir $d }

New-File "$base\usuarios\domain\Usuario.java" @"
package com.techmovil.usuarios.domain;

import java.util.Set;

/**
 * Entidad de dominio Usuario.
 * El dominio NO depende de JPA ni de Spring (Clean Architecture).
 */
public class Usuario {
    private Long   id;
    private String username;
    private String passwordHash;
    private String nombres;
    private String apellidos;
    private String email;
    private boolean activo;
    private int intentosFallidos;
    private boolean bloqueado;
    private Set<String> roles;

    /** RF-01: regla de negocio - bloquear tras 3 intentos fallidos */
    public void registrarIntentoFallido() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= 3) {
            this.bloqueado = true;
        }
    }

    public void resetearIntentos() {
        this.intentosFallidos = 0;
        this.bloqueado = false;
    }

    public boolean estaBloqueado() { return bloqueado; }

    // Getters y setters omitidos - usar Lombok @Data en implementacion
}
"@

New-File "$base\usuarios\domain\UsuarioRepository.java" @"
package com.techmovil.usuarios.domain;

import java.util.Optional;

/**
 * Puerto (interfaz) del repositorio de usuarios.
 * Definido en el dominio, implementado en infraestructura.
 */
public interface UsuarioRepository {
    Optional<Usuario> buscarPorUsername(String username);
    Optional<Usuario> buscarPorId(Long id);
    Usuario guardar(Usuario usuario);
    boolean existePorUsername(String username);
}
"@

New-File "$base\usuarios\application\usecase\CrearUsuarioUseCase.java" @"
package com.techmovil.usuarios.application.usecase;

// TODO: RF-11 - Crear usuario con rol especifico (Admin/Vendedor)
public interface CrearUsuarioUseCase {
    // UsuarioResponseDto ejecutar(CrearUsuarioDto dto);
}
"@

New-File "$base\usuarios\infrastructure\persistence\UsuarioEntity.java" @"
package com.techmovil.usuarios.infrastructure.persistence;

// TODO: @Entity @Table(name = "usuarios")
// Mapea la tabla usuarios de la BD
// Separada del dominio intencionalmente (Clean Architecture)
public class UsuarioEntity { }
"@

New-File "$base\usuarios\infrastructure\persistence\UsuarioJpaRepository.java" @"
package com.techmovil.usuarios.infrastructure.persistence;

// TODO: interface que extiende JpaRepository<UsuarioEntity, Long>
// + metodo findByUsername
public interface UsuarioJpaRepository { }
"@

New-File "$base\usuarios\presentation\UsuarioController.java" @"
package com.techmovil.usuarios.presentation;

// TODO: @RestController @RequestMapping("/api/usuarios")
// GET    /          -> listar usuarios (solo ADMIN)
// POST   /          -> crear usuario   (RF-11)
// PUT    /{id}      -> actualizar
// DELETE /{id}      -> desactivar
public class UsuarioController { }
"@

# ==============================================================================
# MODULO: productos
# ==============================================================================
Write-Host "  [+] Creando modulo: productos" -ForegroundColor Yellow

$dirs_prod = @(
    "$base\productos\domain",
    "$base\productos\application\dto",
    "$base\productos\application\usecase",
    "$base\productos\infrastructure\persistence",
    "$base\productos\infrastructure\storage",
    "$base\productos\presentation"
)
foreach ($d in $dirs_prod) { New-Dir $d }

New-File "$base\productos\domain\Producto.java" @"
package com.techmovil.productos.domain;

import java.math.BigDecimal;

/**
 * Entidad de dominio Producto (celular o accesorio).
 * RF-02, RF-03: gestion de equipos con marca, modelo, precio y stock.
 */
public class Producto {
    private Long       id;
    private String     sku;
    private String     nombre;
    private String     modelo;
    private String     color;
    private String     capacidad;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal igvPorcentaje;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private boolean    esRastreable;
    private boolean    activo;

    /** RF-09: verifica si el stock esta en nivel critico */
    public boolean tieneStockCritico() {
        return this.stockActual.compareTo(this.stockMinimo) <= 0;
    }

    /** RF-07: descuenta stock al confirmar una venta */
    public void descontarStock(BigDecimal cantidad) {
        if (cantidad.compareTo(this.stockActual) > 0) {
            throw new com.techmovil.shared.exception.StockInsuficienteException(
                this.nombre, this.stockActual.doubleValue()
            );
        }
        this.stockActual = this.stockActual.subtract(cantidad);
    }

    /** RF-08: agrega stock al registrar ingreso de mercaderia */
    public void agregarStock(BigDecimal cantidad) {
        this.stockActual = this.stockActual.add(cantidad);
    }

    // Getters y setters - usar Lombok @Data en implementacion
}
"@

New-File "$base\productos\domain\ProductoRepository.java" @"
package com.techmovil.productos.domain;

import java.util.List;
import java.util.Optional;

/** Puerto del repositorio de productos */
public interface ProductoRepository {
    Optional<Producto> buscarPorId(Long id);
    Optional<Producto> buscarPorSku(String sku);
    Producto guardar(Producto producto);
    List<Producto> listarActivos(Long empresaId);
    List<Producto> buscarConStockCritico(Long empresaId);
}
"@

New-File "$base\productos\application\usecase\CrearProductoUseCase.java" @"
package com.techmovil.productos.application.usecase;

// TODO: RF-02 - Registrar nuevo equipo celular en el catalogo
public interface CrearProductoUseCase { }
"@

New-File "$base\productos\application\usecase\ActualizarProductoUseCase.java" @"
package com.techmovil.productos.application.usecase;

// TODO: RF-02 - Editar producto existente
public interface ActualizarProductoUseCase { }
"@

New-File "$base\productos\application\usecase\DesactivarProductoUseCase.java" @"
package com.techmovil.productos.application.usecase;

// TODO: RF-02 - Baja logica de producto (activo=false)
public interface DesactivarProductoUseCase { }
"@

New-File "$base\productos\application\usecase\BuscarProductosUseCase.java" @"
package com.techmovil.productos.application.usecase;

// TODO: RF-03 - Busqueda con filtros dinamicos: marca, modelo, precio, stock
// Usa Spring Data Specifications (Specification-Driven Development)
public interface BuscarProductosUseCase { }
"@

New-File "$base\productos\application\dto\ProductoRequestDto.java" @"
package com.techmovil.productos.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** DTO de entrada para crear/editar producto */
public record ProductoRequestDto(
    @NotBlank  String     sku,
    @NotBlank  String     nombre,
    @NotNull   Long       marcaId,
    @NotNull   Long       categoriaId,
               String     modelo,
               String     color,
               String     capacidad,
    @NotNull @Positive BigDecimal precioCompra,
    @NotNull @Positive BigDecimal precioVenta,
    @PositiveOrZero    BigDecimal stockInicial,
    @PositiveOrZero    BigDecimal stockMinimo
) { }
"@

New-File "$base\productos\application\dto\ProductoResponseDto.java" @"
package com.techmovil.productos.application.dto;

import java.math.BigDecimal;

/** DTO de salida para producto con IGV calculado */
public record ProductoResponseDto(
    Long       id,
    String     sku,
    String     nombre,
    String     marca,
    String     categoria,
    String     modelo,
    String     color,
    String     capacidad,
    BigDecimal precioVenta,
    BigDecimal igvMonto,
    BigDecimal precioConIgv,
    BigDecimal stockActual,
    BigDecimal stockMinimo,
    boolean    stockCritico,
    boolean    activo
) { }
"@

New-File "$base\productos\application\dto\FiltroProductoDto.java" @"
package com.techmovil.productos.application.dto;

import java.math.BigDecimal;

/** Filtros para busqueda de productos (RF-03, RF-10) */
public record FiltroProductoDto(
    Long       marcaId,
    Long       categoriaId,
    String     modelo,
    BigDecimal precioMin,
    BigDecimal precioMax,
    Boolean    soloConStock,
    Boolean    soloCriticos,
    Boolean    activo
) { }
"@

New-File "$base\productos\infrastructure\persistence\ProductoEntity.java" @"
package com.techmovil.productos.infrastructure.persistence;

// TODO: @Entity @Table(name = "productos")
// Mapea tabla productos de la BD TECHMOVIL
public class ProductoEntity { }
"@

New-File "$base\productos\infrastructure\persistence\ProductoSpecification.java" @"
package com.techmovil.productos.infrastructure.persistence;

// TODO: Specification<ProductoEntity> para filtros dinamicos
// RF-03: filtrar por marca, modelo, precio, disponibilidad
// Ejemplo:
//   Specification<ProductoEntity> spec = where(porMarca(marcaId))
//       .and(porPrecioEntre(min, max))
//       .and(conStockDisponible());
public class ProductoSpecification { }
"@

New-File "$base\productos\infrastructure\storage\ImagenStorageServicio.java" @"
package com.techmovil.productos.infrastructure.storage;

// TODO: RF-05 - Carga y almacenamiento de imagenes
// Guarda archivos en la ruta configurada: app.imagenes.ruta (D:/celulares/imagenes/)
// Retorna la URL/ruta relativa para guardar en BD
public class ImagenStorageServicio { }
"@

New-File "$base\productos\presentation\ProductoController.java" @"
package com.techmovil.productos.presentation;

// TODO: @RestController @RequestMapping("/api/productos")
// GET    /                -> listar con filtros (FiltroProductoDto)
// GET    /{id}            -> obtener por id
// POST   /                -> crear producto (RF-02)
// PUT    /{id}            -> actualizar producto (RF-02)
// DELETE /{id}            -> desactivar producto (RF-02)
// POST   /{id}/imagenes   -> subir imagen (RF-05)
public class ProductoController { }
"@

# Tests productos (TDD)
New-Dir "$test\productos\domain"
New-Dir "$test\productos\application"

New-File "$test\productos\domain\ProductoTest.java" @"
package com.techmovil.productos.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.techmovil.shared.exception.StockInsuficienteException;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD - Pruebas unitarias de la entidad Producto
 * RF-07, RF-08, RF-09: logica de stock y alertas criticas
 */
@DisplayName("Producto - Reglas de negocio de stock")
class ProductoTest {

    private Producto producto;

    @BeforeEach
    void setUp() {
        // TODO: inicializar Producto con builder o constructor
        // producto = new Producto(...)
        //   .stockActual(10)
        //   .stockMinimo(3)
    }

    @Test
    @DisplayName("RF-09: debe detectar stock critico cuando stock <= stockMinimo")
    void debeDetectarStockCritico() {
        // GIVEN: producto con stock=2, stockMinimo=3
        // WHEN:  se llama tieneStockCritico()
        // THEN:  retorna true
        // TODO: implementar
        assertTrue(true, "Placeholder - implementar con instancia real");
    }

    @Test
    @DisplayName("RF-07: debe descontar stock correctamente al vender")
    void debeDescontarStockAlVender() {
        // GIVEN: producto con stock=10
        // WHEN:  descontarStock(3)
        // THEN:  stockActual = 7
        assertTrue(true, "Placeholder - implementar");
    }

    @Test
    @DisplayName("RF-07: debe lanzar excepcion si stock es insuficiente")
    void debeLanzarExcepcionSiStockInsuficiente() {
        // GIVEN: producto con stock=2
        // WHEN:  descontarStock(5)
        // THEN:  lanza StockInsuficienteException
        // assertThrows(StockInsuficienteException.class, () -> producto.descontarStock(new BigDecimal("5")));
        assertTrue(true, "Placeholder - implementar");
    }

    @Test
    @DisplayName("RF-08: debe agregar stock correctamente en ingreso de mercaderia")
    void debeAgregarStockEnIngreso() {
        // GIVEN: producto con stock=5
        // WHEN:  agregarStock(10)
        // THEN:  stockActual = 15
        assertTrue(true, "Placeholder - implementar");
    }
}
"@

New-File "$test\productos\application\IgvCalculadoraTest.java" @"
package com.techmovil.productos.application;

import com.techmovil.shared.util.IgvCalculadora;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD - Pruebas unitarias del calculo de IGV 18%
 * RF-06: El sistema debe calcular el 18% de IGV con precision
 */
@DisplayName("IgvCalculadora - Precision aritmetica del 18% IGV")
class IgvCalculadoraTest {

    @Test
    @DisplayName("Debe calcular IGV correcto para precio base de 100.00")
    void debeCalcularIgvDe100() {
        BigDecimal base = new BigDecimal("100.00");
        BigDecimal igv  = IgvCalculadora.calcularIgv(base);
        assertEquals(new BigDecimal("18.00"), igv);
    }

    @Test
    @DisplayName("Debe calcular total con IGV correcto para precio base de 100.00")
    void debeCalcularTotalCon100() {
        BigDecimal base  = new BigDecimal("100.00");
        BigDecimal total = IgvCalculadora.calcularTotal(base);
        assertEquals(new BigDecimal("118.00"), total);
    }

    @Test
    @DisplayName("Debe calcular IGV correcto para precio de 4999.00 (iPhone)")
    void debeCalcularIgvParaIphone() {
        BigDecimal base  = new BigDecimal("4999.00");
        BigDecimal igv   = IgvCalculadora.calcularIgv(base);
        BigDecimal total = IgvCalculadora.calcularTotal(base);
        assertEquals(new BigDecimal("899.82"), igv);
        assertEquals(new BigDecimal("5898.82"), total);
    }

    @Test
    @DisplayName("Debe extraer base imponible de un precio que ya incluye IGV")
    void debeExtraerBaseDePrecoConIgv() {
        BigDecimal precioConIgv = new BigDecimal("118.00");
        BigDecimal base = IgvCalculadora.extraerBase(precioConIgv);
        assertEquals(new BigDecimal("100.00"), base);
    }

    @Test
    @DisplayName("No debe usar FLOAT - debe usar BigDecimal para evitar errores de precision")
    void noDebeUsarFloatParaCalculos() {
        // Verifica que el resultado es exacto (no tiene errores de punto flotante)
        BigDecimal resultado = IgvCalculadora.calcularIgv(new BigDecimal("0.01"));
        assertNotNull(resultado);
        assertTrue(resultado.scale() == 2, "Debe tener exactamente 2 decimales");
    }
}
"@

# ==============================================================================
# MODULO: inventario
# ==============================================================================
Write-Host "  [+] Creando modulo: inventario" -ForegroundColor Yellow

$dirs_inv = @(
    "$base\inventario\domain",
    "$base\inventario\application\dto",
    "$base\inventario\application\usecase",
    "$base\inventario\infrastructure\persistence",
    "$base\inventario\presentation"
)
foreach ($d in $dirs_inv) { New-Dir $d }

New-File "$base\inventario\domain\MovimientoInventario.java" @"
package com.techmovil.inventario.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para movimientos de inventario.
 * RF-07: actualizacion automatica de stock.
 * RF-08: registro de entradas de mercaderia.
 * RF-12: auditoria de cambios con fecha, hora y usuario.
 */
public class MovimientoInventario {
    private Long              id;
    private Long              productoId;
    private TipoMovimiento    tipo;
    private BigDecimal        cantidad;
    private BigDecimal        stockAnterior;
    private BigDecimal        stockNuevo;
    private String            motivo;
    private String            referencia;
    private Long              usuarioId;
    private LocalDateTime     fechaMovimiento;

    public enum TipoMovimiento {
        INGRESO, VENTA, AJUSTE, DEVOLUCION, MERMA, TRASLADO
    }
}
"@

New-File "$base\inventario\application\usecase\RegistrarIngresoStockUseCase.java" @"
package com.techmovil.inventario.application.usecase;

// TODO: RF-08 - Registrar entrada de nueva mercaderia al almacen
public interface RegistrarIngresoStockUseCase { }
"@

New-File "$base\inventario\application\usecase\ConsultarStockCriticoUseCase.java" @"
package com.techmovil.inventario.application.usecase;

// TODO: RF-09 - Obtener lista de productos con stock critico (<3 unidades)
public interface ConsultarStockCriticoUseCase { }
"@

New-File "$base\inventario\presentation\InventarioController.java" @"
package com.techmovil.inventario.presentation;

// TODO: @RestController @RequestMapping("/api/inventario")
// GET  /stock-critico        -> RF-09 productos con alerta
// GET  /movimientos          -> historial de movimientos
// POST /ingresos             -> RF-08 nuevo ingreso de stock
// GET  /alertas              -> alertas activas
public class InventarioController { }
"@

# Tests inventario
New-Dir "$test\inventario\application"
New-File "$test\inventario\application\RegistrarIngresoStockUseCaseTest.java" @"
package com.techmovil.inventario.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD - Pruebas del caso de uso de ingreso de stock
 * RF-08: Registrar entrada de nueva mercaderia
 */
@DisplayName("RegistrarIngresoStock - Casos de prueba")
class RegistrarIngresoStockUseCaseTest {

    @Test
    @DisplayName("RF-08: debe actualizar stock y registrar movimiento tipo INGRESO")
    void debeActualizarStockYRegistrarMovimiento() {
        // TODO: implementar con Mockito
        assertTrue(true, "Placeholder");
    }

    @Test
    @DisplayName("RF-12: debe registrar auditoria con usuario y fecha al ingresar stock")
    void debeRegistrarAuditoria() {
        // TODO: verificar que se guarda en tabla auditoria
        assertTrue(true, "Placeholder");
    }
}
"@

# ==============================================================================
# MODULO: ventas
# ==============================================================================
Write-Host "  [+] Creando modulo: ventas" -ForegroundColor Yellow

$dirs_ventas = @(
    "$base\ventas\domain",
    "$base\ventas\application\dto",
    "$base\ventas\application\usecase",
    "$base\ventas\infrastructure\persistence",
    "$base\ventas\presentation"
)
foreach ($d in $dirs_ventas) { New-Dir $d }

New-File "$base\ventas\domain\Venta.java" @"
package com.techmovil.ventas.domain;

import com.techmovil.shared.util.IgvCalculadora;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad de dominio Venta.
 * RF-04: registro de ventas de un solo equipo o multiples accesorios.
 * RF-06: genera comprobante con desglose de IGV.
 */
public class Venta {
    private Long              id;
    private String            numeroComprobante;
    private TipoComprobante   tipoComprobante;
    private Long              usuarioId;
    private Long              clienteId;
    private List<VentaItem>   items;
    private BigDecimal        subtotal;
    private BigDecimal        descuento;
    private BigDecimal        igvTotal;
    private BigDecimal        total;
    private MetodoPago        metodoPago;
    private EstadoVenta       estado;
    private LocalDateTime     fechaVenta;

    public enum TipoComprobante { BOLETA, FACTURA, NOTA_VENTA, TICKET }
    public enum MetodoPago { EFECTIVO, TARJETA_DEBITO, TARJETA_CREDITO, TRANSFERENCIA, YAPE, PLIN, MIXTO }
    public enum EstadoVenta { EMITIDA, ANULADA, PENDIENTE }

    /** RF-06: calcula totales con IGV 18% para todos los items */
    public void calcularTotales() {
        this.subtotal = items.stream()
            .map(VentaItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal base = subtotal.subtract(descuento != null ? descuento : BigDecimal.ZERO);
        this.igvTotal   = IgvCalculadora.calcularIgv(base);
        this.total      = base.add(igvTotal);
    }
}
"@

New-File "$base\ventas\domain\VentaItem.java" @"
package com.techmovil.ventas.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Item de una venta (producto + cantidad + precio) */
public class VentaItem {
    private Long       productoId;
    private Long       activoId;    // IMEI si es rastreable
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoItem;
    private BigDecimal igvPorcentaje;

    public BigDecimal getSubtotal() {
        BigDecimal bruto = precioUnitario.multiply(cantidad);
        BigDecimal desc  = descuentoItem != null ? descuentoItem : BigDecimal.ZERO;
        return bruto.subtract(desc).setScale(2, RoundingMode.HALF_UP);
    }
}
"@

New-File "$base\ventas\application\usecase\RegistrarVentaUseCase.java" @"
package com.techmovil.ventas.application.usecase;

// TODO: RF-04 - Registrar venta (un equipo o multiples accesorios)
// Debe:
//   1. Validar stock disponible (RF-07)
//   2. Calcular IGV 18% (RF-06)
//   3. Descontar stock automaticamente (RF-07)
//   4. Generar numero de comprobante
//   5. Registrar movimiento de inventario (RF-12)
public interface RegistrarVentaUseCase { }
"@

New-File "$base\ventas\application\dto\VentaRequestDto.java" @"
package com.techmovil.ventas.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/** DTO de entrada para registrar una venta */
public record VentaRequestDto(
    String             tipoComprobante,
    Long               clienteId,
    @NotEmpty List<VentaItemDto> items,
    BigDecimal         descuento,
    @NotBlank String   metodoPago,
    BigDecimal         efectivoRecibido,
    String             observaciones
) { }
"@

New-File "$base\ventas\application\dto\VentaItemDto.java" @"
package com.techmovil.ventas.application.dto;

import java.math.BigDecimal;

public record VentaItemDto(
    Long       productoId,
    Long       activoId,
    BigDecimal cantidad,
    BigDecimal precioUnitario,
    BigDecimal descuento
) { }
"@

New-File "$base\ventas\application\dto\VentaResponseDto.java" @"
package com.techmovil.ventas.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** DTO de respuesta - comprobante de venta con IGV desglosado (RF-06) */
public record VentaResponseDto(
    Long              id,
    String            numeroComprobante,
    String            tipoComprobante,
    LocalDateTime     fechaVenta,
    String            vendedor,
    String            cliente,
    List<VentaItemDto> items,
    BigDecimal        subtotal,
    BigDecimal        descuento,
    BigDecimal        igvPorcentaje,
    BigDecimal        igvTotal,
    BigDecimal        total,
    String            metodoPago,
    BigDecimal        efectivoRecibido,
    BigDecimal        cambio,
    String            estado
) { }
"@

New-File "$base\ventas\presentation\VentaController.java" @"
package com.techmovil.ventas.presentation;

// TODO: @RestController @RequestMapping("/api/ventas")
// POST /          -> registrar venta (RF-04)
// GET  /          -> listar ventas con filtros (RF-10)
// GET  /{id}      -> obtener comprobante (RF-06)
// PUT  /{id}/anular -> anular venta
public class VentaController { }
"@

# Tests ventas (TDD)
New-Dir "$test\ventas\domain"
New-File "$test\ventas\domain\VentaTest.java" @"
package com.techmovil.ventas.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD - Pruebas unitarias de la entidad Venta
 * RF-04, RF-06: calculo correcto del IGV en ventas
 */
@DisplayName("Venta - Calculo de IGV y totales")
class VentaTest {

    @Test
    @DisplayName("RF-06: debe calcular IGV 18% correctamente para venta de 1 celular")
    void debeCalcularIgvParaVentaDeUnCelular() {
        // GIVEN: 1 iPhone 15 Pro Max a S/ 4999.00
        // WHEN:  se calcula el total
        // THEN:  IGV = 899.82, Total = 5898.82
        // TODO: implementar
        assertTrue(true, "Placeholder - implementar");
    }

    @Test
    @DisplayName("RF-04: debe calcular total correcto para venta con multiples accesorios")
    void debeCalcularTotalParaVentaMultiple() {
        // GIVEN: cargador S/89.00 + funda S/35.00
        // WHEN:  calcularTotales()
        // THEN:  subtotal=124.00, IGV=22.32, total=146.32
        assertTrue(true, "Placeholder - implementar");
    }
}
"@

# ==============================================================================
# MODULO: reportes
# ==============================================================================
Write-Host "  [+] Creando modulo: reportes" -ForegroundColor Yellow

$dirs_rep = @(
    "$base\reportes\application\dto",
    "$base\reportes\application\usecase",
    "$base\reportes\infrastructure",
    "$base\reportes\presentation"
)
foreach ($d in $dirs_rep) { New-Dir $d }

New-File "$base\reportes\application\usecase\GenerarReporteVentasUseCase.java" @"
package com.techmovil.reportes.application.usecase;

// TODO: RF-10 - Reportes de ventas filtrados por fecha, marca y categoria
// Parametros: fechaInicio, fechaFin, marcaId, categoriaId
public interface GenerarReporteVentasUseCase { }
"@

New-File "$base\reportes\application\dto\FiltroReporteDto.java" @"
package com.techmovil.reportes.application.dto;

import java.time.LocalDate;

/** Filtros para generacion de reportes (RF-10) */
public record FiltroReporteDto(
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Long      marcaId,
    Long      categoriaId,
    Long      almacenId
) { }
"@

New-File "$base\reportes\presentation\ReporteController.java" @"
package com.techmovil.reportes.presentation;

// TODO: @RestController @RequestMapping("/api/reportes")
// GET /ventas              -> RF-10 reporte ventas filtrado
// GET /stock               -> reporte de stock actual
// GET /stock-critico        -> RF-09 productos con alerta
// GET /movimientos          -> historial de movimientos
public class ReporteController { }
"@

# ==============================================================================
# CLASE PRINCIPAL (ya existe, no sobreescribir)
# ==============================================================================
Write-Host "  [+] Verificando clase principal TechmovilApplication.java" -ForegroundColor Yellow

if (-not (Test-Path "$base\TechmovilApplication.java")) {
    New-File "$base\TechmovilApplication.java" @"
package com.techmovil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechmovilApplication {
    public static void main(String[] args) {
        SpringApplication.run(TechmovilApplication.class, args);
    }
}
"@
}

# ==============================================================================
# RESUMEN FINAL
# ==============================================================================
Write-Host ""
Write-Host "========================================================" -ForegroundColor Green
Write-Host "  ESTRUCTURA GENERADA EXITOSAMENTE                      " -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
Write-Host ""
Write-Host "  Modulos creados:" -ForegroundColor White
Write-Host "    shared     -> config, security (JWT), exceptions, util (IGV)" -ForegroundColor Gray
Write-Host "    auth       -> login, JWT, bloqueo por intentos (RF-01)" -ForegroundColor Gray
Write-Host "    usuarios   -> CRUD usuarios + roles (RF-11)" -ForegroundColor Gray
Write-Host "    productos  -> catalogo, imagenes, specifications (RF-02,03,05)" -ForegroundColor Gray
Write-Host "    inventario -> stock, movimientos, alertas (RF-07,08,09)" -ForegroundColor Gray
Write-Host "    ventas     -> venta multi-item, IGV 18% (RF-04,06,07)" -ForegroundColor Gray
Write-Host "    reportes   -> filtros por fecha/marca/categoria (RF-10)" -ForegroundColor Gray
Write-Host ""
Write-Host "  Perfiles configurados: dev | test | prod" -ForegroundColor White
Write-Host "  Tests TDD listos para implementar con JUnit 5 + Mockito" -ForegroundColor White
Write-Host ""
Write-Host "  Siguiente paso: agregar dependencias en pom.xml" -ForegroundColor Yellow
Write-Host "    spring-boot-starter-security" -ForegroundColor Gray
Write-Host "    spring-boot-starter-data-jpa" -ForegroundColor Gray
Write-Host "    spring-boot-starter-validation" -ForegroundColor Gray
Write-Host "    mysql-connector-j" -ForegroundColor Gray
Write-Host "    jjwt-api + jjwt-impl + jjwt-jackson" -ForegroundColor Gray
Write-Host "    springdoc-openapi-starter-webmvc-ui" -ForegroundColor Gray
Write-Host "    h2 (scope test)" -ForegroundColor Gray
Write-Host ""
Write-Host "  Ejecutar pruebas: mvn test" -ForegroundColor Yellow
Write-Host "  Levantar con perfil dev: mvn spring-boot:run -Dspring-boot.run.profiles=dev" -ForegroundColor Yellow
Write-Host ""