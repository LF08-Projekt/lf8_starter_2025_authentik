package de.szut.lf8_starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"de.szut.lf8_starter", "de.szut.lf8_projekt"})
@EnableJpaRepositories(basePackages = {"de.szut.lf8_starter", "de.szut.lf8_projekt"})
@EntityScan(basePackages = {"de.szut.lf8_starter", "de.szut.lf8_projekt"})
public class Lf8StarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lf8StarterApplication.class, args);
    }

}
