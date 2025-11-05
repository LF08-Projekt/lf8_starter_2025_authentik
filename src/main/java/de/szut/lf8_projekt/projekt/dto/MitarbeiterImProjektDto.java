package de.szut.lf8_projekt.projekt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO für einen Mitarbeiter innerhalb eines Projekts.
 * Enthält Mitarbeiter-ID, Namen und Qualifikationen.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MitarbeiterImProjektDto {
    private Long mitarbeiterId;
    private String name;
    private List<String> qualifikationen;
}
