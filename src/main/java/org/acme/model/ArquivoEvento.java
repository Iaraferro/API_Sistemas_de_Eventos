package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class ArquivoEvento extends PanacheEntity {

    private String nomeOriginal;
    private String nomeSalvo; // nome UUID que vai para o MinIO
    private String mimeType;
    private LocalDateTime dataUpload;

    @ManyToOne
    private Evento evento;

    // Getters e setters
    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getNomeSalvo() {
        return nomeSalvo;
    }

    public void setNomeSalvo(String nomeSalvo) {
        this.nomeSalvo = nomeSalvo;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}
