package com.techmovil.usuarios.presentation;

import com.techmovil.shared.response.ApiResponse;
import com.techmovil.usuarios.application.dto.UsuarioRequestDto;
import com.techmovil.usuarios.application.dto.UsuarioResponseDto;
import com.techmovil.usuarios.application.usecase.CrearUsuarioUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CrearUsuarioUseCase crearUsuarioUseCase;

    private final com.techmovil.usuarios.application.usecase.ListarUsuariosUseCase listarUsuariosUseCase;
    private final com.techmovil.usuarios.application.usecase.ActualizarUsuarioUseCase actualizarUsuarioUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> crearUsuario(@Valid @RequestBody UsuarioRequestDto request) {
        UsuarioResponseDto response = crearUsuarioUseCase.ejecutar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario creado exitosamente", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<java.util.List<UsuarioResponseDto>>> listarUsuarios() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de usuarios", listarUsuariosUseCase.ejecutar()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDto request) {
        UsuarioResponseDto response = actualizarUsuarioUseCase.ejecutar(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado exitosamente", response));
    }
}
