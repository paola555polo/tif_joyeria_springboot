package ar.edu.centro8.daw.tif.service;

import ar.edu.centro8.daw.tif.dto.request.ClienteRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {
    List<ClienteResponseDTO> obtenerTodos();
    ClienteResponseDTO obtenerPorId(Long id);
    ClienteResponseDTO crear(ClienteRequestDTO requestDTO);
    ClienteResponseDTO actualizar(Long id, ClienteRequestDTO requestDTO);
    void eliminar(Long id);
}
