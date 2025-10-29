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

@Service
public class ValidationService {
    private final RestTemplate restTemplate;

    public ValidationService() {
        restTemplate = new RestTemplate();
    }

    public Boolean validateProjektId(Long id, String securityToken) {
        throw new NotImplementedException();
    }

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

    public Boolean validateKundenId(Long id, String securityToken) {
        return true;
    }
}
