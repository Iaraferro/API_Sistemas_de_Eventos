package org.acme.dto;

public record AuthResponseDTO(
    String token,
    String type,
    Long expiresIn,
    UsuarioResponseDTO user
) {
      public static AuthResponseDTO from(String token, UsuarioResponseDTO user) {
        return new AuthResponseDTO(
            token,
            "Bearer",
            86400L, // 24 horas em segundos
            user
        );
    }
}
