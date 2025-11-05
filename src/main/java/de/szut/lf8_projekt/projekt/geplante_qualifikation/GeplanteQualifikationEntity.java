package de.szut.lf8_projekt.projekt.geplante_qualifikation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA-Entity für geplante Qualifikationen in Projekten.
 * Repräsentiert eine Qualifikation, die für ein Projekt benötigt wird.
 * Die Qualifikationsdaten werden von der externen Employee-API bezogen.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="geplante_qualifikation")
public class GeplanteQualifikationEntity {
    /** Eindeutige ID der geplanten Qualifikation */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID des zugehörigen Projekts */
    private Long projektId;

    /** ID der Qualifikation (Referenz zur externen Employee-API) */
    private Long qualifikationId;

    /** Bezeichnung der Qualifikation */
    private String qualifikation;
}
