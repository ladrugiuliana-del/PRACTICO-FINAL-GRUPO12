package ar.edu.unju.fi.poo.service;

import java.util.List;

import ar.edu.unju.fi.poo.dto.ClienteRequestDTO;
import ar.edu.unju.fi.poo.dto.ClienteResponseDTO;
import ar.edu.unju.fi.poo.dto.EstadoCuentaDTO;

public interface ClienteService {

    ClienteResponseDTO registrarCliente(ClienteRequestDTO dto);
    List<ClienteResponseDTO> obtenerTodosLosClientes();
    ClienteResponseDTO buscarPorId(String id);
    void eliminarCliente(String id);

    List<ClienteResponseDTO> buscarPorDniOCuit(String documento);
    List<ClienteResponseDTO> buscarPorNombreORazonSocial(String nombre);

    EstadoCuentaDTO consultarEstadoCuenta(String idCliente);
}