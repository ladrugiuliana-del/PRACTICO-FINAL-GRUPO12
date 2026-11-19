package ar.edu.unju.fi.poo.exceptions;

import org.springframework.http.HttpStatus;

public class CuotaException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final HttpStatus httpStatus;

    // Constructor por defecto para errores de regla de negocio en cobro de cuotas (400 BAD REQUEST se puede corregir facil si ocurre algun problema.)
    public CuotaException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    // Constructor con HttpStatus personalizado (ej. 404 NOT FOUND o 422 UNPROCESSABLE ENTITY)
    public CuotaException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}