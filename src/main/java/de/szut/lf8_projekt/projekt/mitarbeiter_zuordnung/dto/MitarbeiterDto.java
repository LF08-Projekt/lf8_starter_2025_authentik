package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto;

import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO, das einen Mitarbeiter aus der externen Employee-API abbildet.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MitarbeiterDto {
    /** Eindeutige Mitarbeiter-ID */
    private Long id;
    /** Vorname */
    private String vorname;
    /** Nachname */
    private String nachname;
    /** Straße */
    private String strasse;
    /** Postleitzahl */
    private String postleitzahl;
    /** Stadt */
    private String stadt;
    /** Telefonnummer */
    private String telefon;
    /** Liste der Qualifikationen */
    private List<SkillDto> skillSet;

    /**
     * Gibt den vollständigen Namen des Mitarbeiters zurück.
     * Falls Vorname null oder leer ist, wird "Unbekannt" verwendet.
     * Falls Nachname null oder leer ist, wird nur der Vorname zurückgegeben.
     *
     * @return Der vollständige Name im Format "Vorname Nachname" oder "Unbekannt" bei fehlenden Daten
     */
    public String getVollstaendigerName() {
        String v = (vorname != null && !vorname.trim().isEmpty()) ? vorname : "Unbekannt";
        String n = (nachname != null && !nachname.trim().isEmpty()) ? nachname : "";
        return (v + " " + n).trim();
    }
}
