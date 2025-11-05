package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MitarbeiterZuordnungRepository extends JpaRepository<MitarbeiterZuordnungEntity, Long> {
    Optional<MitarbeiterZuordnungEntity> findByProjektIdAndMitarbeiterId(Long projektId, Long mitarbeiterId);
    List<MitarbeiterZuordnungEntity> getMitarbeiterZuordnungEntitiesByProjektId(Long projektId);
    List<MitarbeiterZuordnungEntity> getMitarbeiterZuordnungEntitiesByMitarbeiterId(Long mitarbeiterId);
}
