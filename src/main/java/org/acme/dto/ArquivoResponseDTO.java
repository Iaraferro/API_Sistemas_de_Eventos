package org.acme.dto;

public record ArquivoResponseDTO(
        Long id,
        String nomeArquivo,
        String urlAcesso) {
    public static ArquivoResponseDTO valueOf(String nomeArquivo, String urlAcesso, Long id) {
        return new ArquivoResponseDTO(id, nomeArquivo, urlAcesso);
    }
}
