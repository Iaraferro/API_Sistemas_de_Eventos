package org.acme.dto; // Pacote atualizado

import org.acme.model.Evento; // Import atualizado

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public record EventoDTO(
        Long id,
        String nome,
        String descricao,
        LocalDateTime dataHora,
        String local,
        String categoria,      // ← ADICIONAR
        String organizador,    // ← ADICIONAR  
        String contato,        // ← ADICIONAR
        String requisitos,     // ← ADICIONAR
        Integer participantes,
        List<String> arquivos,
      String imagem) {
    public static EventoDTO from(Evento evento) {
  List<String> urls = new ArrayList<>();
        if (evento.getArquivos() != null) {
            for (String nomeArquivo : evento.getArquivos()) {
                urls.add("/arquivos/" + nomeArquivo); // URL para baixar a imagem
            }
        }
        return new EventoDTO(
                evento.id,
                evento.getNome(), // Usando getters
                evento.getDescricao(),
                evento.getDataHora(),
                evento.getLocal(),
                evento.getCategoria(),     // ← ADICIONAR
                evento.getOrganizador(),   // ← ADICIONAR
                evento.getContato(),       // ← ADICIONAR
                evento.getRequisitos(),    // ← ADICIONAR
                evento.getParticipantes(),
                evento.getArquivos(),
                evento.getImagem() != null ? 
                    evento.getImagem() : 
                    (evento.getArquivos() != null && !evento.getArquivos().isEmpty() ? 
                     evento.getArquivos().get(0) : null)
              );
    }
}