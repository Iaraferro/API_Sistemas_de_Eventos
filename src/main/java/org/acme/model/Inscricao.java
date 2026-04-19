package org.acme.model;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Inscricao extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    private String nome;
    private String email;
    private String telefone;
    private LocalDateTime dataInscricao;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
     this.id = id;
    }

    public Evento getEvento() { 
        return evento;
    }
    public void setEvento(Evento evento) {
       this.evento = evento;
    }

    public String getNome() { 
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email; 
    }
    public void setEmail(String email) {
         this.email = email;
     }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDateTime getDataInscricao() { 
       return dataInscricao;
    }
    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    

}
