package org.acme.dto;

import java.util.Optional;

public record EventoResponseDTO(
    Long id,
    String nome,
    String descricao,
    String dataHora,
    String local,
    java.util.List<String> arquivos,
    String imagemPrincipal
) {
    public static EventoResponseDTO valueOf(EventoDTO evento) {
        String imagemPrincipal = evento.imagem() != null ? 
            evento.imagem() : 
            Optional.ofNullable(evento.arquivos())
                .filter(list -> !list.isEmpty()) 
                .map(list -> list.get(0)) 
                .orElse(null);
        return new EventoResponseDTO(
            evento.id(),
            evento.nome(),
            evento.descricao(),
            evento.dataHora().toString(),
            evento.local(),
            evento.arquivos(),
            imagemPrincipal
        );
    }
}
