package de.szut.lf8_projekt.projekt;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjektService {
    private final ProjektRepository repository;

    public ProjektService(ProjektRepository repository) {
        this.repository = repository;
    }

    public ProjektEntity create(ProjektEntity entity) {
        return this.repository.save(entity);
    }

    public List<ProjektEntity> readAll() {
        return this.repository.findAll();
    }

    public ProjektEntity readById(Long id) {
        Optional<ProjektEntity> optionalProjekt = this.repository.findById(id);
        return optionalProjekt.orElse(null);
    }

    public void delete(ProjektEntity entity) {
        this.repository.delete(entity);
    }
}
