package com.techmovil.productos.presentation;

import com.techmovil.productos.application.dto.ProductoRequestDto;
import com.techmovil.productos.application.dto.ProductoResponseDto;
import com.techmovil.productos.application.usecase.CrearProductoUseCase;
import com.techmovil.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final com.techmovil.productos.application.usecase.CrearProductoUseCase crearProductoUseCase;
    private final com.techmovil.productos.application.usecase.BuscarProductosUseCase buscarProductosUseCase;
    private final com.techmovil.productos.application.usecase.ActualizarProductoUseCase actualizarProductoUseCase;
    private final com.techmovil.productos.application.usecase.DesactivarProductoUseCase desactivarProductoUseCase;
    private final com.techmovil.productos.application.usecase.ObtenerProductoUseCase obtenerProductoUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> crearProducto(@Valid @RequestBody ProductoRequestDto request) {
        ProductoResponseDto response = crearProductoUseCase.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Producto creado exitosamente", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponseDto>>> listarProductos(
            @RequestParam(required = false) Long marcaId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String modelo) {
        
        List<ProductoResponseDto> response = buscarProductosUseCase.buscar(marcaId, categoriaId, modelo);
        return ResponseEntity.ok(ApiResponse.ok("Lista de productos", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> obtenerProducto(@PathVariable Long id) {
        ProductoResponseDto response = obtenerProductoUseCase.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Detalle de producto", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDto request) {
        ProductoResponseDto response = actualizarProductoUseCase.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado exitosamente", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> desactivarProducto(@PathVariable Long id) {
        desactivarProductoUseCase.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto desactivado exitosamente", null));
    }
}
