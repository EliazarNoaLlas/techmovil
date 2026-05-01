package com.techmovil.productos.presentation;

import com.techmovil.productos.application.impl.MaestrosServiceImpl;
import com.techmovil.productos.infrastructure.persistence.MarcaEntity;
import com.techmovil.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MaestrosServiceImpl service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MarcaEntity>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de marcas", service.listarMarcas()));
    }
}
