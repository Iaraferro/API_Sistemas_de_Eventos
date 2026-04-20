package org.acme.service;

import org.acme.model.ArquivoEvento;
import org.acme.model.Evento;
import org.acme.repository.ArquivoEventoRepository;
import org.acme.repository.EventoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ArquivoEventoService {

    @Inject
    ArquivoEventoRepository arquivoEventoRepository;

    @Inject
    EventoRepository eventoRepository;

    @Transactional
    public ArquivoEvento salvarArquivo(Long idEvento, String nomeOriginal, String urlCloudinary, String mimeType) {

        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new NotFoundException("Evento não encontrado.");
        }

        ArquivoEvento arq = new ArquivoEvento();
        arq.setNomeOriginal(nomeOriginal);
        arq.setNomeSalvo(urlCloudinary);
        arq.setMimeType(mimeType);
        arq.setEvento(evento);
        arq.setDataUpload(LocalDateTime.now());

        arquivoEventoRepository.persist(arq);

        return arq;
    }

    public List<ArquivoEvento> listarArquivosPorEvento(Long idEvento) {
        return arquivoEventoRepository.findByEvento(idEvento);
    }

    @Transactional
    public void deletarArquivo(Long id) {
        ArquivoEvento arq = arquivoEventoRepository.findById(id);
        if (arq == null) {
            throw new NotFoundException("Arquivo não encontrado.");
        }
        arquivoEventoRepository.delete(arq);
    }
}