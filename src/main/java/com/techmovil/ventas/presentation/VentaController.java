package com.techmovil.ventas.presentation;

import com.techmovil.shared.response.ApiResponse;
import com.techmovil.ventas.application.dto.VentaRequestDto;
import com.techmovil.ventas.application.dto.VentaResponseDto;
import com.techmovil.ventas.application.usecase.RegistrarVentaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final RegistrarVentaUseCase registrarVentaUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<ApiResponse<VentaResponseDto>> registrarVenta(@Valid @RequestBody VentaRequestDto request) {
        VentaResponseDto response = registrarVentaUseCase.ejecutar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Venta registrada exitosamente", response));
    }
}
