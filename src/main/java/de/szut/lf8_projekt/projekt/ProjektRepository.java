package de.szut.lf8_projekt.projekt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjektRepository extends JpaRepository<ProjektEntity, Long> {
    public List<ProjektEntity> findAllByStartdatumIsBetweenOrGeplantesEnddatumIsBetween(LocalDateTime startDatum, LocalDateTime startDatum2,
                                                                                        LocalDateTime endDatum, LocalDateTime endDatum2);
}
