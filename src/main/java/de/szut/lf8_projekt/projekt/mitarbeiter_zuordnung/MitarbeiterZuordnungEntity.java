package de.szut.lf8_projekt.projekt.mitarbeiter_zuordnung;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA-Entity für Zuordnungen von Mitarbeitern zu Projekten.
 * Repräsentiert die Zuweisung eines Mitarbeiters zu einem Projekt mit einer spezifischen Qualifikation.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "mitarbeiter_projektzuordnung")
public class MitarbeiterZuordnungEntity {
    /** Eindeutige ID der Zuordnung */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID des Projekts */
    private Long projektId;

    /** ID des zugeordneten Mitarbeiters (Referenz zur externen Employee-API) */
    private Long mitarbeiterId;

    /** ID der Qualifikation, mit der der Mitarbeiter im Projekt arbeitet */
    private Long qualifikationId;
}
