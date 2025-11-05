package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import org.springframework.stereotype.Service;

@Service
public class ProjektMappingService {
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
    public GeplanteQualifikationEntity mapDataToGeplanteQualifikationEntity(Long projektId, String qualifikation) {
        GeplanteQualifikationEntity entity = new GeplanteQualifikationEntity();
        entity.setProjektId(projektId);
        entity.setQualifikation(qualifikation);
        return entity;
    }
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

    public ProjektCompactDto mapProjektEntityToProjektByMitarbeiterDto(ProjektEntity projektEntity) {
        ProjektCompactDto projektCompactDto = new ProjektCompactDto();
        projektCompactDto.setBezeichnung(projektEntity.getBezeichnung());
        projektCompactDto.setId(projektEntity.getId());
        projektCompactDto.setKundenId(projektEntity.getKundenId());
        projektCompactDto.setVerantwortlicherId(projektEntity.getVerantwortlicherId());
        return projektCompactDto;
    }
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
