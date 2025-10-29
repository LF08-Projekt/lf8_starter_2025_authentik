package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import de.szut.lf8_projekt.mitarbeiter.GetMitarbeiterDto;
import de.szut.lf8_projekt.projekt.ProjektEntity;
import de.szut.lf8_projekt.projekt.ProjektService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MitarbeiterZuordnungService {
    private final MitarbeiterZuordnungRepository repository;
    private final ProjektService projektService;

    public MitarbeiterZuordnungService(MitarbeiterZuordnungRepository repository, ProjektService projektService) {
        this.repository = repository;
        this.projektService = projektService;
    }

    public MitarbeiterZuordnungEntity create(MitarbeiterZuordnungEntity entity) {
        return this.repository.save(entity);
    }

    public List<MitarbeiterZuordnungEntity> readAll() {
        return this.repository.findAll();
    }

    public MitarbeiterZuordnungEntity readById(Long id) {
        Optional<MitarbeiterZuordnungEntity> optionalProjekt = this.repository.findById(id);
        return optionalProjekt.orElse(null);
    }

    public void delete(MitarbeiterZuordnungEntity entity) {
        this.repository.delete(entity);
    }

    public boolean projektHasMitarbeiter(Long projektId, Long mitarbeiterId) {
        if (this.repository.findByProjektIdAndMitarbeiterId(projektId, mitarbeiterId) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMitarbeiterAvailable(GetMitarbeiterDto mitarbeiterZuordnungDto, ProjektEntity projekt) {
        List<ProjektEntity> projekte = this.projektService.readByDate(projekt.getStartdatum(), projekt.getGeplantesEnddatum());
        for (ProjektEntity tmpProjekt: projekte) {
            if (this.projektHasMitarbeiter(tmpProjekt.getId(), mitarbeiterZuordnungDto.getId())) {
                return false;
            }
        }
        return true;
    }

}
