package ar.edu.centro8.daw.tif.mapper;

import ar.edu.centro8.daw.tif.dto.response.DetallePedidoResponseDTO;
import ar.edu.centro8.daw.tif.dto.response.PedidoResponseDTO;
import ar.edu.centro8.daw.tif.model.DetallePedido;
import ar.edu.centro8.daw.tif.model.Pedido;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public DetallePedidoResponseDTO toDetalleResponseDTO(DetallePedido detalle) {
        if (detalle == null) return null;
        return DetallePedidoResponseDTO.builder()
                .id(detalle.getId())
                .pedidoId(detalle.getPedido() != null ? detalle.getPedido().getId() : null)
                .joyaId(detalle.getJoya() != null ? detalle.getJoya().getId() : null)
                .joyaNombre(detalle.getJoya() != null ? detalle.getJoya().getNombre() : null)
                .joyaMaterial(detalle.getJoya() != null ? detalle.getJoya().getMaterial() : null)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        if (pedido == null) return null;

        List<DetallePedidoResponseDTO> detallesDTO = (pedido.getDetalles() == null)
                ? Collections.emptyList()
                : pedido.getDetalles().stream()
                .map(this::toDetalleResponseDTO)
                .collect(Collectors.toList());

        String resumen = "Pedido #" + pedido.getId() + " - Estado: " + pedido.getEstado() +
                " - Items: " + detallesDTO.size() + " - Total: $" + pedido.getTotal();

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .fecha(pedido.getFecha())
                .estado(pedido.getEstado())
                .clienteId(pedido.getCliente() != null ? pedido.getCliente().getId() : null)
                .clienteNombreCompleto(pedido.getCliente() != null 
                        ? (pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido()) : null)
                .clienteEmail(pedido.getCliente() != null ? pedido.getCliente().getEmail() : null)
                .detalles(detallesDTO)
                .total(pedido.getTotal())
                .resumenCompra(resumen)
                .build();
    }
}
