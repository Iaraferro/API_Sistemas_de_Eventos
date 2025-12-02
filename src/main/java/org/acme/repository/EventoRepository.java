package org.acme.repository; // Pacote atualizado

import org.acme.model.Evento; // Import atualizado
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class EventoRepository implements PanacheRepository<Evento> {

    public List<Evento> findByNome(String nome) {
        return find("UPPER(nome) LIKE ?1", "%" + nome.toUpperCase() + "%").list();
    }

    public List<Evento> findByOrganizador(Long idOrganizador) {
        return find("organizador.id", idOrganizador).list();
    }

}