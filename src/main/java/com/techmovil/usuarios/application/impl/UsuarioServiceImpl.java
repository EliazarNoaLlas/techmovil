package com.techmovil.usuarios.application.impl;

import com.techmovil.shared.exception.ReglaDeNegocioException;
import com.techmovil.usuarios.application.dto.UsuarioRequestDto;
import com.techmovil.usuarios.application.dto.UsuarioResponseDto;
import com.techmovil.usuarios.application.usecase.CrearUsuarioUseCase;
import com.techmovil.usuarios.domain.Usuario;
import com.techmovil.usuarios.domain.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements 
        CrearUsuarioUseCase,
        com.techmovil.usuarios.application.usecase.ListarUsuariosUseCase,
        com.techmovil.usuarios.application.usecase.ActualizarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public java.util.List<UsuarioResponseDto> ejecutar() {
        return usuarioRepository.listarTodo().stream()
                .map(u -> new UsuarioResponseDto(
                        u.getId(), u.getUsername(), u.getNombres(), u.getApellidos(),
                        u.getEmail(), u.isActivo(), u.estaBloqueado(), u.getRoles()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponseDto ejecutar(Long id, UsuarioRequestDto dto) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDeNegocioException("Usuario no encontrado"));

        usuario.setNombres(dto.nombres());
        usuario.setApellidos(dto.apellidos());
        usuario.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.password()));
        }
        usuario.setRoles(dto.roles());

        Usuario guardado = usuarioRepository.guardar(usuario);
        return mapToResponse(guardado);
    }

    private UsuarioResponseDto mapToResponse(Usuario u) {
        return new UsuarioResponseDto(
                u.getId(), u.getUsername(), u.getNombres(), u.getApellidos(),
                u.getEmail(), u.isActivo(), u.estaBloqueado(), u.getRoles());
    }

    @Override
    @Transactional
    public UsuarioResponseDto ejecutar(UsuarioRequestDto dto) {
        if (usuarioRepository.existePorUsername(dto.username())) {
            throw new ReglaDeNegocioException("El nombre de usuario ya está en uso");
        }

        Usuario usuario = Usuario.builder()
                .username(dto.username())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .email(dto.email())
                .activo(true)
                .intentosFallidos(0)
                .bloqueado(false)
                .roles(dto.roles())
                .build();

        // Note: Empresa association should be done in a real scenario by resolving the EmpresaEntity
        // but since we are focusing on Domain logic, we assume it's attached via repository adapter.
        
        Usuario guardado = usuarioRepository.guardar(usuario);
        
        return new UsuarioResponseDto(
                guardado.getId(),
                guardado.getUsername(),
                guardado.getNombres(),
                guardado.getApellidos(),
                guardado.getEmail(),
                guardado.isActivo(),
                guardado.estaBloqueado(),
                guardado.getRoles()
        );
    }
}
