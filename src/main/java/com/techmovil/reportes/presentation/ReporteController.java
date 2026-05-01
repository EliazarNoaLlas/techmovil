package com.techmovil.reportes.presentation;

import com.techmovil.reportes.application.dto.ReporteVentasDto;
import com.techmovil.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final com.techmovil.reportes.application.usecase.GenerarReporteUseCase generarReporteUseCase;

    @GetMapping("/ventas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<ReporteVentasDto>> getReporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        
        ReporteVentasDto report = generarReporteUseCase.generarVentas(inicio, fin);
        return ResponseEntity.ok(ApiResponse.ok("Reporte generado", report));
    }

    @GetMapping("/stock-critico")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<ApiResponse<List<com.techmovil.productos.application.dto.ProductoResponseDto>>> getStockCritico() {
        List<com.techmovil.productos.application.dto.ProductoResponseDto> list = generarReporteUseCase.generarStockCritico();
        return ResponseEntity.ok(ApiResponse.ok("Lista de stock crítico", list));
    }
}
