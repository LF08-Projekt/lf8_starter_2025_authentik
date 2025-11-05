package de.szut.lf8_projekt.projekt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO für die Rückgabe aller Mitarbeiter eines Projekts.
 * Enthält die Projekt-ID und eine Liste aller zugeordneten Mitarbeiter.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "DTO für die Rückgabe aller Mitarbeiter eines Projekts")
public class ProjektMitarbeiterGetDto {
    @Schema(description = "Eindeutige ID des Projekts", example = "1")
    private Long projektId;

    @Schema(description = "Liste aller dem Projekt zugeordneten Mitarbeiter")
    private List<MitarbeiterImProjektDto> mitarbeiter;
}
