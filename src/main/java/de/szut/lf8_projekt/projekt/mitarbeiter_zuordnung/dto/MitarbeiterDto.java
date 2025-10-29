package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MitarbeiterDto {
    private Long id;
    private String vorname;
    private String nachname;
    private String strasse;
    private String postleitzahl;
    private String stadt;
    private String telefon;

    public String getVollstaendigerName() {
        String v = (vorname != null && !vorname.trim().isEmpty()) ? vorname : "Unbekannt";
        String n = (nachname != null && !nachname.trim().isEmpty()) ? nachname : "";
        return (v + " " + n).trim();
    }
}
