package de.szut.lf8_projekt.projekt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projekt")
public class ProjektEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bezeichnung;

    private Long verantwortlicherId;

    private Long kundenId;

    private String  kundeAnsprechperson;

    private String projektzielKommentar;

    private LocalDateTime startdatum;

    private LocalDateTime geplantesEnddatum;

    private LocalDateTime wirklichesEnddatum;
}
