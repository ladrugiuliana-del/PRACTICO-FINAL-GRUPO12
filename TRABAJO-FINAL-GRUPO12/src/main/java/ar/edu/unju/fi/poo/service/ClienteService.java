package ar.edu.unju.fi.poo.service;

import java.util.List;
import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.model.Cliente;

public interface ClienteService {
    
    Cliente registrarCliente(Cliente cliente);
    List<Cliente> obtenerTodosLosClientes();
    
    Cliente buscarPorId(String id);
    void eliminarCliente(String id);
    
    Cliente buscarPorDniOCuit(String documento);
    List<Cliente> buscarPorNombreORazonSocial(String nombre);
    
    ClienteResponseDTO mapearADTO(Cliente cliente);
    Cliente mapearAEntidad(ClienteRequestDTO dto);
}