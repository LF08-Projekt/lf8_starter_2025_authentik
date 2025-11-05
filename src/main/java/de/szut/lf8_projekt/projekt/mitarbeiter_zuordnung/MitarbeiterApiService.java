package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service-Klasse für die Kommunikation mit der externen Employee-API.
 * Ruft Mitarbeiterdaten von https://employee-api.szut.dev ab.
 */
@Service
public class MitarbeiterApiService {
    private final RestTemplate restTemplate;
    private static final String EMPLOYEE_API_URL = "https://employee-api.szut.dev/employees";

    /**
     * Konstruktor für den MitarbeiterApiService.
     * Initialisiert das RestTemplate für API-Aufrufe.
     */
    public MitarbeiterApiService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Ruft Mitarbeiterdaten von der externen Employee API ab.
     *
     * @param mitarbeiterId Die ID des Mitarbeiters
     * @return Ein MitarbeiterDto mit den Mitarbeiterdaten oder null wenn der Mitarbeiter nicht gefunden wurde
     */
    public MitarbeiterDto getMitarbeiterById(Long mitarbeiterId) {
        try {
            String url = EMPLOYEE_API_URL + "/" + mitarbeiterId;
            return restTemplate.getForObject(url, MitarbeiterDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}
