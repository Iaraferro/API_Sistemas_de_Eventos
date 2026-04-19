package org.acme.dto;

import java.time.LocalDateTime;

public record InscricaoResponseDTO(
    Long id,
    String nome,
    String email,
    String telefone,
    String eventoNome,
    LocalDateTime dataInscricao
) {
}
