package de.szut.lf8_projekt.mitarbeiter;

import de.szut.lf8_projekt.Exceptions.ApiCallFailedException;
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

    public GetMitarbeiterDto getMitarbeiterDto(Long id) {
        String url = base_url + "/employees/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", " Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjgyYzg5YjIxNTA4YjMwODQ1NDQyNGRiOGYyMDU1ODI3IiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwczovL2F1dGhlbnRpay5zenV0LmRldi9hcHBsaWNhdGlvbi9vL2hpdGVjLyIsInN1YiI6IjhhYjRkZTBiNmJhZGQ2ZGVhNTM1M2Y3YzExNWVmN2FiZGMyMmEzYmQyZTNmZjRjMmU5NDYzN2JlY2I2OGUzMGEiLCJhdWQiOiJoaXRlY19hcGlfY2xpZW50IiwiZXhwIjoxNzU5OTIxOTE5LCJpYXQiOjE3NTk5MTQ3MTksImF1dGhfdGltZSI6MTc1OTkxNDcxOSwiYWNyIjoiZ29hdXRoZW50aWsuaW8vcHJvdmlkZXJzL29hdXRoMi9kZWZhdWx0IiwiYXpwIjoiaGl0ZWNfYXBpX2NsaWVudCIsInVpZCI6IlVlaTI4MzNGc3RjZTNWd3JDQWYzaVNHUTJHbmF5RG52M3d0UEZRcGMifQ.Hs1ofSKjFLLQ-MMVYZaY_o_f-P__YqU8aE0cdXXAspk8wO9dUlkfcAjfLCxxWql5hTXU9g7bwh5BCSBAkMEe2S4IZ-kMY0KtvMFswVc-U6-HvgSQaIddntGtTa7-CUdwRjiZIMgtPVOX94gMCwb6ckGu164RcmxkyEQmjo1ewgrZ9zLVkxKlCiIrf6ReXovj1DrgvZPbg181QHpqXTn_QCtN9rmaix2EC6fyBRzXxevPGd313LCeK1_zPCVBllc8dNYGVFHsUiXgIGEbi_EWzQq-r3Qnd_98mdbrByKE4y1upH-XOtz4e--MQxrXiUpSPkDuqA0dP6MGxHdWtx3qjK3iHOhzcJHgPNOizg-490JLuA9pPBHD933G7zqJZEAb8pbWc3vIRuAGDdmXI6gdS6eGsRB4nQDxXNTyUFxQxH7IFR_EvEIwh_keW8XVCsqAFRSyF_zEJzr0Q2gLdSLdoklAzYAD4IQvNc1kqotnHOA_ONM3zhM_OlG8k_QgmZ6eiVMdEvtYQcclfDraQXCue6-1amRGGCW0Y5DFfbOwWr67pROz8GBg8B7vSEUqJn2BIV6B2yHwA-7pHDTsbFNe8bkF5mQ7FA9qm3XWvhS_Vw8mYQkoOmJsLBerZ-IUMcoswoSOkZK9kdG2OkPebp2YPbz1VAwew_g7Q9ezWinqPlU");
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<GetMitarbeiterDto> response = this.restTemplate.exchange(url, HttpMethod.GET, request, GetMitarbeiterDto.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new ResourceNotFoundException("Der Mitarbeiter mit der id " + id + " existiert nicht.");
        } else {
            throw new ApiCallFailedException("Der Aufruf zu der Employee API ist mit dem Statuscode " + response.getStatusCode() + "fehlgeschlagen.");
        }
    }

}
