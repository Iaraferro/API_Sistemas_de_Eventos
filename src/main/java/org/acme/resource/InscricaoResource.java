package org.acme.resource;

import org.acme.dto.InscricaoRequestDTO;
import org.acme.dto.InscricaoResponseDTO;
import org.acme.service.InscricaoService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DefaultValue;

@Path("/inscricoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InscricaoResource {
    @Inject
    InscricaoService service;

    @GET
    @RolesAllowed({ "Adm" })
    public Response listarTodas(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Response.ok(service.listarTodas(page, size)).build();
    }

    @POST
    public Response salvar(InscricaoRequestDTO dto) {

        InscricaoResponseDTO response = service.salvar(dto);

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();

    }

    @GET
    @Path("/evento/{eventoId}")
    @RolesAllowed({ "Adm" })
    public Response listarPorEvento(
            @PathParam("eventoId") Long eventoId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        return Response.ok(
                service.listarPorEvento(eventoId, page, size)).build();

    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Adm" })
    public Response deletar(@PathParam("id") Long id) {
        service.deletar(id);
        return Response.noContent().build();

    }
}
