package org.acme.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para eventos
 * Inclui URLs completas para facilitar o consumo no front-end
 */
public record EventoResponseDTO(
    Long id,
    String nome,
    String descricao,
    LocalDateTime dataHora,
    String local,
    String categoria,
    String organizador,
    String contato,
    String requisitos,
    Integer participantes,
    
    // URL completa da imagem principal
    String imagemPrincipalUrl,
    
    // Link para formulário de inscrição
    String linkInscricao,
    
    // URLs dos arquivos
    List<String> arquivosUrls
) {
    
    /**
     * Converte EventoDTO para EventoResponseDTO
     * Adiciona o prefixo /arquivos/ nas URLs
     */
    public static EventoResponseDTO from(EventoDTO dto) {
        
        // Monta URL da imagem principal
        String imagemUrl = dto.imagemPrincipal() != null 
            ? "/arquivos/" + dto.imagemPrincipal() 
            : null;
        
        // Monta URLs dos arquivos
        List<String> arquivosUrls = null;
        if (dto.arquivos() != null && !dto.arquivos().isEmpty()) {
            arquivosUrls = dto.arquivos()
                .stream()
                .map(arquivo -> "/arquivos/" + arquivo)
                .toList();
        }
        
        return new EventoResponseDTO(
            dto.id(),
            dto.nome(),
            dto.descricao(),
            dto.dataHora(),
            dto.local(),
            dto.categoria(),
            dto.organizador(),
            dto.contato(),
            dto.requisitos(),
            dto.participantes(),
            imagemUrl,
            dto.linkInscricao(),
            arquivosUrls
        );
    }
}

