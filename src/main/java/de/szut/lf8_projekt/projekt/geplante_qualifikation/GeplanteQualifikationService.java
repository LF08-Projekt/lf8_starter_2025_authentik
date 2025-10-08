package de.szut.lf8_projekt.projekt.geplante_qualifikation;

import java.util.List;
import java.util.Optional;

public class GeplanteQualifikationService {
    private final GeplanteQualifikationRepository repository;

    public GeplanteQualifikationService(GeplanteQualifikationRepository repository) {
        this.repository = repository;
    }

    public GeplanteQualifikationEntity create(GeplanteQualifikationEntity entity) {
        return this.repository.save(entity);
    }

    public List<GeplanteQualifikationEntity> readAll() {
        return this.repository.findAll();
    }

    public GeplanteQualifikationEntity readById(Long id) {
        Optional<GeplanteQualifikationEntity> optionalGeplanteQualifikation = this.repository.findById(id);
        return optionalGeplanteQualifikation.orElse(null);
    }

    public void delete(GeplanteQualifikationEntity entity) {
        this.repository.delete(entity);
    }
}
