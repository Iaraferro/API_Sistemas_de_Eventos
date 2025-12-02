package org.acme.dto;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.PartType;
import jakarta.ws.rs.core.MediaType;

public class UploadArquivoEventoDTO {

    @RestForm("arquivo")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] arquivo;

    @RestForm("nomeArquivo")
    @PartType(MediaType.TEXT_PLAIN)
    public String nomeArquivo;

    @RestForm("idEvento")
    @PartType(MediaType.TEXT_PLAIN)
    public Long idEvento;
}
