package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto;

import de.szut.lf8_projekt.mitarbeiter.SkillDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<SkillDto> skillSet;

    public String getVollstaendigerName() {
        String v = (vorname != null && !vorname.trim().isEmpty()) ? vorname : "Unbekannt";
        String n = (nachname != null && !nachname.trim().isEmpty()) ? nachname : "";
        return (v + " " + n).trim();
    }
}
