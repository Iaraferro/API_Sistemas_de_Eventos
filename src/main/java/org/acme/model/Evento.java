package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Evento extends PanacheEntity {

    private String nome;
    @Column(nullable = false, length = 5000)
    private String descricao;
    private LocalDateTime dataHora;
    private String categoria;
    private String local;  
    private String contato;
    private String requisitos;
    private Integer participantes; 
    private String organizador;
    @OneToMany(mappedBy = "evento")
    private List<Inscricao> inscricoes;
    // Imagem principal do evento (capa/banner)
    private String imagemPrincipal;
    
    // Link para formulário de inscrição (ex: Google Forms)
    private String linkInscricao;

    // Arquivos adicionais anexados ao evento
    @ElementCollection
    private List<String> arquivos;

    // ==================== GETTERS E SETTERS ====================
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public String getImagemPrincipal() {
        return imagemPrincipal;
    }

    public void setImagemPrincipal(String imagemPrincipal) {
        this.imagemPrincipal = imagemPrincipal;
    }
    
    public String getLinkInscricao() {
        return linkInscricao;
    }

    public void setLinkInscricao(String linkInscricao) {
        this.linkInscricao = linkInscricao;
    }

    public List<String> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<String> arquivos) {
        this.arquivos = arquivos;
    }

    public String getCategoria() { 
        return categoria; 
    }
    
    public void setCategoria(String categoria) { 
        this.categoria = categoria; 
    }
    
    public String getContato() { 
        return contato; 
    }
    
    public void setContato(String contato) { 
        this.contato = contato; 
    }
    
    public String getRequisitos() { 
        return requisitos; 
    }
    
    public void setRequisitos(String requisitos) { 
        this.requisitos = requisitos; 
    }
    
    public Integer getParticipantes() { 
        return participantes; 
    }
    
    public void setParticipantes(Integer participantes) { 
        this.participantes = participantes; 
    }

    public String getTitulo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTitulo'");
    }
}
