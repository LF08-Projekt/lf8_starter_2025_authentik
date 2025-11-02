package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.Application;
import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class ProjektLoeschenIT extends AbstractIntegrationTest {

    @MockBean
    private MitarbeiterApiService mitarbeiterApiService;

    @Test
    void projektLoeschenOhneAuthentifizierung() throws Exception {
        this.mockMvc.perform(delete("/LF08Projekt/Projekt/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void projektLoeschenNichtGefunden() throws Exception {
        this.mockMvc.perform(delete("/LF08Projekt/Projekt/999")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("existiert nicht")));
    }

    @Test
    @WithMockUser(roles = "user")
    void projektLoeschenHappyPath() throws Exception {
        // Projekt erstellen
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt zum Löschen");
        projekt.setProjektzielKommentar("Dieses Projekt soll gelöscht werden");
        projekt.setKundenId(456L);
        projekt.setKundeAnsprechperson("Anna Test");
        projekt.setVerantwortlicherId(10L);
        projekt.setStartdatum(LocalDateTime.of(2025, 2, 1, 0, 0));
        projekt.setGeplantesEnddatum(LocalDateTime.of(2025, 8, 31, 23, 59));
        projekt = projektRepository.save(projekt);

        // Geplante Qualifikationen
        GeplanteQualifikationEntity qual1 = new GeplanteQualifikationEntity();
        qual1.setProjektId(projekt.getId());
        qual1.setQualifikation("Python");
        geplanteQualifikationRepository.save(qual1);

        GeplanteQualifikationEntity qual2 = new GeplanteQualifikationEntity();
        qual2.setProjektId(projekt.getId());
        qual2.setQualifikation("Docker");
        geplanteQualifikationRepository.save(qual2);

        // Mitarbeiter zuordnen
        MitarbeiterZuordnungEntity zuordnung1 = new MitarbeiterZuordnungEntity();
        zuordnung1.setProjektId(projekt.getId());
        zuordnung1.setMitarbeiterId(100L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung1);

        MitarbeiterZuordnungEntity zuordnung2 = new MitarbeiterZuordnungEntity();
        zuordnung2.setProjektId(projekt.getId());
        zuordnung2.setMitarbeiterId(101L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung2);

        // Mock Mitarbeiter 1
        MitarbeiterDto mitarbeiter1 = new MitarbeiterDto();
        mitarbeiter1.setId(100L);
        mitarbeiter1.setVorname("Peter");
        mitarbeiter1.setNachname("Python");
        SkillDto pythonSkill = new SkillDto();
        pythonSkill.setId(10L);
        pythonSkill.setSkill("Python");
        mitarbeiter1.setSkillSet(Arrays.asList(pythonSkill));
        when(mitarbeiterApiService.getMitarbeiterById(100L)).thenReturn(mitarbeiter1);

        // Mock Mitarbeiter 2
        MitarbeiterDto mitarbeiter2 = new MitarbeiterDto();
        mitarbeiter2.setId(101L);
        mitarbeiter2.setVorname("Diana");
        mitarbeiter2.setNachname("Docker");
        SkillDto dockerSkill = new SkillDto();
        dockerSkill.setId(11L);
        dockerSkill.setSkill("Docker");
        mitarbeiter2.setSkillSet(Arrays.asList(dockerSkill));
        when(mitarbeiterApiService.getMitarbeiterById(101L)).thenReturn(mitarbeiter2);

        // Test DELETE
        this.mockMvc.perform(delete("/LF08Projekt/Projekt/" + projekt.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projektId", is(projekt.getId().intValue())))
                .andExpect(jsonPath("$.bezeichnung", is("Testprojekt zum Löschen")))
                .andExpect(jsonPath("$.kommentar", is("Dieses Projekt soll gelöscht werden")))
                .andExpect(jsonPath("$.kundenId", is(456)))
                .andExpect(jsonPath("$.kundenAnsprechpartnerName", is("Anna Test")))
                .andExpect(jsonPath("$.verantwortlicherMitarbeiterId", is(10)))
                .andExpect(jsonPath("$.benoetigteQualifikationen", hasSize(2)))
                .andExpect(jsonPath("$.benoetigteQualifikationen", containsInAnyOrder("Python", "Docker")))
                .andExpect(jsonPath("$.mitarbeiter", hasSize(2)))
                .andExpect(jsonPath("$.mitarbeiter[0].mitarbeiterId", is(100)))
                .andExpect(jsonPath("$.mitarbeiter[0].name", is("Peter Python")))
                .andExpect(jsonPath("$.mitarbeiter[0].qualifikationen", hasSize(1)))
                .andExpect(jsonPath("$.mitarbeiter[0].qualifikationen[0]", is("Python")))
                .andExpect(jsonPath("$.mitarbeiter[1].mitarbeiterId", is(101)))
                .andExpect(jsonPath("$.mitarbeiter[1].name", is("Diana Docker")))
                .andExpect(jsonPath("$.mitarbeiter[1].qualifikationen", hasSize(1)))
                .andExpect(jsonPath("$.mitarbeiter[1].qualifikationen[0]", is("Docker")));

        // Verify projekt wurde wirklich gelöscht
        assert projektRepository.findById(projekt.getId()).isEmpty();
    }

    @Test
    @WithMockUser(roles = "user")
    void projektLoeschenOhneMitarbeiter() throws Exception {
        // Projekt ohne Mitarbeiter
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Leeres Projekt");
        projekt.setVerantwortlicherId(5L);
        projekt.setKundenId(789L);
        projekt.setKundeAnsprechperson("Test Person");
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        this.mockMvc.perform(delete("/LF08Projekt/Projekt/" + projekt.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projektId", is(projekt.getId().intValue())))
                .andExpect(jsonPath("$.bezeichnung", is("Leeres Projekt")))
                .andExpect(jsonPath("$.mitarbeiter", hasSize(0)))
                .andExpect(jsonPath("$.benoetigteQualifikationen", hasSize(0)));

        // Verify projekt wurde gelöscht
        assert projektRepository.findById(projekt.getId()).isEmpty();
    }

    @Test
    @WithMockUser(roles = "user")
    void projektLoeschenOhneQualifikationen() throws Exception {
        // Projekt ohne geplante Qualifikationen
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Projekt ohne Qualifikationen");
        projekt.setVerantwortlicherId(7L);
        projekt.setKundenId(111L);
        projekt.setKundeAnsprechperson("Kunde X");
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        // Mitarbeiter zuordnen
        MitarbeiterZuordnungEntity zuordnung = new MitarbeiterZuordnungEntity();
        zuordnung.setProjektId(projekt.getId());
        zuordnung.setMitarbeiterId(200L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung);

        // Mock Mitarbeiter
        MitarbeiterDto mitarbeiter = new MitarbeiterDto();
        mitarbeiter.setId(200L);
        mitarbeiter.setVorname("Max");
        mitarbeiter.setNachname("Müller");
        SkillDto skill = new SkillDto();
        skill.setId(20L);
        skill.setSkill("Java");
        mitarbeiter.setSkillSet(Arrays.asList(skill));
        when(mitarbeiterApiService.getMitarbeiterById(200L)).thenReturn(mitarbeiter);

        this.mockMvc.perform(delete("/LF08Projekt/Projekt/" + projekt.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.benoetigteQualifikationen", hasSize(0)))
                .andExpect(jsonPath("$.mitarbeiter", hasSize(1)));

        // Verify projekt wurde gelöscht
        assert projektRepository.findById(projekt.getId()).isEmpty();
    }
}
