package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import lombok.Data;

/**
 * DTO für die Zuordnung eines Mitarbeiters zu einem Projekt.
 */
@Data
public class MitarbeiterZuordnungDto {

    /** Eindeutige ID der Zuordnung */
    private Long id;

    /** ID des Projekts */
    private Long projektId;

    /** ID des Mitarbeiters */
    private Long mitarbeiterId;

    /** ID der Qualifikation */
    private Long qualifikationId;
}
