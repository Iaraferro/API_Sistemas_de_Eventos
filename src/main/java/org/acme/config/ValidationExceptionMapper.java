package org.acme.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> erros = exception.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));
        
        Map<String, Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("titulo", "Dados inválidos");
        error.put("mensagem", "Verifique os campos obrigatórios");
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("erros", erros);
        
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
}
