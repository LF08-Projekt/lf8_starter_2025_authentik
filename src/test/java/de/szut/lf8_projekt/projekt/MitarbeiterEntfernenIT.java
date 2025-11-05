package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.Application;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class MitarbeiterEntfernenIT extends AbstractIntegrationTest {

    @MockBean
    private MitarbeiterApiService mitarbeiterApiService;

    @Test
    void authorization() throws Exception {
        // Ohne JWT sollte Zugriff verweigert werden
        this.mockMvc.perform(delete("/LF08Projekt/1/mitarbeiter/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void happyPath() throws Exception {
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

        // Mock für externe API
        MitarbeiterDto mitarbeiter = new MitarbeiterDto();
        mitarbeiter.setId(42L);
        mitarbeiter.setVorname("Max");
        mitarbeiter.setNachname("Mustermann");
        when(mitarbeiterApiService.getMitarbeiterById(eq(42L), nullable(String.class))).thenReturn(mitarbeiter);

        // Test durchführen
        this.mockMvc.perform(delete("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/42")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mitarbeiterId", is(42)))
                .andExpect(jsonPath("$.mitarbeiterName", is("Max Mustermann")))
                .andExpect(jsonPath("$.projektId", is(projekt.getId().intValue())))
                .andExpect(jsonPath("$.projektBezeichnung", is("Testprojekt")));

        // Verifizieren dass Zuordnung gelöscht wurde
        assertThat(geplanteMitarbeiterZurdnungRepository.findByProjektIdAndMitarbeiterId(projekt.getId(), 42L))
                .isEmpty();
    }

    @Test
    @WithMockUser(roles = "user")
    void projektNichtGefunden() throws Exception {
        this.mockMvc.perform(delete("/LF08Projekt/999/mitarbeiter/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("existiert nicht")));
    }

    @Test
    @WithMockUser(roles = "user")
    void mitarbeiterNichtGefunden() throws Exception {
        // Projekt erstellen
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        // Mock: Mitarbeiter existiert nicht
        when(mitarbeiterApiService.getMitarbeiterById(ArgumentMatchers.any(Long.class), nullable(String.class))).thenReturn(null);

        this.mockMvc.perform(delete("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/999")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Mitarbeiter mit der ID 999 existiert nicht")));
    }

    @Test
    @WithMockUser(roles = "user")
    void mitarbeiterNichtImProjekt() throws Exception {
        // Projekt erstellen
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        // Mock: Mitarbeiter existiert
        MitarbeiterDto mitarbeiter = new MitarbeiterDto();
        mitarbeiter.setId(42L);
        mitarbeiter.setVorname("Max");
        mitarbeiter.setNachname("Mustermann");
        when(mitarbeiterApiService.getMitarbeiterById(any(Long.class), nullable(String.class))).thenReturn(mitarbeiter);

        // Keine Zuordnung erstellt!

        this.mockMvc.perform(delete("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/42")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(
                        "Mitarbeiter mit der Mitarbeiternummer 42 arbeitet nicht in dem angegebenen Projekt mit der Projekt-ID " + projekt.getId()
                )));
    }
}
