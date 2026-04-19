package org.acme.dto;

public record InscricaoRequestDTO(
    Long eventoId,
    String nome,
    String email,
    String telefone
) {
} 
