package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.exceptionHandling.*;
import de.szut.lf8_projekt.mapping.MitarbeiterMappingService;
import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import de.szut.lf8_projekt.ValidationService;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationService;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.QualifikationApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungDto;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungService;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_projekt.projekt.dto.ProjektGetDto;
import de.szut.lf8_projekt.projekt.dto.ProjektLoeschenResponseDto;
import de.szut.lf8_projekt.projekt.dto.ProjektMitarbeiterGetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterEntfernenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * REST-Controller für die Verwaltung von Projekten.
 * Bietet Endpunkte für CRUD-Operationen auf Projekten sowie für die Verwaltung von Mitarbeiterzuordnungen.
 */
@RestController
@RequestMapping(value="/LF08Projekt")
@Validated
@Tag(name = "Projekt-Verwaltung", description = "Verwaltung von Projekten, Mitarbeiterzuordnungen und Qualifikationen")
public class ProjektController {
    private final MitarbeiterMappingService mitarbeiterMappingService;
    private final MitarbeiterApiService mitarbeiterApiService;
    private final ProjektService projektService;
    private final GeplanteQualifikationService geplanteQualifikationService;
    private final MitarbeiterZuordnungService mitarbeiterZuordnungService;
    private final ProjektMappingService projektMappingService;
    private final ValidationService validationService;
    private final QualifikationApiService qualifikationApiService;

    /**
     * Konstruktor für den ProjektController.
     *
     * @param projektService Service für Projekt-Operationen
     * @param geplanteQualifikationService Service für geplante Qualifikationen
     * @param mitarbeiterZuordnungService Service für Mitarbeiterzuordnungen
     * @param projektMappingService Service für Projekt-Mappings
     * @param validationService Service für Validierungen
     * @param mitarbeiterMappingService Service für Mitarbeiter-Mappings
     * @param mitarbeiterApiService Service für externe Mitarbeiter-API-Aufrufe
     * @param qualifikationApiService Service für externe Qualifikations-API-Aufrufe
     */
    public ProjektController(ProjektService projektService,
                             GeplanteQualifikationService geplanteQualifikationService,
                             MitarbeiterZuordnungService mitarbeiterZuordnungService,
                             ProjektMappingService projektMappingService,
                             ValidationService validationService,
                             MitarbeiterMappingService mitarbeiterMappingService,
                             MitarbeiterApiService mitarbeiterApiService,
                             QualifikationApiService qualifikationApiService) {
        this.projektService = projektService;
        this.geplanteQualifikationService = geplanteQualifikationService;
        this.mitarbeiterZuordnungService = mitarbeiterZuordnungService;
        this.projektMappingService = projektMappingService;
        this.validationService = validationService;
        this.mitarbeiterApiService = mitarbeiterApiService;
        this.mitarbeiterMappingService = mitarbeiterMappingService;
        this.qualifikationApiService = qualifikationApiService;
    }

    /**
     * Legt ein neues Projekt an.
     * Validiert den verantwortlichen Mitarbeiter, Kunden und die geplanten Qualifikationen
     * über die externe API bevor das Projekt erstellt wird.
     *
     * @param dto Das DTO mit den Projektdaten
     * @param jwt Das JWT-Token für die Authentifizierung
     * @return Bestätigung mit den angelegten Projektdaten
     * @throws ResourceNotFoundException wenn Mitarbeiter, Kunde oder Qualifikationen nicht gefunden werden
     */
    @Operation(summary = "Legt ein neues Projekt an")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Das Projekt wurde erfolgreich angelegt",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjektCreateConfirmationDto.class))}),
            @ApiResponse(responseCode = "400", description = "Fehlende Pflichtangabe im DTO", content = @Content),
            @ApiResponse(responseCode = "404", description = "Der verantwortliche Mitarbeiter, Kunde oder" +
                    " eine der geplanten Qualifikationen konnte nicht gefunden werden", content = @Content),
            @ApiResponse(responseCode = "425", description = "Das geplante Ende des Projekts kann nicht vor dem wirklichen Ende des " +
                    "Projektes liegen", content = @Content),
            @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @PostMapping(path="/Projekt")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjektCreateConfirmationDto create(@RequestBody @Valid ProjektCreateDto dto, @AuthenticationPrincipal Jwt jwt) {
        String securityToken = jwt != null ? jwt.getTokenValue() : null;
        if (!this.validationService.validateMitarbeiterId(dto.getVerantwortlicherId(), securityToken)) {
            throw new ResourceNotFoundException("Mitarbeiter mit der ID " + dto.getVerantwortlicherId() + " existiert nicht!");
        }
        if (!this.validationService.validateKundenId(dto.getKundenId(), securityToken)) {
            throw new ResourceNotFoundException("Kunde mit der ID + " + dto.getKundenId() + " existiert nicht!");
        }
        if (dto.getGeplanteQualifikationen() != null && !this.validationService.validateQualifications(Arrays.asList(dto.getGeplanteQualifikationen()), securityToken)) {
            throw new ResourceNotFoundException("Liste der geplanten Qualifikationen enthält eine ungültige Qualifikation");
        }
        if (dto.getGeplantesEnddatum().isBefore(dto.getStartdatum())) {
            throw new SleepyException("Das geplante Ende des Projekts kann nicht vor dem Start des Projekts liegen");
        }

        ProjektEntity projektEntity = this.projektMappingService.mapProjektCreateDtoToProjektEntity(dto);
        projektEntity = this.projektService.create(projektEntity);

        List<String> geplanteQualifikationen = new ArrayList<>();
        if (dto.getGeplanteQualifikationen() != null) {
            SkillDto[] allQualifikations = this.qualifikationApiService.getAllQualifikations(securityToken);

            for (String qualifikation : dto.getGeplanteQualifikationen()) {
                SkillDto matchingQualifikation = Arrays.stream(allQualifikations)
                        .filter(x -> qualifikation.equals(x.getSkill())).findFirst().orElse(null);
                if (matchingQualifikation == null) {
                    throw new ResourceNotFoundException("Liste der geplanten Qualifikationen enthält eine ungültige Qualifikation");
                }

                GeplanteQualifikationEntity geplanteQualifikationEntity =
                        this.projektMappingService.mapDataToGeplanteQualifikationEntity(projektEntity.getId(), matchingQualifikation.getId());
                geplanteQualifikationEntity = this.geplanteQualifikationService.create(geplanteQualifikationEntity);
                geplanteQualifikationen.add(qualifikation);
            }
        }

        ProjektCreateConfirmationDto returnDto = this.projektMappingService.mapProjektEntityToProjektCreateConfirmationDto(projektEntity);
        returnDto.setGeplanteQualifikationen(geplanteQualifikationen);
        return returnDto;
    }

    /**
     * Fügt einen Mitarbeiter zu einem Projekt hinzu.
     * Validiert, dass die Qualifikation im Projekt benötigt wird, der Mitarbeiter diese besitzt
     * und im Projektzeitraum verfügbar ist.
     *
     * @param projekt_id Die ID des Projekts
     * @param mitarbeiter_id Die ID des Mitarbeiters
     * @param skill Die Qualifikation, mit der der Mitarbeiter zugeordnet werden soll
     * @return Bestätigung mit Projekt- und Mitarbeiterdaten
     * @throws ResourceNotFoundException wenn Projekt oder Mitarbeiter nicht gefunden werden
     * @throws IllegalArgumentException wenn Validierungen fehlschlagen
     */
    @Operation(summary = "Hinzufügen eines Mitarbeiters zu einem Projekt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mitarbeiter erfolgreich zu einem Projekt hinzugefügt"),
            @ApiResponse(responseCode = "400", description = "Falsche Qualifikation, Mitarbeiter besitzt die Qualifikation nicht oder Mitarbeiter ist bereits im Zeitraum verplant", content = @Content),
            @ApiResponse(responseCode = "404", description = "Projekt oder Mitarbeiter nicht gefunden", content = @Content),
            @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @PostMapping("{projekt_id}/mitarbeiter/{mitarbeiter_id}")
    public ResponseEntity<Object> addMitarbeiterToProjekt(@PathVariable final Long projekt_id, @PathVariable final Long mitarbeiter_id,
                                                                    @Valid @RequestBody final SkillDto skill,
                                                                    @AuthenticationPrincipal Jwt jwt) {
        String securityToken = jwt != null ? jwt.getTokenValue() : null;
    public ResponseEntity<Object> addMitarbeiterToProjekt(
            @Parameter(description = "ID des Projekts", example = "1") @PathVariable final Long projekt_id,
            @Parameter(description = "ID des Mitarbeiters", example = "42") @PathVariable final Long mitarbeiter_id,
            @Valid @RequestBody final SkillDto skill) {
        boolean qualifikationInProjekt = false;
        ProjektEntity projekt = this.projektService.readById(projekt_id);
        MitarbeiterDto mitarbeiterDto = this.mitarbeiterApiService.getMitarbeiterById(mitarbeiter_id, securityToken);

        if (projekt == null) {
            throw new ResourceNotFoundException("Das Projekt mit der id " + projekt_id + " existiert nicht.");
        }
        MitarbeiterDto mitarbeiterDto = this.mitarbeiterApiService.getMitarbeiterById(mitarbeiter_id);
        if (mitarbeiterDto == null) {
            throw new ResourceNotFoundException("Der Mitarbeiter mit der Id " + mitarbeiter_id + " existiert nicht");
        }
        List<GeplanteQualifikationEntity> geplanteQualifikationen = this.geplanteQualifikationService.readByProjektId(projekt_id);

        for (GeplanteQualifikationEntity geplanteQualifikation : geplanteQualifikationen) {
            if (Objects.equals(geplanteQualifikation.getQualifikation(), skill.getSkill())) {
                qualifikationInProjekt = true;
                break;
            }
        }
        if (!qualifikationInProjekt) {
            throw new IllegalArgumentException("Die angegebene Qualifikation mit der Id " + skill.getId() + " wird nicht im projekt benötigt.");
        }
        if (!mitarbeiterDto.getSkillSet().contains(skill)) {
            throw new IllegalArgumentException("Der Mitarbeiter mit der Id " + mitarbeiter_id + " besitzt die Qualifikation " + skill.getSkill() + " nicht.");
        }
        if (!this.mitarbeiterZuordnungService.isMitarbeiterAvailable(mitarbeiterDto, projekt)) {
            throw new IllegalArgumentException("Der Mitarbeiter ist bereits im Zeitraum des Projekts verplant.");
        }

        // Create mitarbeiterZuordnungDto
        MitarbeiterZuordnungDto mitarbeiterZuordnungDto = new MitarbeiterZuordnungDto();
        mitarbeiterZuordnungDto.setMitarbeiterId(mitarbeiter_id);
        mitarbeiterZuordnungDto.setProjektId(projekt_id);
        mitarbeiterZuordnungDto.setQualifikationId(skill.getId());

        MitarbeiterZuordnungEntity mitarbeiterZuordnung = this.mitarbeiterMappingService.mapMitarbeiterZuordnungDtoToMitarbeiterZuordnungEntity(mitarbeiterZuordnungDto);
        mitarbeiterZuordnung = this.mitarbeiterZuordnungService.create(mitarbeiterZuordnung);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("projektId", projekt_id);
        map.put("projektName", projekt.getBezeichnung());
        map.put("mitarbeiterId", mitarbeiter_id);
        map.put("mitarbeiterName", mitarbeiterDto.getVollstaendigerName());
        map.put("qualifikation", mitarbeiterDto.getSkillSet());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * Aktualisiert ein bestehendes Projekt.
     * Validiert alle geänderten Daten und prüft auf Konflikte bei Änderungen des Enddatums.
     *
     * @param dto Das DTO mit den zu aktualisierenden Projektdaten
     * @param projektId Die ID des zu aktualisierenden Projekts
     * @param jwt Das JWT-Token für die Authentifizierung
     * @return Bestätigung mit den aktualisierten Projektdaten
     * @throws ResourceNotFoundException wenn Projekt, Mitarbeiter, Kunde oder Qualifikationen nicht gefunden werden
     * @throws ResourceConflictException wenn Mitarbeiter im neuen Zeitraum bereits verplant sind
     */
    @Operation(summary = "Bearbeitet die Daten eines Projekts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Das Projekt wurde erfolgreich bearbeitet",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjektUpdateConfirmationDto.class))}),
            @ApiResponse(responseCode = "400", description = "Fehlende Pflichtangabe im DTO", content = @Content),
            @ApiResponse(responseCode = "404", description = "Das zu bearbeitende Projekt, der verantwortliche Mitarbeiter, Kunde oder" +
                    " eine der geplanten Qualifikationen konnte nicht gefunden werden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Beim Verschieben des geplanten Enddatum des Projektes ist ein Konflikt aufgetreten, " +
                    "weil einer der geplanten Mitarbeiter zu diesem Zeitpunkt bereits in einem anderen Projekt eingeplant ist", content = @Content),
            @ApiResponse(responseCode = "425", description = "Das geplante oder wirkliche Ende des Projekts kann nicht vor dem " +
                    "Start des Projektes liegen", content = @Content),
    })
    @PutMapping(path="/Projekt/{projektId}")
    public ProjektUpdateConfirmationDto updateProjekt(
            @RequestBody @Valid ProjektUpdateDto dto,
            @Parameter(description = "ID des zu aktualisierenden Projekts", example = "1") @PathVariable Long projektId,
            @AuthenticationPrincipal Jwt jwt) {
        ProjektEntity previousSaveState = this.projektService.readById(projektId);
        if (previousSaveState == null) {
            throw new ResourceNotFoundException("Projekt mit der ID " + projektId + " existiert nicht");
        }

        String securityToken = jwt != null ? jwt.getTokenValue() : null;
        if (dto.getVerantwortlicherId() != null && !this.validationService.validateMitarbeiterId(dto.getVerantwortlicherId(), securityToken)) {
            throw new ResourceNotFoundException("Mitarbeiter mit der ID " + dto.getVerantwortlicherId() + " existiert nicht!");
        }
        if (dto.getKundenId() != null && !this.validationService.validateKundenId(dto.getKundenId(), securityToken)) {
            throw new ResourceNotFoundException("Kunde mit der ID " + dto.getKundenId() + " existiert nicht!");
        }
        if (dto.getGeplanteQualifikationen() != null && !this.validationService.validateQualifications(Arrays.asList(dto.getGeplanteQualifikationen()), securityToken)) {
            throw new ResourceNotFoundException("Liste der geplanten Qualifikationen enthält eine ungültige Qualifikation");
        }
        if (dto.getWirklichesEnddatum() != null && dto.getWirklichesEnddatum().isBefore(dto.getStartdatum())) {
            throw new SleepyException("Das wirkliche Ende des Projekts kann nicht vor dem Start des Projekts liegen");
        }
        if (dto.getGeplantesEnddatum() != null) {
            if (dto.getGeplantesEnddatum().isBefore(dto.getStartdatum())) {
                throw new SleepyException("Das geplante Ende des Projekts kann nicht vor dem Start des Projekts liegen");
            }

            LocalDateTime neuesGeplantesEnddatum = dto.getGeplantesEnddatum();
            List<ProjektEntity> possibleCollisions = this.projektService.readByDate(previousSaveState.getGeplantesEnddatum(), neuesGeplantesEnddatum);
            for (ProjektEntity projektEntity : possibleCollisions) {
                if (projektEntity.getId() != projektId) {
                    List<MitarbeiterZuordnungEntity> mitarbeiterZuordnungEntities
                            = mitarbeiterZuordnungService.getMitarbeiterZuordnungEntitiesByProjektId(projektId);

                    for (MitarbeiterZuordnungEntity mitarbeiterZuordnungEntity : mitarbeiterZuordnungEntities) {
                        if (mitarbeiterZuordnungService.projektHasMitarbeiter(projektEntity.getId(), mitarbeiterZuordnungEntity.getMitarbeiterId())) {
                            throw new ResourceConflictException("Mitarbeiter " + mitarbeiterZuordnungEntity.getMitarbeiterId() + " ist" +
                                    " zu diesem Zeit bereits in Projekt " + mitarbeiterZuordnungEntity.getProjektId() + " verplant." +
                                    " Bitte klären sie diesen Konflikt");
                        }
                    }
                }
            }
        }

        ProjektEntity projektEntity = this.projektMappingService.mapProjektUpdateDtoToProjektEntity(dto, projektId);
        projektEntity = this.projektService.save(projektEntity);

        List<String> geplanteQualifikationen = new ArrayList<>();
        List<Long> geplanteQualifikationenIds = new ArrayList<>();
        if (dto.getGeplanteQualifikationen() != null) {
            List<GeplanteQualifikationEntity> qualifikationEntities = this.geplanteQualifikationService.readByProjektId(projektId);
            SkillDto[] allQualifikations = this.qualifikationApiService.getAllQualifikations(securityToken);

            for (String qualifikation : dto.getGeplanteQualifikationen()) {
                SkillDto matchingQualifikation = Arrays.stream(allQualifikations)
                        .filter(x -> qualifikation.equals(x.getSkill())).findFirst().orElse(null);
                if (matchingQualifikation == null) {
                    throw new ResourceNotFoundException("Liste der geplanten Qualifikationen enthält eine ungültige Qualifikation");
                }

                if (qualifikationEntities.stream().filter(x -> x.getQualifikationId() == matchingQualifikation.getId()).findAny().isEmpty()) {
                    GeplanteQualifikationEntity geplanteQualifikationEntity = this.projektMappingService.mapDataToGeplanteQualifikationEntity(projektEntity.getId(), matchingQualifikation.getId());
                    geplanteQualifikationEntity = this.geplanteQualifikationService.create(geplanteQualifikationEntity);
                    geplanteQualifikationen.add(qualifikation);
                    geplanteQualifikationenIds.add(matchingQualifikation.getId());
                }
            }
            for (GeplanteQualifikationEntity qualifikationEntity : qualifikationEntities) {
                if (geplanteQualifikationenIds.stream().filter(x -> x.equals(qualifikationEntity.getQualifikationId())).findAny().isEmpty()) {
                    this.geplanteQualifikationService.delete(qualifikationEntity);
                }
            }
        }

        ProjektUpdateConfirmationDto returnDto = this.projektMappingService.mapProjektEntityToProjektUpdateConfirmationDto(projektEntity);
        returnDto.setGeplanteQualifikationen(geplanteQualifikationen);
        return returnDto;
    }

    /**
     * Entfernt einen Mitarbeiter aus einem Projekt.
     *
     * @param projektId Die ID des Projekts
     * @param mitarbeiterId Die ID des zu entfernenden Mitarbeiters
     * @return Bestätigung mit den Details der entfernten Zuordnung
     * @throws ResourceNotFoundException wenn Projekt, Mitarbeiter oder Zuordnung nicht gefunden werden
     */
    @Operation(summary = "Entfernt einen Mitarbeiter aus einem Projekt")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mitarbeiter erfolgreich aus Projekt entfernt",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = MitarbeiterEntfernenResponseDto.class))}),
        @ApiResponse(responseCode = "404", description = "Projekt, Mitarbeiter oder Zuordnung nicht gefunden", content = @Content),
        @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @RequestMapping(value = "/{projektId}/mitarbeiter/{mitarbeiterId}", method = RequestMethod.DELETE)
    public ResponseEntity<MitarbeiterEntfernenResponseDto> entferneMitarbeiterAusProjekt(
            @PathVariable Long projektId,
            @PathVariable Long mitarbeiterId,
            @AuthenticationPrincipal Jwt jwt) {
        String securityToken = jwt != null ? jwt.getTokenValue() : null;
        MitarbeiterEntfernenResponseDto response = mitarbeiterZuordnungService.entferneMitarbeiterAusProjekt(projektId, mitarbeiterId, securityToken);
            @Parameter(description = "ID des Projekts", example = "1") @PathVariable Long projektId,
            @Parameter(description = "ID des Mitarbeiters", example = "42") @PathVariable Long mitarbeiterId) {

        MitarbeiterEntfernenResponseDto response = mitarbeiterZuordnungService.entferneMitarbeiterAusProjekt(projektId, mitarbeiterId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Findet alle Projekte, in denen ein bestimmter Mitarbeiter eingeplant ist.
     *
     * @param mitarbeiterId Die ID des Mitarbeiters
     * @return Liste aller Projekte des Mitarbeiters in kompakter Form
     */
    @Operation(summary = "Projekte anhand eines Mitarbeiters finden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projekte werden erfolgreich zurück gegeben",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjektCompactDto.class))}),
            @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @GetMapping("/Mitarbeiter/{mitarbeiterId}/Projekt")
    public ResponseEntity<Object> ReadProjektsByMitarbeiterId(@PathVariable final Long mitarbeiterId) {
    @RequestMapping("/Mitarbeiter/{mitarbeiterId}/Projekt")
    public ResponseEntity<Object> ReadProjektsByMitarbeiterId(
            @Parameter(description = "ID des Mitarbeiters", example = "42") @PathVariable final Long mitarbeiterId) {
        List<MitarbeiterZuordnungEntity> mitarbeiterZuordnungen = this.mitarbeiterZuordnungService.getAllProjektsFromMitarbeiter(mitarbeiterId);
        List<ProjektCompactDto> projekts = this.projektService.readByMitarbeiterId(mitarbeiterZuordnungen);
        return new ResponseEntity<>(projekts, HttpStatus.OK);
    }

    /**
     * Gibt alle Projekte in kompakter Form zurück.
     * Enthält nur die wichtigsten Informationen (ID, Bezeichnung, Verantwortlicher, Kunde).
     *
     * @return Liste aller Projekte in kompakter Form
     */
    @Operation(summary = "Alle Projekt mit id, bezeichnung, verantwortlicherId und kundenId ausgeben.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projekte werden erfolgreich zurück gegeben",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjektCompactDto.class))}),
            @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @GetMapping("/Projekt")
    public ResponseEntity<Object> ReadAllProjekts() {
        List<ProjektEntity> projekts = this.projektService.readAll();
        List<ProjektCompactDto> compactProjekts = new ArrayList<>();
        for(ProjektEntity projekt: projekts) {
            ProjektCompactDto projektCompactDto = this.projektMappingService.mapProjektEntityToProjektByMitarbeiterDto(projekt);
            compactProjekts.add(projektCompactDto);
        }
        return new ResponseEntity<>(compactProjekts, HttpStatus.OK);
    }

    /**
     * Holt alle Informationen über ein Projekt.
     *
     * @param id Die ID des Projekts
     * @return Projektdetails inklusive geplanter und fehlender Qualifikationen
     */
    @Operation(summary = "Ruft alle Informationen über ein Projekt ab")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Projektdetails erfolgreich abgerufen",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ProjektGetDto.class))}),
        @ApiResponse(responseCode = "404", description = "Projekt nicht gefunden", content = @Content),
        @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @GetMapping(value = "/Projekt/{id}")
    public ResponseEntity<ProjektGetDto> holeProjekt(
            @Parameter(description = "ID des Projekts", example = "1") @PathVariable Long id) {
        ProjektGetDto projekt = projektService.holeProjektDetails(id);
    public ResponseEntity<ProjektGetDto> holeProjekt(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String securityToken = jwt != null ? jwt.getTokenValue() : null;
        ProjektGetDto projekt = projektService.holeProjektDetails(id, securityToken);
        return new ResponseEntity<>(projekt, HttpStatus.OK);
    }

    /**
     * Holt alle Mitarbeiter eines Projekts mit ihren Qualifikationen.
     *
     * @param id Die ID des Projekts
     * @return Liste aller Mitarbeiter im Projekt mit ID, Name und Qualifikationen
     */
    @Operation(summary = "Ruft alle Mitarbeiter eines Projekts mit ihren Qualifikationen ab")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mitarbeiterliste erfolgreich abgerufen",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ProjektMitarbeiterGetDto.class))}),
        @ApiResponse(responseCode = "404", description = "Projekt nicht gefunden", content = @Content),
        @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @GetMapping(value = "/Projekt/{id}/Mitarbeiter")
    public ResponseEntity<ProjektMitarbeiterGetDto> holeProjektMitarbeiter(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String securityToken = jwt != null ? jwt.getTokenValue() : null;
        ProjektMitarbeiterGetDto mitarbeiter = projektService.holeProjektMitarbeiter(id,securityToken);
    public ResponseEntity<ProjektMitarbeiterGetDto> holeProjektMitarbeiter(
            @Parameter(description = "ID des Projekts", example = "1") @PathVariable Long id) {
        ProjektMitarbeiterGetDto mitarbeiter = projektService.holeProjektMitarbeiter(id);
        return new ResponseEntity<>(mitarbeiter, HttpStatus.OK);
    }

    /**
     * Löscht ein Projekt anhand der Projekt-ID.
     *
     * @param id Die ID des zu löschenden Projekts
     * @return Alle Projektinformationen des gelöschten Projekts
     */
    @Operation(summary = "Löscht ein Projekt")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Projekt erfolgreich gelöscht",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ProjektLoeschenResponseDto.class))}),
        @ApiResponse(responseCode = "404", description = "Projekt nicht gefunden", content = @Content),
        @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @DeleteMapping(value = "/Projekt/{id}")
    public ResponseEntity<ProjektLoeschenResponseDto> loescheProjekt(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String securityToken = jwt != null ? jwt.getTokenValue() : null;
        ProjektLoeschenResponseDto response = projektService.loescheProjekt(id, securityToken);
    public ResponseEntity<ProjektLoeschenResponseDto> loescheProjekt(
            @Parameter(description = "ID des zu löschenden Projekts", example = "1") @PathVariable Long id) {
        ProjektLoeschenResponseDto response = projektService.loescheProjekt(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
