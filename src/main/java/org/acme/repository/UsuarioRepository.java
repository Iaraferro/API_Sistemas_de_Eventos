package org.acme.repository; // Pacote atualizado

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.model.Usuario;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

    public Usuario findByUsernameAndSenha(String username, String senha) {
        return find("SELECT u FROM Usuario u WHERE u.username = ?1 AND u.senha = ?2", username, senha).firstResult();
    }

    public Usuario findByUsername(String username) {
        return find("SELECT u FROM Usuario u WHERE u.username = ?1", username).firstResult();
    }

    public Usuario findByEmail(String email) {
        return find("SELECT u FROM Usuario u WHERE u.email = ?1", email).firstResult();
    }


}