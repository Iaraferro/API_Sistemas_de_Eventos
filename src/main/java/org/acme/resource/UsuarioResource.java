package org.acme.resource;

import jakarta.ws.rs.Path;

import org.acme.dto.UsuarioDTO;
import org.acme.dto.UsuarioResponseDTO;
import org.acme.service.UsuarioService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import jakarta.ws.rs.NotFoundException;

@Path("/usuarios") // Path ajustado para o plural, uma convenção comum para recursos de coleção
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioService usuarioService;
    
    @POST
    public Response create(@Valid UsuarioDTO dto) {
        try {
            UsuarioResponseDTO novoUsuario = usuarioService.create(dto);
            return Response.status(Status.CREATED).entity(novoUsuario).build();
        } catch (ConstraintViolationException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (ValidationException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @PUT
    @Path("/{id}")
    @RolesAllowed({"User", "Adm"})
    @Transactional
    public Response update(@PathParam("id") Long id, @Valid UsuarioDTO dto) {
        // Validação adicional: Um 'User' só pode atualizar os seus próprios dados.
        String usernameLogado = jwt.getSubject();
        UsuarioResponseDTO usuarioParaAtualizar = usuarioService.findById(id);
        if (!usuarioParaAtualizar.username().equals(usernameLogado) && !jwt.getGroups().contains("Adm")) {
             return Response.status(Status.FORBIDDEN).entity("Você não tem permissão para atualizar este usuário.").build();
        }

        try {
            UsuarioResponseDTO usuarioAtualizado = usuarioService.update(id, dto);
            return Response.ok(usuarioAtualizado).build();
        } catch (ConstraintViolationException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (ValidationException e) {
             return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @DELETE
    @Path("/{id}")
    @RolesAllowed({"Adm"})
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        try {
            usuarioService.delete(id);
            return Response.status(Status.NO_CONTENT).build();
        } catch (NotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
    

    @GET
    @RolesAllowed({"Adm"})
    public List<UsuarioResponseDTO> findAll() {
        return usuarioService.findAll();
    }


    @GET
    @Path("/{id}")
    @RolesAllowed({"Adm"})
    public Response findById(@PathParam("id") Long id) {
        try {
            UsuarioResponseDTO usuario = usuarioService.findById(id);
            return Response.ok(usuario).build();
        } catch (NotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    // Endpoint para o usuário buscar o seu próprio perfil
    @GET
    @Path("/perfil")
    @RolesAllowed({"User", "Adm"})
    public Response buscarUsuarioLogado() { 
        String username = jwt.getSubject();
        UsuarioResponseDTO usuario = usuarioService.findByUsername(username);
        return Response.ok(usuario).build();
    }
}