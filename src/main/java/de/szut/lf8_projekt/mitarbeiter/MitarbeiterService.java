package de.szut.lf8_projekt.mitarbeiter;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service für Mitarbeiter-bezogene Vorgänge innerhalb dieses Projekts.
 * Aktuell Platzhalter für spätere Erweiterungen (z. B. Proxy-Aufrufe zur Employee-API).
 */
@Service
public class MitarbeiterService {

    private final RestTemplate restTemplate;
    private final String base_url = "https://employee-api.szut.dev";

    /**
     * Erstellt den Service und initialisiert das {@link RestTemplate}.
     */
    public MitarbeiterService() {
        this.restTemplate = new RestTemplate();
    }
}
