package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.mitarbeiter.MitarbeiterApiClient;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProjektService {

    private final ProjektRepository projektRepository;
    private final ProjektMitarbeiterRepository projektMitarbeiterRepository;
    private final MitarbeiterApiClient mitarbeiterApiClient;

    public ProjektService(
            ProjektRepository projektRepository,
            ProjektMitarbeiterRepository projektMitarbeiterRepository,
            MitarbeiterApiClient mitarbeiterApiClient) {
        this.projektRepository = projektRepository;
        this.projektMitarbeiterRepository = projektMitarbeiterRepository;
        this.mitarbeiterApiClient = mitarbeiterApiClient;
    }

    public MitarbeiterEntferntDto entferneMitarbeiterAusProjekt(Long projektId, Long mitarbeiterId) {
        // 1. Projekt prüfen
        var projekt = projektRepository.findById(projektId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projekt mit der ID " + projektId + " existiert nicht"
                ));

        // 2. Mitarbeiter von externer API prüfen
        var mitarbeiter = mitarbeiterApiClient.getMitarbeiter(mitarbeiterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mitarbeiter mit der ID " + mitarbeiterId + " existiert nicht"
                ));

        // 3. Zuordnung prüfen
        var zuordnung = projektMitarbeiterRepository
                .findByProjektIdAndMitarbeiterId(projektId, mitarbeiterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mitarbeiter mit der Mitarbeiternummer " + mitarbeiterId +
                                " arbeitet nicht in dem angegebenen Projekt mit der Projekt-ID " + projektId
                ));

        // 4. Zuordnung löschen
        projektMitarbeiterRepository.delete(zuordnung);

        // 5. Response erstellen
        return new MitarbeiterEntferntDto(
                mitarbeiterId,
                mitarbeiter.getFullName(),
                projekt.getId(),
                projekt.getBezeichnung()
        );
    }
}
