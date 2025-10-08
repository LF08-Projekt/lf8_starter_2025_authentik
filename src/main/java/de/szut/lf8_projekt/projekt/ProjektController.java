package de.szut.lf8_projekt.projekt;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projekt")
public class ProjektController {

    private final ProjektService projektService;

    public ProjektController(ProjektService projektService) {
        this.projektService = projektService;
    }

    @DeleteMapping("/{projektId}/mitarbeiter/{mitarbeiterId}")
    public MitarbeiterEntferntDto entferneMitarbeiter(
            @PathVariable Long projektId,
            @PathVariable Long mitarbeiterId) {
        return projektService.entferneMitarbeiterAusProjekt(projektId, mitarbeiterId);
    }
}