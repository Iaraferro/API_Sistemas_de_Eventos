package org.acme.service;

import org.acme.dto.EventoDTO;
import org.acme.model.Evento;
import org.acme.repository.EventoRepository;
import org.acme.repository.UsuarioRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventoService {

    @Inject
    JsonWebToken jwt;

    @Inject
    EventoRepository eventoRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    public List<EventoDTO> findAll() {
        return eventoRepository.listAll().stream()
                .map(EventoDTO::from)
                .collect(Collectors.toList());
    }

    public EventoDTO findById(Long id) {
        Evento evento = eventoRepository.findById(id);
        if (evento == null) {
            throw new NotFoundException("Evento não encontrado.");
        }
        return EventoDTO.from(evento);
    }

    @Transactional
    public EventoDTO create(EventoDTO dto, String username) {

        Evento entity = new Evento();
        entity.setNome(dto.nome());
        entity.setDescricao(dto.descricao());
        entity.setDataHora(dto.dataHora());
        entity.setLocal(dto.local());
        entity.setRequisitos(dto.requisitos());
        entity.setCategoria(dto.categoria());
        entity.setContato(dto.contato());
        entity.setParticipantes(dto.participantes() != null ? dto.participantes() : 0); 
        
        
        entity.setOrganizador(username);
        
        entity.setImagemPrincipal(dto.imagemPrincipal());
        entity.setLinkInscricao(dto.linkInscricao());
        entity.setArquivos(dto.arquivos());
        
        eventoRepository.persist(entity);

        return EventoDTO.from(entity);
    }

    @Transactional
    public EventoDTO update(Long id, EventoDTO dto) {
        Evento entity = eventoRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Evento não encontrado.");
        }

        String username = jwt.getSubject();

        if (username == null) {
            throw new NotFoundException("Usuário não autenticado.");
        }

        // Atualiza os campos
        entity.setNome(dto.nome());
        entity.setDescricao(dto.descricao());
        entity.setDataHora(dto.dataHora());
        entity.setLocal(dto.local());
        entity.setCategoria(dto.categoria());
        entity.setContato(dto.contato());
        entity.setRequisitos(dto.requisitos());
        entity.setParticipantes(dto.participantes());
        entity.setImagemPrincipal(dto.imagemPrincipal());
        entity.setLinkInscricao(dto.linkInscricao());
        entity.setArquivos(dto.arquivos());
        
        // Mantém o organizador original
        // entity.setOrganizador(username); <- Não muda o organizador no update

        return EventoDTO.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Evento entity = eventoRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Evento não encontrado.");
        }
        eventoRepository.delete(entity);
    }

    /**
 * Atualiza a imagem principal de um evento
 */
@Transactional
public void atualizarImagemPrincipal(Long idEvento, String nomeArquivo) {
    Evento evento = eventoRepository.findById(idEvento);
    if (evento == null) {
        throw new NotFoundException("Evento não encontrado.");
    }
    
    evento.setImagemPrincipal(nomeArquivo);
    eventoRepository.persist(evento);
}

/**
 * Remove a imagem principal de um evento
 */
@Transactional
public void removerImagemPrincipal(Long idEvento) {
    Evento evento = eventoRepository.findById(idEvento);
    if (evento == null) {
        throw new NotFoundException("Evento não encontrado.");
    }
    
    evento.setImagemPrincipal(null);
    eventoRepository.persist(evento);
}
    // Adicione no EventoService.java
public String salvarImagemPrincipal(File imagem, String nomeArquivo) throws Exception {
    // Solução SIMPLES: salva localmente (sem Cloudinary)
    String uploadDir = System.getProperty("user.home") + "/uploads/eventos/";
    File dir = new File(uploadDir);
    if (!dir.exists()) dir.mkdirs();
    
    String nomeUnico = UUID.randomUUID().toString() + "_" + nomeArquivo;
    File destino = new File(uploadDir + nomeUnico);
    
    // Copia arquivo
    try (InputStream in = new FileInputStream(imagem);
         OutputStream out = new FileOutputStream(destino)) {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }
    
    // Retorna URL pública (precisa configurar servidor para servir arquivos)
    return "/uploads/eventos/" + nomeUnico;
}
}