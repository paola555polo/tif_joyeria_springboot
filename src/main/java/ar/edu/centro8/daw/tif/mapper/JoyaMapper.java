package ar.edu.centro8.daw.tif.mapper;

import ar.edu.centro8.daw.tif.dto.request.JoyaRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.JoyaResponseDTO;
import ar.edu.centro8.daw.tif.model.Joya;
import org.springframework.stereotype.Component;

@Component
public class JoyaMapper {

    public Joya toEntity(JoyaRequestDTO dto) {
        if (dto == null) return null;
        return Joya.builder()
                .nombre(dto.getNombre().trim())
                .descripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null)
                .material(dto.getMaterial().trim())
                .categoria(dto.getCategoria().trim())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .build();
    }

    public void updateEntity(Joya joya, JoyaRequestDTO dto) {
        if (joya == null || dto == null) return;
        joya.setNombre(dto.getNombre().trim());
        joya.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        joya.setMaterial(dto.getMaterial().trim());
        joya.setCategoria(dto.getCategoria().trim());
        joya.setPrecio(dto.getPrecio());
        joya.setStock(dto.getStock());
    }

    public JoyaResponseDTO toResponseDTO(Joya joya) {
        if (joya == null) return null;

        // =====================================================================
        // REGLA DE NEGOCIO EN ResponseDTO:
        // Clasificación comercial de gama y disponibilidad en stock.
        // =====================================================================
        String gama;
        double precio = (joya.getPrecio() != null) ? joya.getPrecio() : 0.0;
        if (precio >= 200000.0) {
            gama = "ALTA GAMA / EXCLUSIVA";
        } else if (precio >= 50000.0) {
            gama = "MEDIA GAMA / SELECTA";
        } else {
            gama = "LÍNEA ACCESIBLE";
        }

        String estadoStock;
        int stock = (joya.getStock() != null) ? joya.getStock() : 0;
        if (stock <= 0) {
            estadoStock = "AGOTADO";
        } else if (stock <= 3) {
            estadoStock = "ÚLTIMAS " + stock + " UNIDADES";
        } else {
            estadoStock = "DISPONIBLE (" + stock + " u.)";
        }

        return JoyaResponseDTO.builder()
                .id(joya.getId())
                .nombre(joya.getNombre())
                .descripcion(joya.getDescripcion())
                .material(joya.getMaterial())
                .categoria(joya.getCategoria())
                .precio(joya.getPrecio())
                .stock(joya.getStock())
                .clasificacionGama(gama)
                .estadoStock(estadoStock)
                .build();
    }
}
