package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_projekt.projekt.dto.MitarbeiterImProjektDto;
import de.szut.lf8_projekt.projekt.dto.ProjektGetDto;
import de.szut.lf8_projekt.projekt.dto.ProjektLoeschenResponseDto;
import de.szut.lf8_projekt.projekt.dto.ProjektMitarbeiterGetDto;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationRepository;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungRepository;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjektService {
    private final ProjektRepository repository;
    private final GeplanteQualifikationRepository geplanteQualifikationRepository;
    private final MitarbeiterZuordnungRepository mitarbeiterZuordnungRepository;
    private final MitarbeiterApiService mitarbeiterApiService;

    public ProjektService(ProjektRepository repository,
                          GeplanteQualifikationRepository geplanteQualifikationRepository,
                          MitarbeiterZuordnungRepository mitarbeiterZuordnungRepository,
                          MitarbeiterApiService mitarbeiterApiService) {
        this.repository = repository;
        this.geplanteQualifikationRepository = geplanteQualifikationRepository;
        this.mitarbeiterZuordnungRepository = mitarbeiterZuordnungRepository;
        this.mitarbeiterApiService = mitarbeiterApiService;
    }

    public ProjektEntity create(ProjektEntity entity) {
        return this.repository.save(entity);
    }

    public ProjektEntity save(ProjektEntity entity) {
        ProjektEntity updatedProjektEntity = readById(entity.getId());
        if (entity.getBezeichnung() != null) {
            updatedProjektEntity.setBezeichnung(entity.getBezeichnung());
        }
        if (entity.getVerantwortlicherId() != null) {
            updatedProjektEntity.setVerantwortlicherId(entity.getVerantwortlicherId());
        }
        if (entity.getKundenId() != null) {
            updatedProjektEntity.setKundenId(entity.getKundenId());
        }
        if (entity.getKundeAnsprechperson() != null) {
            updatedProjektEntity.setKundeAnsprechperson(entity.getKundeAnsprechperson());
        }
        if (entity.getProjektzielKommentar() != null) {
            updatedProjektEntity.setProjektzielKommentar(entity.getProjektzielKommentar());
        }
        if (entity.getStartdatum() != null) {
            updatedProjektEntity.setStartdatum(entity.getStartdatum());
        }
        if (entity.getGeplantesEnddatum() != null) {
            updatedProjektEntity.setGeplantesEnddatum(entity.getGeplantesEnddatum());
        }
        if (entity.getWirklichesEnddatum() != null) {
            updatedProjektEntity.setWirklichesEnddatum(entity.getWirklichesEnddatum());
        }
        return this.repository.save(updatedProjektEntity);
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

    /**
     * Holt alle Details eines Projekts inklusive der fehlenden Qualifikationen.
     * Die fehlenden Qualifikationen werden berechnet als: geplante Qualifikationen minus
     * vorhandene Qualifikationen aller im Projekt arbeitenden Mitarbeiter.
     *
     * @param projektId Die ID des Projekts
     * @return Ein DTO mit allen Projektdetails und berechneten fehlenden Qualifikationen
     * @throws ResourceNotFoundException wenn das Projekt nicht existiert
     */
    public ProjektGetDto holeProjektDetails(Long projektId) {
        ProjektEntity projekt = readById(projektId);

        List<GeplanteQualifikationEntity> geplant = geplanteQualifikationRepository.getGeplanteQualifikationEntitiesByProjektId(projektId);
        List<String> geplanteQualifikationen = geplant.stream()
                .map(GeplanteQualifikationEntity::getQualifikation)
                .collect(Collectors.toList());

        List<String> fehlendeQualifikationen = berechneFehlendeQualifikationen(projektId, geplanteQualifikationen);

        ProjektGetDto dto = new ProjektGetDto();
        dto.setProjektId(projekt.getId());
        dto.setBezeichnung(projekt.getBezeichnung());
        dto.setKommentar(projekt.getProjektzielKommentar());
        dto.setKundenId(projekt.getKundenId());
        dto.setKundenAnsprechpartnerName(projekt.getKundeAnsprechperson());
        dto.setStartdatum(projekt.getStartdatum());
        dto.setGeplantesEnddatum(projekt.getGeplantesEnddatum());
        dto.setTatsaechlichesEnddatum(projekt.getWirklichesEnddatum());
        dto.setGeplanteQualifikationen(geplanteQualifikationen);
        dto.setFehlendeQualifikationen(fehlendeQualifikationen);

        return dto;
    }

    /**
     * Holt alle Mitarbeiter eines Projekts mit ihren Qualifikationen.
     * Die Mitarbeiterdaten werden von der externen Employee API abgerufen.
     *
     * @param projektId Die ID des Projekts
     * @return Ein DTO mit allen Mitarbeitern des Projekts inklusive ihrer Qualifikationen
     * @throws ResourceNotFoundException wenn das Projekt nicht existiert
     */
    public ProjektMitarbeiterGetDto holeProjektMitarbeiter(Long projektId) {
        ProjektEntity projekt = readById(projektId);

        List<MitarbeiterZuordnungEntity> zuordnungen = mitarbeiterZuordnungRepository.getMitarbeiterZuordnungEntitiesByProjektId(projektId);
        List<MitarbeiterImProjektDto> mitarbeiter = new ArrayList<>();

        for (MitarbeiterZuordnungEntity zuordnung : zuordnungen) {
            MitarbeiterDto mitarbeiterDto = mitarbeiterApiService.getMitarbeiterById(zuordnung.getMitarbeiterId());
            if (mitarbeiterDto != null) {
                MitarbeiterImProjektDto imProjekt = new MitarbeiterImProjektDto();
                imProjekt.setMitarbeiterId(mitarbeiterDto.getId());
                imProjekt.setName(mitarbeiterDto.getVollstaendigerName());

                List<String> qualifikationen = mitarbeiterDto.getSkillSet() != null
                        ? mitarbeiterDto.getSkillSet().stream()
                                .map(skill -> skill.getSkill())
                                .collect(Collectors.toList())
                        : new ArrayList<>();
                imProjekt.setQualifikationen(qualifikationen);

                mitarbeiter.add(imProjekt);
            }
        }

        return new ProjektMitarbeiterGetDto(projektId, mitarbeiter);
    }

    /**
     * Berechnet die fehlenden Qualifikationen eines Projekts.
     * Sammelt alle Qualifikationen der im Projekt arbeitenden Mitarbeiter und
     * vergleicht diese mit den geplanten Qualifikationen.
     *
     * @param projektId Die ID des Projekts
     * @param geplanteQualifikationen Die Liste der geplanten Qualifikationen
     * @return Liste der Qualifikationen die geplant sind aber von keinem Mitarbeiter erfüllt werden
     */
    private List<String> berechneFehlendeQualifikationen(Long projektId, List<String> geplanteQualifikationen) {
        List<MitarbeiterZuordnungEntity> zuordnungen = mitarbeiterZuordnungRepository.getMitarbeiterZuordnungEntitiesByProjektId(projektId);
        List<String> vorhandeneQualifikationen = new ArrayList<>();

        for (MitarbeiterZuordnungEntity zuordnung : zuordnungen) {
            MitarbeiterDto mitarbeiter = mitarbeiterApiService.getMitarbeiterById(zuordnung.getMitarbeiterId());
            if (mitarbeiter != null && mitarbeiter.getSkillSet() != null) {
                vorhandeneQualifikationen.addAll(
                        mitarbeiter.getSkillSet().stream()
                                .map(skill -> skill.getSkill())
                                .collect(Collectors.toList())
                );
            }
        }

        return geplanteQualifikationen.stream()
                .filter(geplant -> !vorhandeneQualifikationen.contains(geplant))
                .collect(Collectors.toList());
    }

    /**
     * Löscht ein Projekt und gibt alle Projektinformationen zurück.
     * Sammelt vor dem Löschen alle Projektdaten inklusive zugeordneter Mitarbeiter
     * und benötigter Qualifikationen.
     *
     * @param projektId Die ID des zu löschenden Projekts
     * @return Ein DTO mit allen Informationen des gelöschten Projekts
     * @throws ResourceNotFoundException wenn das Projekt nicht existiert
     */
    public ProjektLoeschenResponseDto loescheProjekt(Long projektId) {
        ProjektEntity projekt = readById(projektId);

        List<GeplanteQualifikationEntity> geplant = geplanteQualifikationRepository.getGeplanteQualifikationEntitiesByProjektId(projektId);
        List<String> benoetigteQualifikationen = geplant.stream()
                .map(GeplanteQualifikationEntity::getQualifikation)
                .collect(Collectors.toList());

        ProjektMitarbeiterGetDto mitarbeiterDto = holeProjektMitarbeiter(projektId);

        ProjektLoeschenResponseDto responseDto = new ProjektLoeschenResponseDto();
        responseDto.setProjektId(projekt.getId());
        responseDto.setBezeichnung(projekt.getBezeichnung());
        responseDto.setVerantwortlicherMitarbeiterId(projekt.getVerantwortlicherId());
        responseDto.setKundenId(projekt.getKundenId());
        responseDto.setKundenAnsprechpartnerName(projekt.getKundeAnsprechperson());
        responseDto.setKommentar(projekt.getProjektzielKommentar());
        responseDto.setStartdatum(projekt.getStartdatum());
        responseDto.setGeplantesEnddatum(projekt.getGeplantesEnddatum());
        responseDto.setTatsaechlichesEnddatum(projekt.getWirklichesEnddatum());
        responseDto.setMitarbeiter(mitarbeiterDto.getMitarbeiter());
        responseDto.setBenoetigteQualifikationen(benoetigteQualifikationen);

        delete(projekt);

        return responseDto;
    }
}
