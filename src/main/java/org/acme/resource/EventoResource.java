package org.acme.resource;

import org.acme.dto.EventoDTO;
import org.acme.service.EventoService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import jakarta.annotation.security.RolesAllowed;

@Path("/eventos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventoResource {

    private static final Logger LOG = Logger.getLogger(EventoResource.class);

    @Inject
    JsonWebToken jwt;

    @Inject
    EventoService service;

    @GET
    public List<EventoDTO> listAll() {
        return service.findAll();
    }

    


    @GET
    @Path("/{id}")
    public EventoDTO findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    @RolesAllowed({"User", "Adm"})
    @Transactional
    public Response create(EventoDTO dto) {
        String username = jwt.getSubject();
        LOG.infov("POST /eventos called by user={0}, dto={1}", username, dto);
        EventoDTO eventoCriado = service.create(dto, username);
        return Response.status(Response.Status.CREATED).entity(eventoCriado).build();
    }

    @GET
    @Path("/health")
    public Response health() {
        LOG.infov("GET /eventos/health called");
        return Response.ok("ok").build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public EventoDTO update(@PathParam("id") Long id, EventoDTO dto) {
        return service.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }




    
}
