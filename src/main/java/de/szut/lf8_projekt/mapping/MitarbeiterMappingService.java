package de.szut.lf8_projekt.mapping;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungDto;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import org.springframework.stereotype.Service;

/**
 * Mapping-Service für Mitarbeiter-bezogene DTOs und Entities.
 */
@Service
public class MitarbeiterMappingService {

    /**
     * Mappt ein {@link MitarbeiterZuordnungDto} auf ein {@link MitarbeiterZuordnungEntity}.
     *
     * @param mitarbeiterZuordnungDto Quell-DTO
     * @return neues Entity mit den Daten aus dem DTO
     */
    public MitarbeiterZuordnungEntity mapMitarbeiterZuordnungDtoToMitarbeiterZuordnungEntity(MitarbeiterZuordnungDto mitarbeiterZuordnungDto) {
        MitarbeiterZuordnungEntity mitarbeiterZuordnungEntity = new MitarbeiterZuordnungEntity();
        mitarbeiterZuordnungEntity.setMitarbeiterId(mitarbeiterZuordnungDto.getMitarbeiterId());
        mitarbeiterZuordnungEntity.setProjektId(mitarbeiterZuordnungDto.getProjektId());
        mitarbeiterZuordnungEntity.setQualifikationId(mitarbeiterZuordnungEntity.getQualifikationId());
        return mitarbeiterZuordnungEntity;
    }

}
