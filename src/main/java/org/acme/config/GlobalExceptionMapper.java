package org.acme.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        // 404 - Not Found
        if (exception instanceof NotFoundException) {
            Map<String, Object> error = createErrorResponse(
                    404,
                    "Recurso não encontrado",
                    exception.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // 400 - Validação (ValidationException genérica)
        if (exception instanceof ValidationException) {
            Map<String, Object> error = createErrorResponse(
                    400,
                    "Dados inválidos",
                    exception.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        // 500 - Erro interno
        Map<String, Object> error = createErrorResponse(
                500,
                "Erro interno no servidor",
                exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }

    private Map<String, Object> createErrorResponse(int status, String titulo, String mensagem) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", status);
        error.put("titulo", titulo);
        error.put("mensagem", mensagem);
        error.put("timestamp", LocalDateTime.now().toString());
        return error;
    }
}