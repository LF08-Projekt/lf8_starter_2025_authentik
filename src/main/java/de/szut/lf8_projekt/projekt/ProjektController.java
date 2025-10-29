package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.mapping.MappingService;
import de.szut.lf8_projekt.mitarbeiter.GetMitarbeiterDto;
import de.szut.lf8_projekt.mitarbeiter.MitarbeiterService;
import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungDto;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value="/LF08Projekt")
public class ProjektController {

    private final ProjektService projektService;
    private final GeplanteQualifikationService geplanteQualifikationService;
    private final MitarbeiterZuordnungService mitarbeiterZuordnungService;
    private final MitarbeiterService mitarbeiterService;
    private final MappingService mappingService;

    public ProjektController(ProjektService projektService, GeplanteQualifikationService geplanteQualifikationService,
                             MitarbeiterZuordnungService mitarbeiterZuordnungService, MitarbeiterService mitarbeiterService, MappingService mappingService) {
        this.projektService = projektService;
        this.geplanteQualifikationService = geplanteQualifikationService;
        this.mitarbeiterZuordnungService = mitarbeiterZuordnungService;
        this.mitarbeiterService = mitarbeiterService;
        this.mappingService = mappingService;
    }

    @PostMapping("{projekt_id}/mitarbeiter/{mitarbeiter_id}")
    public ResponseEntity<Object> addMitarbeiterToProjekt(@PathVariable final Long projekt_id, @PathVariable final Long mitarbeiter_id,
                                                                     @Valid @RequestBody final SkillDto skill) {
        //JWT authentifizerung fehlt
        boolean qualifikationInProjekt = false;
        ProjektEntity projekt = this.projektService.readById(projekt_id);
        GetMitarbeiterDto mitarbeiterDto = this.mitarbeiterService.getMitarbeiterDto(mitarbeiter_id);
        List<GeplanteQualifikationEntity> geplanteQualifikationen = this.geplanteQualifikationService.readByProjektId(projekt_id);

        for (GeplanteQualifikationEntity geplanteQualifikation : geplanteQualifikationen) {
            if (Objects.equals(geplanteQualifikation.getProjektId(), skill.getId())) {
                qualifikationInProjekt = true;
                break;
            }
        }
        if (!qualifikationInProjekt) {
            throw new IllegalArgumentException("Die angegebene Qualifikation mit der Id " + skill.getId() + " wird nicht im projekt benötigt.");
        }
        if (!mitarbeiterDto.getSkillset().contains(skill)) {
            throw new IllegalArgumentException("Der Mitarbeiter mit der Id " + mitarbeiter_id + " besitzt die Qualifikation " + skill.getSkill() + " nicht.");
        }
        if (!this.mitarbeiterZuordnungService.isMitarbeiterAvailable(mitarbeiterDto, projekt)) {
            throw new IllegalArgumentException("Der Mitarbeiter ist bereits im Zeitraum des Projekts bereits verplant.");
        }

        // Create mitarbeiterZuordnungDto
        MitarbeiterZuordnungDto mitarbeiterZuordnungDto = new MitarbeiterZuordnungDto();
        mitarbeiterZuordnungDto.setMitarbeiterId(mitarbeiter_id);
        mitarbeiterZuordnungDto.setProjektId(projekt_id);
        mitarbeiterZuordnungDto.setQualifikationId(skill.getId());

        MitarbeiterZuordnungEntity mitarbeiterZuordnung = this.mappingService.mapMitarbeiterZuordnungDtoToMitarbeiterZuordnungEntity(mitarbeiterZuordnungDto);
        mitarbeiterZuordnung = this.mitarbeiterZuordnungService.create(mitarbeiterZuordnung);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ProjektId", projekt_id);
        map.put("ProjketName", projekt.getBezeichnung());
        map.put("MitarbeiterId", mitarbeiter_id);
        map.put("MitarbeiterName", mitarbeiterDto.getFirstName() + " " + mitarbeiterDto.getLastName());
        map.put("Qualifikation", mitarbeiterDto.getSkillset());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
