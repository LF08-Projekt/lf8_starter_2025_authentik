package de.szut.lf8_projekt.projekt.geplante_qualifikation;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service-Klasse für die Geschäftslogik rund um geplante Qualifikationen.
 * Verwaltet die Qualifikationen, die für Projekte benötigt werden.
 */
@Service
public class GeplanteQualifikationService {
    private final GeplanteQualifikationRepository repository;

    /**
     * Konstruktor für den GeplanteQualifikationService.
     *
     * @param repository Repository für geplante Qualifikationen
     */
    public GeplanteQualifikationService(GeplanteQualifikationRepository repository) {
        this.repository = repository;
    }

    /**
     * Erstellt eine neue geplante Qualifikation.
     *
     * @param entity Die zu speichernde Qualifikation
     * @return Die gespeicherte Qualifikation mit generierter ID
     */
    public GeplanteQualifikationEntity create(GeplanteQualifikationEntity entity) {
        return this.repository.save(entity);
    }

    /**
     * Liest alle geplanten Qualifikationen.
     *
     * @return Liste aller geplanten Qualifikationen
     */
    public List<GeplanteQualifikationEntity> readAll() {
        return this.repository.findAll();
    }

    /**
     * Liest eine geplante Qualifikation anhand ihrer ID.
     *
     * @param id Die ID der Qualifikation
     * @return Die gefundene Qualifikation oder null wenn nicht gefunden
     */
    public GeplanteQualifikationEntity readById(Long id) {
        Optional<GeplanteQualifikationEntity> optionalGeplanteQualifikation = this.repository.findById(id);
        return optionalGeplanteQualifikation.orElse(null);
    }

    /**
     * Löscht eine geplante Qualifikation.
     *
     * @param entity Die zu löschende Qualifikation
     */
    public void delete(GeplanteQualifikationEntity entity) {
        this.repository.delete(entity);
    }

    /**
     * Findet alle geplanten Qualifikationen für ein bestimmtes Projekt.
     *
     * @param projektId Die ID des Projekts
     * @return Liste aller für das Projekt geplanten Qualifikationen
     */
    public List<GeplanteQualifikationEntity> readByProjektId(Long projektId) {
        return this.repository.getGeplanteQualifikationEntitiesByProjektId(projektId);
    }

}
