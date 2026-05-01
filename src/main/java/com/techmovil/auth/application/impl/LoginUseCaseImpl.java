package com.techmovil.auth.application.impl;

import com.techmovil.auth.application.LoginUseCase;
import com.techmovil.auth.application.dto.LoginRequestDto;
import com.techmovil.auth.application.dto.LoginResponseDto;
import com.techmovil.auth.infrastructure.security.CustomUserDetails;
import com.techmovil.shared.exception.ReglaDeNegocioException;
import com.techmovil.shared.security.jwt.JwtService;
import com.techmovil.usuarios.domain.Usuario;
import com.techmovil.usuarios.domain.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public LoginResponseDto ejecutar(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.buscarPorUsername(request.username())
                .orElseThrow(() -> new ReglaDeNegocioException("Credenciales incorrectas"));

        if (usuario.estaBloqueado()) {
            throw new ReglaDeNegocioException("Usuario bloqueado por múltiples intentos fallidos");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            
            usuario.resetearIntentos();
            usuarioRepository.guardar(usuario);

            CustomUserDetails userDetails = new CustomUserDetails(usuario);
            String token = jwtService.generarToken(userDetails);

            return new LoginResponseDto(
                    token,
                    "Bearer",
                    usuario.getUsername(),
                    usuario.getNombres() + " " + usuario.getApellidos(),
                    new ArrayList<>(usuario.getRoles())
            );
        } catch (BadCredentialsException e) {
            usuario.registrarIntentoFallido();
            usuarioRepository.guardar(usuario);
            throw new ReglaDeNegocioException("Credenciales incorrectas");
        }
    }
}
