package org.acme.repository;

import org.acme.model.Evento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class EventoRepository implements PanacheRepository<Evento> {

    public List<Evento> findByNome(String nome) {
        return find("UPPER(nome) LIKE ?1", "%" + nome.toUpperCase() + "%").list();
    }

    public List<Evento> findByOrganizador(String organizador) {
        return find("organizador", organizador).list();
    }
}