package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import de.szut.lf8_projekt.projekt.ProjektEntity;
import de.szut.lf8_projekt.projekt.ProjektService;
import org.springframework.stereotype.Service;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterEntfernenResponseDto;
import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service-Klasse für die Geschäftslogik rund um Mitarbeiter-Projekt-Zuordnungen.
 * Verwaltet die Zuordnung von Mitarbeitern zu Projekten und prüft deren Verfügbarkeit.
 */
@Service
public class MitarbeiterZuordnungService {
    private final MitarbeiterZuordnungRepository repository;
    private final ProjektService projektService;
    private final MitarbeiterApiService mitarbeiterApiService;

    /**
     * Konstruktor für den MitarbeiterZuordnungService.
     *
     * @param repository Repository für Mitarbeiter-Zuordnungen
     * @param projektService Service für Projekt-Operationen
     * @param mitarbeiterApiService Service für externe Mitarbeiter-API-Aufrufe
     */
    public MitarbeiterZuordnungService(MitarbeiterZuordnungRepository repository,
                                       ProjektService projektService,
                                       MitarbeiterApiService mitarbeiterApiService) {
        this.repository = repository;
        this.projektService = projektService;
        this.mitarbeiterApiService = mitarbeiterApiService;
    }

    /**
     * Erstellt eine neue Mitarbeiter-Projekt-Zuordnung.
     *
     * @param entity Die zu speichernde Zuordnung
     * @return Die gespeicherte Zuordnung mit generierter ID
     */
    public MitarbeiterZuordnungEntity create(MitarbeiterZuordnungEntity entity) {
        return this.repository.save(entity);
    }

    /**
     * Liest alle Mitarbeiter-Projekt-Zuordnungen.
     *
     * @return Liste aller Zuordnungen
     */
    public List<MitarbeiterZuordnungEntity> readAll() {
        return this.repository.findAll();
    }

    /**
     * Findet alle Mitarbeiterzuordnungen für ein bestimmtes Projekt.
     *
     * @param projektId Die ID des Projekts
     * @return Liste aller Mitarbeiter, die dem Projekt zugeordnet sind
     */
    public List<MitarbeiterZuordnungEntity> getMitarbeiterZuordnungEntitiesByProjektId(Long projektId) {
        return this.repository.getMitarbeiterZuordnungEntitiesByProjektId(projektId);
    }

    /**
     * Findet alle Projektzuordnungen für einen bestimmten Mitarbeiter.
     *
     * @param mitarbeiterId Die ID des Mitarbeiters
     * @return Liste aller Projekte, denen der Mitarbeiter zugeordnet ist
     */
    public List<MitarbeiterZuordnungEntity> getMitarbeiterZuordnungEntitiesByMitarbeiterId(Long mitarbeiterId) {
        return this.repository.getMitarbeiterZuordnungEntitiesByMitarbeiterId(mitarbeiterId);
    }

    /**
     * Liest eine Mitarbeiter-Zuordnung anhand ihrer ID.
     *
     * @param id Die ID der Zuordnung
     * @return Die gefundene Zuordnung oder null wenn nicht gefunden
     */
    public MitarbeiterZuordnungEntity readById(Long id) {
        Optional<MitarbeiterZuordnungEntity> optionalProjekt = this.repository.findById(id);
        return optionalProjekt.orElse(null);
    }

    /**
     * Löscht eine Mitarbeiter-Projekt-Zuordnung.
     *
     * @param entity Die zu löschende Zuordnung
     */
    public void delete(MitarbeiterZuordnungEntity entity) {
        this.repository.delete(entity);
    }

    /**
     * Entfernt einen Mitarbeiter aus einem Projekt.
     * Validiert zunächst die Existenz von Projekt und Mitarbeiter,
     * prüft ob eine Zuordnung besteht und löscht diese dann aus der Datenbank.
     *
     * @param projektId Die ID des Projekts, aus dem der Mitarbeiter entfernt werden soll
     * @param mitarbeiterId Die ID des Mitarbeiters, der entfernt werden soll
     * @return Ein DTO mit den Informationen zum entfernten Mitarbeiter und Projekt
     * @throws IllegalArgumentException wenn projektId oder mitarbeiterId null oder <= 0 ist
     * @throws ResourceNotFoundException wenn das Projekt nicht existiert
     * @throws ResourceNotFoundException wenn der Mitarbeiter nicht existiert
     * @throws ResourceNotFoundException wenn der Mitarbeiter nicht in dem Projekt arbeitet
     */
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

    /**
     * Prüft ob ein Mitarbeiter einem bestimmten Projekt zugeordnet ist.
     *
     * @param projektId Die ID des Projekts
     * @param mitarbeiterId Die ID des Mitarbeiters
     * @return true wenn der Mitarbeiter dem Projekt zugeordnet ist, sonst false
     */
    public boolean projektHasMitarbeiter(Long projektId, Long mitarbeiterId) {
        Optional<MitarbeiterZuordnungEntity> mitarbeiterZuordnungEntity = this.repository.findByProjektIdAndMitarbeiterId(projektId, mitarbeiterId);
        if (mitarbeiterZuordnungEntity.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prüft ob ein Mitarbeiter für ein Projekt verfügbar ist.
     * Ein Mitarbeiter ist nicht verfügbar wenn er bereits in einem Projekt arbeitet,
     * dessen Zeitraum sich mit dem neuen Projekt überschneidet.
     *
     * @param mitarbeiterZuordnungDto Der Mitarbeiter dessen Verfügbarkeit geprüft werden soll
     * @param projekt Das Projekt für das die Verfügbarkeit geprüft werden soll
     * @return true wenn der Mitarbeiter verfügbar ist, false wenn er bereits in einem überschneidenden Projekt arbeitet
     */
    public boolean isMitarbeiterAvailable(MitarbeiterDto mitarbeiterZuordnungDto, ProjektEntity projekt) {
        List<ProjektEntity> projekte = this.projektService.readByDate(projekt.getStartdatum(), projekt.getGeplantesEnddatum());
        for (ProjektEntity tmpProjekt: projekte) {
            if (this.projektHasMitarbeiter(tmpProjekt.getId(), mitarbeiterZuordnungDto.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Findet alle Projekte eines Mitarbeiters.
     *
     * @param mitarbeiterId Die ID des Mitarbeiters
     * @return Liste aller Projektzuordnungen des Mitarbeiters
     * @throws ResourceNotFoundException wenn keine Projekte für den Mitarbeiter gefunden wurden
     */
    public List<MitarbeiterZuordnungEntity> getAllProjektsFromMitarbeiter(Long mitarbeiterId) {
        Optional<List<MitarbeiterZuordnungEntity>> mitarbeiterZuordnungen = this.repository.findAllByMitarbeiterId(mitarbeiterId);
        if(mitarbeiterZuordnungen.isEmpty()) {
            throw new ResourceNotFoundException("Es wurden keine Projekte mit der Mitarbeiter Id" + mitarbeiterId + " gefundne");
        }
        return mitarbeiterZuordnungen.get();
    }

}
