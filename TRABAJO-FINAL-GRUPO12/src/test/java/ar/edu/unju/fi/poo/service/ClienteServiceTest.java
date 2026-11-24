package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.entity.Particular;
import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
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

    @Test
    void testRegistrarClienteExitoso() {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setTipoCliente("PARTICULAR");
        dto.setDni("40123456");
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setEmail("juan.test@gmail.com");
        dto.setTelefono("3884123456");
        dto.setDireccion("Av. Belgrano 123");

        ClienteResponseDTO resultado = clienteService.registrarCliente(dto);

        assertNotNull(resultado);
        assertEquals("juan.test@gmail.com", resultado.getEmail());
        assertEquals("PARTICULAR", resultado.getTipoCliente());
    }

    @Test
    void testRegistrarClienteEmailDuplicado() {
        ClienteRequestDTO empresa1 = new ClienteRequestDTO();
        empresa1.setTipoCliente("EMPRESA");
        empresa1.setCuit("30111111119");
        empresa1.setRazonSocial("Limpieza SRL");
        empresa1.setEmail("duplicado@empresa.com");
        empresa1.setTelefono("3884111111");
        empresa1.setDireccion("Calle 1");
        clienteService.registrarCliente(empresa1);

        ClienteRequestDTO empresa2 = new ClienteRequestDTO();
        empresa2.setTipoCliente("EMPRESA");
        empresa2.setCuit("30222222229");
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
        cliente.setId("50123456");
        cliente.setDni("50123456");
        cliente.setNombre("Maria");
        cliente.setApellido("Gomez");
        cliente.setEmail("maria.test@gmail.com");
        cliente.setTelefono("3884333333");
        cliente.setDireccion("Calle 3");
        clienteRepo.save(cliente);

        ClienteResponseDTO resultado = clienteService.buscarPorId("50123456");

        assertNotNull(resultado);
        assertEquals("Maria Gomez", resultado.getNombreORazonSocial());
    }

    @Test
    void testBuscarPorDniOCuit() {
        Particular cliente = new Particular();
        cliente.setId("60123456");
        cliente.setDni("60123456");
        cliente.setNombre("Carlos");
        cliente.setApellido("Ruiz");
        cliente.setEmail("carlos.test@gmail.com");
        cliente.setTelefono("3884444444");
        cliente.setDireccion("Calle 4");
        clienteRepo.save(cliente);

        List<ClienteResponseDTO> resultado = clienteService.buscarPorDniOCuit("60123456");

        assertFalse(resultado.isEmpty());
        assertEquals("Carlos Ruiz", resultado.get(0).getNombreORazonSocial());
    }

    @Test
    void testBuscarPorNombreORazonSocial() {
        Particular p = new Particular();
        p.setId("70123456");
        p.setDni("70123456");
        p.setNombre("Carlos");
        p.setApellido("Gomez");
        p.setEmail("gomez.test@gmail.com");
        p.setTelefono("3884555555");
        p.setDireccion("Calle 5");
        clienteRepo.save(p);

        List<ClienteResponseDTO> resultado = clienteService.buscarPorNombreORazonSocial("Carlos");

        assertFalse(resultado.isEmpty());
    }
}