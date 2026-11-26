package ar.edu.centro8.daw.tif.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoItemDTO {

    @NotNull(message = "El ID de la joya es obligatorio.")
    private Long joyaId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad mínima debe ser al menos 1 unidad.")
    private Integer cantidad;
}
