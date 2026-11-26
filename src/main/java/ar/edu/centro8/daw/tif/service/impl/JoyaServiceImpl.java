package ar.edu.centro8.daw.tif.service.impl;

import ar.edu.centro8.daw.tif.dto.request.JoyaRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.JoyaResponseDTO;
import ar.edu.centro8.daw.tif.exception.ResourceNotFoundException;
import ar.edu.centro8.daw.tif.mapper.JoyaMapper;
import ar.edu.centro8.daw.tif.model.Joya;
import ar.edu.centro8.daw.tif.repository.JoyaRepository;
import ar.edu.centro8.daw.tif.service.JoyaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoyaServiceImpl implements JoyaService {

    private final JoyaRepository joyaRepository;
    private final JoyaMapper joyaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<JoyaResponseDTO> obtenerTodas() {
        return joyaRepository.findAll()
                .stream()
                .map(joyaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JoyaResponseDTO obtenerPorId(Long id) {
        Joya joya = buscarJoyaPorId(id);
        return joyaMapper.toResponseDTO(joya);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoyaResponseDTO> buscarPorCategoria(String categoria) {
        return joyaRepository.findByCategoriaIgnoreCase(categoria)
                .stream()
                .map(joyaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JoyaResponseDTO crear(JoyaRequestDTO requestDTO) {
        Joya joya = joyaMapper.toEntity(requestDTO);
        Joya guardada = joyaRepository.save(joya);
        return joyaMapper.toResponseDTO(guardada);
    }

    @Override
    @Transactional
    public JoyaResponseDTO actualizar(Long id, JoyaRequestDTO requestDTO) {
        Joya joya = buscarJoyaPorId(id);
        joyaMapper.updateEntity(joya, requestDTO);
        Joya actualizada = joyaRepository.save(joya);
        return joyaMapper.toResponseDTO(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Joya joya = buscarJoyaPorId(id);
        joyaRepository.delete(joya);
    }

    private Joya buscarJoyaPorId(Long id) {
        return joyaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Joya con ID " + id + " no encontrada."));
    }
}
