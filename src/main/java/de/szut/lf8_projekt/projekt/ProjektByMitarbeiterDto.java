package de.szut.lf8_projekt.projekt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProjektByMitarbeiterDto {
    private Long id;
    private String bezeichnung;
    private Long verantwortlicherId;
    private Long kundenId;
}
