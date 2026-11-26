package ar.edu.centro8.daw.tif.service.impl;

import ar.edu.centro8.daw.tif.dto.request.DetallePedidoItemDTO;
import ar.edu.centro8.daw.tif.dto.request.PedidoRequestDTO;
import ar.edu.centro8.daw.tif.dto.request.PedidoUpdateRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.PedidoResponseDTO;
import ar.edu.centro8.daw.tif.exception.BusinessRuleException;
import ar.edu.centro8.daw.tif.exception.ResourceNotFoundException;
import ar.edu.centro8.daw.tif.mapper.PedidoMapper;
import ar.edu.centro8.daw.tif.model.Cliente;
import ar.edu.centro8.daw.tif.model.DetallePedido;
import ar.edu.centro8.daw.tif.model.Joya;
import ar.edu.centro8.daw.tif.model.Pedido;
import ar.edu.centro8.daw.tif.repository.ClienteRepository;
import ar.edu.centro8.daw.tif.repository.JoyaRepository;
import ar.edu.centro8.daw.tif.repository.PedidoRepository;
import ar.edu.centro8.daw.tif.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    // Constante para la regla de negocio de Service
    public static final double MONTO_MINIMO_PEDIDO = 10000.0;

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final JoyaRepository joyaRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPorId(Long id) {
        Pedido pedido = buscarPedidoPorId(id);
        return pedidoMapper.toResponseDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoResponseDTO crear(PedidoRequestDTO requestDTO) {
        Cliente cliente = clienteRepository.findById(requestDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con ID " + requestDTO.getClienteId() + " no encontrado."));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .fecha(LocalDateTime.now())
                .estado(requestDTO.getEstado() != null ? requestDTO.getEstado().trim().toUpperCase() : "PENDIENTE")
                .detalles(new ArrayList<>())
                .total(0.0)
                .build();

        for (DetallePedidoItemDTO item : requestDTO.getDetalles()) {
            Joya joya = joyaRepository.findById(item.getJoyaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Joya con ID " + item.getJoyaId() + " no encontrada."));

            // Invocación a la REGLA DE DOMINIO en la entidad Joya (descontarStock):
            joya.descontarStock(item.getCantidad());
            joyaRepository.save(joya);

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .joya(joya)
                    .cantidad(item.getCantidad())
                    .precioUnitario(joya.getPrecio())
                    .build();

            // Invocación a la regla de dominio de cálculo de subtotal en DetallePedido:
            detalle.calcularSubtotal();
            pedido.getDetalles().add(detalle);
        }

        // Calcula el total general del pedido
        pedido.calcularTotal();

        // =====================================================================
        // ÚNICA REGLA DE NEGOCIO EN LA CAPA SERVICE:
        // Política de Monto Mínimo de Pedido:
        // La joyería no procesa compras cuyo total acumulado sea inferior
        // a $10,000.00 ARS.
        // =====================================================================
        if (pedido.getTotal() < MONTO_MINIMO_PEDIDO) {
            throw new BusinessRuleException("Regla de negocio en Service: El monto total del pedido ($" 
                    + pedido.getTotal() + " ARS) no alcanza el monto mínimo de compra requerido ($" 
                    + MONTO_MINIMO_PEDIDO + " ARS).");
        }

        Pedido guardado = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public PedidoResponseDTO actualizarEstado(Long id, PedidoUpdateRequestDTO requestDTO) {
        Pedido pedido = buscarPedidoPorId(id);
        String nuevoEstado = requestDTO.getEstado().trim().toUpperCase();

        // Si se cancela un pedido, reponemos el stock de las joyas
        if ("CANCELADO".equalsIgnoreCase(nuevoEstado) && !"CANCELADO".equalsIgnoreCase(pedido.getEstado())) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Joya joya = detalle.getJoya();
                joya.reponerStock(detalle.getCantidad());
                joyaRepository.save(joya);
            }
        }

        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Pedido pedido = buscarPedidoPorId(id);
        // Al eliminar, reponemos stock si el pedido no estaba cancelado
        if (!"CANCELADO".equalsIgnoreCase(pedido.getEstado())) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Joya joya = detalle.getJoya();
                if (joya != null) {
                    joya.reponerStock(detalle.getCantidad());
                    joyaRepository.save(joya);
                }
            }
        }
        pedidoRepository.delete(pedido);
    }

    private Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido con ID " + id + " no encontrado."));
    }
}
