package ar.edu.centro8.daw.tif.service;

import ar.edu.centro8.daw.tif.dto.request.JoyaRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.JoyaResponseDTO;

import java.util.List;

public interface JoyaService {
    List<JoyaResponseDTO> obtenerTodas();
    JoyaResponseDTO obtenerPorId(Long id);
    List<JoyaResponseDTO> buscarPorCategoria(String categoria);
    JoyaResponseDTO crear(JoyaRequestDTO requestDTO);
    JoyaResponseDTO actualizar(Long id, JoyaRequestDTO requestDTO);
    void eliminar(Long id);
}
