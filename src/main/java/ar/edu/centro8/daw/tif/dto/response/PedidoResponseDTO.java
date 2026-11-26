package ar.edu.centro8.daw.tif.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fecha;

    private String estado;
    private Long clienteId;
    private String clienteNombreCompleto;
    private String clienteEmail;
    private List<DetallePedidoResponseDTO> detalles;
    private Double total;
    private String resumenCompra;
}
