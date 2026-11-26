package ar.edu.centro8.daw.tif.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoyaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String material;
    private String categoria;
    private Double precio;
    private Integer stock;

    // =========================================================================
    // REGLA DE NEGOCIO EN EL ResponseDTO:
    // Campos calculados dinámicamente para clasificar la joya y ofrecer información
    // de disponibilidad al cliente consumidor de la API:
    // 1) clasificacionGama:
    //    - "ALTA GAMA / EXCLUSIVA" (Precio >= $200.000)
    //    - "MEDIA GAMA / SELECTA" (Precio >= $50.000 y < $200.000)
    //    - "LÍNEA ACCESIBLE" (Precio < $50.000)
    // 2) estadoStock:
    //    - "AGOTADO" (stock == 0)
    //    - "ÚLTIMAS UNIDADES" (stock entre 1 y 3)
    //    - "DISPONIBLE" (stock > 3)
    // =========================================================================
    private String clasificacionGama;
    private String estadoStock;
}
