package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.mapping.MitarbeiterMappingService;
import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.ValidationService;
import de.szut.lf8_projekt.exceptionHandling.ResourceConflictException;
import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungDto;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungService;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_starter.hello.dto.HelloGetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/LF08Projekt")
@Validated
public class ProjektController {
    private final MitarbeiterMappingService mitarbeiterMappingService;
    private final MitarbeiterApiService mitarbeiterApiService;
    private final ProjektService projektService;
    private final GeplanteQualifikationService geplanteQualifikationService;
    private final MitarbeiterZuordnungService mitarbeiterZuordnungService;
    private final ProjektMappingService projektMappingService;
    private final ValidationService validationService;

    public ProjektController(ProjektService projektService,
                             GeplanteQualifikationService geplanteQualifikationService,
                             MitarbeiterZuordnungService mitarbeiterZuordnungService,
                             ProjektMappingService projektMappingService,
                             ValidationService validationService,
                             MitarbeiterMappingService mitarbeiterMappingService,
                             MitarbeiterApiService mitarbeiterApiService) {
        this.projektService = projektService;
        this.geplanteQualifikationService = geplanteQualifikationService;
        this.mitarbeiterZuordnungService = mitarbeiterZuordnungService;
        this.projektMappingService = projektMappingService;
        this.validationService = validationService;
        this.mitarbeiterApiService = mitarbeiterApiService;
        this.mitarbeiterMappingService = mitarbeiterMappingService;
    }

    @Operation(summary = "Legt ein neues Projekt an")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Das Projekt wurde erfolgreich angelegt",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjektCreateConfirmationDto.class))}),
            @ApiResponse(responseCode = "400", description = "Fehlende Pflichtangabe im DTO", content = @Content),
            @ApiResponse(responseCode = "404", description = "Der verantwortliche Mitarbeiter, Kunde oder" +
                    " eine der geplanten Qualifikationen konnte nicht gefunden werden", content = @Content),
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

        ProjektEntity projektEntity = this.projektMappingService.mapProjektCreateDtoToProjektEntity(dto);
        projektEntity = this.projektService.create(projektEntity);

        List<String> geplanteQualifikationen = new ArrayList<>();
        if (dto.getGeplanteQualifikationen() != null) {
            for (String qualifikation : dto.getGeplanteQualifikationen()) {
                GeplanteQualifikationEntity geplanteQualifikationEntity = this.projektMappingService.mapDataToGeplanteQualifikationEntity(projektEntity.getId(), qualifikation);
                geplanteQualifikationEntity = this.geplanteQualifikationService.create(geplanteQualifikationEntity);
                geplanteQualifikationen.add(geplanteQualifikationEntity.getQualifikation());
            }
        }

        ProjektCreateConfirmationDto returnDto = this.projektMappingService.mapProjektEntityToProjektCreateConfirmationDto(projektEntity);
        returnDto.setGeplanteQualifikationen(geplanteQualifikationen);
        return returnDto;
    }

    @Operation(summary = "Hinzufügen eines Mitarbeiters zu einem Projekt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mitarbeiter erfolgreich zu einem Projekt hinzugefügt"),
            @ApiResponse(responseCode = "400", description = "Falsche Qualifikation, Mitarbeiter besitzt die Qualifikation nicht oder Mitarbeiter ist bereits im Zeitraum verplant", content = @Content),
            @ApiResponse(responseCode = "404", description = "Projekt oder Mitarbeiter nicht gefunden", content = @Content),
            @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @PostMapping("{projekt_id}/mitarbeiter/{mitarbeiter_id}")
    public ResponseEntity<Object> addMitarbeiterToProjekt(@PathVariable final Long projekt_id, @PathVariable final Long mitarbeiter_id,
                                                                     @Valid @RequestBody final SkillDto skill) {
        boolean qualifikationInProjekt = false;
        ProjektEntity projekt = this.projektService.readById(projekt_id);
        MitarbeiterDto mitarbeiterDto = this.mitarbeiterApiService.getMitarbeiterById(mitarbeiter_id);
        if (mitarbeiterDto == null) {
            throw new ResourceNotFoundException("Der Mitarbeiter mit der Id " + mitarbeiter_id + " existiert nicht");
        }
        List<GeplanteQualifikationEntity> geplanteQualifikationen = this.geplanteQualifikationService.readByProjektId(projekt_id);

        for (GeplanteQualifikationEntity geplanteQualifikation : geplanteQualifikationen) {
            if (Objects.equals(geplanteQualifikation.getQualifikationId(), skill.getId())) {
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

    @PutMapping(path="/Projekt/{projektId}")
    public ProjektUpdateConfirmationDto updateProjekt(@RequestBody @Valid ProjektUpdateDto dto,
                                                      @PathVariable Long projektId,
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
            throw new ResourceNotFoundException("Kunde mit der ID + " + dto.getKundenId() + " existiert nicht!");
        }
        if (dto.getGeplanteQualifikationen() != null && !this.validationService.validateQualifications(Arrays.asList(dto.getGeplanteQualifikationen()), securityToken)) {
            throw new ResourceNotFoundException("Liste der geplanten Qualifikationen enthält eine ungültige Qualifikation");
        }
        if (dto.getGeplantesEnddatum() != null) {
            Date neuesGeplantesEnddatum = dto.getGeplantesEnddatum();
            if (neuesGeplantesEnddatum.after(previousSaveState.getGeplantesEnddatum())) {
                List<MitarbeiterZuordnungEntity> mitarbeiterZuordnungEntities
                        = mitarbeiterZuordnungService.getMitarbeiterZuordnungEntitiesByProjektId(projektId);

                for (MitarbeiterZuordnungEntity mitarbeiterZuordnungEntity : mitarbeiterZuordnungEntities) {
                    if (mitarbeiterZuordnungEntity.getProjektId() == projektId) continue;

                    ProjektEntity kollisionsProjektEntity = this.projektService.readById(mitarbeiterZuordnungEntity.getProjektId());
                    if (kollisionsProjektEntity.getStartdatum().after(previousSaveState.getGeplantesEnddatum())
                        && neuesGeplantesEnddatum.after(kollisionsProjektEntity.getStartdatum())) {
                        throw new ResourceConflictException("Mitarbeiter " + mitarbeiterZuordnungEntity.getMitarbeiterId() + " ist" +
                                " zu diesem Zeit bereits in Projekt " + mitarbeiterZuordnungEntity.getProjektId() + " verplant." +
                                " Bitte klären sie diesen Konflikt");
                    }
                }
            }
        }

        ProjektEntity projektEntity = this.projektMappingService.mapProjektUpdateDtoToProjektEntity(dto, projektId);
        projektEntity = this.projektService.save(projektEntity);

        List<String> geplanteQualifikationen = new ArrayList<>();
        if (dto.getGeplanteQualifikationen() != null) {
            List<GeplanteQualifikationEntity> qualifikationEntities = this.geplanteQualifikationService.readByProjektId(projektId);
            for (String qualifikation : dto.getGeplanteQualifikationen()) {
                if (qualifikationEntities.stream().filter(x -> x.getQualifikation().equals(qualifikation)).findAny().isEmpty()) {
                    GeplanteQualifikationEntity geplanteQualifikationEntity = this.projektMappingService.mapDataToGeplanteQualifikationEntity(projektEntity.getId(), qualifikation);
                    geplanteQualifikationEntity = this.geplanteQualifikationService.create(geplanteQualifikationEntity);
                    geplanteQualifikationen.add(geplanteQualifikationEntity.getQualifikation());
                }
            }
            for (GeplanteQualifikationEntity qualifikationEntity : qualifikationEntities) {
                if (geplanteQualifikationen.stream().filter(x -> x.equals(qualifikationEntity.getQualifikation())).findAny().isEmpty()) {
                    this.geplanteQualifikationService.delete(qualifikationEntity);
                }
            }
        }

        ProjektUpdateConfirmationDto returnDto = this.projektMappingService.mapProjektEntityToProjektUpdateConfirmationDto(projektEntity);
        returnDto.setGeplanteQualifikationen(geplanteQualifikationen);
        return returnDto;
    }
  
    @Operation(summary = "Entfernt einen Mitarbeiter aus einem Projekt")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mitarbeiter erfolgreich aus Projekt entfernt"),
        @ApiResponse(responseCode = "404", description = "Projekt, Mitarbeiter oder Zuordnung nicht gefunden", content = @Content),
        @ApiResponse(responseCode = "401", description = "Ungültiges Token", content = @Content)
    })
    @RequestMapping(value = "/{projektId}/mitarbeiter/{mitarbeiterId}", method = RequestMethod.DELETE)
    public ResponseEntity<MitarbeiterEntfernenResponseDto> entferneMitarbeiterAusProjekt(
            @PathVariable Long projektId,
            @PathVariable Long mitarbeiterId) {

        MitarbeiterEntfernenResponseDto response = mitarbeiterZuordnungService.entferneMitarbeiterAusProjekt(projektId, mitarbeiterId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
