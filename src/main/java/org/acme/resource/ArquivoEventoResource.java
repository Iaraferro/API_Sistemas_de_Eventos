package org.acme.resource;

import org.acme.dto.UploadArquivoEventoDTO;
import org.acme.model.ArquivoEvento;
import org.acme.service.ArquivoEventoService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/eventos/{idEvento}/arquivos")
public class ArquivoEventoResource {

    @Inject
    ArquivoEventoService service;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response upload(@PathParam("idEvento") Long idEvento, UploadArquivoEventoDTO dto) {

        if (dto.arquivo == null || dto.nomeArquivo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Envie arquivo e nomeArquivo").build();
        }

        String mimeType;
        try {
            mimeType = Files.probeContentType(Paths.get(dto.nomeArquivo));
            if (mimeType == null) mimeType = MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM;
        }

        ArquivoEvento arq = service.salvarArquivo(idEvento, dto.nomeArquivo, dto.arquivo, mimeType);

        return Response.status(Response.Status.CREATED).entity(arq).build();
    }
}
