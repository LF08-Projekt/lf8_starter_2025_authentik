package de.szut.lf8_projekt.mitarbeiter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO, das eine Qualifikation aus der externen Employee-API repräsentiert.
 */
@NoArgsConstructor
@Getter
@Setter
public class QualificationGetDto {
    /** Bezeichnung der Qualifikation (z. B. "Java") */
    private String skill;
}
