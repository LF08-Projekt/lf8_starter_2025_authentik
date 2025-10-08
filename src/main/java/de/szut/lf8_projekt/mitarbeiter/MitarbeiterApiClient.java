package de.szut.lf8_projekt.mitarbeiter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class MitarbeiterApiClient {

    private final RestClient restClient;

    public MitarbeiterApiClient() {
        this.restClient = RestClient.create("https://employee-api.szut.dev");
    }

    public Optional<MitarbeiterDto> getMitarbeiter(Long mitarbeiterId) {
        try {
            var mitarbeiter = restClient.get()
                    .uri("/employees/{id}", mitarbeiterId)
                    .header("Authorization", "Bearer " + getToken())
                    .retrieve()
                    .body(MitarbeiterDto.class);

            return Optional.ofNullable(mitarbeiter);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        return "";
    }
}