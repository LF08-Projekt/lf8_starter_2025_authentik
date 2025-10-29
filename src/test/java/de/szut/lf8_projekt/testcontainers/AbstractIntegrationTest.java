package de.szut.lf8_projekt.testcontainers;

import  de.szut.lf8_projekt.projekt.ProjektRepository;
import de.szut.lf8_projekt.projekt.geplante_qualifikation.GeplanteQualifikationRepository;
import de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.MitarbeiterZuordnungRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
@ContextConfiguration(initializers = PostgresContextInitializer.class)
public class AbstractIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ProjektRepository projektRepository;

    @Autowired
    protected GeplanteQualifikationRepository geplanteQualifikationRepository;

    @Autowired
    protected MitarbeiterZuordnungRepository geplanteMitarbeiterZurdnungRepository;

    @BeforeEach
    void setUp() {
        projektRepository.deleteAll();
        geplanteQualifikationRepository.deleteAll();
        geplanteMitarbeiterZurdnungRepository.deleteAll();
    }
}
