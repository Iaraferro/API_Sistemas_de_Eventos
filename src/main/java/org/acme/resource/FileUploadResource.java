package org.acme.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.acme.service.FileService;
import jakarta.annotation.security.RolesAllowed;

@Path("/arquivos")
public class FileUploadResource {

    @Inject
    FileService fileService;

    public static class UploadDTO {

        @RestForm("arquivo")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        public byte[] arquivo;

        @RestForm("nomeArquivo")
        @PartType(MediaType.TEXT_PLAIN)
        public String nomeArquivo;
    }

    @POST
    @Path("/upload")
    @RolesAllowed({ "Adm" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(UploadDTO dto) {

        if (dto == null || dto.arquivo == null || dto.nomeArquivo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Envie nomeArquivo e arquivo.").build();
        }

        String mimeType;
        try {
            mimeType = Files.probeContentType(Paths.get(dto.nomeArquivo));
            if (mimeType == null)
                mimeType = MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM;
        }

        String nomeSalvo = fileService.salvar(dto.nomeArquivo, dto.arquivo, mimeType);

        return Response.ok(nomeSalvo).build();
    }

    @GET
    @Path("/{nome}")
    public Response baixar(@PathParam("nome") String nome) {

        byte[] data = fileService.baixar(nome);

        String mimeType;
        try {
            mimeType = Files.probeContentType(Paths.get(nome));
            if (mimeType == null)
                mimeType = MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return Response.ok(data)
                .type(mimeType)
                .build();
    }

    @DELETE
    @Path("/{nome}")
    @RolesAllowed({ "Adm" })
    public Response deletar(@PathParam("nome") String nome) {
        try {
            fileService.deletar(nome);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Arquivo não encontrado: " + nome)
                    .build();
        }
    }

}
