package de.szut.lf8_projekt.projekt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA-Entity für Projekte.
 * Repräsentiert ein Software-Projekt mit allen relevanten Informationen wie
 * Bezeichnung, Verantwortlicher, Kunde, Zeitraum und Projektziel.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projekt")
public class ProjektEntity {
    /** Eindeutige ID des Projekts */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Bezeichnung/Name des Projekts */
    private String bezeichnung;

    /** ID des verantwortlichen Mitarbeiters (Referenz zur externen Employee-API) */
    private Long verantwortlicherId;

    /** ID des Kunden (Referenz zur externen Employee-API) */
    private Long kundenId;

    /** Name der Ansprechperson beim Kunden */
    private String  kundeAnsprechperson;

    /** Kommentar zum Projektziel */
    private String projektzielKommentar;

    /** Startdatum des Projekts */
    private LocalDateTime startdatum;

    /** Geplantes Enddatum des Projekts */
    private LocalDateTime geplantesEnddatum;

    /** Tatsächliches Enddatum des Projekts (wenn abgeschlossen) */
    private LocalDateTime wirklichesEnddatum;
}
