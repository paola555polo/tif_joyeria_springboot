package ar.edu.centro8.daw.tif.service;

import ar.edu.centro8.daw.tif.dto.request.DetallePedidoRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.DetallePedidoResponseDTO;

import java.util.List;

public interface DetallePedidoService {
    List<DetallePedidoResponseDTO> obtenerTodos();
    DetallePedidoResponseDTO obtenerPorId(Long id);
    List<DetallePedidoResponseDTO> obtenerPorPedido(Long pedidoId);
    DetallePedidoResponseDTO crear(DetallePedidoRequestDTO requestDTO);
    DetallePedidoResponseDTO actualizar(Long id, DetallePedidoRequestDTO requestDTO);
    void eliminar(Long id);
}
