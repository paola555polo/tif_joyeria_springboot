package ar.edu.centro8.daw.tif.config;

import ar.edu.centro8.daw.tif.model.Cliente;
import ar.edu.centro8.daw.tif.model.DetallePedido;
import ar.edu.centro8.daw.tif.model.Joya;
import ar.edu.centro8.daw.tif.model.Pedido;
import ar.edu.centro8.daw.tif.repository.ClienteRepository;
import ar.edu.centro8.daw.tif.repository.DetallePedidoRepository;
import ar.edu.centro8.daw.tif.repository.JoyaRepository;
import ar.edu.centro8.daw.tif.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final JoyaRepository joyaRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    @Override
    public void run(String... args) {
        if (clienteRepository.count() > 0 || joyaRepository.count() > 0) {
            log.info("La base de datos ya contiene datos iniciales. Omitiendo seed inicial.");
            return;
        }

        log.info("Inicializando datos de prueba para la Tienda de Joyas...");

        // 1. Clientes iniciales
        Cliente c1 = clienteRepository.save(Cliente.builder()
                .nombre("Valentina")
                .apellido("Rossi")
                .email("valentina.rossi@example.com")
                .telefono("+54 11 5544-3322")
                .direccion("Av. Alvear 1850, Recoleta, CABA")
                .build());

        Cliente c2 = clienteRepository.save(Cliente.builder()
                .nombre("Lucas")
                .apellido("Benitez")
                .email("lucas.benitez@example.com")
                .telefono("+54 11 4433-2211")
                .direccion("Gorriti 4800, Palermo, CABA")
                .build());

        // 2. Joyas iniciales
        Joya j1 = joyaRepository.save(Joya.builder()
                .nombre("Anillo Solitario de Plata 925")
                .descripcion("Anillo de plata fina de ley 925 con circona central engarzada")
                .material("Plata 925")
                .categoria("Anillo")
                .precio(25000.0)
                .stock(15)
                .build());

        Joya j2 = joyaRepository.save(Joya.builder()
                .nombre("Collar Gargantilla en Oro 18k")
                .descripcion("Gargantilla de eslabones italianos en oro amarillo de 18 kilates")
                .material("Oro 18k")
                .categoria("Collar")
                .precio(280000.0)
                .stock(5)
                .build());

        Joya j3 = joyaRepository.save(Joya.builder()
                .nombre("Dije de Acero Quirúrgico Minimalista")
                .descripcion("Dije pulido en acero quirúrgico hipoalergénico")
                .material("Acero Quirúrgico")
                .categoria("Dije")
                .precio(4500.0)
                .stock(30)
                .build());

        Joya j4 = joyaRepository.save(Joya.builder()
                .nombre("Pulsera Tennis Platino y Diamantes")
                .descripcion("Pulsera riviere tennis en platino con brillantes naturales")
                .material("Platino")
                .categoria("Pulsera")
                .precio(450000.0)
                .stock(2)
                .build());

        // 3. Pedido inicial de prueba
        Pedido p1 = Pedido.builder()
                .cliente(c1)
                .fecha(LocalDateTime.now())
                .estado("PAGADO")
                .detalles(new ArrayList<>())
                .total(0.0)
                .build();

        p1 = pedidoRepository.save(p1);

        // Descontar stock inicial por regla de dominio
        j1.descontarStock(1);
        joyaRepository.save(j1);

        DetallePedido d1 = DetallePedido.builder()
                .pedido(p1)
                .joya(j1)
                .cantidad(1)
                .precioUnitario(j1.getPrecio())
                .subtotal(j1.getPrecio())
                .build();
        d1 = detallePedidoRepository.save(d1);

        p1.getDetalles().add(d1);
        p1.calcularTotal();
        pedidoRepository.save(p1);

        log.info("Datos iniciales cargados exitosamente: 2 clientes, 4 joyas y 1 pedido de prueba.");
    }
}
