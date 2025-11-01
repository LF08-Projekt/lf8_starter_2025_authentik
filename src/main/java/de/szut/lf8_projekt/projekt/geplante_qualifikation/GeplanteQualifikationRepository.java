package de.szut.lf8_projekt.projekt.geplante_qualifikation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeplanteQualifikationRepository extends JpaRepository<GeplanteQualifikationEntity, Long> {
    public List<GeplanteQualifikationEntity> getGeplanteQualifikationEntitiesByProjektId(Long projektID);
}
