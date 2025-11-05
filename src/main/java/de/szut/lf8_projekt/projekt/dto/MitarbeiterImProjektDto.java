package de.szut.lf8_projekt.projekt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO für einen Mitarbeiter innerhalb eines Projekts.
 * Enthält Mitarbeiter-ID, Namen und Qualifikationen.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "DTO für einen Mitarbeiter innerhalb eines Projekts")
public class MitarbeiterImProjektDto {
    @Schema(description = "Eindeutige ID des Mitarbeiters", example = "42")
    private Long mitarbeiterId;

    @Schema(description = "Name des Mitarbeiters", example = "Max Mustermann")
    private String name;

    @Schema(description = "Liste der Qualifikationen des Mitarbeiters im Projekt", example = "[\"Java\", \"Spring Boot\"]")
    private List<String> qualifikationen;
}
