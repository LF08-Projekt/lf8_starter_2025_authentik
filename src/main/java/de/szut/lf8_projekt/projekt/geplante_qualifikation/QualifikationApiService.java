package de.szut.lf8_projekt.projekt.geplante_qualifikation;

import de.szut.lf8_projekt.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Array;
import java.util.Arrays;

@Service
public class QualifikationApiService {
    private final RestTemplate restTemplate;
    private static final String QUALIFICATION_API_URL = "https://employee-api.szut.dev/qualifications";

    public QualifikationApiService() {
        this.restTemplate = new RestTemplate();
    }

    public SkillDto[] getAllQualifikations(String securityToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(securityToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<SkillDto[]> resp = restTemplate.exchange(
                QUALIFICATION_API_URL, HttpMethod.GET, request, SkillDto[].class);

        var respBody = resp.getBody();
        if (respBody == null) {
            throw new ResourceNotFoundException("No list of qualifications was found");
        }

        return respBody;
    }

    public SkillDto getQualifikationById(Long id, String securityToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(securityToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<SkillDto> resp = restTemplate.exchange(
                QUALIFICATION_API_URL + "/" + id, HttpMethod.GET, request, SkillDto.class);

        var respBody = resp.getBody();
        if (respBody == null) {
            throw new ResourceNotFoundException("No list of qualifications was found");
        }

        return respBody;
    }
}
