package de.szut.lf8_projekt.projekt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projekt_mitarbeiter")
public class ProjektMitarbeiterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projektId;

    private Long mitarbeiterId;
}
