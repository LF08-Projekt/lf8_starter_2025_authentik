package de.szut.lf8_projekt.projekt.geplante_qualifikation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA-Repository für geplante Qualifikationen.
 * Bietet Datenbankzugriff für GeplanteQualifikationEntity-Objekte.
 */
public interface GeplanteQualifikationRepository extends JpaRepository<GeplanteQualifikationEntity, Long> {
    /**
     * Findet alle geplanten Qualifikationen für ein bestimmtes Projekt.
     *
     * @param projektID Die ID des Projekts
     * @return Liste aller für das Projekt geplanten Qualifikationen
     */
    public List<GeplanteQualifikationEntity> getGeplanteQualifikationEntitiesByProjektId(Long projektID);
}
