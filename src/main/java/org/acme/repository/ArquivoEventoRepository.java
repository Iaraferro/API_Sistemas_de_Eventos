package org.acme.repository;

import org.acme.model.ArquivoEvento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ArquivoEventoRepository implements PanacheRepository<ArquivoEvento> {

    public List<ArquivoEvento> findByEvento(Long idEvento) {
        return find("evento.id", idEvento).list();
    }

}
