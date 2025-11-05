package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO für die Zuordnung eines Mitarbeiters zu einem Projekt.
 */
@Data
@Schema(description = "DTO für die Zuordnung eines Mitarbeiters zu einem Projekt mit einer Qualifikation")
public class MitarbeiterZuordnungDto {

    @Schema(description = "Eindeutige ID der Zuordnung", example = "1")
    private Long id;

    @Schema(description = "ID des Projekts", example = "1")
    private Long projektId;

    @Schema(description = "ID des Mitarbeiters", example = "42")
    private Long mitarbeiterId;

    @Schema(description = "ID der Qualifikation", example = "5")
    private Long qualifikationId;
}
