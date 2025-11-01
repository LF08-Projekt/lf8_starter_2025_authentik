package de.szut.lf8_projekt.projekt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Schema(description = "DTO zum Erstellen eines Projekts")
public class ProjektCreateDto {
    @NotBlank(message = "Can't be empty")
    @Schema(description = "Projektbezeichnung", example = "LF08_Projekt")
    private String bezeichnung;

    @NotNull(message = "Can't be null")
    @Schema(description = "Die Mitarbeiter-ID des für das Projekt verantwortliche Mitarbeiters", example = "1")
    private Long verantwortlicherId;

    @NotNull(message = "Can't be null")
    @Schema(description = "Die Kunden-ID des Kunden des Projekts", example = "12345")
    private Long kundenId;

    @NotBlank(message = "Can't be empty")
    @Schema(description = "Der Name der Ansprechperson beim Kunden", example = "Max Mustermann")
    private String kundeAnsprechperson;

    @NotBlank(message = "Can't be empty")
    @Schema(description = "Eine kurze Beschreibung des Projektziels", example = "Erschaffung eines einfaches Projektmanagementsystem")
    private String projektzielKommentar;

    @NotNull(message = "Can't be null")
    @Schema(description = "Startdatum (ISO-8601)", example = "2025-10-15T00:00:00Z")
    private Date startdatum;

    @NotNull(message = "Can't be null")
    @Schema(description = "Geplantes Enddatum (ISO-8601)", example = "2026-01-31T23:59:59Z")
    private Date geplantesEnddatum;

    @Schema(description = "Liste der für das Projekt geplanten Qualifikationen", example = "[\"Java\",\"Angular\"]")
    private String[] geplanteQualifikationen;
}
