package de.szut.lf8_projekt.projekt;

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
public class ProjektCompactDto {
    /** Eindeutige ID des Projekts */
    private Long id;
    /** Bezeichnung/Name des Projekts */
    private String bezeichnung;
    /** ID des verantwortlichen Mitarbeiters */
    private Long verantwortlicherId;
    /** ID des Kunden */
    private Long kundenId;
}
