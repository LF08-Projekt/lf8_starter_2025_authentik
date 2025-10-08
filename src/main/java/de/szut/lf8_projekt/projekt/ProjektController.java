package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/LF08Projekt")
public class ProjektController {
    private ProjektService projektService;
    private GeplanteQualifikationService geplanteQualifikationService;
    private MitarbeiterZuordnungService mitarbeiterZuordnungService;

    public ProjektController(ProjektService projektService, GeplanteQualifikationService geplanteQualifikationService, MitarbeiterZuordnungService mitarbeiterZuordnungService) {
        this.projektService = projektService;
        this.geplanteQualifikationService = geplanteQualifikationService;
        this.mitarbeiterZuordnungService = mitarbeiterZuordnungService;
    }


}
