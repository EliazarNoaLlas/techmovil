package com.techmovil.auth.application;

import com.techmovil.auth.application.dto.LoginRequestDto;
import com.techmovil.auth.application.dto.LoginResponseDto;
import com.techmovil.auth.application.impl.LoginUseCaseImpl;
import com.techmovil.auth.infrastructure.security.CustomUserDetails;
import com.techmovil.shared.exception.ReglaDeNegocioException;
import com.techmovil.shared.security.jwt.JwtService;
import com.techmovil.usuarios.domain.Usuario;
import com.techmovil.usuarios.domain.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el Caso de Uso de Login (RF-01).
 * <p>
 * Cubre todos los escenarios de autenticación: éxito, fallo por credenciales,
 * bloqueo por intentos fallidos, y validaciones de seguridad.
 *
 * @author Kevin, Yohan, Roy
 * @version 1.0
 * @since 2026-04-304}
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🧪 LoginUseCase - Pruebas Unitarias de Autenticación (RF-01)")
class LoginUseCaseTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginUseCaseImpl loginUseCase;

    // Datos de prueba constantes
    private static final String USERNAME_VALIDO = "admin@techmovil.com";
    private static final String PASSWORD_VALIDO = "SecurePass123!";
    private static final String NOMBRE_USUARIO = "Kevin";
    private static final String APELLIDO_USUARIO = "Mamani";
    private static final String TOKEN_JWT = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZWNobW92aWwuY29tIn0.test";

    private LoginRequestDto requestValido;
    private Usuario usuarioValido;
    private Set<String> rolesUsuario;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba comunes
        requestValido = new LoginRequestDto(USERNAME_VALIDO, PASSWORD_VALIDO);

        rolesUsuario = new HashSet<>(Arrays.asList("ADMIN", "VENDEDOR"));

        usuarioValido = Usuario.builder()
                .id(1L)
                .username(USERNAME_VALIDO)
                .passwordHash("$2a$10$encryptedPasswordHash")
                .nombres(NOMBRE_USUARIO)
                .apellidos(APELLIDO_USUARIO)
                .email("admin@techmovil.com")
                .roles(rolesUsuario)
                .activo(true)
                .intentosFallidos(0)
                .bloqueado(false)
                .build();
    }

    // ==================== ESCENARIOS DE ÉXITO ====================

    @Nested
    @DisplayName("✅ Escenarios de Autenticación Exitosa")
    class AutenticacionExitosa {

        @Test
        @DisplayName("TC-001: Debe retornar token JWT cuando credenciales son correctas")
        void debeRetornarTokenCuandoCredencialesCorrectas() {
            // GIVEN - Usuario existe y credenciales válidas
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(null);
            when(jwtService.generarToken(any(CustomUserDetails.class)))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Se ejecuta el caso de uso
            LoginResponseDto response = loginUseCase.ejecutar(requestValido);

            // THEN - Se retorna respuesta exitosa con token
            assertAll("Verificar respuesta de login exitoso",
                    () -> assertNotNull(response, "La respuesta no debe ser nula"),
                    () -> assertEquals(TOKEN_JWT, response.token(), "El token JWT debe coincidir"),
                    () -> assertEquals("Bearer", response.tipo(), "El tipo debe ser Bearer"),
                    () -> assertEquals(USERNAME_VALIDO, response.username(), "El username debe coincidir"),
                    () -> assertEquals(NOMBRE_USUARIO + " " + APELLIDO_USUARIO,
                            response.nombreCompleto(), "El nombre completo debe estar formateado"),
                    () -> assertTrue(response.roles().containsAll(rolesUsuario),
                            "Debe contener todos los roles del usuario"),
                    () -> assertEquals(rolesUsuario.size(), response.roles().size(),
                            "La cantidad de roles debe coincidir")
            );

            // Verificar interacciones con dependencias
            verify(usuarioRepository, times(1)).buscarPorUsername(USERNAME_VALIDO);
            verify(authenticationManager, times(1))
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtService, times(1)).generarToken(any(CustomUserDetails.class));
            verify(usuarioRepository, times(1)).guardar(any(Usuario.class));
        }

        @Test
        @DisplayName("TC-002: Debe resetear contador de intentos tras login exitoso")
        void debeResetearContadorIntentosTrasLoginExitoso() {
            // GIVEN - Usuario con intentos fallidos previos
            usuarioValido.setIntentosFallidos(2);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any()))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Login exitoso
            loginUseCase.ejecutar(requestValido);

            // THEN - El contador de intentos se resetea a 0
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).guardar(usuarioCaptor.capture());

            Usuario usuarioGuardado = usuarioCaptor.getValue();
            assertEquals(0, usuarioGuardado.getIntentosFallidos(),
                    "Los intentos fallidos deben resetearse a 0");
            assertFalse(usuarioGuardado.isBloqueado(),
                    "El usuario no debe estar bloqueado");
        }

        @Test
        @DisplayName("TC-003: Debe generar token con claims correctos del usuario")
        void debeGenerarTokenConClaimsCorrectos() {
            // GIVEN - Usuario autenticado
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any(CustomUserDetails.class)))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Se ejecuta login
            loginUseCase.ejecutar(requestValido);

            // THEN - El UserDetails pasado al JWT contiene la info correcta
            ArgumentCaptor<CustomUserDetails> userDetailsCaptor =
                    ArgumentCaptor.forClass(CustomUserDetails.class);
            verify(jwtService).generarToken(userDetailsCaptor.capture());

            CustomUserDetails userDetails = userDetailsCaptor.getValue();
            assertAll("Verificar UserDetails",
                    () -> assertEquals(USERNAME_VALIDO, userDetails.getUsername()),
                    () -> assertTrue(userDetails.isEnabled()),
                    () -> assertTrue(userDetails.isAccountNonLocked()),
                    () -> assertTrue(userDetails.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))),
                    () -> assertTrue(userDetails.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_VENDEDOR")))
            );
        }
    }

    // ==================== ESCENARIOS DE FALLO ====================

    @Nested
    @DisplayName("❌ Escenarios de Credenciales Inválidas")
    class CredencialesInvalidas {

        @Test
        @DisplayName("TC-004: Debe lanzar excepción cuando contraseña es incorrecta")
        void debeLanzarExcepcionCuandoContrasenaIncorrecta() {
            // GIVEN - Usuario existe pero contraseña incorrecta
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // WHEN & THEN - Debe lanzar excepción de negocio
            ReglaDeNegocioException exception = assertThrows(
                    ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido),
                    "Debe lanzar ReglaDeNegocioException por credenciales incorrectas"
            );

            assertEquals("Credenciales incorrectas", exception.getMessage(),
                    "El mensaje debe ser genérico por seguridad");
            verify(usuarioRepository, times(1)).guardar(any(Usuario.class));
            verify(jwtService, never()).generarToken(any());
        }

        @Test
        @DisplayName("TC-005: Debe lanzar excepción cuando usuario no existe")
        void debeLanzarExcepcionCuandoUsuarioNoExiste() {
            // GIVEN - Usuario no registrado
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.empty());

            // WHEN & THEN - Debe lanzar excepción
            ReglaDeNegocioException exception = assertThrows(
                    ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido)
            );

            assertEquals("Credenciales incorrectas", exception.getMessage(),
                    "El mensaje no debe revelar que el usuario no existe");
            verify(authenticationManager, never()).authenticate(any());
            verify(jwtService, never()).generarToken(any());
        }

        @Test
        @DisplayName("TC-006: Debe incrementar contador de intentos fallidos")
        void debeIncrementarContadorIntentosFallidos() {
            // GIVEN - Usuario existe, primer intento fallido
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // WHEN - Intento fallido
            assertThrows(ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido));

            // THEN - El contador se incrementa en 1
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).guardar(usuarioCaptor.capture());

            Usuario usuarioGuardado = usuarioCaptor.getValue();
            assertEquals(1, usuarioGuardado.getIntentosFallidos(),
                    "Los intentos fallidos deben incrementarse en 1");
        }

        @Test
        @DisplayName("TC-007: Debe mantener historial de intentos fallidos consecutivos")
        void debeMantenerHistorialIntentosFallidosConsecutivos() {
            // GIVEN - Usuario con 1 intento fallido previo
            usuarioValido.setIntentosFallidos(1);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // WHEN - Segundo intento fallido
            assertThrows(ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido));

            // THEN - El contador debe ser 2
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).guardar(usuarioCaptor.capture());

            assertEquals(2, usuarioCaptor.getValue().getIntentosFallidos(),
                    "El contador debe acumular intentos fallidos consecutivos");
        }
    }

    // ==================== ESCENARIOS DE BLOQUEO ====================

    @Nested
    @DisplayName("🔒 Escenarios de Bloqueo de Seguridad (RF-01)")
    class BloqueoSeguridad {

        @Test
        @DisplayName("TC-008: Debe bloquear usuario tras alcanzar máximo de intentos fallidos")
        void debeBloquearUsuarioTrasMaximoIntentosFallidos() {
            // GIVEN - Usuario con 2 intentos fallidos
            usuarioValido.setIntentosFallidos(2);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // WHEN - Tercer intento fallido (alcanza el límite)
            assertThrows(ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido));

            // THEN - Usuario queda bloqueado
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).guardar(usuarioCaptor.capture());

            Usuario usuarioGuardado = usuarioCaptor.getValue();
            assertAll("Verificar estado de bloqueo",
                    () -> assertEquals(3, usuarioGuardado.getIntentosFallidos(),
                            "Debe registrar el tercer intento fallido"),
                    () -> assertTrue(usuarioGuardado.estaBloqueado(),
                            "El usuario debe quedar bloqueado")
            );
        }

        @Test
        @DisplayName("TC-009: Debe rechazar autenticación de usuario bloqueado")
        void debeRechazarAutenticacionUsuarioBloqueado() {
            // GIVEN - Usuario ya está bloqueado
            usuarioValido.setIntentosFallidos(3);
            usuarioValido.setBloqueado(true);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));

            // WHEN & THEN - Debe lanzar excepción por bloqueo
            ReglaDeNegocioException exception = assertThrows(
                    ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido)
            );

            assertEquals("Usuario bloqueado por múltiples intentos fallidos",
                    exception.getMessage(),
                    "Debe informar claramente el motivo de bloqueo");
            verify(authenticationManager, never()).authenticate(any());
            verify(jwtService, never()).generarToken(any());
        }

        @Test
        @DisplayName("TC-010: No debe verificar credenciales si usuario está bloqueado")
        void noDebeVerificarCredencialesSiUsuarioBloqueado() {
            // GIVEN - Usuario bloqueado con credenciales potencialmente correctas
            usuarioValido.setIntentosFallidos(3);
            usuarioValido.setBloqueado(true);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));

            // WHEN - Intento de login
            assertThrows(ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido));

            // THEN - No debe llegar a verificar la contraseña
            verify(authenticationManager, never()).authenticate(any());
            verify(jwtService, never()).generarToken(any());
            verify(usuarioRepository, never()).guardar(any(Usuario.class));
        }
    }

    // ==================== ESCENARIOS DE USUARIO INACTIVO ====================

    @Nested
    @DisplayName("🚫 Escenarios de Usuario Desactivado")
    class UsuarioInactivo {

        @Test
        @DisplayName("TC-011: Debe rechazar autenticación de usuario desactivado")
        void debeRechazarAutenticacionUsuarioDesactivado() {
            // GIVEN - Usuario existe pero está inactivo
            usuarioValido.setActivo(false);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("User is disabled"));

            // WHEN & THEN - Debe lanzar excepción
            ReglaDeNegocioException exception = assertThrows(
                    ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido)
            );

            assertEquals("Credenciales incorrectas", exception.getMessage(),
                    "El mensaje debe ser genérico incluso para usuarios desactivados");
        }

        @Test
        @DisplayName("TC-012: Debe permitir intentos fallidos a usuarios desactivados")
        void debePermitirIntentosFallidosUsuariosDesactivados() {
            // GIVEN - Usuario inactivo
            usuarioValido.setActivo(false);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("User is disabled"));

            // WHEN - Intento de login
            assertThrows(ReglaDeNegocioException.class,
                    () -> loginUseCase.ejecutar(requestValido));

            // THEN - Aún incrementa el contador
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).guardar(usuarioCaptor.capture());

            assertEquals(1, usuarioCaptor.getValue().getIntentosFallidos(),
                    "Debe registrar el intento fallido aunque esté inactivo");
        }
    }

    // ==================== ESCENARIOS DE VALIDACIÓN DE DATOS ====================

    @Nested
    @DisplayName("📝 Escenarios de Validación de Entrada")
    class ValidacionEntrada {

        @Test
        @DisplayName("TC-013: Debe manejar correctamente roles múltiples")
        void debeManejarCorrectamenteRolesMultiples() {
            // GIVEN - Usuario con múltiples roles
            Set<String> rolesMultiples = new HashSet<>(Arrays.asList("ADMIN", "VENDEDOR", "SUPERVISOR"));
            usuarioValido.setRoles(rolesMultiples);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any()))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Login exitoso
            LoginResponseDto response = loginUseCase.ejecutar(requestValido);

            // THEN - Todos los roles están presentes
            assertAll("Verificar roles en respuesta",
                    () -> assertEquals(3, response.roles().size(),
                            "Debe tener todos los roles"),
                    () -> assertTrue(response.roles().contains("ADMIN")),
                    () -> assertTrue(response.roles().contains("VENDEDOR")),
                    () -> assertTrue(response.roles().contains("SUPERVISOR"))
            );
        }

        @Test
        @DisplayName("TC-014: Debe formatear correctamente nombre completo")
        void debeFormatearCorrectamenteNombreCompleto() {
            // GIVEN - Usuario con nombres compuestos
            usuarioValido.setNombres("Juan Carlos");
            usuarioValido.setApellidos("Pérez García");
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any()))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Login exitoso
            LoginResponseDto response = loginUseCase.ejecutar(requestValido);

            // THEN - Nombre completo bien formateado
            assertEquals("Juan Carlos Pérez García", response.nombreCompleto(),
                    "Debe unir nombres y apellidos con espacio");
            assertNotEquals("Juan Carlos" + " " + "Pérez García" + " ",
                    response.nombreCompleto(),
                    "No debe tener espacios extras");
        }

        @Test
        @DisplayName("TC-015: Debe preservar roles vacíos si el usuario no tiene roles")
        void debePreservarRolesVacios() {
            // GIVEN - Usuario sin roles
            usuarioValido.setRoles(new HashSet<>());
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any()))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Login exitoso
            LoginResponseDto response = loginUseCase.ejecutar(requestValido);

            // THEN - Lista de roles vacía
            assertNotNull(response.roles(), "La lista de roles no debe ser null");
            assertTrue(response.roles().isEmpty(),
                    "Debe retornar lista vacía si no tiene roles");
        }
    }

    // ==================== ESCENARIOS DE INTEGRIDAD ====================

    @Nested
    @DisplayName("🔐 Escenarios de Integridad y Seguridad")
    class IntegridadSeguridad {

        @Test
        @DisplayName("TC-016: Debe usar AuthenticationManager correctamente")
        void debeUsarAuthenticationManagerCorrectamente() {
            // GIVEN - Usuario válido
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any()))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Login
            loginUseCase.ejecutar(requestValido);

            // THEN - Se pasa el token de autenticación correcto
            ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(authCaptor.capture());

            UsernamePasswordAuthenticationToken authToken = authCaptor.getValue();
            assertAll("Verificar token de autenticación",
                    () -> assertEquals(USERNAME_VALIDO, authToken.getPrincipal(),
                            "El principal debe ser el username"),
                    () -> assertEquals(PASSWORD_VALIDO, authToken.getCredentials(),
                            "Las credenciales deben ser la contraseña")
            );
        }

        @Test
        @DisplayName("TC-017: Debe manejar excepciones inesperadas del AuthenticationManager")
        void debeManejarExcepcionesInesperadas() {
            // GIVEN - Error inesperado en autenticación
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new RuntimeException("Error interno inesperado"));

            // WHEN & THEN - Debe propagar la excepción
            assertThrows(RuntimeException.class,
                    () -> loginUseCase.ejecutar(requestValido),
                    "Debe propagar excepciones no controladas");

            // Verificar que no se genera token
            verify(jwtService, never()).generarToken(any());
        }

        @Test
        @DisplayName("TC-018: Debe verificar que el repositorio se llama solo una vez")
        void debeVerificarRepositorioLlamadoSoloUnaVez() {
            // GIVEN - Usuario válido
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any()))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Login
            loginUseCase.ejecutar(requestValido);

            // THEN - Buscar usuario solo una vez (eficiencia)
            verify(usuarioRepository, times(1)).buscarPorUsername(anyString());
        }
    }

    // ==================== PRUEBAS PARAMETRIZADAS ====================

    @Nested
    @DisplayName("📊 Pruebas Parametrizadas de Límites")
    class PruebasParametrizadas {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2})
        @DisplayName("TC-019: Debe permitir login con intentos previos mientras no alcance el máximo")
        void debePermitirLoginConIntentosPrevios(int intentosPrevios) {
            // GIVEN - Usuario con intentos pero no bloqueado
            usuarioValido.setIntentosFallidos(intentosPrevios);
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any()))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Login exitoso
            LoginResponseDto response = loginUseCase.ejecutar(requestValido);

            // THEN - Debe permitir el acceso y resetear
            assertNotNull(response);
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).guardar(usuarioCaptor.capture());
            assertEquals(0, usuarioCaptor.getValue().getIntentosFallidos(),
                    "Debe resetear contador independientemente de intentos previos");
        }

        @ParameterizedTest
        @ValueSource(strings = {"ADMIN", "VENDEDOR", "SUPERVISOR"})
        @DisplayName("TC-020: Debe manejar diferentes roles individuales")
        void debeManejarDiferentesRolesIndividuales(String rol) {
            // GIVEN - Usuario con un solo rol
            usuarioValido.setRoles(Set.of(rol));
            when(usuarioRepository.buscarPorUsername(USERNAME_VALIDO))
                    .thenReturn(Optional.of(usuarioValido));
            when(authenticationManager.authenticate(any()))
                    .thenReturn(null);
            when(jwtService.generarToken(any()))
                    .thenReturn(TOKEN_JWT);

            // WHEN - Login
            LoginResponseDto response = loginUseCase.ejecutar(requestValido);

            // THEN - El rol está presente
            assertTrue(response.roles().contains(rol),
                    "Debe contener el rol: " + rol);
            assertEquals(1, response.roles().size(),
                    "Debe tener exactamente un rol");
        }
    }
}