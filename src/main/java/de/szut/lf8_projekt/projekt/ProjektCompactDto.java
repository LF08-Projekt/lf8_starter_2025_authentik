package de.szut.lf8_projekt.projekt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Kompaktes DTO mit den wichtigsten Projektinformationen.
 * Enthält ID, Bezeichnung, Verantwortlicher und Kunde.
 */
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Kompaktes DTO mit den wichtigsten Projektinformationen für Listenansichten")
public class ProjektCompactDto {
    @Schema(description = "Eindeutige ID des Projekts", example = "1")
    private Long id;

    @Schema(description = "Bezeichnung/Name des Projekts", example = "LF08_Projekt")
    private String bezeichnung;

    @Schema(description = "ID des verantwortlichen Mitarbeiters", example = "42")
    private Long verantwortlicherId;

    @Schema(description = "ID des Kunden", example = "12345")
    private Long kundenId;
}
