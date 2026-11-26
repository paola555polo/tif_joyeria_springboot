package ar.edu.centro8.daw.tif.controller;

import ar.edu.centro8.daw.tif.dto.request.JoyaRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.JoyaResponseDTO;
import ar.edu.centro8.daw.tif.service.JoyaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joyas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JoyaController {

    private final JoyaService joyaService;

    @GetMapping
    public ResponseEntity<List<JoyaResponseDTO>> obtenerTodas(
            @RequestParam(required = false) String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(joyaService.buscarPorCategoria(categoria));
        }
        return ResponseEntity.ok(joyaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JoyaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(joyaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<JoyaResponseDTO> crear(@Valid @RequestBody JoyaRequestDTO requestDTO) {
        JoyaResponseDTO creada = joyaService.crear(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JoyaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody JoyaRequestDTO requestDTO) {
        JoyaResponseDTO actualizada = joyaService.actualizar(id, requestDTO);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        joyaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
