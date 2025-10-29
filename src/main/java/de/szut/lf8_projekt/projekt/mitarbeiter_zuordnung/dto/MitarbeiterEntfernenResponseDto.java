package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MitarbeiterEntfernenResponseDto {
    private Long mitarbeiterId;
    private String mitarbeiterName;
    private Long projektId;
    private String projektBezeichnung;
}
