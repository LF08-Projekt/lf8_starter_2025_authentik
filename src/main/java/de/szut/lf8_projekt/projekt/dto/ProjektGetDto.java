package de.szut.lf8_projekt.projekt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO für die Rückgabe von Projektinformationen.
 * Enthält alle relevanten Projektdaten inklusive geplanter und fehlender Qualifikationen.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "DTO für die Rückgabe von vollständigen Projektinformationen")
public class ProjektGetDto {
    @Schema(description = "Eindeutige ID des Projekts", example = "1")
    private Long projektId;

    @Schema(description = "Bezeichnung/Name des Projekts", example = "LF08_Projekt")
    private String bezeichnung;

    @Schema(description = "Kommentar zum Projektziel", example = "Erschaffung eines einfaches Projektmanagementsystem")
    private String kommentar;

    @Schema(description = "ID des Kunden", example = "12345")
    private Long kundenId;

    @Schema(description = "Name der Ansprechperson beim Kunden", example = "Max Mustermann")
    private String kundenAnsprechpartnerName;

    @Schema(description = "Startdatum des Projekts (ISO-8601)", example = "2025-10-15T00:00:00")
    private LocalDateTime startdatum;

    @Schema(description = "Geplantes Enddatum des Projekts (ISO-8601)", example = "2026-01-31T23:59:59")
    private LocalDateTime geplantesEnddatum;

    @Schema(description = "Tatsächliches Enddatum des Projekts (ISO-8601)", example = "2026-02-15T23:59:59", nullable = true)
    private LocalDateTime tatsaechlichesEnddatum;

    @Schema(description = "Liste der für das Projekt geplanten Qualifikationen", example = "[\"Java\", \"Angular\", \"Spring Boot\"]")
    private List<String> geplanteQualifikationen;

    @Schema(description = "Liste der noch fehlenden Qualifikationen", example = "[\"React\", \"Docker\"]")
    private List<String> fehlendeQualifikationen;
}
