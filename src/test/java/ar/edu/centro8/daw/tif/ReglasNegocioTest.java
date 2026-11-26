package ar.edu.centro8.daw.tif;

import ar.edu.centro8.daw.tif.dto.request.DetallePedidoItemDTO;
import ar.edu.centro8.daw.tif.dto.request.JoyaRequestDTO;
import ar.edu.centro8.daw.tif.dto.request.PedidoRequestDTO;
import ar.edu.centro8.daw.tif.dto.response.JoyaResponseDTO;
import ar.edu.centro8.daw.tif.exception.BusinessRuleException;
import ar.edu.centro8.daw.tif.mapper.JoyaMapper;
import ar.edu.centro8.daw.tif.model.Cliente;
import ar.edu.centro8.daw.tif.model.Joya;
import ar.edu.centro8.daw.tif.repository.ClienteRepository;
import ar.edu.centro8.daw.tif.repository.JoyaRepository;
import ar.edu.centro8.daw.tif.repository.PedidoRepository;
import ar.edu.centro8.daw.tif.service.PedidoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReglasNegocioTest {

    private Validator validator;

    @Autowired
    private JoyaMapper joyaMapper;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private JoyaRepository joyaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("1. Regla en RequestDTO: Rechazar Joya de Oro con precio menor a $50.000")
    void testReglaRequestDTO_JoyaOroPrecioBajo() {
        JoyaRequestDTO joyaInvalida = JoyaRequestDTO.builder()
                .nombre("Anillo Oro Invalido")
                .descripcion("Prueba de validacion")
                .material("Oro 18k")
                .categoria("Anillo")
                .precio(5000.0) // Menor a $50.000 -> viola la regla
                .stock(10)
                .build();

        Set<ConstraintViolation<JoyaRequestDTO>> violations = validator.validate(joyaInvalida);
        assertFalse(violations.isEmpty(), "Debe fallar por la regla de negocio del RequestDTO");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Regla de negocio en RequestDTO")));
    }

    @Test
    @DisplayName("2. Regla en ResponseDTO: Clasificar Joya según precio y stock comercial")
    void testReglaResponseDTO_CalculoGamaYStock() {
        Joya joyaAltaGama = Joya.builder()
                .id(100L)
                .nombre("Collar Exclusivo")
                .material("Platino")
                .categoria("Collar")
                .precio(350000.0)
                .stock(2)
                .build();

        JoyaResponseDTO responseDTO = joyaMapper.toResponseDTO(joyaAltaGama);

        assertEquals("ALTA GAMA / EXCLUSIVA", responseDTO.getClasificacionGama(), 
                "Debe ser catalogada como ALTA GAMA por superar los $200.000");
        assertEquals("ÚLTIMAS 2 UNIDADES", responseDTO.getEstadoStock(), 
                "Debe indicar ÚLTIMAS 2 UNIDADES por tener stock <= 3");
    }

    @Test
    @DisplayName("3. Regla de Dominio en Joya: Lanzar BusinessRuleException si no hay stock")
    void testReglaDominio_StockInsuficiente() {
        Joya joya = Joya.builder()
                .id(1L)
                .nombre("Anillo Plata")
                .material("Plata 925")
                .categoria("Anillo")
                .precio(20000.0)
                .stock(3)
                .build();

        assertThrows(BusinessRuleException.class, () -> {
            joya.descontarStock(5); // Solicita 5 con stock de 3
        }, "Debe lanzar BusinessRuleException por stock insuficiente según la regla de dominio.");
    }

    @Test
    @DisplayName("4. Regla en Service: Lanzar BusinessRuleException si pedido total < $10.000")
    void testReglaService_MontoMinimoRequerido() {
        // Aseguramos cliente y joya de bajo valor
        Cliente cliente = clienteRepository.save(Cliente.builder()
                .nombre("Carlos")
                .apellido("Gomez")
                .email("carlos.test.service@example.com")
                .build());

        Joya joyaBarata = joyaRepository.save(Joya.builder()
                .nombre("Aros Acero Test")
                .material("Acero")
                .categoria("Aros")
                .precio(3000.0)
                .stock(10)
                .build());

        PedidoRequestDTO pedidoRequest = PedidoRequestDTO.builder()
                .clienteId(cliente.getId())
                .detalles(List.of(DetallePedidoItemDTO.builder()
                        .joyaId(joyaBarata.getId())
                        .cantidad(1) // 1 * 3000 = $3000 < $10000 mínimo
                        .build()))
                .build();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            pedidoService.crear(pedidoRequest);
        });

        assertTrue(exception.getMessage().contains("Regla de negocio en Service: El monto total del pedido"),
                "Debe fallar por la regla del monto mínimo de compra en la capa Service");
    }
}
