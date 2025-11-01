package de.szut.lf8_projekt.projekt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProjektRepository extends JpaRepository<ProjektEntity, Long> {
    public List<ProjektEntity> findAllByStartdatumIsBetweenOrGeplantesEnddatumIsBetween(Date startDatum, Date startDatum2,
                                                                                        Date endDatum, Date endDatum2);
}
