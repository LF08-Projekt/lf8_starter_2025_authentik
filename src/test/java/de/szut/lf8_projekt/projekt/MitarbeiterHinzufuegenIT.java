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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class MitarbeiterHinzufuegenIT extends AbstractIntegrationTest {

    @MockBean
    private MitarbeiterApiService mitarbeiterApiService;
    @Autowired
    private MitarbeiterZuordnungRepository mitarbeiterZuordnungRepository;

    public ProjektEntity setUpDefaultProjektEntity(){
        ProjektEntity projekt = new ProjektEntity();
        projekt.setBezeichnung("Testprojekt");
        projekt.setVerantwortlicherId(1L);
        projekt.setKundenId(123L);
        projekt.setStartdatum(LocalDateTime.now());
        projekt.setGeplantesEnddatum(LocalDateTime.of(2026, 12, 23, 0, 0));
        projekt = projektRepository.save(projekt);
        GeplanteQualifikationEntity geplanteQualifikation = new GeplanteQualifikationEntity();
        geplanteQualifikation.setQualifikationId(1L);
        geplanteQualifikation.setProjektId(projekt.getId());
        geplanteQualifikationRepository.save(geplanteQualifikation);
        return projekt;
    }

    public SkillDto createSkillDto(Long id, String name) {
        SkillDto skill = new SkillDto();
        skill.setId(id);
        skill.setSkill(name);
        return skill;
    }

    public MitarbeiterDto setUpDefaultMitarbeiter() {
        MitarbeiterDto mitarbeiter = new MitarbeiterDto();
        mitarbeiter.setId(42L);
        mitarbeiter.setVorname("Max");
        mitarbeiter.setNachname("Mustermann");
        return mitarbeiter;
    }

    @Test
    void authorization() throws Exception {
        // Ohne JWT sollte Zugriff verweigert werden
        this.mockMvc.perform(post("/LF08Projekt/1/mitarbeiter/42")
                .content("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void happyPath() throws Exception {
        // Testdaten vorbereiten
        ProjektEntity projekt = setUpDefaultProjektEntity();

        // Mock für externe API
        SkillDto skill = createSkillDto(1L,"Java");
        List<SkillDto> skills = new ArrayList<>();
        skills.add(skill);
        MitarbeiterDto mitarbeiter = setUpDefaultMitarbeiter();
        mitarbeiter.setSkillSet(skills);
        when(mitarbeiterApiService.getMitarbeiterById(any(Long.class), nullable(String.class))).thenReturn(mitarbeiter);

        //map skillDto to json string
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonContent = mapper.writeValueAsString(skill);

        // Test durchführen
        this.mockMvc.perform(post("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/42")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mitarbeiterId", is(42)))
                .andExpect(jsonPath("$.mitarbeiterName", is("Max Mustermann")))
                .andExpect(jsonPath("$.projektId", is(projekt.getId().intValue())))
                .andExpect(jsonPath("$.projektName", is("Testprojekt")))
                .andExpect(jsonPath("$.qualifikation[0].skill", is("Java")))
                .andExpect(jsonPath("$.qualifikation[0].id", is(1)));

        assertThat(geplanteMitarbeiterZurdnungRepository.findByProjektIdAndMitarbeiterId(projekt.getId(), 42L))
                .isNotEmpty();
    }

    @Test
    @WithMockUser(roles = "user")
    void wrongSkillgiven() throws Exception {
        // Testdaten vorbereiten
        ProjektEntity projekt = setUpDefaultProjektEntity();

        SkillDto skill = createSkillDto(2L, "Ruby");

        List<SkillDto> skills = new ArrayList<>();
        skills.add(skill);
        MitarbeiterDto mitarbeiter = setUpDefaultMitarbeiter();
        mitarbeiter.setSkillSet(skills);
        mitarbeiter.setSkillSet(skills);
        when(mitarbeiterApiService.getMitarbeiterById(any(Long.class), nullable(String.class))).thenReturn(mitarbeiter);

        //map skillDto to json string
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonContent = mapper.writeValueAsString(skill);

        this.mockMvc.perform(post("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/42")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Die angegebene Qualifikation mit der Id 2 wird nicht im projekt benötigt.")));
    }

    @Test
    @WithMockUser(roles = "user")
    void mitarbeiterDoesntMeetRequirements() throws Exception {
        ProjektEntity projekt = setUpDefaultProjektEntity();
        MitarbeiterDto mitarbeiter = setUpDefaultMitarbeiter();
        when(mitarbeiterApiService.getMitarbeiterById(any(Long.class), nullable(String.class))).thenReturn(mitarbeiter);
        SkillDto mSkill = createSkillDto(2L,"Ruby");
        SkillDto skill = createSkillDto(1L, "Java");
        List<SkillDto> skills = new ArrayList<>();
        skills.add(mSkill);
        mitarbeiter.setSkillSet(skills);

        //map skillDto to json string
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonContent = mapper.writeValueAsString(skill);

        this.mockMvc.perform(post("/LF08Projekt/" + projekt.getId() + "/mitarbeiter/42")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Der Mitarbeiter mit der Id 42 besitzt die Qualifikation Java nicht.")));
    }

    @Test
    @WithMockUser(roles = "user")
    void mitarbeiterAlreadyInUse() throws Exception {
        ProjektEntity projekt = setUpDefaultProjektEntity();
        ProjektEntity projekt2 = setUpDefaultProjektEntity();
        MitarbeiterDto mitarbeiter = setUpDefaultMitarbeiter();

        SkillDto skill = createSkillDto(1L, "Java");
        List<SkillDto> skills = new ArrayList<>();
        skills.add(skill);
        mitarbeiter.setSkillSet(skills);
        when(mitarbeiterApiService.getMitarbeiterById(any(Long.class), nullable(String.class))).thenReturn(mitarbeiter);

        MitarbeiterZuordnungEntity mitarbeiterZuordnungEntity = new MitarbeiterZuordnungEntity();
        mitarbeiterZuordnungEntity.setMitarbeiterId(42L);
        mitarbeiterZuordnungEntity.setProjektId(projekt.getId());
        mitarbeiterZuordnungEntity.setQualifikationId(1L);
        mitarbeiterZuordnungRepository.save(mitarbeiterZuordnungEntity);

        //map skillDto to json string
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonContent = mapper.writeValueAsString(skill);

        this.mockMvc.perform(post("/LF08Projekt/" + projekt2.getId() + "/mitarbeiter/42")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Der Mitarbeiter ist bereits im Zeitraum des Projekts verplant.")));
    }

    @Test
    @WithMockUser(roles = "user")
    void ProjectDoesntExists() throws Exception {
        MitarbeiterDto mitarbeiter = setUpDefaultMitarbeiter();

        SkillDto skill = createSkillDto(1L, "Java");
        List<SkillDto> skills = new ArrayList<>();
        skills.add(skill);
        mitarbeiter.setSkillSet(skills);
        when(mitarbeiterApiService.getMitarbeiterById(any(Long.class), nullable(String.class))).thenReturn(mitarbeiter);

        //map skillDto to json string
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonContent = mapper.writeValueAsString(skill);

        this.mockMvc.perform(post("/LF08Projekt/" + 3 + "/mitarbeiter/42")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Projekt mit der ID 3 existiert nicht")));
    }

    @Test
    @WithMockUser(roles = "user")
    void MitarbeiterDoesntExists() throws Exception {
        ProjektEntity projektEntity = setUpDefaultProjektEntity();

        MitarbeiterDto mitarbeiter = setUpDefaultMitarbeiter();
        SkillDto skill = createSkillDto(1L, "Java");
        List<SkillDto> skills = new ArrayList<>();
        skills.add(skill);
        mitarbeiter.setSkillSet(skills);
        when(mitarbeiterApiService.getMitarbeiterById(any(Long.class), nullable(String.class))).thenReturn(null);

        //map skillDto to json string
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonContent = mapper.writeValueAsString(skill);

        this.mockMvc.perform(post("/LF08Projekt/" + projektEntity.getId() + "/mitarbeiter/42")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Der Mitarbeiter mit der Id 42 existiert nicht")));
    }

}
