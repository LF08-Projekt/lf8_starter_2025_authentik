package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.ValidationService;
import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungService;
import jakarta.validation.Valid;
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
import java.util.List;

@RestController
@RequestMapping(value="/LF08Projekt")
@Validated
public class ProjektController {
    private ProjektService projektService;
    private GeplanteQualifikationService geplanteQualifikationService;
    private MitarbeiterZuordnungService mitarbeiterZuordnungService;
    private ProjektMappingService projektMappingService;
    private ValidationService validationService;

    public ProjektController(ProjektService projektService,
                             GeplanteQualifikationService geplanteQualifikationService,
                             MitarbeiterZuordnungService mitarbeiterZuordnungService,
                             ProjektMappingService projektMappingService,
                             ValidationService validationService) {
        this.projektService = projektService;
        this.geplanteQualifikationService = geplanteQualifikationService;
        this.mitarbeiterZuordnungService = mitarbeiterZuordnungService;
        this.projektMappingService = projektMappingService;
        this.validationService = validationService;
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
