package ar.edu.unju.fi.poo.exceptions;

import ar.edu.unju.fi.poo.dto.RespuestaErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 409 CONFLICT: DNI/CUIT o Email duplicado
    @ExceptionHandler(ClienteDuplicadoException.class)
    public ResponseEntity<RespuestaErrorDTO> handleClienteDuplicado(ClienteDuplicadoException ex, HttpServletRequest request) {
    	RespuestaErrorDTO error = new RespuestaErrorDTO(
                HttpStatus.CONFLICT.value(),
                "Conflicto de Datos",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Excepción personalizada de Productos (utiliza el HttpStatus asignado en ProductoException)
    @ExceptionHandler(ProductoException.class)
    public ResponseEntity<RespuestaErrorDTO> handleProductoException(ProductoException ex, HttpServletRequest request) {
        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_REQUEST;
        RespuestaErrorDTO error = new RespuestaErrorDTO(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
 // Excepción personalizada para cobro correlativo de cuotas y estado de ventas
    @ExceptionHandler(CuotaException.class)
    public ResponseEntity<RespuestaErrorDTO> handleCuotaException(CuotaException ex, HttpServletRequest request) {
        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_REQUEST;
        RespuestaErrorDTO error = new RespuestaErrorDTO(
                status.value(),
                "Error en Operación de Cuotas",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
    
    // 422 UNPROCESSABLE ENTITY: Reglas de negocio (ej. Stock insuficiente)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RespuestaErrorDTO> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
    	RespuestaErrorDTO error = new RespuestaErrorDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Error de Regla de Negocio",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    // 404 NOT FOUND: Recurso no encontrado (Cliente, Producto o Venta inexistente)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RespuestaErrorDTO> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
    	RespuestaErrorDTO error = new RespuestaErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso No Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 400 BAD REQUEST: Argumentos o importes inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaErrorDTO> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
    	RespuestaErrorDTO error = new RespuestaErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Petición Inválida",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 400 BAD REQUEST: Errores de validación de campos (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        RespuestaErrorDTO error = new RespuestaErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Error de Validación",
                detalles,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 500 INTERNAL SERVER ERROR: Errores inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaErrorDTO> handleGeneralException(Exception ex, HttpServletRequest request) {
    	RespuestaErrorDTO error = new RespuestaErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                "Ocurrió un error inesperado: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}