package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository für Mitarbeiter-Projekt-Zuordnungen.
 */
public interface MitarbeiterZuordnungRepository extends JpaRepository<MitarbeiterZuordnungEntity, Long> {
    /**
     * Sucht eine Zuordnung anhand Projekt- und Mitarbeiter-ID.
     *
     * @param projektId Projekt-ID
     * @param mitarbeiterId Mitarbeiter-ID
     * @return Optionale Zuordnung, falls vorhanden
     */
    Optional<MitarbeiterZuordnungEntity> findByProjektIdAndMitarbeiterId(Long projektId, Long mitarbeiterId);
    /**
     * Sucht alle Zuordnungen eines Mitarbeiters.
     *
     * @param mitarbeiterId Mitarbeiter-ID
     * @return Optionale Liste von Zuordnungen
     */
    Optional<List<MitarbeiterZuordnungEntity>> findAllByMitarbeiterId(Long mitarbeiterId);
    /**
     * Sucht alle Zuordnungen eines Projekts.
     *
     * @param projektId Projekt-ID
     * @return Liste von Zuordnungen
     */
    List<MitarbeiterZuordnungEntity> findAllByProjektId(Long projektId);
    /**
     * Alias zu {@link #findAllByProjektId(Long)}.
     */
    List<MitarbeiterZuordnungEntity> getMitarbeiterZuordnungEntitiesByProjektId(Long projektId);
    /**
     * Sucht alle Zuordnungen eines Mitarbeiters.
     *
     * @param mitarbeiterId Mitarbeiter-ID
     * @return Liste von Zuordnungen
     */
    List<MitarbeiterZuordnungEntity> getMitarbeiterZuordnungEntitiesByMitarbeiterId(Long mitarbeiterId);
}
