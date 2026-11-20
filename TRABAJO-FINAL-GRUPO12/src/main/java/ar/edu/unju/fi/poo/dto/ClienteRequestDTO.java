package ar.edu.unju.fi.poo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {
    
    private String tipoCliente;
    private String email;
    private String telefono;
    private String direccion;
    private String dni;
    private String nombre;
    private String apellido;
    private String cuit;
    private String razonSocial;
}