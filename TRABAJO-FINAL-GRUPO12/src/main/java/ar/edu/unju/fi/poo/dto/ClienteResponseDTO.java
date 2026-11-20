package ar.edu.unju.fi.poo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {
    
    private String id;
    private String tipoCliente;
    private String identificacion;
    private String nombreORazonSocial;
    private String email;
}