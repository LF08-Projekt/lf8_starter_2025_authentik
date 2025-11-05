package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO für die Antwort beim Entfernen eines Mitarbeiters aus einem Projekt.
 * Enthält Basisinformationen zu Mitarbeiter und Projekt.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "DTO für die Bestätigung beim Entfernen eines Mitarbeiters aus einem Projekt")
public class MitarbeiterEntfernenResponseDto {
    @Schema(description = "ID des entfernten Mitarbeiters", example = "42")
    private Long mitarbeiterId;

    @Schema(description = "Name des entfernten Mitarbeiters", example = "Max Mustermann")
    private String mitarbeiterName;

    @Schema(description = "Projekt-ID, aus dem der Mitarbeiter entfernt wurde", example = "1")
    private Long projektId;

    @Schema(description = "Bezeichnung des Projekts", example = "LF08_Projekt")
    private String projektBezeichnung;
}
