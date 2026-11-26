package ar.edu.centro8.daw.tif.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio.")
    private Long clienteId;

    @NotEmpty(message = "El pedido debe contener al menos un detalle de joya.")
    @Valid
    private List<DetallePedidoItemDTO> detalles;

    private String estado;
}
