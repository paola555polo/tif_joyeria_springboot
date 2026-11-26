package ar.edu.centro8.daw.tif.service;

import ar.edu.centro8.daw.tif.dto.request.PedidoRequestDTO;
import ar.edu.centro8.daw.tif.dto.request.PedidoUpdateRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.PedidoResponseDTO;

import java.util.List;

public interface PedidoService {
    List<PedidoResponseDTO> obtenerTodos();
    PedidoResponseDTO obtenerPorId(Long id);
    List<PedidoResponseDTO> obtenerPorCliente(Long clienteId);
    PedidoResponseDTO crear(PedidoRequestDTO requestDTO);
    PedidoResponseDTO actualizarEstado(Long id, PedidoUpdateRequestDTO requestDTO);
    void eliminar(Long id);
}
