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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class ProjektInformationenAbrufenIT extends AbstractIntegrationTest {

    @MockBean
    private MitarbeiterApiService mitarbeiterApiService;

    @Test
    void projektDetailsOhneAuthentifizierung() throws Exception {
        this.mockMvc.perform(get("/LF08Projekt/Projekt/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void projektMitarbeiterOhneAuthentifizierung() throws Exception {
        this.mockMvc.perform(get("/LF08Projekt/Projekt/1/Mitarbeiter"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void projektDetailsNichtGefunden() throws Exception {
        this.mockMvc.perform(get("/LF08Projekt/Projekt/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("existiert nicht")));
    }

    @Test
    @WithMockUser(roles = "user")
    void projektMitarbeiterNichtGefunden() throws Exception {
        this.mockMvc.perform(get("/LF08Projekt/Projekt/999/Mitarbeiter"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("existiert nicht")));
    }

    @Test
    @WithMockUser(roles = "user")
    void projektDetailsHappyPath() throws Exception {
        // Projekt erstellen
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Webshop Relaunch");
        projekt.setProjektzielKommentar("Modernisierung des Online-Shops");
        projekt.setKundenId(123L);
        projekt.setKundeAnsprechperson("Max Kunde");
        projekt.setVerantwortlicherId(1L);
        projekt.setStartdatum(LocalDateTime.of(2025, 1, 1, 0, 0));
        projekt.setGeplantesEnddatum(LocalDateTime.of(2025, 6, 30, 23, 59));
        projekt = projektRepository.save(projekt);

        // Geplante Qualifikationen
        GeplanteQualifikationEntity qual1 = new GeplanteQualifikationEntity();
        qual1.setProjektId(projekt.getId());
        qual1.setQualifikation("Java");
        geplanteQualifikationRepository.save(qual1);

        GeplanteQualifikationEntity qual2 = new GeplanteQualifikationEntity();
        qual2.setProjektId(projekt.getId());
        qual2.setQualifikation("React");
        geplanteQualifikationRepository.save(qual2);

        // Mitarbeiter zuordnen
        MitarbeiterZuordnungEntity zuordnung = new MitarbeiterZuordnungEntity();
        zuordnung.setProjektId(projekt.getId());
        zuordnung.setMitarbeiterId(42L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung);

        // Mock Mitarbeiter mit nur Java-Skills
        MitarbeiterDto mitarbeiter = new MitarbeiterDto();
        mitarbeiter.setId(42L);
        mitarbeiter.setVorname("Max");
        mitarbeiter.setNachname("Mustermann");
        SkillDto javaSkill = new SkillDto();
        javaSkill.setId(1L);
        javaSkill.setSkill("Java");
        mitarbeiter.setSkillSet(Arrays.asList(javaSkill));
        when(mitarbeiterApiService.getMitarbeiterById(42L)).thenReturn(mitarbeiter);

        // Test
        this.mockMvc.perform(get("/LF08Projekt/Projekt/" + projekt.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projektId", is(projekt.getId().intValue())))
                .andExpect(jsonPath("$.bezeichnung", is("Webshop Relaunch")))
                .andExpect(jsonPath("$.kommentar", is("Modernisierung des Online-Shops")))
                .andExpect(jsonPath("$.kundenId", is(123)))
                .andExpect(jsonPath("$.kundenAnsprechpartnerName", is("Max Kunde")))
                .andExpect(jsonPath("$.geplanteQualifikationen", hasSize(2)))
                .andExpect(jsonPath("$.geplanteQualifikationen", containsInAnyOrder("Java", "React")))
                .andExpect(jsonPath("$.fehlendeQualifikationen", hasSize(1)))
                .andExpect(jsonPath("$.fehlendeQualifikationen[0]", is("React")));
    }

    @Test
    @WithMockUser(roles = "user")
    void projektMitarbeiterHappyPath() throws Exception {
        // Projekt erstellen
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        // Zwei Mitarbeiter zuordnen
        MitarbeiterZuordnungEntity zuordnung1 = new MitarbeiterZuordnungEntity();
        zuordnung1.setProjektId(projekt.getId());
        zuordnung1.setMitarbeiterId(42L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung1);

        MitarbeiterZuordnungEntity zuordnung2 = new MitarbeiterZuordnungEntity();
        zuordnung2.setProjektId(projekt.getId());
        zuordnung2.setMitarbeiterId(43L);
        geplanteMitarbeiterZurdnungRepository.save(zuordnung2);

        // Mock Mitarbeiter 1
        MitarbeiterDto mitarbeiter1 = new MitarbeiterDto();
        mitarbeiter1.setId(42L);
        mitarbeiter1.setVorname("Max");
        mitarbeiter1.setNachname("Mustermann");
        SkillDto javaSkill = new SkillDto();
        javaSkill.setId(1L);
        javaSkill.setSkill("Java");
        mitarbeiter1.setSkillSet(Arrays.asList(javaSkill));
        when(mitarbeiterApiService.getMitarbeiterById(42L)).thenReturn(mitarbeiter1);

        // Mock Mitarbeiter 2
        MitarbeiterDto mitarbeiter2 = new MitarbeiterDto();
        mitarbeiter2.setId(43L);
        mitarbeiter2.setVorname("Anna");
        mitarbeiter2.setNachname("Schmidt");
        SkillDto reactSkill = new SkillDto();
        reactSkill.setId(2L);
        reactSkill.setSkill("React");
        mitarbeiter2.setSkillSet(Arrays.asList(reactSkill));
        when(mitarbeiterApiService.getMitarbeiterById(43L)).thenReturn(mitarbeiter2);

        // Test
        this.mockMvc.perform(get("/LF08Projekt/Projekt/" + projekt.getId() + "/Mitarbeiter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projektId", is(projekt.getId().intValue())))
                .andExpect(jsonPath("$.mitarbeiter", hasSize(2)))
                .andExpect(jsonPath("$.mitarbeiter[0].mitarbeiterId", is(42)))
                .andExpect(jsonPath("$.mitarbeiter[0].name", is("Max Mustermann")))
                .andExpect(jsonPath("$.mitarbeiter[0].qualifikationen", hasSize(1)))
                .andExpect(jsonPath("$.mitarbeiter[0].qualifikationen[0]", is("Java")))
                .andExpect(jsonPath("$.mitarbeiter[1].mitarbeiterId", is(43)))
                .andExpect(jsonPath("$.mitarbeiter[1].name", is("Anna Schmidt")))
                .andExpect(jsonPath("$.mitarbeiter[1].qualifikationen", hasSize(1)))
                .andExpect(jsonPath("$.mitarbeiter[1].qualifikationen[0]", is("React")));
    }

    @Test
    @WithMockUser(roles = "user")
    void projektOhneMitarbeiter() throws Exception {
        // Projekt ohne Mitarbeiter
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Leeres Projekt");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        this.mockMvc.perform(get("/LF08Projekt/Projekt/" + projekt.getId() + "/Mitarbeiter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projektId", is(projekt.getId().intValue())))
                .andExpect(jsonPath("$.mitarbeiter", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "user")
    void alleFehlendenQualifikationen() throws Exception {
        // Projekt mit geplanten Qualifikationen aber ohne Mitarbeiter
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Projekt ohne MA");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt = projektRepository.save(projekt);

        GeplanteQualifikationEntity qual1 = new GeplanteQualifikationEntity();
        qual1.setProjektId(projekt.getId());
        qual1.setQualifikation("Java");
        geplanteQualifikationRepository.save(qual1);

        GeplanteQualifikationEntity qual2 = new GeplanteQualifikationEntity();
        qual2.setProjektId(projekt.getId());
        qual2.setQualifikation("React");
        geplanteQualifikationRepository.save(qual2);

        this.mockMvc.perform(get("/LF08Projekt/Projekt/" + projekt.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.geplanteQualifikationen", hasSize(2)))
                .andExpect(jsonPath("$.fehlendeQualifikationen", hasSize(2)))
                .andExpect(jsonPath("$.fehlendeQualifikationen", containsInAnyOrder("Java", "React")));
    }
}
