package ar.edu.centro8.daw.tif.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoUpdateRequestDTO {

    @NotBlank(message = "El estado del pedido es obligatorio (PENDIENTE, PAGADO, ENVIADO, CANCELADO).")
    private String estado;
}
