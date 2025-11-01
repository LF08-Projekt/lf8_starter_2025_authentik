package de.szut.lf8_projekt.projekt.geplante_qualifikation;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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

    public List<GeplanteQualifikationEntity> readByProjektId(Long projektId) {
        return this.repository.getGeplanteQualifikationEntitiesByProjektId(projektId);
    }

    public void delete(GeplanteQualifikationEntity entity) {
        this.repository.delete(entity);
    }

    public List<GeplanteQualifikationEntity> readByProjektId(Long projektId) {
        return this.repository.getGeplanteQualifikationEntitiesByProjektId(projektId); //TODO
    }
}
