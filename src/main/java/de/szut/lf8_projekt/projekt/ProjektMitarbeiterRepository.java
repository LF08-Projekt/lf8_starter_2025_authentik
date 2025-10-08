package de.szut.lf8_projekt.projekt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjektMitarbeiterRepository extends JpaRepository<ProjektMitarbeiterEntity, Long> {
    Optional<ProjektMitarbeiterEntity> findByProjektIdAndMitarbeiterId(Long projektId, Long mitarbeiterId);
}
