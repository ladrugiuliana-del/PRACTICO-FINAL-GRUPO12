package ar.edu.unju.fi.poo;

import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.model.Particular;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ParticularRepository;
import ar.edu.unju.fi.poo.service.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    
    
    private ClienteRepository clienteRepo;

    @Mock
    private ParticularRepository particularRepo;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Particular p;

    @BeforeEach
    void setUp() {
        p = new Particular();
        p.setId("1");
        p.setEmail("test@gmail.com");
        p.setDni("11222333");
    }

    @Test
    void testGuardarClienteBien() {
        when(clienteRepo.existsByEmail(p.getEmail())).thenReturn(false);
        when(particularRepo.existsByDni(p.getDni())).thenReturn(false);
        when(clienteRepo.save(p)).thenReturn(p);

        Particular guardado = (Particular) clienteService.registrarCliente(p);

        assertNotNull(guardado);
        assertEquals("test@gmail.com", guardado.getEmail());
    }

    @Test
    void testFallaPorEmailRepetido() {
        when(clienteRepo.existsByEmail(p.getEmail())).thenReturn(true);

        assertThrows(ClienteDuplicadoException.class, () -> {
            clienteService.registrarCliente(p);
        });
    }
}