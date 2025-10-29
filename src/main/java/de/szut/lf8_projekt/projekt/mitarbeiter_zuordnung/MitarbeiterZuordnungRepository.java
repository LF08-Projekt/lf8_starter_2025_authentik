package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MitarbeiterZuordnungRepository extends JpaRepository<MitarbeiterZuordnungEntity, Long> {
    public MitarbeiterZuordnungEntity findByProjektIdAndMitarbeiterId(Long projektId, Long mitarbeiterId);
}
