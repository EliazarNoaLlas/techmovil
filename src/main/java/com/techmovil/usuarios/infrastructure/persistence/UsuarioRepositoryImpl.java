package com.techmovil.usuarios.infrastructure.persistence;

import com.techmovil.usuarios.domain.Usuario;
import com.techmovil.usuarios.domain.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        // Note: In a real app we might need to resolve roles from DB instead of just mapping
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existePorUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public java.util.List<Usuario> listarTodo() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return Usuario.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .passwordHash(entity.getPasswordHash())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .email(entity.getEmail())
                .activo(entity.isActivo())
                .intentosFallidos(entity.getIntentosFallidos())
                .bloqueado(entity.isBloqueado())
                .roles(entity.getRoles().stream().map(RolEntity::getNombre).collect(Collectors.toSet()))
                .build();
    }

    private UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        UsuarioEntity entity = new UsuarioEntity();
        if (domain.getId() != null) {
            entity = jpaRepository.findById(domain.getId()).orElse(new UsuarioEntity());
        }
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setNombres(domain.getNombres());
        entity.setApellidos(domain.getApellidos());
        entity.setEmail(domain.getEmail());
        entity.setActivo(domain.isActivo());
        entity.setIntentosFallidos(domain.getIntentosFallidos());
        entity.setBloqueado(domain.isBloqueado());
        // For roles and empresa, mapping should handle attaching existing instances.
        // For simplicity, we assume role entities are added appropriately elsewhere.
        return entity;
    }
}
