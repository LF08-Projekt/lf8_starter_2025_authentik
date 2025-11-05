package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto;

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
public class MitarbeiterEntfernenResponseDto {
    /** ID des entfernten Mitarbeiters */
    private Long mitarbeiterId;
    /** Name des entfernten Mitarbeiters */
    private String mitarbeiterName;
    /** Projekt-ID, aus dem entfernt wurde */
    private Long projektId;
    /** Bezeichnung des Projekts */
    private String projektBezeichnung;
}
