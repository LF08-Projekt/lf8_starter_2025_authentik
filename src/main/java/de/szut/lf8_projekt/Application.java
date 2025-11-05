package de.szut.lf8_projekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Einstiegspunkt der Spring-Boot-Anwendung für das LF8-Projekt.
 * Konfiguriert Component-Scan, JPA-Repositories und Entity-Scan für den Namensraum
 * {@code de.szut.lf8_projekt}.
 */
@SpringBootApplication(scanBasePackages = {"de.szut.lf8_projekt"})
@EnableJpaRepositories(basePackages = {"de.szut.lf8_projekt"})
@EntityScan(basePackages = {"de.szut.lf8_projekt"})
public class Application {

    /**
     * Startet die Spring-Boot-Anwendung.
     *
     * @param args Programmargumente
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
