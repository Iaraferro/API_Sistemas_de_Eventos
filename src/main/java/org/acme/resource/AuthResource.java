package org.acme.resource;

import org.acme.dto.AuthDTO;
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
public class AuthResource {

    @Inject
    HashService hashService;

    @Inject
    JwtService jwtService;

    @Inject
    UsuarioService usuarioService;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(AuthDTO dto) {
        String hash;
        try {
            hash = hashService.getHashSenha(dto.senha());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao processar senha")
                    .build();
        }

        UsuarioResponseDTO usuario = usuarioService.findByUsernameAndSenha(dto.username(), hash);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        }
        
        String token = jwtService.generateJwt(usuario.username(), usuario.perfil().getNome());
        
        // ✅ SEMPRE retornar 200 com o token no corpo
        return Response.ok(token)
                .header("Authorization", "Bearer " + token)
                .build();
    }
}
