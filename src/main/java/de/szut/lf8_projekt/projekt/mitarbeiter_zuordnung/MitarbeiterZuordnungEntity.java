package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "mitarbeiter_projektzuordnung")
public class MitarbeiterZuordnungEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projektId;

    private Long mitarbeiterId;
}
