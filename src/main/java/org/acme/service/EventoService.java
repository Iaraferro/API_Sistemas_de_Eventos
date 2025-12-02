package org.acme.service; // Pacote atualizado

// Imports atualizados
import org.acme.dto.EventoDTO;
import org.acme.dto.EventoResponseDTO;
import org.acme.model.Evento;
import org.acme.repository.EventoRepository;
import org.acme.repository.UsuarioRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventoService {

    @Inject
    JsonWebToken jwt;

    @Inject
    EventoRepository eventoRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    public List<EventoDTO> listAll() {
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
        entity.setCategoria(dto.categoria());     // ← ADICIONAR
        entity.setContato(dto.contato());
        entity.setParticipantes(dto.participantes() != null ? dto.participantes() : 0); 
        // ← AUTOMÁTICO, sem enviar idOrganizador
        entity.setOrganizador(username);
        entity.setImagem(dto.imagem());
        entity.setOrganizador("admin@ecoeventos.com");

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
            throw new NotFoundException("Organizador (Usuário) não encontrado.");
        }

        // Usando setters
        entity.setNome(dto.nome());
        entity.setDescricao(dto.descricao());
        entity.setDataHora(dto.dataHora());
        entity.setLocal(dto.local());
        entity.setOrganizador(username);
        entity.setArquivos(dto.arquivos());

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

    public List<EventoResponseDTO> findAll() {

        return eventoRepository.findAll().stream()
                .map(EventoDTO::from)
                .map(EventoResponseDTO::valueOf)
                .collect(Collectors.toList());

    }



}