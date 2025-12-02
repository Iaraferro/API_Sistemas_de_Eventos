package org.acme.model; // Pacote atualizado

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Usuario extends DefaultEntity {

    @Column(length = 30, unique = true)
    private String username;

    @Column(length = 88)
    private String senha;

    private Perfil perfil;


    private String email;

    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

}
