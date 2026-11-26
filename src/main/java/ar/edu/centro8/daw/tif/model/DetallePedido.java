package ar.edu.centro8.daw.tif.model;

import ar.edu.centro8.daw.tif.exception.BusinessRuleException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalles_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joya_id", nullable = false)
    private Joya joya;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Double subtotal;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        calcularSubtotal();
    }

    /**
     * Regla de dominio en DetallePedido:
     * Calcula el subtotal multiplicando cantidad por precio unitario congelado.
     */
    public void calcularSubtotal() {
        if (this.cantidad == null || this.cantidad <= 0) {
            throw new BusinessRuleException("Regla de dominio en Detalle: La cantidad debe ser mayor a cero.");
        }
        if (this.precioUnitario == null || this.precioUnitario < 0) {
            throw new BusinessRuleException("Regla de dominio en Detalle: El precio unitario no puede ser negativo.");
        }
        this.subtotal = Math.round(this.cantidad * this.precioUnitario * 100.0) / 100.0;
    }
}
