package org.acme.resource;

import org.acme.model.ArquivoEvento;
import org.acme.service.ArquivoEventoService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/eventos/{idEvento}/arquivos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArquivoEventoResource {

    @Inject
    ArquivoEventoService service;

    public record ArquivoRequest(
            String nomeOriginal,
            String urlCloudinary,
            String mimeType) {
    }

    @POST
    @RolesAllowed({ "Adm" })
    @Transactional
    public Response salvar(@PathParam("idEvento") Long idEvento, ArquivoRequest req) {
        if (req.nomeOriginal() == null || req.urlCloudinary() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Informe nomeOriginal e urlCloudinary").build();
        }

        String mimeType = req.mimeType() != null ? req.mimeType() : "application/octet-stream";

        ArquivoEvento arq = service.salvarArquivo(
                idEvento,
                req.nomeOriginal(),
                req.urlCloudinary(),
                mimeType);

        return Response.status(Response.Status.CREATED).entity(arq).build();
    }

    @GET
    @RolesAllowed({ "Adm" })
    public Response listar(@PathParam("idEvento") Long idEvento) {
        List<ArquivoEvento> arquivos = service.listarArquivosPorEvento(idEvento);
        return Response.ok(arquivos).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Adm" })
    @Transactional
    public Response deletar(@PathParam("idEvento") Long idEvento, @PathParam("id") Long id) {
        service.deletarArquivo(id);
        return Response.noContent().build();
    }
}