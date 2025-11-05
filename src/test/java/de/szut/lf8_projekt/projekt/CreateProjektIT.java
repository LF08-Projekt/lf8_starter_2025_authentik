package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.ValidationService;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateProjektIT extends AbstractIntegrationTest {
    @MockBean
    private ValidationService validationService;

    @Test
    public void authorization() throws Exception {
        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 42,
                    "kundenId": 1001,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2025-10-15T00:00:00Z",
                    "geplantesEnddatum": "2026-01-31T23:59:59Z",
                    "geplanteQualifikationen": [
                        "CRM-Administration",
                        "Datenschutz-Grundlagen",
                        "Vertriebsschulung"
                        ]
                }
                """;

        final var contentAsString = this.mockMvc.perform(post("/LF08Projekt/Projekt")
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testFehlendePflichtfelder() throws Exception {
        final String content = """
            {
                "bezeichnung": "Einführung CRM-System",
                "verantwortlicherId": 2,
                "kundenId": 1,
                "kundeAnsprechperson": "Sabine Bauer",
                "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                "startdatum": "2025-10-15T00:00:00Z"
                "geplantesEnddatum": "2026-01-31T23:59:59Z",
            }
            """;


        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        fehlendePflichtangabeHilfsmethode(content, "bezeichnung");
        fehlendePflichtangabeHilfsmethode(content, "verantwortlicherId");
        fehlendePflichtangabeHilfsmethode(content, "kundenId");
        fehlendePflichtangabeHilfsmethode(content, "kundeAnsprechperson");
        fehlendePflichtangabeHilfsmethode(content, "projektzielKommentar");
        fehlendePflichtangabeHilfsmethode(content, "startdatum");
        fehlendePflichtangabeHilfsmethode(content, "geplantesEnddatum");
    }


    private void fehlendePflichtangabeHilfsmethode(String json, String feldName) throws Exception {
        String invalidJson = entferneFeld(json, feldName);

        mockMvc.perform(post("/LF08Projekt/Projekt")
                        .content(invalidJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    private String entferneFeld(String json, String feldName) {
        return Arrays.stream(json.split("\n"))
                .filter(line -> !line.contains("\"" + feldName + "\""))
                .collect(Collectors.joining("\n"));
    }

    @Test
    @WithMockUser
    public void verantwortlerExistiertNicht() throws Exception {
        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 22322,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2025-10-15T00:00:00Z",
                    "geplantesEnddatum": "2026-01-31T23:59:59Z",
                    "geplanteQualifikationen": [
                        "CRM-Administration",
                        "Datenschutz-Grundlagen",
                        "Vertriebsschulung"
                        ]
                }
                """;
        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(false);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        final var contentAsString = this.mockMvc.perform(post("/LF08Projekt/Projekt")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Mitarbeiter mit der ID 22322 existiert nicht!")));
    }

    @Test
    @WithMockUser
    public void kundeExistiertNicht() throws Exception {
        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 123456,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2025-10-15T00:00:00Z",
                    "geplantesEnddatum": "2026-01-31T23:59:59Z",
                    "geplanteQualifikationen": [
                        "CRM-Administration",
                        "Datenschutz-Grundlagen",
                        "Vertriebsschulung"
                        ]
                }
                """;

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(false);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        final var contentAsString = this.mockMvc.perform(post("/LF08Projekt/Projekt")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Kunde mit der ID + 123456 existiert nicht!")));
    }

    @Test
    @WithMockUser
    public void qualifikationExistiertNicht() throws Exception {
        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2025-10-15T00:00:00Z",
                    "geplantesEnddatum": "2026-01-31T23:59:59Z",
                    "geplanteQualifikationen": [
                        "Alien"
                        ]
                }
                """;

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(false);

        final var contentAsString = this.mockMvc.perform(post("/LF08Projekt/Projekt")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Liste der geplanten Qualifikationen enthält eine ungültige Qualifikation")));
    }

    @Test
    @WithMockUser
    public void enddatumVorStartdatum() throws Exception {
        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2025-10-15T00:00:00Z",
                    "geplantesEnddatum": "2024-01-31T23:59:59Z",
                    "geplanteQualifikationen": [
                        "Alien"
                        ]
                }
                """;

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        final var contentAsString = this.mockMvc.perform(post("/LF08Projekt/Projekt")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isTooEarly())
                .andExpect(jsonPath("$.message", is("Das geplante Ende des Projekts kann nicht vor dem Start des Projekts liegen")));
    }

    @Test
    @WithMockUser
    public void projektErstellenErfolgreich() throws Exception {
        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2025-10-15T00:00:00Z",
                    "geplantesEnddatum": "2026-01-31T23:59:59Z",
                    "geplanteQualifikationen": [
                        "CRM-Administration",
                        "Datenschutz-Grundlagen",
                        "Vertriebsschulung"
                        ]
                }
                """;

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        final var contentAsString = this.mockMvc.perform(post("/LF08Projekt/Projekt")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("bezeichnung", is("Einführung CRM-System")))
                .andExpect(jsonPath("verantwortlicherId", is(2)))
                .andExpect(jsonPath("kundenId", is(1)))
                .andExpect(jsonPath("kundeAnsprechperson", is("Sabine Bauer")))
                .andExpect(jsonPath("projektzielKommentar", is("Pilot im Vertrieb Q4, Rollout Q1")))
                .andExpect(jsonPath("startdatum", is("2025-10-15T00:00:00")))
                .andExpect(jsonPath("geplantesEnddatum", is("2026-01-31T23:59:59")))
                .andExpect(jsonPath("geplanteQualifikationen", hasItems("CRM-Administration", "Datenschutz-Grundlagen", "Vertriebsschulung")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var id = Long.parseLong(new JSONObject(contentAsString).get("id").toString());

        final var loadedEntity = projektRepository.findById(id);
        final var loadedQualifications = this.geplanteQualifikationRepository.getGeplanteQualifikationEntitiesByProjektId(id);

        assertThat(loadedEntity).isPresent();
        assertThat(loadedEntity.get().getId()).isEqualTo(id);
        assertThat(loadedEntity.get().getBezeichnung()).isEqualTo("Einführung CRM-System");
        assertThat(loadedEntity.get().getVerantwortlicherId()).isEqualTo(2);
        assertThat(loadedEntity.get().getKundenId()).isEqualTo(1);
        assertThat(loadedEntity.get().getKundeAnsprechperson()).isEqualTo("Sabine Bauer");
        assertThat(loadedEntity.get().getProjektzielKommentar()).isEqualTo("Pilot im Vertrieb Q4, Rollout Q1");
        assertThat(loadedEntity.get().getStartdatum()).isNotNull();
        assertThat(loadedEntity.get().getGeplantesEnddatum()).isNotNull();
        assertThat(loadedQualifications.stream().map(GeplanteQualifikationEntity::getQualifikation).toList())
                .containsExactlyInAnyOrder("CRM-Administration", "Datenschutz-Grundlagen", "Vertriebsschulung");
    }
}
