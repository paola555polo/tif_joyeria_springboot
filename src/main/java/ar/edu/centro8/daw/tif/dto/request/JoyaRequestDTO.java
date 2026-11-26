package ar.edu.centro8.daw.tif.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoyaRequestDTO {

    @NotBlank(message = "El nombre de la joya es obligatorio.")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres.")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres.")
    private String descripcion;

    @NotBlank(message = "El material es obligatorio (ej: Oro 18k, Plata 925, Platino, Acero).")
    @Size(max = 60, message = "El material no puede superar los 60 caracteres.")
    private String material;

    @NotBlank(message = "La categoría es obligatoria (ej: Anillo, Collar, Aros, Pulsera).")
    @Size(max = 60, message = "La categoría no puede superar los 60 caracteres.")
    private String categoria;

    @NotNull(message = "El precio es obligatorio.")
    @Positive(message = "El precio debe ser un número positivo mayor a 0.")
    private Double precio;

    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    // =========================================================================
    // REGLA DE NEGOCIO EN EL RequestDTO:
    // Las piezas confeccionadas en materiales nobles ("Oro" o "Platino") deben
    // registrar un precio mínimo de catálogo de $50,000.00 ARS.
    // Si no se cumple, Bean Validation dispara esta regla de validación de entrada.
    // =========================================================================
    @AssertTrue(message = "Regla de negocio en RequestDTO: Las piezas de 'Oro' o 'Platino' deben tener un precio mínimo de $50000.00 ARS.")
    public boolean isPrecioMinimoSegunMaterial() {
        if (material != null && precio != null) {
            String matUpper = material.trim().toUpperCase();
            if (matUpper.contains("ORO") || matUpper.contains("PLATINO")) {
                return precio >= 50000.0;
            }
        }
        return true;
    }
}
