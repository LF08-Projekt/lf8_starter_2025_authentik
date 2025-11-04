package de.szut.lf8_projekt.projekt;


import de.szut.lf8_projekt.Application;
import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterApiService;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungEntity;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungRepository;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto.MitarbeiterDto;
import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class ReadAllProjektsIT extends AbstractIntegrationTest {

    @MockBean
    private MitarbeiterApiService mitarbeiterApiService;
    @Autowired
    private MitarbeiterZuordnungRepository mitarbeiterZuordnungRepository;
    public ProjektEntity setUpDefaultProjektEntity(String name){
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt " + name);
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(new Date());
        projekt.setGeplantesEnddatum(new Date(2026, Calendar.DECEMBER,23));
        projekt = projektRepository.save(projekt);
        GeplanteQualifikationEntity geplanteQualifikation = new GeplanteQualifikationEntity();
        geplanteQualifikation.setQualifikationId(1L);
        geplanteQualifikation.setQualifikation("Java");
        geplanteQualifikation.setProjektId(projekt.getId());
        geplanteQualifikationRepository.save(geplanteQualifikation);
        return projekt;
    }

    @Test
    void authorization() throws Exception {
        // Ohne JWT sollte Zugriff verweigert werden
        this.mockMvc.perform(get("/LF08Projekt/Mitarbeiter/1/Projekt")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void happyPath() throws Exception {
        ProjektEntity projekt1 = setUpDefaultProjektEntity("1");
        ProjektEntity projekt2 = setUpDefaultProjektEntity("2");

        this.mockMvc.perform(get("/LF08Projekt/Projekt")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].bezeichnung", is("Testprojekt 1")))
                .andExpect(jsonPath("$[0].verantwortlicherId", is(1)))
                .andExpect(jsonPath("$[0].kundenId", is(123)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].bezeichnung", is("Testprojekt 2")))
                .andExpect(jsonPath("$[1].verantwortlicherId", is(1)))
                .andExpect(jsonPath("$[1].kundenId", is(123)));
    }

    @Test
    @WithMockUser(roles = "user")
    void noProjektsToReturn() throws Exception {

        this.mockMvc.perform(get("/LF08Projekt/Projekt")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }


}
