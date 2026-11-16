package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.entity.Cliente;
import ar.edu.unju.fi.poo.entity.Empresa;
import ar.edu.unju.fi.poo.entity.Particular;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ParticularRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private ParticularRepository particularRepo;

    @Test
    void testRegistrarClienteExitoso() {
        Particular cliente = new Particular();
        cliente.setId("20401234567");
        cliente.setDni("40123456");
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setEmail("juan.test@gmail.com");
        cliente.setTelefono("3884123456");
        cliente.setDireccion("Av. Belgrano 123");

        Cliente resultado = clienteService.registrarCliente(cliente);

        assertNotNull(resultado);
        assertEquals("juan.test@gmail.com", resultado.getEmail());
    }

    @Test
    void testRegistrarClienteEmailDuplicado() {
        Empresa empresa1 = new Empresa();
        empresa1.setId("30701234567");
        empresa1.setRazonSocial("Limpieza SRL");
        empresa1.setEmail("duplicado@empresa.com");
        empresa1.setTelefono("3884111111");
        empresa1.setDireccion("Calle 1");
        clienteService.registrarCliente(empresa1);

        Empresa empresa2 = new Empresa();
        empresa2.setId("30709876543");
        empresa2.setRazonSocial("Otra SRL");
        empresa2.setEmail("duplicado@empresa.com");
        empresa2.setTelefono("3884222222");
        empresa2.setDireccion("Calle 2");

        assertThrows(ClienteDuplicadoException.class, () -> {
            clienteService.registrarCliente(empresa2);
        });
    }

    @Test
    void testBuscarClientePorId() {
        Particular cliente = new Particular();
        cliente.setId("20501234567");
        cliente.setDni("50123456");
        cliente.setNombre("Maria");
        cliente.setApellido("Gomez");
        cliente.setEmail("maria.test@gmail.com");
        cliente.setTelefono("3884333333");
        cliente.setDireccion("Calle 3");
        clienteRepo.save(cliente);

        Cliente resultado = clienteService.buscarPorId("20501234567");

        assertNotNull(resultado);
        assertEquals("Maria", ((Particular) resultado).getNombre());
    }

    @Test
    void testBuscarPorDniOCuit() {
        Particular cliente = new Particular();
        cliente.setId("20601234567");
        cliente.setDni("60123456");
        cliente.setNombre("Carlos");
        cliente.setApellido("Ruiz");
        cliente.setEmail("carlos.test@gmail.com");
        cliente.setTelefono("3884444444");
        cliente.setDireccion("Calle 4");
        clienteRepo.save(cliente);

        Cliente resultado = clienteService.buscarPorDniOCuit("60123456");

        assertNotNull(resultado);
        assertEquals("Carlos", ((Particular) resultado).getNombre());
    }

    @Test
    void testBuscarPorNombreORazonSocial() {
        Particular p = new Particular();
        p.setId("20701234567");
        p.setDni("70123456");
        p.setNombre("Carlos");
        p.setApellido("Gomez");
        p.setEmail("gomez.test@gmail.com");
        p.setTelefono("3884555555");
        p.setDireccion("Calle 5");
        clienteRepo.save(p);

        List<Cliente> resultado = clienteService.buscarPorNombreORazonSocial("Carlos");

        assertFalse(resultado.isEmpty());
    }
}