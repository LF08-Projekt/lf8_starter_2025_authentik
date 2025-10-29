package de.szut.lf8_projekt.projekt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProjektCreateConfirmationDto {
    private Long id;
    private String bezeichnung;
    private Long verantwortlicherId;
    private Long kundenId;
    private String kundeAnsprechperson;
    private String projektzielKommentar;
    private Date startdatum;
    private Date geplantesEnddatum;
    private List<String> geplanteQualifikationen;
}
