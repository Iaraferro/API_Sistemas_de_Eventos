package org.acme.dto;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import jakarta.ws.rs.core.MediaType;

public class ArquivoDTO {

    @RestForm("arquivo")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] arquivo;

    @RestForm("nomeArquivo")
    @PartType(MediaType.TEXT_PLAIN)
    public String nomeArquivo;
}
