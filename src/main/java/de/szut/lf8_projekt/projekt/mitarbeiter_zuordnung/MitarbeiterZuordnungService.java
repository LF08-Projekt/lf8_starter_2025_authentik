package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MitarbeiterZuordnungService {
    private final MitarbeiterZuordnungRepository repository;

    public MitarbeiterZuordnungService(MitarbeiterZuordnungRepository repository) {
        this.repository = repository;
    }

    public MitarbeiterZuordnungEntity create(MitarbeiterZuordnungEntity entity) {
        return this.repository.save(entity);
    }

    public List<MitarbeiterZuordnungEntity> readAll() {
        return this.repository.findAll();
    }

    public MitarbeiterZuordnungEntity readById(Long id) {
        Optional<MitarbeiterZuordnungEntity> optionalProjekt = this.repository.findById(id);
        return optionalProjekt.orElse(null);
    }

    public void delete(MitarbeiterZuordnungEntity entity) {
        this.repository.delete(entity);
    }
}
