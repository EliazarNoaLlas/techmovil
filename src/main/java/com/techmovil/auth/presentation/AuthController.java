package com.techmovil.auth.presentation;

import com.techmovil.auth.application.LoginUseCase;
import com.techmovil.auth.application.dto.LoginRequestDto;
import com.techmovil.auth.application.dto.LoginResponseDto;
import com.techmovil.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = loginUseCase.ejecutar(request);
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // En una implementación stateless JWT pura, el logout se maneja comúnmente del lado del cliente (borrando el token)
        // Opcionalmente se puede invalidar en el backend con una blacklist de tokens
        return ResponseEntity.ok(ApiResponse.ok("Logout exitoso", null));
    }
}
