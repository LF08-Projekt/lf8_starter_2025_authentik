package de.szut.lf8_projekt.mapping;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungDto;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import org.springframework.stereotype.Service;

@Service
public class MitarbeiterMappingService {

    public MitarbeiterZuordnungEntity mapMitarbeiterZuordnungDtoToMitarbeiterZuordnungEntity(MitarbeiterZuordnungDto mitarbeiterZuordnungDto) {
        MitarbeiterZuordnungEntity mitarbeiterZuordnungEntity = new MitarbeiterZuordnungEntity();
        mitarbeiterZuordnungEntity.setMitarbeiterId(mitarbeiterZuordnungDto.getMitarbeiterId());
        mitarbeiterZuordnungEntity.setProjektId(mitarbeiterZuordnungDto.getProjektId());
        mitarbeiterZuordnungEntity.setQualifikationId(mitarbeiterZuordnungEntity.getQualifikationId());
        return mitarbeiterZuordnungEntity;
    }

}
