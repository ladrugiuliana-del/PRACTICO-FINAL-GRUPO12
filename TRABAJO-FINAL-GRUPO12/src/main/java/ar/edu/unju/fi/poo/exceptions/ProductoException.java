package ar.edu.unju.fi.poo.exceptions;

import org.springframework.http.HttpStatus;

public class ProductoException extends RuntimeException {
    
    private HttpStatus httpStatus;

    public ProductoException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}