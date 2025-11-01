package de.szut.lf8_projekt.mitarbeiter;

import de.szut.lf8_projekt.exceptionHandling.ApiCallFailedException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MitarbeiterService {

    private final RestTemplate restTemplate;
    private final String base_url = "https://employee-api.szut.dev";

    public MitarbeiterService() {
        this.restTemplate = new RestTemplate();
    }
}
