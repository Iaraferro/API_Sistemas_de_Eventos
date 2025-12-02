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
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ArquivoEventoService {

    @Inject
    FileService fileService;

    @Inject
    ArquivoEventoRepository arquivoEventoRepository;

    @Inject
    EventoRepository eventoRepository;

    @Transactional
    public ArquivoEvento salvarArquivo(Long idEvento, String nomeOriginal, byte[] conteudo, String mimeType) {

        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new NotFoundException("Evento não encontrado.");
        }

        // Salva no MinIO
        String nomeSalvo = fileService.salvar(nomeOriginal, conteudo, mimeType);

        // Cria o registro
        ArquivoEvento arq = new ArquivoEvento();
        arq.setNomeOriginal(nomeOriginal);
        arq.setNomeSalvo(nomeSalvo);
        arq.setMimeType(mimeType);
        arq.setEvento(evento);
        arq.setDataUpload(LocalDateTime.now());

        arquivoEventoRepository.persist(arq);

        List<String> arquivosDoEvento = evento.getArquivos();
        if (arquivosDoEvento == null) {
            arquivosDoEvento = new ArrayList<>();
            evento.setArquivos(arquivosDoEvento);
        }
        arquivosDoEvento.add(nomeSalvo);
        eventoRepository.persist(evento);

        return arq;
    }

    public List<ArquivoEvento> listarArquivosPorEvento(Long idEvento) {
        return arquivoEventoRepository.findByEvento(idEvento);
    }

    public String buscarPrimeiraImagemEvento(Long idEvento) {
    List<ArquivoEvento> arquivos = listarArquivosPorEvento(idEvento);
    if (arquivos != null && !arquivos.isEmpty()) {
        // Buscar primeiro arquivo que seja imagem
        for (ArquivoEvento arquivo : arquivos) {
            if (arquivo.getMimeType() != null && arquivo.getMimeType().startsWith("image/")) {
                return arquivo.getNomeSalvo();
            }
        }
        // Se não encontrou imagem, retorna o primeiro arquivo
        return arquivos.get(0).getNomeSalvo();
    }
    return null;
}

    public void deletarArquivo(String nomeArquivo) {
        fileService.deletar(nomeArquivo);
    }
}
