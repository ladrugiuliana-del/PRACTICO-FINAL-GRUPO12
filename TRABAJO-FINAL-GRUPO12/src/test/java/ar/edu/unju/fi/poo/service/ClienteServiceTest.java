package ar.edu.unju.fi.poo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.model.Particular;

@SpringBootTest
public class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Test
    public void testGuardarCliente() {
        Particular p = new Particular();
        p.setId("C02");
        p.setDni("87654321");
        p.setNombre("Enrique");
        p.setApellido("Medina");
        p.setEmail("kike@test.com");
        p.setTelefono("388456789");
        p.setDireccion("San Pedro de Jujuy");

        Particular guardado = (Particular) clienteService.registrarCliente(p);
        ClienteResponseDTO response = clienteService.mapearADTO(guardado);

        assertNotNull(response);
        assertEquals("Enrique Medina", response.getNombreORazonSocial());
    }
}