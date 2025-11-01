package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.ValidationService;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjektUpdatenIT extends AbstractIntegrationTest {
    @MockBean
    private ValidationService validationService;

    @Test
    public void authorization() throws Exception {
        ProjektEntity entity = new ProjektEntity();
        entity.setBezeichnung("CRM-System");
        entity.setVerantwortlicherId(5L);
        entity.setKundenId(4L);
        entity.setKundeAnsprechperson("Mike Herrmann");
        entity.setProjektzielKommentar("Geld");
        entity.setStartdatum(new Date("2024-10-15T00:00:00Z"));
        entity.setGeplantesEnddatum(new Date("2025-01-31T23:59:59Z"));
        entity.setWirklichesEnddatum(new Date("2025-02-15T23:59:59Z"));

        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2025-10-15T00:00:00Z",
                    "geplantesEnddatum": "2026-01-31T23:59:59Z",
                    "wirklichesEnddatum": "2026-02-15T23:59:59Z",
                    "geplanteQualifikationen": [
                        "CRM-Administration",
                        "Datenschutz-Grundlagen",
                        "Vertriebsschulung"
                        ]
                }
                """;

        this.projektRepository.save(entity);
        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/0")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void UpdatenErfolgreich() throws Exception {
        ProjektEntity entity = new ProjektEntity();
        entity.setBezeichnung("CRM-System");
        entity.setVerantwortlicherId(5L);
        entity.setKundenId(4L);
        entity.setKundeAnsprechperson("Mike Herrmann");
        entity.setProjektzielKommentar("Geld");
        entity.setStartdatum(new Date("2024-10-15T00:00:00Z"));
        entity.setGeplantesEnddatum(new Date("2025-01-31T23:59:59Z"));
        entity.setWirklichesEnddatum(new Date("2025-02-15T23:59:59Z"));

        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2025-10-15T00:00:00Z",
                    "geplantesEnddatum": "2026-01-31T23:59:59Z",
                    "wirklichesEnddatum": "2026-02-15T23:59:59Z",
                    "geplanteQualifikationen": [
                        "CRM-Administration",
                        "Datenschutz-Grundlagen",
                        "Vertriebsschulung"
                        ]
                }
                """;

        this.projektRepository.save(entity);
        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/0")
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
                .andExpect(jsonPath("startdatum", is("2025-10-15T00:00:00.000+00:00")))
                .andExpect(jsonPath("geplantesEnddatum", is("2026-01-31T23:59:59.000+00:00")))
                .andExpect(jsonPath("wirklichesEnddatum", is("2026-02-15T23:59:59Z")))
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
        assertThat(loadedEntity.get().getGeplantesEnddatum()).isNotNull();
        assertThat(loadedQualifications.stream().map(GeplanteQualifikationEntity::getQualifikation).toList())
                .containsExactlyInAnyOrder("CRM-Administration", "Datenschutz-Grundlagen", "Vertriebsschulung");
    }
}
