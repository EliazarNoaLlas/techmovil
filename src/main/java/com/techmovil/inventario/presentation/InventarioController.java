package com.techmovil.inventario.presentation;

import com.techmovil.inventario.application.dto.MovimientoRequestDto;
import com.techmovil.inventario.application.dto.MovimientoResponseDto;
import com.techmovil.inventario.application.usecase.RegistrarMovimientoUseCase;
import com.techmovil.shared.response.ApiResponse;
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
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;

    @PostMapping("/movimientos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<ApiResponse<MovimientoResponseDto>> registrarMovimiento(@Valid @RequestBody MovimientoRequestDto request) {
        MovimientoResponseDto response = registrarMovimientoUseCase.ejecutar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Movimiento registrado exitosamente", response));
    }
}
