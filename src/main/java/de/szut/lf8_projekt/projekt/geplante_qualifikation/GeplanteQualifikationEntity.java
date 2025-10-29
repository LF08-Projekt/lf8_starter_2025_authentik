package de.szut.lf8_projekt.projekt.geplante_qualifikation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="geplante_qualifikation")
public class GeplanteQualifikationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projektId;

    private String qualifikation;
}
