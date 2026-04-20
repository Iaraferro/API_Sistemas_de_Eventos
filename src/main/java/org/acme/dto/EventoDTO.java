package org.acme.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import org.acme.model.Evento;

public record EventoDTO(
        Long id,

        @NotBlank(message = "O nome deve ser informado.") String nome,

        @NotBlank(message = "A descrição deve ser informada.") String descricao,

        @NotNull(message = "A data/hora deve ser informada.")

        LocalDateTime dataHora,

        @NotBlank(message = "O local deve ser informado.") String local,

        String categoria,
        String organizador,

        @Email(message = "Email de contato inválido.", regexp = ".*") String contato,

        String requisitos,

        @Min(value = 0, message = "Número de participantes não pode ser negativo.") Integer participantes,

        List<String> arquivos,
        String imagemPrincipal,

        @Pattern(regexp = "^(https?://.*)?$", message = "Link de inscrição deve ser uma URL válida.") String linkInscricao) {
    public static EventoDTO from(Evento evento) {
        return new EventoDTO(
                evento.id, // ✅ CORRIGIDO: evento.id em vez de evento.getId()
                evento.getNome(),
                evento.getDescricao(),
                evento.getDataHora(),
                evento.getLocal(),
                evento.getCategoria(),
                evento.getOrganizador(),
                evento.getContato(),
                evento.getRequisitos(),
                evento.getParticipantes(),
                evento.getArquivos(),
                evento.getImagemPrincipal(),
                evento.getLinkInscricao());
    }
}