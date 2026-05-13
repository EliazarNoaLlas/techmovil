package com.techmovil.productos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmovil.productos.application.dto.ProductoRequestDto;
import com.techmovil.productos.infrastructure.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración de los endpoints REST.
 * Valida contratos de API y flujos completos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("🌐 ProductoController - Integración API REST")
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MarcaJpaRepository marcaRepository;

    @Autowired
    private CategoriaJpaRepository categoriaRepository;

    @Autowired
    private ProductoJpaRepository productoRepository;

    private MarcaEntity marcaApple;
    private CategoriaEntity categoriaSmartphone;
    private ProductoRequestDto requestValido;

    @BeforeEach
    void setUp() {
        marcaApple = new MarcaEntity();
        marcaApple.setNombre("Apple");
        marcaApple.setActivo(true);
        marcaApple = marcaRepository.save(marcaApple);

        categoriaSmartphone = new CategoriaEntity();
        categoriaSmartphone.setNombre("Smartphones");
        categoriaSmartphone.setNivel(1);
        categoriaSmartphone.setActivo(true);
        categoriaSmartphone = categoriaRepository.save(categoriaSmartphone);

        requestValido = new ProductoRequestDto(
                "IP15P-128-BLK", "iPhone 15 Pro", marcaApple.getId(),
                categoriaSmartphone.getId(), "A2848", "Negro Titanio", "128GB",
                new BigDecimal("1200.00"), new BigDecimal("1500.00"),
                new BigDecimal("10"), new BigDecimal("3")
        );
    }

    @Nested
    @DisplayName("POST /api/productos - Crear Producto")
    class CrearProductoEndpoint {

        @Test
        @WithMockUser(roles = "ADMINISTRADOR")
        @DisplayName("Debe crear producto con rol ADMINISTRADOR")
        void debeCrearProductoConRolAdmin() throws Exception {
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.sku").value("IP15P-128-BLK"))
                    .andExpect(jsonPath("$.data.nombre").value("iPhone 15 Pro"))
                    .andExpect(jsonPath("$.data.marca").value("Apple"))
                    .andExpect(jsonPath("$.data.precioVenta").value(1500.00))
                    .andExpect(jsonPath("$.data.precioConIgv").value(1770.00))
                    .andExpect(jsonPath("$.data.stockActual").value(10))
                    .andExpect(jsonPath("$.data.activo").value(true));
        }

        @Test
        @WithMockUser(roles = "VENDEDOR")
        @DisplayName("Debe crear producto con rol VENDEDOR")
        void debeCrearProductoConRolVendedor() throws Exception {
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Debe rechazar creación sin autenticación")
        void debeRechazarCreacionSinAutenticacion() throws Exception {
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMINISTRADOR")
        @DisplayName("Debe calcular IGV correctamente en la respuesta")
        void debeCalcularIgvCorrectamente() throws Exception {
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.igvMonto").value(270.00))
                    .andExpect(jsonPath("$.data.precioConIgv").value(1770.00))
                    .andExpect(jsonPath("$.data.precioConIgv").value(
                            greaterThan(1500.00)));
        }
    }

    @Nested
    @DisplayName("GET /api/productos - Listar Productos")
    class ListarProductosEndpoint {

        @Test
        @WithMockUser(roles = "ADMINISTRADOR")
        @DisplayName("Debe listar productos con filtros")
        void debeListarProductosConFiltros() throws Exception {
            // Crear un producto primero
            mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isCreated());

            // Listar con filtro por marca
            mockMvc.perform(get("/api/productos")
                            .param("marcaId", marcaApple.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].marca").value("Apple"));
        }

        @Test
        @WithMockUser(roles = "VENDEDOR")
        @DisplayName("Debe retornar array vacío si no hay productos")
        void debeRetornarArrayVacioSinProductos() throws Exception {
            mockMvc.perform(get("/api/productos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("DELETE /api/productos/{id} - Desactivar Producto")
    class DesactivarProductoEndpoint {

        @Test
        @WithMockUser(roles = "ADMINISTRADOR")
        @DisplayName("Solo ADMIN puede desactivar productos")
        void soloAdminPuedeDesactivarProductos() throws Exception {
            // Crear producto
            String response = mockMvc.perform(post("/api/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long productId = objectMapper.readTree(response)
                    .get("data").get("id").asLong();

            // VENDEDOR no puede desactivar
            mockMvc.perform(delete("/api/productos/" + productId)
                            .with(request -> {
                                request.addHeader("Authorization", "Bearer token-vendedor");
                                return request;
                            }))
                    .andExpect(status().isForbidden());
        }
    }
}