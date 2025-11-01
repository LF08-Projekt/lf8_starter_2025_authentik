package de.szut.lf8_projekt.projekt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO, welches die Daten eines Projekts nach einem Update zurückgibt")
public class ProjektUpdateConfirmationDto {
    @Schema(description = "ProjektID", example = "1")
    private Long id;
    @Schema(description = "Projektbezeichnung", example = "LF08_Projekt")
    private String bezeichnung;
    @Schema(description = "Die Mitarbeiter-ID des für das Projekt verantwortliche Mitarbeiters", example = "1")
    private Long verantwortlicherId;
    @Schema(description = "Die Kunden-ID des Kunden des Projekts", example = "12345")
    private Long kundenId;
    @Schema(description = "Der Name der Ansprechperson beim Kunden", example = "Max Mustermann")
    private String kundeAnsprechperson;
    @Schema(description = "Eine kurze Beschreibung des Projektziels", example = "Erschaffung eines einfaches Projektmanagementsystem")
    private String projektzielKommentar;
    @Schema(description = "Startdatum (ISO-8601)", example = "2025-10-15T00:00:00Z")
    private Date startdatum;
    @Schema(description = "Geplantes Enddatum (ISO-8601)", example = "2026-01-31T23:59:59Z")
    private Date geplantesEnddatum;
    @Schema(description = "Tatsächliche Enddatum (ISO-8601)", example = "2026-02-15T23:59:59Z")
    private Date wirklichesEnddatum;
    @Schema(description = "Liste der für das Projekt geplanten Qualifikationen", example = "[\"Java\",\"Angular\"]")
    private List<String> geplanteQualifikationen;
}
