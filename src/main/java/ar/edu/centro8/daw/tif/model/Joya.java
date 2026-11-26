package ar.edu.centro8.daw.tif.model;

import ar.edu.centro8.daw.tif.exception.BusinessRuleException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "joyas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Joya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, length = 60)
    private String material;

    @Column(nullable = false, length = 60)
    private String categoria;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    // =========================================================================
    // REGLA DE DOMINIO EN EL MODELO:
    // Control de invariante de stock en la joya. La entidad se protege a sí misma
    // garantizando que el stock no sea negativo y que las operaciones de entrada/salida
    // cumplan con las restricciones del negocio.
    // =========================================================================

    /**
     * Regla de dominio: Descuenta stock ante una venta o reserva.
     * Lanza BusinessRuleException si no hay stock suficiente o si la cantidad es inválida.
     */
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new BusinessRuleException("Regla de dominio en Joya: La cantidad a descontar debe ser mayor a 0.");
        }
        int actual = (this.stock == null) ? 0 : this.stock;
        if (actual < cantidad) {
            throw new BusinessRuleException("Regla de dominio en Joya (Stock Insuficiente): La joya '" 
                    + this.nombre + "' dispone de " + actual + " unidad(es), pero se solicitaron " + cantidad + ".");
        }
        this.stock = actual - cantidad;
    }

    /**
     * Regla de dominio: Repone stock ante cancelaciones o devoluciones.
     */
    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new BusinessRuleException("Regla de dominio en Joya: La cantidad a reponer debe ser mayor a 0.");
        }
        int actual = (this.stock == null) ? 0 : this.stock;
        this.stock = actual + cantidad;
    }
}
