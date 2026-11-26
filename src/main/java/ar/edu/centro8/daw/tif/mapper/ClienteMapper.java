package ar.edu.centro8.daw.tif.mapper;

import ar.edu.centro8.daw.tif.dto.request.ClienteRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.ClienteResponseDTO;
import ar.edu.centro8.daw.tif.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto) {
        if (dto == null) return null;
        return Cliente.builder()
                .nombre(dto.getNombre().trim())
                .apellido(dto.getApellido().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .telefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null)
                .direccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null)
                .build();
    }

    public void updateEntity(Cliente cliente, ClienteRequestDTO dto) {
        if (cliente == null || dto == null) return;
        cliente.setNombre(dto.getNombre().trim());
        cliente.setApellido(dto.getApellido().trim());
        cliente.setEmail(dto.getEmail().trim().toLowerCase());
        cliente.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        cliente.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        if (cliente == null) return null;
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .nombreCompleto(cliente.getNombre() + " " + cliente.getApellido())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .cantidadPedidos(cliente.getPedidos() != null ? cliente.getPedidos().size() : 0)
                .build();
    }
}
