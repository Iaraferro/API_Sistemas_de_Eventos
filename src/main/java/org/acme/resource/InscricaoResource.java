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

@Path("/inscricoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InscricaoResource {
     @Inject
    InscricaoService service;

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
    public Response listarPorEvento(
        @PathParam("eventoId") Long eventoId,
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("10") int size
    ) {

    return Response.ok(
        service.listarPorEvento(eventoId, page, size)
    ).build();

    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
    service.deletar(id);
    return Response.noContent().build();

    }
}
