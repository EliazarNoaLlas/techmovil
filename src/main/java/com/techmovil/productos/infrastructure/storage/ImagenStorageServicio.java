package com.techmovil.productos.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImagenStorageServicio {

    @Value("${app.imagenes.ruta:D:/celulares/imagenes/}")
    private String rutaDirectorio;

    public String guardarImagen(MultipartFile file, Long productoId) {
        try {
            Path dir = Paths.get(rutaDirectorio);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            String extension = getExtension(file.getOriginalFilename());
            String filename = "prod_" + productoId + "_" + UUID.randomUUID() + "." + extension;
            Path destino = dir.resolve(filename);
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la imagen: " + e.getMessage(), e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
