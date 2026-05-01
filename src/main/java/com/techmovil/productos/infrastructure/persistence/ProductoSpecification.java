package com.techmovil.productos.infrastructure.persistence;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductoSpecification {

    public static Specification<ProductoEntity> porMarca(Long marcaId) {
        return (root, query, cb) -> marcaId == null ? cb.conjunction() : cb.equal(root.get("marca").get("id"), marcaId);
    }

    public static Specification<ProductoEntity> porCategoria(Long categoriaId) {
        return (root, query, cb) -> categoriaId == null ? cb.conjunction() : cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<ProductoEntity> porModelo(String modelo) {
        return (root, query, cb) -> modelo == null || modelo.isBlank() ? cb.conjunction() : cb.like(cb.lower(root.get("modelo")), "%" + modelo.toLowerCase() + "%");
    }

    public static Specification<ProductoEntity> porPrecioEntre(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("precioVenta"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("precioVenta"), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("precioVenta"), max);
            } else {
                return cb.conjunction();
            }
        };
    }

    public static Specification<ProductoEntity> soloConStock() {
        return (root, query, cb) -> cb.greaterThan(root.get("stockActual"), BigDecimal.ZERO);
    }

    public static Specification<ProductoEntity> soloCriticos() {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("stockActual"), root.get("stockMinimo"));
    }

    public static Specification<ProductoEntity> esActivo(Boolean activo) {
        return (root, query, cb) -> activo == null ? cb.conjunction() : cb.equal(root.get("activo"), activo);
    }
}
