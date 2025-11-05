package de.szut.lf8_projekt.projekt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-Repository für Projekte.
 * Bietet Datenbankzugriff für ProjektEntity-Objekte.
 */
public interface ProjektRepository extends JpaRepository<ProjektEntity, Long> {
    /**
     * Findet alle Projekte, deren Startdatum oder geplantes Enddatum im angegebenen Zeitraum liegt.
     * Wird verwendet um Überschneidungen bei Mitarbeiterzuordnungen zu prüfen.
     *
     * @param startDatum Beginn des Zeitraums für Startdatum-Prüfung
     * @param startDatum2 Ende des Zeitraums für Startdatum-Prüfung
     * @param endDatum Beginn des Zeitraums für Enddatum-Prüfung
     * @param endDatum2 Ende des Zeitraums für Enddatum-Prüfung
     * @return Liste aller Projekte im angegebenen Zeitraum
     */
    public List<ProjektEntity> findAllByStartdatumIsBetweenOrGeplantesEnddatumIsBetween(LocalDateTime startDatum, LocalDateTime startDatum2,
                                                                                        LocalDateTime endDatum, LocalDateTime endDatum2);
}
