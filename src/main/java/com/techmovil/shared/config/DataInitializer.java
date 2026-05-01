package com.techmovil.shared.config;

import com.techmovil.inventario.infrastructure.persistence.AlmacenEntity;
import com.techmovil.inventario.infrastructure.persistence.AlmacenJpaRepository;
import com.techmovil.productos.infrastructure.persistence.*;
import com.techmovil.usuarios.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioJpaRepository usuarioRepository;
    private final RolJpaRepository rolRepository;
    private final EmpresaJpaRepository empresaRepository;
    private final AlmacenJpaRepository almacenRepository;
    private final MarcaJpaRepository marcaRepository;
    private final CategoriaJpaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) return;

        // 1. Roles
        RolEntity adminRole = createRolIfNotExist("ADMINISTRADOR");
        RolEntity vendedorRole = createRolIfNotExist("VENDEDOR");

        // 2. Empresa
        EmpresaEntity empresa = new EmpresaEntity();
        empresa.setNombre("TECHMOVIL Puno");
        empresa.setRuc("20123456789");
        empresa.setDireccion("Jr. Lima 123, Puno");
        empresa = empresaRepository.save(empresa);

        // 3. Usuario Administrador
        UsuarioEntity admin = new UsuarioEntity();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setNombres("Admin");
        admin.setApellidos("Techmovil");
        admin.setEmail("admin@techmovil.com");
        admin.setActivo(true);
        admin.setEmpresa(empresa);
        admin.setRoles(Set.of(adminRole));
        usuarioRepository.save(admin);

        // 4. Almacén Principal
        AlmacenEntity almacen = new AlmacenEntity();
        almacen.setNombre("Almacén Central Puno");
        almacen.setDireccion("Av. Sesquicentenario 456");
        almacen.setEsPrincipal(true);
        almacen.setActivo(true);
        almacen.setEmpresa(empresa);
        almacenRepository.save(almacen);

        // 5. Datos de prueba: Marcas y Categorías
        createMarca("SAMSUNG");
        createMarca("APPLE");
        createMarca("XIAOMI");

        createCategoria("SMARTPHONES");
        createCategoria("ACCESORIOS");
        createCategoria("TABLETS");
    }

    private RolEntity createRolIfNotExist(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    RolEntity r = new RolEntity();
                    r.setNombre(nombre);
                    return rolRepository.save(r);
                });
    }

    private void createMarca(String nombre) {
        MarcaEntity m = new MarcaEntity();
        m.setNombre(nombre);
        m.setActivo(true);
        marcaRepository.save(m);
    }

    private void createCategoria(String nombre) {
        CategoriaEntity c = new CategoriaEntity();
        c.setNombre(nombre);
        c.setActivo(true);
        categoriaRepository.save(c);
    }
}
