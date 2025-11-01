package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProjektService {
    private final ProjektRepository repository;

    public ProjektService(ProjektRepository repository) {
        this.repository = repository;
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

    public List<ProjektEntity> readByDate(LocalDateTime startdatum, LocalDateTime endDatum) {
        return this.repository.findAllByStartdatumIsBetweenOrGeplantesEnddatumIsBetween(startdatum, endDatum, startdatum, endDatum);
    }
}
