package ar.edu.centro8.daw.tif.service.impl;

import ar.edu.centro8.daw.tif.dto.request.DetallePedidoRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.DetallePedidoResponseDTO;
import ar.edu.centro8.daw.tif.exception.ResourceNotFoundException;
import ar.edu.centro8.daw.tif.mapper.PedidoMapper;
import ar.edu.centro8.daw.tif.model.DetallePedido;
import ar.edu.centro8.daw.tif.model.Joya;
import ar.edu.centro8.daw.tif.model.Pedido;
import ar.edu.centro8.daw.tif.repository.DetallePedidoRepository;
import ar.edu.centro8.daw.tif.repository.JoyaRepository;
import ar.edu.centro8.daw.tif.repository.PedidoRepository;
import ar.edu.centro8.daw.tif.service.DetallePedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final JoyaRepository joyaRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoResponseDTO> obtenerTodos() {
        return detallePedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toDetalleResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DetallePedidoResponseDTO obtenerPorId(Long id) {
        DetallePedido detalle = buscarDetallePorId(id);
        return pedidoMapper.toDetalleResponseDTO(detalle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoResponseDTO> obtenerPorPedido(Long pedidoId) {
        return detallePedidoRepository.findByPedidoId(pedidoId)
                .stream()
                .map(pedidoMapper::toDetalleResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DetallePedidoResponseDTO crear(DetallePedidoRequestDTO requestDTO) {
        Pedido pedido = pedidoRepository.findById(requestDTO.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido con ID " + requestDTO.getPedidoId() + " no encontrado."));

        Joya joya = joyaRepository.findById(requestDTO.getJoyaId())
                .orElseThrow(() -> new ResourceNotFoundException("Joya con ID " + requestDTO.getJoyaId() + " no encontrada."));

        // Aplica regla de dominio en Joya
        joya.descontarStock(requestDTO.getCantidad());
        joyaRepository.save(joya);

        Double precioUnitario = (requestDTO.getPrecioUnitario() != null && requestDTO.getPrecioUnitario() > 0)
                ? requestDTO.getPrecioUnitario()
                : joya.getPrecio();

        DetallePedido detalle = DetallePedido.builder()
                .pedido(pedido)
                .joya(joya)
                .cantidad(requestDTO.getCantidad())
                .precioUnitario(precioUnitario)
                .build();

        detalle.calcularSubtotal();
        DetallePedido guardado = detallePedidoRepository.save(detalle);

        // Actualiza el total del pedido
        pedido.calcularTotal();
        pedidoRepository.save(pedido);

        return pedidoMapper.toDetalleResponseDTO(guardado);
    }

    @Override
    @Transactional
    public DetallePedidoResponseDTO actualizar(Long id, DetallePedidoRequestDTO requestDTO) {
        DetallePedido detalle = buscarDetallePorId(id);
        Joya joyaActual = detalle.getJoya();

        // Reponer stock previo
        joyaActual.reponerStock(detalle.getCantidad());

        // Verificar si cambió la joya
        Joya nuevaJoya = joyaRepository.findById(requestDTO.getJoyaId())
                .orElseThrow(() -> new ResourceNotFoundException("Joya con ID " + requestDTO.getJoyaId() + " no encontrada."));

        nuevaJoya.descontarStock(requestDTO.getCantidad());
        if (joyaActual.getId().equals(nuevaJoya.getId())) {
            joyaRepository.save(nuevaJoya);
        } else {
            joyaRepository.save(joyaActual);
            joyaRepository.save(nuevaJoya);
        }

        Double precioUnitario = (requestDTO.getPrecioUnitario() != null && requestDTO.getPrecioUnitario() > 0)
                ? requestDTO.getPrecioUnitario()
                : nuevaJoya.getPrecio();

        detalle.setJoya(nuevaJoya);
        detalle.setCantidad(requestDTO.getCantidad());
        detalle.setPrecioUnitario(precioUnitario);
        detalle.calcularSubtotal();

        DetallePedido actualizado = detallePedidoRepository.save(detalle);

        Pedido pedido = detalle.getPedido();
        pedido.calcularTotal();
        pedidoRepository.save(pedido);

        return pedidoMapper.toDetalleResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        DetallePedido detalle = buscarDetallePorId(id);
        Joya joya = detalle.getJoya();
        if (joya != null) {
            joya.reponerStock(detalle.getCantidad());
            joyaRepository.save(joya);
        }
        Pedido pedido = detalle.getPedido();
        detallePedidoRepository.delete(detalle);

        if (pedido != null) {
            pedido.getDetalles().remove(detalle);
            pedido.calcularTotal();
            pedidoRepository.save(pedido);
        }
    }

    private DetallePedido buscarDetallePorId(Long id) {
        return detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetallePedido con ID " + id + " no encontrado."));
    }
}
