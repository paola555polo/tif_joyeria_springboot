package ar.edu.centro8.daw.tif.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoResponseDTO {
    private Long id;
    private Long pedidoId;
    private Long joyaId;
    private String joyaNombre;
    private String joyaMaterial;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
