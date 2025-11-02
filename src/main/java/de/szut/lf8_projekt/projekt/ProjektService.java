package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProjektService {
    private final ProjektRepository repository;
    private final ProjektMappingService projektMappingService;

    public ProjektService(ProjektRepository repository, ProjektMappingService projektMappingService) {
        this.repository = repository;
        this.projektMappingService = projektMappingService;
    }

    public ProjektEntity create(ProjektEntity entity) {
        return this.repository.save(entity);
    }

    public List<ProjektEntity> readAll() {
        return this.repository.findAll();
    }

    public ProjektEntity readById(Long id) {
        Optional<ProjektEntity> optionalProjekt = this.repository.findById(id);
        if (optionalProjekt.isEmpty()) {
            throw new ResourceNotFoundException("Das Projekt mit der id " + id + " existiert nicht.");
        }
        return optionalProjekt.get();
    }

    public void delete(ProjektEntity entity) {
        this.repository.delete(entity);
    }

    public List<ProjektEntity> readByDate(Date startdatum, Date endDatum) {
        return this.repository.findAllByStartdatumIsBetweenOrGeplantesEnddatumIsBetween(startdatum, endDatum, startdatum, endDatum);
    }

    public List<ProjektCompactDto> readByMitarbeiterId(List<MitarbeiterZuordnungEntity> mitarbeiterZuordnungen) {
        List<ProjektCompactDto> projekts = new ArrayList<>();
        for(MitarbeiterZuordnungEntity mitarbeiterZuordnung: mitarbeiterZuordnungen) {
            Optional<ProjektEntity> projekt = this.repository.findById(mitarbeiterZuordnung.getProjektId());
            if (projekt.isPresent()) {
                ProjektCompactDto projektCompactDto = this.projektMappingService.mapProjektEntityToProjektByMitarbeiterDto(projekt.get());
                projekts.add(projektCompactDto);
            }
        }
        return projekts;
    }

}
