package org.acme.model; // Pacote atualizado

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;


import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Evento extends PanacheEntity {

    // Campos agora são private
    private String nome;
    private String descricao;
    private LocalDateTime dataHora;
    private String categoria;
    private String local;  
    private String contato;        // ← ADICIONAR
    private String requisitos;     // ← ADICIONAR
    private Integer participantes; 
    private String organizador;
    private String imagemPrincipal;  // ✅ Imagem de capa do evento

    /**
     * @return the organizador
     */
    public String getOrganizador() {
        return organizador;
    }

    /**
     * @param organizador the organizador to set
     */
    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    @ElementCollection
    private List<String> arquivos;   // ✅ Arquivos adicionais (PDFs, imagens extras)
    // Getters e Setters
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

   public String getImagem(){
         return imagemPrincipal;
   }
   public void setImagem(String imagem){
    this.imagem = imagemPrincipal;
   }

    public List<String> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<String> arquivos) {
        this.arquivos = arquivos;
    }


       public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
    
    public String getRequisitos() { return requisitos; }
    public void setRequisitos(String requisitos) { this.requisitos = requisitos; }
    
    public Integer getParticipantes() { return participantes; }
    public void setParticipantes(Integer participantes) { this.participantes = participantes; }
}

