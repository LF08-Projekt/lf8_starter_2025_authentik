package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import de.szut.lf8_projekt.projekt.ProjektEntity;
import de.szut.lf8_projekt.projekt.ProjektService;
import org.springframework.stereotype.Service;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterEntfernenResponseDto;
import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MitarbeiterZuordnungService {
    private final MitarbeiterZuordnungRepository repository;
    private final ProjektService projektService;
    private final MitarbeiterApiService mitarbeiterApiService;

    public MitarbeiterZuordnungService(MitarbeiterZuordnungRepository repository,
                                       ProjektService projektService,
                                       MitarbeiterApiService mitarbeiterApiService) {
        this.repository = repository;
        this.projektService = projektService;
        this.mitarbeiterApiService = mitarbeiterApiService;
    }

    public MitarbeiterZuordnungEntity create(MitarbeiterZuordnungEntity entity) {
        return this.repository.save(entity);
    }

    public List<MitarbeiterZuordnungEntity> readAll() {
        return this.repository.findAll();
    }

    public MitarbeiterZuordnungEntity readById(Long id) {
        Optional<MitarbeiterZuordnungEntity> optionalProjekt = this.repository.findById(id);
        return optionalProjekt.orElse(null);
    }

    public void delete(MitarbeiterZuordnungEntity entity) {
        this.repository.delete(entity);
    }

    public MitarbeiterEntfernenResponseDto entferneMitarbeiterAusProjekt(Long projektId, Long mitarbeiterId) {
        // Validiere IDs
        if (projektId == null || projektId <= 0) {
            throw new IllegalArgumentException("Projekt-ID muss größer als 0 sein");
        }
        if (mitarbeiterId == null || mitarbeiterId <= 0) {
            throw new IllegalArgumentException("Mitarbeiter-ID muss größer als 0 sein");
        }

        // Prüfe ob Projekt existiert
        ProjektEntity projekt = projektService.readById(projektId);
        if (projekt == null) {
            throw new ResourceNotFoundException("Projekt mit der ID " + projektId + " existiert nicht");
        }

        // Prüfe ob Mitarbeiter existiert
        MitarbeiterDto mitarbeiter = mitarbeiterApiService.getMitarbeiterById(mitarbeiterId);
        if (mitarbeiter == null) {
            throw new ResourceNotFoundException("Mitarbeiter mit der ID " + mitarbeiterId + " existiert nicht");
        }

        // Prüfe ob Zuordnung existiert
        Optional<MitarbeiterZuordnungEntity> zuordnung = repository.findByProjektIdAndMitarbeiterId(projektId, mitarbeiterId);
        if (zuordnung.isEmpty()) {
            throw new ResourceNotFoundException(
                "Mitarbeiter mit der Mitarbeiternummer " + mitarbeiterId +
                " arbeitet nicht in dem angegebenen Projekt mit der Projekt-ID " + projektId
            );
        }

        // Lösche die Zuordnung
        repository.delete(zuordnung.get());

        // Erstelle Response DTO
        return new MitarbeiterEntfernenResponseDto(
            mitarbeiterId,
            mitarbeiter.getVollstaendigerName(),
            projektId,
            projekt.getBezeichnung()
        );
    }

    public boolean projektHasMitarbeiter(Long projektId, Long mitarbeiterId) {
        Optional<MitarbeiterZuordnungEntity> mitarbeiterZuordnungEntity = this.repository.findByProjektIdAndMitarbeiterId(projektId, mitarbeiterId);
        if (mitarbeiterZuordnungEntity.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMitarbeiterAvailable(MitarbeiterDto mitarbeiterZuordnungDto, ProjektEntity projekt) {
        List<ProjektEntity> projekte = this.projektService.readByDate(projekt.getStartdatum(), projekt.getGeplantesEnddatum());
        for (ProjektEntity tmpProjekt: projekte) {
            if (this.projektHasMitarbeiter(tmpProjekt.getId(), mitarbeiterZuordnungDto.getId())) {
                return false;
            }
        }
        return true;
    }

    public List<MitarbeiterZuordnungEntity> getAllProjektsFromMitarbeiter(Long mitarbeiterId) {
        Optional<List<MitarbeiterZuordnungEntity>> mitarbeiterZuordnungen = this.repository.findAllByMitarbeiterId(mitarbeiterId);
        if(mitarbeiterZuordnungen.isEmpty()) {
            throw new ResourceNotFoundException("Es wurden keine Projekte mit der Mitarbeiter Id" + mitarbeiterId + " gefundne");
        }
        return mitarbeiterZuordnungen.get();
    }

}
