package org.acme.config;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapeia exceções para respostas HTTP padronizadas
 * Evita expor detalhes internos da aplicação
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        
        LOG.errorv("Erro capturado: {0}", exception.getMessage());
        
        // 404 - Recurso não encontrado
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(criarErro(404, "Recurso não encontrado", exception.getMessage()))
                    .build();
        }

        // 400 - Validação de campos (Bean Validation)
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            
            Map<String, String> erros = cve.getConstraintViolations()
                    .stream()
                    .collect(Collectors.toMap(
                            cv -> cv.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    ));

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(criarErroValidacao(400, "Dados inválidos", erros))
                    .build();
        }

        // 400 - Validação customizada (regras de negócio)
        if (exception instanceof ValidationException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(criarErro(400, "Erro de validação", exception.getMessage()))
                    .build();
        }

        // 500 - Erro interno do servidor
        LOG.error("Erro interno não tratado", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(criarErro(500, "Erro interno do servidor", 
                        "Ocorreu um erro inesperado. Tente novamente mais tarde."))
                .build();
    }

    /**
     * Cria objeto de erro padronizado
     */
    private ErrorResponse criarErro(int status, String titulo, String mensagem) {
        return new ErrorResponse(
                status,
                titulo,
                mensagem,
                LocalDateTime.now()
        );
    }

    /**
     * Cria objeto de erro com detalhes de validação
     */
    private ErrorValidationResponse criarErroValidacao(int status, String titulo, 
                                                        Map<String, String> erros) {
        return new ErrorValidationResponse(
                status,
                titulo,
                "Um ou mais campos possuem erros de validação",
                erros,
                LocalDateTime.now()
        );
    }

    /**
     * Classe de resposta de erro padrão
     */
    public static class ErrorResponse {
        public int status;
        public String titulo;
        public String mensagem;
        public LocalDateTime timestamp;

        public ErrorResponse(int status, String titulo, String mensagem, LocalDateTime timestamp) {
            this.status = status;
            this.titulo = titulo;
            this.mensagem = mensagem;
            this.timestamp = timestamp;
        }
    }

    /**
     * Classe de resposta de erro de validação
     */
    public static class ErrorValidationResponse extends ErrorResponse {
        public Map<String, String> erros;

        public ErrorValidationResponse(int status, String titulo, String mensagem,
                                        Map<String, String> erros, LocalDateTime timestamp) {
            super(status, titulo, mensagem, timestamp);
            this.erros = erros;
        }
    }
}