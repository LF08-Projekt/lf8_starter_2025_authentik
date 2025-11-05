package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.ValidationService;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungRepository;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjektUpdatenIT extends AbstractIntegrationTest {
    @MockBean
    private ValidationService validationService;

    private ProjektEntity setUpDefaultProjekt() {
        ProjektEntity entity = new ProjektEntity();
        entity.setBezeichnung("CRM-System");
        entity.setVerantwortlicherId(5L);
        entity.setKundenId(4L);
        entity.setKundeAnsprechperson("Mike Herrmann");
        entity.setProjektzielKommentar("Geld");
        entity.setStartdatum(LocalDateTime.parse("2024-10-15T00:00:00"));
        entity.setGeplantesEnddatum(LocalDateTime.parse("2025-01-31T23:59:59"));
        entity.setWirklichesEnddatum(LocalDateTime.parse("2025-02-15T23:59:59"));
        return this.projektRepository.save(entity);
    }

    private String setUpDefaultContent() {
        return """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2024-11-15T00:00:00",
                    "geplantesEnddatum": "2025-03-15T23:59:59",
                    "wirklichesEnddatum": "2025-03-16T23:59:59",
                    "geplanteQualifikationen": [
                        "CRM-Administration",
                        "Datenschutz-Grundlagen",
                        "Vertriebsschulung"
                        ]
                }
                """;
    }

    @Test
    public void authorization() throws Exception {
        final String content = this.setUpDefaultContent();

        this.setUpDefaultProjekt();
        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/0")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void ProjektExistiertNicht() throws Exception {
        final String content = this.setUpDefaultContent();

        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/99")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Projekt mit der ID 99 existiert nicht")));
    }

    @Test
    @WithMockUser
    public void MitarbeiterUnbekannt() throws Exception {
        ProjektEntity entity = this.setUpDefaultProjekt();
        final String content = this.setUpDefaultContent();

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(false);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/" + entity.getId())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Mitarbeiter mit der ID 2 existiert nicht!")));
    }

    @Test
    @WithMockUser
    public void KundeUnbekannt() throws Exception {
        ProjektEntity entity = this.setUpDefaultProjekt();
        final String content = this.setUpDefaultContent();

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(false);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        this.projektRepository.save(entity);
        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/" + entity.getId())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Kunde mit der ID 1 existiert nicht!")));
    }

    @Test
    @WithMockUser
    public void QualifikationUnbekannt() throws Exception {
        ProjektEntity entity = this.setUpDefaultProjekt();
        final String content = this.setUpDefaultContent();

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(false);

        this.projektRepository.save(entity);
        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/" + entity.getId())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Liste der geplanten Qualifikationen enthält eine ungültige Qualifikation")));
    }

    @Test
    @WithMockUser
    public void verschobenesGeplantesEnddatumvorStartdatum() throws Exception {
        ProjektEntity entity = this.setUpDefaultProjekt();
        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2024-11-15T00:00:00",
                    "geplantesEnddatum": "2015-03-15T23:59:59",
                    "wirklichesEnddatum": "2025-03-16T23:59:59",
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

        this.projektRepository.save(entity);
        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/" + entity.getId())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isTooEarly())
                .andExpect(jsonPath("$.message", is("Das geplante Ende des Projekts kann nicht vor dem Start des Projekts liegen")));
    }

    @Test
    @WithMockUser
    public void verschobenesWirklichesEnddatumvorStartdatum() throws Exception {
        ProjektEntity entity = this.setUpDefaultProjekt();
        final String content = """
                {
                    "bezeichnung": "Einführung CRM-System",
                    "verantwortlicherId": 2,
                    "kundenId": 1,
                    "kundeAnsprechperson": "Sabine Bauer",
                    "projektzielKommentar": "Pilot im Vertrieb Q4, Rollout Q1",
                    "startdatum": "2024-11-15T00:00:00",
                    "geplantesEnddatum": "2025-03-15T23:59:59",
                    "wirklichesEnddatum": "2015-03-16T23:59:59",
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

        this.projektRepository.save(entity);
        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/" + entity.getId())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isTooEarly())
                .andExpect(jsonPath("$.message", is("Das wirkliche Ende des Projekts kann nicht vor dem Start des Projekts liegen")));
    }

    public void verschobenesEnddatumUngültig() throws Exception {
        ProjektEntity entity = this.setUpDefaultProjekt();

        ProjektEntity entity2 = new ProjektEntity();
        entity2.setBezeichnung("CRM-System2");
        entity2.setVerantwortlicherId(3L);
        entity2.setKundenId(4L);
        entity2.setKundeAnsprechperson("Herr Mikemann");
        entity2.setProjektzielKommentar("Kohle");
        entity2.setStartdatum(LocalDateTime.parse("2025-2-1T00:00:00"));
        entity2.setGeplantesEnddatum(LocalDateTime.parse("2025-03-31T23:59:59"));
        entity2.setWirklichesEnddatum(LocalDateTime.parse("2025-04-15T23:59:59"));
        entity2 = this.projektRepository.save(entity2);

        MitarbeiterZuordnungEntity zuordnungEntity = new MitarbeiterZuordnungEntity();
        zuordnungEntity.setProjektId(entity.getId());
        zuordnungEntity.setMitarbeiterId(42L);
        zuordnungEntity.setQualifikationId(1L);
        zuordnungEntity = this.geplanteMitarbeiterZurdnungRepository.save(zuordnungEntity);

        MitarbeiterZuordnungEntity zuordnungEntity2 = new MitarbeiterZuordnungEntity();
        zuordnungEntity2.setProjektId(entity2.getId());
        zuordnungEntity2.setMitarbeiterId(42L);
        zuordnungEntity2.setQualifikationId(1L);
        zuordnungEntity2 = this.geplanteMitarbeiterZurdnungRepository.save(zuordnungEntity2);

        final String content = this.setUpDefaultContent();

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/" + entity.getId())
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Mitarbeiter 42 ist" +
                        " zu diesem Zeit bereits in Projekt " + entity2.getId() +
                        " Bitte klären sie diesen Konflikt")));
    }

    @Test
    @WithMockUser
    public void UpdatenErfolgreich() throws Exception {
        ProjektEntity entity = this.setUpDefaultProjekt();

        final String content = this.setUpDefaultContent();

        when(validationService.validateMitarbeiterId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateKundenId(any(Long.class), nullable(String.class))).thenReturn(true);
        when(validationService.validateQualifications(any(), nullable(String.class))).thenReturn(true);

        this.projektRepository.save(entity);
        final var contentAsString = this.mockMvc.perform(put("/LF08Projekt/Projekt/" + entity.getId())
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
                .andExpect(jsonPath("startdatum", is("2024-11-15T00:00:00")))
                .andExpect(jsonPath("geplantesEnddatum", is("2025-03-15T23:59:59")))
                .andExpect(jsonPath("wirklichesEnddatum", is("2025-03-16T23:59:59")))
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
