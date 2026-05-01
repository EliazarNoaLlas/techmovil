package com.techmovil.productos.application.impl;

import com.techmovil.productos.infrastructure.persistence.CategoriaEntity;
import com.techmovil.productos.infrastructure.persistence.CategoriaJpaRepository;
import com.techmovil.productos.infrastructure.persistence.MarcaEntity;
import com.techmovil.productos.infrastructure.persistence.MarcaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaestrosServiceImpl {

    private final MarcaJpaRepository marcaRepository;
    private final CategoriaJpaRepository categoriaRepository;

    public List<MarcaEntity> listarMarcas() {
        return marcaRepository.findAll();
    }

    public List<CategoriaEntity> listarCategorias() {
        return categoriaRepository.findAll();
    }
}
