package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.Application;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class MitarbeiterEntfernenSecurityIT extends AbstractIntegrationTest {

    @MockBean
    private MitarbeiterApiService mitarbeiterApiService;

    @Test
    @WithMockUser(roles = "user")
    void testUngueltigerDatentyp_BuchstabenStattZahlen() throws Exception {
        // Test: Buchstaben statt Zahlen in Path-Parameter
        // Spring's PathVariable Konvertierung sollte dies abfangen
        this.mockMvc.perform(delete("/LF08Projekt/abc/mitarbeiter/123")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "user")
    void testNegativeIds() throws Exception {
        // Test: Negative IDs - sollten jetzt mit Validierung abgelehnt werden
        this.mockMvc.perform(delete("/LF08Projekt/-1/mitarbeiter/123")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "user")
    void testNullIds() throws Exception {
        // Test: ID 0 (oft problematisch) - sollte jetzt mit Validierung abgelehnt werden
        this.mockMvc.perform(delete("/LF08Projekt/0/mitarbeiter/0")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "user")
    void testExterneApiTimeout() throws Exception {
        // Testdaten vorbereiten
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        MitarbeiterZuordnungEntity zuordnung = new MitarbeiterZuordnungEntity();
        zuordnung.setProjektId(projekt.getId());
        zuordnung.setMitarbeiterId(42L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung);

        // Mock: Externe API wirft Exception (Timeout/Netzwerkfehler)
        when(mitarbeiterApiService.getMitarbeiterById(42L, anyString()))
                .thenThrow(new RestClientException("Connection timeout"));

        // Sollte 500 Internal Server Error zurückgeben
        this.mockMvc.perform(delete("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/42")
                        .with(csrf()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(roles = "user")
    void testExterneApiGibtNullZurueck() throws Exception {
        // Testdaten vorbereiten
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        MitarbeiterZuordnungEntity zuordnung = new MitarbeiterZuordnungEntity();
        zuordnung.setProjektId(projekt.getId());
        zuordnung.setMitarbeiterId(999L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung);

        // Mock: Externe API gibt null zurück
        when(mitarbeiterApiService.getMitarbeiterById(999L, anyString())).thenReturn(null);

        // Sollte 404 zurückgeben mit passender Fehlermeldung
        this.mockMvc.perform(delete("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "user")
    void testExterneApiGibtUnvollstaendigeDatenZurueck() throws Exception {
        // Testdaten vorbereiten
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        MitarbeiterZuordnungEntity zuordnung = new MitarbeiterZuordnungEntity();
        zuordnung.setProjektId(projekt.getId());
        zuordnung.setMitarbeiterId(42L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung);

        // Mock: Externe API gibt unvollständige Daten zurück (null Vorname/Nachname)
        MitarbeiterDto mitarbeiter = new MitarbeiterDto();
        mitarbeiter.setId(42L);
        mitarbeiter.setVorname(null);
        mitarbeiter.setNachname(null);
        when(mitarbeiterApiService.getMitarbeiterById(42L, anyString())).thenReturn(mitarbeiter);

        // Sollte trotzdem funktionieren, Name sollte jetzt "Unbekannt" sein (nicht "null null")
        this.mockMvc.perform(delete("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/42")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mitarbeiterName", is("Unbekannt")));
    }

    @Test
    @WithMockUser(roles = "user")
    void testSehrGrosseIds() throws Exception {
        // Test: Sehr große IDs (Long.MAX_VALUE)
        this.mockMvc.perform(delete("/LF08Projekt/9223372036854775807/mitarbeiter/9223372036854775807")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
