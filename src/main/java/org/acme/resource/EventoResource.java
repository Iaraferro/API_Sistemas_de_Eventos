package org.acme.resource;

import org.acme.dto.EventoDTO;
import org.acme.model.Evento;
import org.acme.service.EventoService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
@Path("/paginado")
public Response listarPaginado(
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("6") int size,
        @QueryParam("status") String status) {

    LocalDateTime agora = LocalDateTime.now();
    PanacheQuery<Evento> query;
    
    if ("realizados".equals(status)) {
        query = Evento.find("dataHora < ?1 order by dataHora desc", agora);
    } else if ("proximos".equals(status)) {
        query = Evento.find("dataHora >= ?1 order by dataHora asc", agora);
    } else {
        // ✅ MOSTRA TODOS os eventos
        query = Evento.find("order by dataHora desc");
    }
    
    List<EventoDTO> content = query.page(Page.of(page, size))
            .list()
            .stream()
            .map(EventoDTO::from)
            .collect(Collectors.toList());
    
    Map<String, Object> response = new HashMap<>();
    response.put("content", content);
    response.put("totalElements", query.count());
    response.put("totalPages", query.pageCount());
    response.put("size", size);
    response.put("number", page);
    
    return Response.ok(response).build();
}

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

    // Adicione no EventoResource.java
    @POST
    @Path("/{id}/imagem-principal")
    @RolesAllowed({"Adm"})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response uploadImagemPrincipal(
    @PathParam("id") Long id,
    @FormParam("imagem") File imagem,
    @FormParam("nomeArquivo") String nomeArquivo
    ) {
    try {
        // Salva no Cloudinary ou servidor local
        String urlImagem = service.salvarImagemPrincipal(imagem, nomeArquivo);
        service.atualizarImagemPrincipal(id, urlImagem);
        
        return Response.ok(Map.of("url", urlImagem)).build();
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(Map.of("error", e.getMessage()))
            .build();
    }
}
}