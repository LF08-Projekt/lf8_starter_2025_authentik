package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.mapping.MitarbeiterMappingService;
import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.ValidationService;
import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungDto;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungService;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
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

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping(value="/LF08Projekt")
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

    @PostMapping(path="/Projekt")
    public ProjektCreateConfirmationDto create(@RequestBody @Valid ProjektCreateDto dto, @AuthenticationPrincipal Jwt jwt) {
        String securityTocken = jwt.getTokenValue();
        if (!this.validationService.validateMitarbeiterId(dto.getVerantwortlicherId(), securityTocken)) {
            throw new ResourceNotFoundException("Mitarbeiter mit der ID " + dto.getVerantwortlicherId() + " existiert nicht!");
        }
        if (!this.validationService.validateKundenId(dto.getKundenId(), securityTocken)) {
            throw new ResourceNotFoundException("Kunde mit der ID + " + dto.getKundenId() + " existiert nicht!");
        }
        if (!this.validationService.validateQualifications(Arrays.asList(dto.getGeplanteQualifikationen()), securityTocken)) {
            throw new ResourceNotFoundException("Liste der geplanten Qualifikationen enthält eine ungültige Qualifikation");
        }

        ProjektEntity projektEntity = this.projektMappingService.mapProjektCreateDtoToProjektEntity(dto);
        projektEntity = this.projektService.create(projektEntity);

        List<String> geplanteQualifikationen = new ArrayList<>();
        for (String qualifikation : dto.getGeplanteQualifikationen()) {
            GeplanteQualifikationEntity geplanteQualifikationEntity = this.projektMappingService.mapDataToGeplanteQualifikationEntity(projektEntity.getId(), qualifikation);
            geplanteQualifikationEntity = this.geplanteQualifikationService.create(geplanteQualifikationEntity);
            geplanteQualifikationen.add(geplanteQualifikationEntity.getQualifikation());
        }

        ProjektCreateConfirmationDto returnDto = this.projektMappingService.mapProjektEntityToProjektCreateConfirmationDto(projektEntity);
        returnDto.setGeplanteQualifikationen(geplanteQualifikationen);
        return returnDto;
    }

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
