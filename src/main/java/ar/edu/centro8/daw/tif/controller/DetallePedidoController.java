package ar.edu.centro8.daw.tif.controller;

import ar.edu.centro8.daw.tif.dto.request.DetallePedidoRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.DetallePedidoResponseDTO;
import ar.edu.centro8.daw.tif.service.DetallePedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-pedido")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    @GetMapping
    public ResponseEntity<List<DetallePedidoResponseDTO>> obtenerTodos(
            @RequestParam(required = false) Long pedidoId) {
        if (pedidoId != null) {
            return ResponseEntity.ok(detallePedidoService.obtenerPorPedido(pedidoId));
        }
        return ResponseEntity.ok(detallePedidoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detallePedidoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DetallePedidoResponseDTO> crear(@Valid @RequestBody DetallePedidoRequestDTO requestDTO) {
        DetallePedidoResponseDTO creado = detallePedidoService.crear(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedidoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DetallePedidoRequestDTO requestDTO) {
        DetallePedidoResponseDTO actualizado = detallePedidoService.actualizar(id, requestDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detallePedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
