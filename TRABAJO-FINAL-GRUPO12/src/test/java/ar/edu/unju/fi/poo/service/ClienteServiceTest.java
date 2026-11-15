package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.impl.ClienteServiceImpl;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Empresa;
import ar.edu.unju.fi.poo.model.Particular;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ParticularRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepo;

    @Mock
    private ParticularRepository particularRepo;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrarClienteExitoso() {
        Particular cliente = new Particular();
        cliente.setId("20401234567");
        cliente.setDni("40123456");
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setEmail("juan@gmail.com");
        cliente.setTelefono("3884123456");
        cliente.setDireccion("Av. Belgrano 123");

        when(clienteRepo.existsByEmail("juan@gmail.com")).thenReturn(false);
        when(particularRepo.existsByDni("40123456")).thenReturn(false);
        when(clienteRepo.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.registrarCliente(cliente);

        assertNotNull(resultado);
        assertEquals("juan@gmail.com", resultado.getEmail());
        verify(clienteRepo, times(1)).save(cliente);
    }

    @Test
    void testRegistrarClienteEmailDuplicado() {
        Empresa empresa = new Empresa();
        empresa.setId("30701234567");
        empresa.setRazonSocial("Limpieza SRL");
        empresa.setEmail("contacto@empresa.com");

        when(clienteRepo.existsByEmail("contacto@empresa.com")).thenReturn(true);

        assertThrows(ClienteDuplicadoException.class, () -> {
            clienteService.registrarCliente(empresa);
        });
    }

    @Test
    void testBuscarClientePorId() {
        Particular cliente = new Particular();
        cliente.setId("20401234567");
        cliente.setNombre("Maria");
        cliente.setEmail("maria@gmail.com");

        when(clienteRepo.findById("20401234567")).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.buscarPorId("20401234567");

        assertNotNull(resultado);
        assertEquals("Maria", ((Particular) resultado).getNombre());
    }
}