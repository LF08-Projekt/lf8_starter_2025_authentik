package de.szut.lf8_projekt.integrationtests.projekt;

import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostIT extends AbstractIntegrationTest {
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
    public void storeAndFind() throws Exception {
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
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("bezeichnung", is("Einführung CRM-System")))
                .andExpect(jsonPath("verantwortlicherId", is(42)))
                .andExpect(jsonPath("kundenId", is(1001)))
                .andExpect(jsonPath("kundeAnsprechperson", is("Sabine Bauer")))
                .andExpect(jsonPath("projektzielKommentar", is("Pilot im Vertrieb Q4, Rollout Q1")))
                .andExpect(jsonPath("startdatum", is("2025-10-15T00:00:00Z")))
                .andExpect(jsonPath("geplantesEnddatum", is("2026-01-31T23:59:59Z")))
                .andExpect(jsonPath("geplanteQualifikationen", hasItems("CRM-Administration", "Datenschutz-Grundlagen", "Vertriebsschulung")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var id = Long.parseLong(new JSONObject(contentAsString).get("id").toString());

        final var loadedEntity = projektRepository.findById(id);

        assertThat(loadedEntity).isPresent();
        assertThat(loadedEntity.get().getId()).isEqualTo(id);
        assertThat(loadedEntity.get().getBezeichnung()).isEqualTo("Foo");
        assertThat(loadedEntity.get().getVerantwortlicherId()).isEqualTo("Foo");
        assertThat(loadedEntity.get().getKundenId()).isEqualTo("Foo");
        assertThat(loadedEntity.get().getKundeAnsprechperson()).isEqualTo("Foo");
        assertThat(loadedEntity.get().getProjektzielKommentar()).isEqualTo("Foo");
        assertThat(loadedEntity.get().getStartdatum()).isEqualTo("Foo");
        assertThat(loadedEntity.get().getGeplantesEnddatum()).isEqualTo("Foo");
//        assertThat(loadedEntity.get().get()).isEqualTo("Foo");
    }
}
