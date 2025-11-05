package de.szut.lf8_projekt.projekt;

import de.szut.lf8_projekt.Application;
import de.szut.lf8_projekt.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
public class MitarbeiterEntfernenMissingParamsIT extends AbstractIntegrationTest {

    @Test
    @WithMockUser(roles = "user")
    void testFehlendesProjektId() throws Exception {
        this.mockMvc.perform(delete("/LF08Projekt//mitarbeiter/123")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "user")
    void testFehlendesMitarbeiterId() throws Exception {
        this.mockMvc.perform(delete("/LF08Projekt/123/mitarbeiter/")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "user")
    void testBeideIdsFehlen() throws Exception {
        this.mockMvc.perform(delete("/LF08Projekt//mitarbeiter/")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "user")
    void testNurBasisPfad() throws Exception {
        this.mockMvc.perform(delete("/LF08Projekt/mitarbeiter")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
