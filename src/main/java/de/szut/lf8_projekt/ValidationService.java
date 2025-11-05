package de.szut.lf8_projekt;

import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_projekt.mitarbeiter.QualificationGetDto;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Service-Klasse für die Validierung von Daten gegen die externe Employee-API.
 * Prüft die Existenz von Mitarbeitern, Qualifikationen und Kunden.
 */
@Service
public class ValidationService {
    private final RestTemplate restTemplate;

    /**
     * Konstruktor für den ValidationService.
     * Initialisiert das RestTemplate für API-Aufrufe.
     */
    public ValidationService() {
        restTemplate = new RestTemplate();
    }

    /**
     * Validiert eine Projekt-ID (noch nicht implementiert).
     *
     * @param id Die zu validierende Projekt-ID
     * @param securityToken Das Security-Token für die API-Authentifizierung
     * @return true wenn das Projekt existiert
     * @throws NotImplementedException da noch nicht implementiert
     */
    public Boolean validateProjektId(Long id, String securityToken) {
        throw new NotImplementedException();
    }

    /**
     * Validiert eine Mitarbeiter-ID über die externe Employee-API.
     * Prüft ob ein Mitarbeiter mit der gegebenen ID existiert.
     *
     * @param id Die zu validierende Mitarbeiter-ID
     * @param securityToken Das Security-Token für die API-Authentifizierung
     * @return true wenn der Mitarbeiter existiert, false bei Fehler oder wenn nicht gefunden
     */
    public Boolean validateMitarbeiterId(Long id, String securityToken) {
        try {
            String url = "https://employee-api.szut.dev/employees/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(securityToken);

            HttpEntity<Void> request = new HttpEntity<>(headers); // GET ohne Body
            ResponseEntity<Object> resp = restTemplate.exchange(
                    url, HttpMethod.GET, request, Object.class);

            return resp.getBody() != null;
        }
        catch(Exception e) {
            return false;
        }
    }

    /**
     * Validiert eine Liste von Qualifikationen über die externe Employee-API.
     * Prüft ob alle angegebenen Qualifikationen im System existieren.
     *
     * @param qualifications Liste der zu validierenden Qualifikationsbezeichnungen
     * @param securityToken Das Security-Token für die API-Authentifizierung
     * @return true wenn alle Qualifikationen existieren, false bei Fehler
     * @throws ResourceNotFoundException wenn eine Qualifikation nicht existiert
     */
    public Boolean validateQualifications(List<String> qualifications, String securityToken) {
        try {
            String url = "https://employee-api.szut.dev/qualifications";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(securityToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<QualificationGetDto[]> resp = restTemplate.exchange(
                    url, HttpMethod.GET, request, QualificationGetDto[].class);

            var respBody = resp.getBody();
            if (respBody == null) {
                throw new ResourceNotFoundException("No list of qualifications was found");
            }

            for (String qualification : qualifications) {
                if (!Arrays.stream(respBody).anyMatch(x -> x.getSkill().equals(qualification))) {
                    throw new ResourceNotFoundException("The qualification \"" + qualification + "\" does not exist!");
                }
            }

            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Validiert eine Kunden-ID.
     * Aktuell wird immer true zurückgegeben (Platzhalter-Implementierung).
     *
     * @param id Die zu validierende Kunden-ID
     * @param securityToken Das Security-Token für die API-Authentifizierung
     * @return immer true
     */
    public Boolean validateKundenId(Long id, String securityToken) {
        return true;
    }
}
