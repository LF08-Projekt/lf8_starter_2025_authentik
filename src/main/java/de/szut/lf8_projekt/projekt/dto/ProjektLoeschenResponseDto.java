package de.szut.lf8_projekt.projekt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO für die Rückgabe beim Löschen eines Projekts.
 * Enthält alle Projektinformationen des gelöschten Projekts.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjektLoeschenResponseDto {
    private Long projektId;
    private String bezeichnung;
    private Long verantwortlicherMitarbeiterId;
    private Long kundenId;
    private String kundenAnsprechpartnerName;
    private String kommentar;
    private LocalDateTime startdatum;
    private LocalDateTime geplantesEnddatum;
    private LocalDateTime tatsaechlichesEnddatum;
    private List<MitarbeiterImProjektDto> mitarbeiter;
    private List<String> benoetigteQualifikationen;
}
