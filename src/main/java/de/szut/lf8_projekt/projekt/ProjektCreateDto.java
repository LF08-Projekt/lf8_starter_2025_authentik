package de.szut.lf8_projekt.projekt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProjektCreateDto {
    @NotBlank(message = "Can't be empty")
    private String bezeichnung;

    @NotBlank(message = "Can't be empty")
    private Long verantwortlicherId;

    @NotBlank(message = "Can't be empty")
    private Long kundenId;

    @NotBlank(message = "Can't be empty")
    private String kundeAnsprechperson;

    @NotBlank(message = "Can't be empty")
    private String projektzielKommentar;

    @NotBlank(message = "Can't be empty")
    private Date startdatum;

    @NotBlank(message = "Can't be empty")
    private Date geplantesEnddatum;

    private String[] geplanteQualifikationen;
}
