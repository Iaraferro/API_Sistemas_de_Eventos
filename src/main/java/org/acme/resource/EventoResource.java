package org.acme.resource;

import org.acme.dto.EventoDTO;
import org.acme.service.EventoService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/eventos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventoResource {

    private static final Logger LOG = Logger.getLogger(EventoResource.class);

    @Inject
    JsonWebToken jwt;

    @Inject
    EventoService service;

    /**
     * Lista todos os eventos (PÚBLICO - sem autenticação)
     * Para o público ver os eventos disponíveis
     */
    @GET
    public List<EventoDTO> listAll() {
        LOG.info("GET /eventos - Listando todos os eventos (público)");
        return service.findAll();
    }

    /**
     * Busca evento por ID (PÚBLICO - sem autenticação)
     * Para o público ver detalhes de um evento específico
     */
    @GET
    @Path("/{id}")
    public EventoDTO findById(@PathParam("id") Long id) {
        LOG.infov("GET /eventos/{0} - Buscando evento (público)", id);
        return service.findById(id);
    }

    /**
     * Health check (público)
     */
    @GET
    @Path("/health")
    public Response health() {
        LOG.info("GET /eventos/health called");
        return Response.ok("EcoEventos API is running! ✅").build();
    }

    // =========== ENDPOINTS ADMINISTRATIVOS (APENAS ADM) ===========

    /**
     * Cria novo evento (APENAS ADMINISTRADORES)
     */
    @POST
    @RolesAllowed({ "Adm" })
    @Transactional
    public Response create(@Valid EventoDTO dto) {
        String username = jwt.getSubject();
        LOG.infov("POST /eventos called by user={0}, dto={1}", username, dto);

        try {
            EventoDTO eventoCriado = service.create(dto, username);
            return Response.status(Response.Status.CREATED).entity(eventoCriado).build();
        } catch (Exception e) {
            LOG.errorv("Erro ao criar evento: {0}", e.getMessage());
            throw e; // ← Deixa o GlobalExceptionMapper tratar
        }
    }

    /**
     * Atualiza evento (APENAS ADMINISTRADORES)
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed({ "Adm" }) // ✅ APENAS ADM!
    @Transactional
    public EventoDTO update(@PathParam("id") Long id, @Valid EventoDTO dto) {
        String username = jwt.getSubject();
        LOG.infov("PUT /eventos/{0} called by ADMIN user={1}", id, username);

        return service.update(id, dto);
    }

    /**
     * Deleta evento (APENAS ADMINISTRADORES)
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Adm" }) // ✅ APENAS ADM!
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        String username = jwt.getSubject();
        LOG.infov("DELETE /eventos/{0} called by ADMIN user={1}", id, username);

        service.delete(id);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}