package de.szut.lf8_projekt.projekt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO für die Rückgabe aller Mitarbeiter eines Projekts.
 * Enthält die Projekt-ID und eine Liste aller zugeordneten Mitarbeiter.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjektMitarbeiterGetDto {
    private Long projektId;
    private List<MitarbeiterImProjektDto> mitarbeiter;
}
