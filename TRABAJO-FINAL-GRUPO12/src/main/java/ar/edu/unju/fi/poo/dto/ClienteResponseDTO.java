package ar.edu.unju.fi.poo.dto;

public class ClienteResponseDTO {
    
    private String id;
    private String tipoCliente;
    private String identificacion;
    private String nombreORazonSocial;
    private String email;

    public ClienteResponseDTO() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getNombreORazonSocial() { return nombreORazonSocial; }
    public void setNombreORazonSocial(String nombreORazonSocial) { this.nombreORazonSocial = nombreORazonSocial; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}