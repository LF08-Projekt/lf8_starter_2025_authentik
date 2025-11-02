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

    public ProjektByMitarbeiterDto mapProjektEntityToProjektByMitarbeiterDto(ProjektEntity projektEntity) {
        ProjektByMitarbeiterDto projektByMitarbeiterDto = new ProjektByMitarbeiterDto();
        projektByMitarbeiterDto.setBezeichnung(projektEntity.getBezeichnung());
        projektByMitarbeiterDto.setId(projektEntity.getId());
        projektByMitarbeiterDto.setKundenId(projektEntity.getKundenId());
        projektByMitarbeiterDto.setVerantwortlicherId(projektEntity.getVerantwortlicherId());
        return projektByMitarbeiterDto;
    }
}
