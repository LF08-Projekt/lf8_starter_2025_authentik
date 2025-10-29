package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class MitarbeiterApiService {
    private final RestTemplate restTemplate;
    private static final String EMPLOYEE_API_URL = "https://employee-api.szut.dev/employees";

    public MitarbeiterApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MitarbeiterDto getMitarbeiterById(Long mitarbeiterId) {
        try {
            String url = EMPLOYEE_API_URL + "/" + mitarbeiterId;
            return restTemplate.getForObject(url, MitarbeiterDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}
