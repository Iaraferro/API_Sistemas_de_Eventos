package org.acme.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.acme.dto.InscricaoRequestDTO;
import org.acme.dto.InscricaoResponseDTO;
import org.acme.model.Evento;
import org.acme.model.Inscricao;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class InscricaoService {
    @Transactional
    public InscricaoResponseDTO salvar(InscricaoRequestDTO dto) {

        Evento evento = Evento.findById(dto.eventoId());

        if (evento == null) {
            throw new NotFoundException("Evento não encontrado.");
        }

        boolean emailJaExiste = Inscricao.count(
            "evento.id = ?1 and lower(email) = lower(?2)",
            dto.eventoId(),
            dto.email()
        ) > 0;

        if (emailJaExiste) {
            throw new BadRequestException("Este e-mail já foi inscrito neste evento.");
        }

        Inscricao inscricao = new Inscricao();
        inscricao.setEvento(evento);
        inscricao.setNome(dto.nome());
        inscricao.setEmail(dto.email());
        inscricao.setTelefone(dto.telefone());
        inscricao.setDataInscricao(LocalDateTime.now());

        inscricao.persist();

        return toResponse(inscricao);
    }

    public List<InscricaoResponseDTO> listarPorEvento(Long eventoId, int page, int size) {

        return Inscricao.find(
                "evento.id = ?1 order by dataInscricao desc",
                eventoId
        )
        .page(Page.of(page, size))
        .list()
        .stream()
        .map(i -> toResponse((Inscricao) i))
        .collect(Collectors.toList());
    }

    @Transactional
    public void deletar(Long id) {

        Inscricao inscricao = Inscricao.findById(id);

        if (inscricao == null) {
            throw new NotFoundException("Inscrição não encontrada.");
        }

        inscricao.delete();
    }

    private InscricaoResponseDTO toResponse(Inscricao i) {
        return new InscricaoResponseDTO(
            i.getId(),
            i.getNome(),
            i.getEmail(),
            i.getTelefone(),
            i.getEvento().getNome(),
            i.getDataInscricao()
        );
    }
}
