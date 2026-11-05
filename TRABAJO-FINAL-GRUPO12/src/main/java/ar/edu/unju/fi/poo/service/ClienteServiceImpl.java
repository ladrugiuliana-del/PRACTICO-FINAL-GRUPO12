package ar.edu.unju.fi.poo.service;

import ar.edu.unju.fi.poo.exceptions.ClienteDuplicadoException;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Particular;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.ParticularRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    
    private ClienteRepository clienteRepo;

    @Autowired
    
    private ParticularRepository particularRepo;

    @Override
    public Cliente registrarCliente(Cliente cliente) {
        
        if (clienteRepo.existsByEmail(cliente.getEmail())) {
            throw new ClienteDuplicadoException("El email ya esta registrado");
        }

        if (cliente instanceof Particular) {
            Particular p = (Particular) cliente;
            if (particularRepo.existsByDni(p.getDni())) {
                throw new ClienteDuplicadoException("El DNI ya existe en el sistema");
            }
        }

        return clienteRepo.save(cliente);
    }
}