package org.acme.resource;

import java.util.Map;

import org.acme.dto.AuthDTO;
import org.acme.dto.AuthResponseDTO;
import org.acme.dto.UsuarioResponseDTO;
import org.acme.service.HashService;
import org.acme.service.JwtService;
import org.acme.service.UsuarioService;


import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    HashService hashService;

    @Inject
    JwtService jwtService;

    @Inject
    UsuarioService usuarioService;

    
    @POST
    public Response login(AuthDTO dto) {
        String hash;
        try {
            hash = hashService.getHashSenha(dto.senha());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(
                        "error", "Erro ao processar senha",
                        "timestamp", System.currentTimeMillis()
                    ))
                    .build();
        }

        UsuarioResponseDTO usuario = usuarioService.findByUsernameAndSenha(
            dto.username(), 
            hash
        );

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of(
                        "error", "Credenciais inválidas",
                        "timestamp", System.currentTimeMillis()
                    ))
                    .build();
        }
        
        String token = jwtService.generateJwt(
            usuario.username(), 
            usuario.perfil().getNome()
        );
        
        // ✅ Retorna JSON estruturado (profissional!)
        return Response.ok(AuthResponseDTO.from(token, usuario))
                .build();
    }

}
