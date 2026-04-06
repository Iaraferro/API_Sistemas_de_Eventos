package org.acme.resource;

import org.acme.service.EventoService;
import org.acme.service.FileService;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Resource para gerenciar upload de imagem principal dos eventos
 */
@Path("/eventos/{idEvento}/imagem")
public class EventoImagemResource {

    @Inject
    FileService fileService;

    @Inject
    EventoService eventoService;

    public static class ImagemUploadDTO {
        @RestForm("imagem")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        public byte[] imagem;

        @RestForm("nomeArquivo")
        @PartType(MediaType.TEXT_PLAIN)
        public String nomeArquivo;
    }

    /**
     * Faz upload da imagem principal (capa) do evento
     * 
     * @param idEvento ID do evento
     * @param dto DTO com a imagem e nome do arquivo
     * @return Resposta com a URL da imagem
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response uploadImagemPrincipal(
            @PathParam("idEvento") Long idEvento,
            ImagemUploadDTO dto) {

        // Validações
        if (dto == null || dto.imagem == null || dto.nomeArquivo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Envie a imagem e o nome do arquivo")
                    .build();
        }

        // Valida se é realmente uma imagem
        String mimeType;
        try {
            mimeType = Files.probeContentType(Paths.get(dto.nomeArquivo));
            if (mimeType == null || !mimeType.startsWith("image/")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O arquivo deve ser uma imagem (PNG, JPG, JPEG, GIF)")
                        .build();
            }
        } catch (Exception e) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM;
        }

        // Salva a imagem no MinIO
        String nomeArquivoSalvo = fileService.salvar(dto.nomeArquivo, dto.imagem, mimeType);

        // Atualiza o evento com a imagem principal
        eventoService.atualizarImagemPrincipal(idEvento, nomeArquivoSalvo);

        // Retorna a URL completa da imagem
        String urlImagem = "/arquivos/" + nomeArquivoSalvo;

        return Response.ok()
                .entity(new ImagemResponse(nomeArquivoSalvo, urlImagem))
                .build();
    }

    /**
     * Remove a imagem principal do evento
     * 
     * @param idEvento ID do evento
     * @return Resposta sem conteúdo
     */
    @DELETE
    @Transactional
    public Response removerImagemPrincipal(@PathParam("idEvento") Long idEvento) {
        
        eventoService.removerImagemPrincipal(idEvento);
        
        return Response.noContent().build();
    }

    /**
     * Classe de resposta para o upload de imagem
     */
    public static class ImagemResponse {
        public String nomeArquivo;
        public String urlAcesso;

        public ImagemResponse(String nomeArquivo, String urlAcesso) {
            this.nomeArquivo = nomeArquivo;
            this.urlAcesso = urlAcesso;
        }
    }
}
