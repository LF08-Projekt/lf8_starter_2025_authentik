package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import org.springframework.stereotype.Service;

/**
 * Mapping-Service für die Konvertierung zwischen Projekt-Entities und DTOs.
 * Enthält Mapping-Methoden für verschiedene Projekt-bezogene Objekte.
 */
@Service
public class ProjektMappingService {
    /**
     * Mapped ein ProjektCreateDto zu einem ProjektEntity.
     *
     * @param dto Das DTO mit den Projekt-Erstellungsdaten
     * @return Ein neues ProjektEntity mit den Daten aus dem DTO
     */
    public ProjektEntity mapProjektCreateDtoToProjektEntity(ProjektCreateDto dto) {
        ProjektEntity entity = new ProjektEntity();
        entity.setBezeichnung(dto.getBezeichnung());
        entity.setVerantwortlicherId(dto.getVerantwortlicherId());
        entity.setKundenId(dto.getKundenId());
        entity.setKundeAnsprechperson(dto.getKundeAnsprechperson());
        entity.setProjektzielKommentar(dto.getProjektzielKommentar());
        entity.setStartdatum(dto.getStartdatum());
        entity.setGeplantesEnddatum(dto.getGeplantesEnddatum());
        return entity;
    }

    /**
     * Erstellt ein GeplanteQualifikationEntity aus Projekt-ID und Qualifikationsbezeichnung.
     *
     * @param projektId Die ID des Projekts
     * @param qualifikationId Die ID der Qualifikation
     * @return Ein neues GeplanteQualifikationEntity
     */
    public GeplanteQualifikationEntity mapDataToGeplanteQualifikationEntity(Long projektId, Long qualifikationId) {
        GeplanteQualifikationEntity entity = new GeplanteQualifikationEntity();
        entity.setProjektId(projektId);
        entity.setQualifikationId(qualifikationId);
        return entity;
    }

    /**
     * Mapped ein ProjektEntity zu einem ProjektCreateConfirmationDto.
     *
     * @param entity Das Projekt-Entity
     * @return Ein DTO zur Bestätigung der Projekterstellung
     */
    public ProjektCreateConfirmationDto mapProjektEntityToProjektCreateConfirmationDto(ProjektEntity entity) {
        ProjektCreateConfirmationDto dto = new ProjektCreateConfirmationDto();
        dto.setId(entity.getId());
        dto.setBezeichnung(entity.getBezeichnung());
        dto.setVerantwortlicherId(entity.getVerantwortlicherId());
        dto.setKundenId(entity.getKundenId());
        dto.setKundeAnsprechperson(entity.getKundeAnsprechperson());
        dto.setProjektzielKommentar(entity.getProjektzielKommentar());
        dto.setStartdatum(entity.getStartdatum());
        dto.setGeplantesEnddatum(entity.getGeplantesEnddatum());
        return dto;
    }

    /**
     * Mapped ein ProjektEntity zu einem kompakten ProjektCompactDto.
     * Enthält nur die wichtigsten Projektinformationen.
     *
     * @param projektEntity Das Projekt-Entity
     * @return Ein kompaktes Projekt-DTO
     */
    public ProjektCompactDto mapProjektEntityToProjektByMitarbeiterDto(ProjektEntity projektEntity) {
        ProjektCompactDto projektCompactDto = new ProjektCompactDto();
        projektCompactDto.setBezeichnung(projektEntity.getBezeichnung());
        projektCompactDto.setId(projektEntity.getId());
        projektCompactDto.setKundenId(projektEntity.getKundenId());
        projektCompactDto.setVerantwortlicherId(projektEntity.getVerantwortlicherId());
        return projektCompactDto;
    }

    /**
     * Mapped ein ProjektEntity zu einem ProjektUpdateConfirmationDto.
     *
     * @param entity Das aktualisierte Projekt-Entity
     * @return Ein DTO zur Bestätigung der Projektaktualisierung
     */
    public ProjektUpdateConfirmationDto mapProjektEntityToProjektUpdateConfirmationDto(ProjektEntity entity) {
        ProjektUpdateConfirmationDto dto = new ProjektUpdateConfirmationDto();
        dto.setId(entity.getId());
        dto.setBezeichnung(entity.getBezeichnung());
        dto.setVerantwortlicherId(entity.getVerantwortlicherId());
        dto.setKundenId(entity.getKundenId());
        dto.setKundeAnsprechperson(entity.getKundeAnsprechperson());
        dto.setProjektzielKommentar(entity.getProjektzielKommentar());
        dto.setStartdatum(entity.getStartdatum());
        dto.setGeplantesEnddatum(entity.getGeplantesEnddatum());
        dto.setWirklichesEnddatum(entity.getWirklichesEnddatum());
        return dto;
    }

    /**
     * Mapped ein ProjektUpdateDto zu einem ProjektEntity.
     *
     * @param dto Das DTO mit den Aktualisierungsdaten
     * @param id Die ID des zu aktualisierenden Projekts
     * @return Ein ProjektEntity mit den Daten aus dem DTO
     */
    public ProjektEntity mapProjektUpdateDtoToProjektEntity(ProjektUpdateDto dto, Long id) {
        ProjektEntity entity = new ProjektEntity();
        entity.setId(id);
        entity.setBezeichnung(dto.getBezeichnung());
        entity.setVerantwortlicherId(dto.getVerantwortlicherId());
        entity.setKundenId(dto.getKundenId());
        entity.setKundeAnsprechperson(dto.getKundeAnsprechperson());
        entity.setProjektzielKommentar(dto.getProjektzielKommentar());
        entity.setStartdatum(dto.getStartdatum());
        entity.setGeplantesEnddatum(dto.getGeplantesEnddatum());
        entity.setWirklichesEnddatum(dto.getWirklichesEnddatum());
        return entity;
    }
}
