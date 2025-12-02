package org.acme.dto; // Pacote atualizado

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioDTO(
    @NotBlank(message = "O nome deve ser informado.")
    @Size(max = 60)
    String nome,

    @NotBlank(message = "O email deve ser informado.")
    @Email(message = "Formato de e-mail inválido.")
    String email,

    @NotBlank(message = "O username deve ser informado.")
    @Size(min = 4, max = 30)
    String username,

    @NotBlank(message = "A senha deve ser informada.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    String senha,

    Integer id_perfil
) {}