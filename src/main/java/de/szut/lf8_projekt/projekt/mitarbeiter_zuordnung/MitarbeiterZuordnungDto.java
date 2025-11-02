package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import lombok.Data;

@Data
public class MitarbeiterZuordnungDto {

    private Long id;

    private Long projektId;

    private Long mitarbeiterId;

    private Long qualifikationId;
}
