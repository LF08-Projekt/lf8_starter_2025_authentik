package de.szut.lf8_projekt.projekt.dto;

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
public class ProjektGetDto {
    private Long projektId;
    private String bezeichnung;
    private String kommentar;
    private Long kundenId;
    private String kundenAnsprechpartnerName;
    private LocalDateTime startdatum;
    private LocalDateTime geplantesEnddatum;
    private LocalDateTime tatsaechlichesEnddatum;
    private List<String> geplanteQualifikationen;
    private List<String> fehlendeQualifikationen;
}
