package ar.edu.centro8.daw.tif.service.impl;

import ar.edu.centro8.daw.tif.dto.request.ClienteRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.ClienteResponseDTO;
import ar.edu.centro8.daw.tif.exception.BusinessRuleException;
import ar.edu.centro8.daw.tif.exception.ResourceNotFoundException;
import ar.edu.centro8.daw.tif.mapper.ClienteMapper;
import ar.edu.centro8.daw.tif.model.Cliente;
import ar.edu.centro8.daw.tif.repository.ClienteRepository;
import ar.edu.centro8.daw.tif.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorId(Long id) {
        Cliente cliente = buscarClientePorId(id);
        return clienteMapper.toResponseDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO crear(ClienteRequestDTO requestDTO) {
        String email = requestDTO.getEmail().trim().toLowerCase();
        if (clienteRepository.existsByEmail(email)) {
            throw new BusinessRuleException("Ya existe un cliente registrado con el correo: " + email);
        }
        Cliente cliente = clienteMapper.toEntity(requestDTO);
        Cliente guardado = clienteRepository.save(cliente);
        return clienteMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizar(Long id, ClienteRequestDTO requestDTO) {
        Cliente cliente = buscarClientePorId(id);
        String email = requestDTO.getEmail().trim().toLowerCase();
        if (clienteRepository.existsByEmailAndIdNot(email, id)) {
            throw new BusinessRuleException("El correo " + email + " ya pertenece a otro cliente.");
        }
        clienteMapper.updateEntity(cliente, requestDTO);
        Cliente actualizado = clienteRepository.save(cliente);
        return clienteMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscarClientePorId(id);
        clienteRepository.delete(cliente);
    }

    private Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con ID " + id + " no encontrado."));
    }
}
