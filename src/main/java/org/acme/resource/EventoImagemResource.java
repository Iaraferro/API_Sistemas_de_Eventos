package org.acme.resource;

import org.acme.service.EventoService;
import org.acme.service.FileService;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Resource para gerenciar upload de imagem principal dos eventos
 * APENAS ADMINISTRADORES podem fazer upload
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
     * Upload de imagem principal (APENAS ADMINISTRADORES)
     */
    @POST
    @RolesAllowed({"Adm"}) // ✅ APENAS ADM!
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response uploadImagemPrincipal(
            @PathParam("idEvento") Long idEvento,
            ImagemUploadDTO dto) {

        if (dto == null || dto.imagem == null || dto.nomeArquivo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Envie a imagem e o nome do arquivo")
                    .build();
        }

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

        String nomeArquivoSalvo = fileService.salvar(dto.nomeArquivo, dto.imagem, mimeType);
        eventoService.atualizarImagemPrincipal(idEvento, nomeArquivoSalvo);

        String urlImagem = "/arquivos/" + nomeArquivoSalvo;

        return Response.ok()
                .entity(new ImagemResponse(nomeArquivoSalvo, urlImagem))
                .build();
    }

    /**
     * Remove imagem principal (APENAS ADMINISTRADORES)
     */
    @DELETE
    @RolesAllowed({"Adm"}) // ✅ APENAS ADM!
    @Transactional
    public Response removerImagemPrincipal(@PathParam("idEvento") Long idEvento) {
        eventoService.removerImagemPrincipal(idEvento);
        return Response.noContent().build();
    }

    public static class ImagemResponse {
        public String nomeArquivo;
        public String urlAcesso;

        public ImagemResponse(String nomeArquivo, String urlAcesso) {
            this.nomeArquivo = nomeArquivo;
            this.urlAcesso = urlAcesso;
        }
    }
}