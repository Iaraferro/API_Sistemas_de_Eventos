package org.acme.dto;

import org.acme.model.Evento;

import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public record EventoDTO(
        Long id,
        
        @NotBlank(message = "O nome do evento é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,
        
        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
        String descricao,
        
        @NotNull(message = "Data e hora do evento são obrigatórias")
        @Future(message = "A data do evento deve ser no futuro")
        LocalDateTime dataHora,
        
        @NotBlank(message = "O local do evento é obrigatório")
        String local,
        
        String categoria,
        String organizador,
        
        @Email(message = "Email de contato inválido")
        String contato,
        
        String requisitos,
        
        @Min(value = 0, message = "Número de participantes não pode ser negativo")
        Integer participantes,
        
        List<String> arquivos,
        
        // Imagem principal do evento (URL ou nome do arquivo no MinIO)
        String imagemPrincipal,
        
        // Link para formulário de inscrição (Google Forms, Sympla, etc)
        @Pattern(regexp = "^(https?://)?[\\w\\-]+(\\.[\\w\\-]+)+.*$|^$", 
                 message = "Link de inscrição deve ser uma URL válida")
        String linkInscricao
) {
    public static EventoDTO from(Evento evento) {
        List<String> urls = new ArrayList<>();
        if (evento.getArquivos() != null) {
            for (String nomeArquivo : evento.getArquivos()) {
                urls.add("/arquivos/" + nomeArquivo);
            }
        }
        
        return new EventoDTO(
                evento.id,
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
                evento.getImagemPrincipal() != null ? 
                    evento.getImagemPrincipal() : 
                    (evento.getArquivos() != null && !evento.getArquivos().isEmpty() ? 
                     evento.getArquivos().get(0) : null),
                evento.getLinkInscricao()
        );
    }
}