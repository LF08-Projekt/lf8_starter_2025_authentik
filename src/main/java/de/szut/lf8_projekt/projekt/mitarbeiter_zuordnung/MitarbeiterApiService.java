package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class MitarbeiterApiService {
    private final RestTemplate restTemplate;
    private static final String EMPLOYEE_API_URL = "https://employee-api.szut.dev/employees";

    public MitarbeiterApiService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Ruft Mitarbeiterdaten von der externen Employee API ab.
     *
     * @param mitarbeiterId Die ID des Mitarbeiters
     * @return Ein MitarbeiterDto mit den Mitarbeiterdaten oder null wenn der Mitarbeiter nicht gefunden wurde
     */
    public MitarbeiterDto getMitarbeiterById(Long mitarbeiterId, String securityToken) {
        try {
            String url = EMPLOYEE_API_URL + "/" + mitarbeiterId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(securityToken);

            HttpEntity<Void> request = new HttpEntity<>(headers); // GET ohne Body
            ResponseEntity<MitarbeiterDto> resp = restTemplate.exchange(
                    url, HttpMethod.GET, request, MitarbeiterDto.class);

            return resp.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}
