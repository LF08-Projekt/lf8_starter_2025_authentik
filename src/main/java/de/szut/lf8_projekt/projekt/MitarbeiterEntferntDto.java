package de.szut.lf8_projekt.projekt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MitarbeiterEntferntDto {
    private Long mitarbeiternummer;
    private String mitarbeitername;
    private Long projektId;
    private String projektbezeichnung;
}
