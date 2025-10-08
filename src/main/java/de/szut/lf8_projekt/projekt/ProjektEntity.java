package de.szut.lf8_projekt.projekt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
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

    private String kundenId;

    private String projektzielKommentar;

    private Date startdatum;

    private Date geplantesEnddatum;

    private Date wirklichesEnddatum;
}
