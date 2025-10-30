package de.szut.lf8_projekt.projekt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProjektCreateDto {
    @NotBlank(message = "Can't be empty")
    private String bezeichnung;

    @NotNull(message = "Can't be null")
    private Long verantwortlicherId;

    @NotNull(message = "Can't be null")
    private Long kundenId;

    @NotBlank(message = "Can't be empty")
    private String kundeAnsprechperson;

    @NotBlank(message = "Can't be empty")
    private String projektzielKommentar;

    @NotNull(message = "Can't be null")
    private Date startdatum;

    @NotNull(message = "Can't be null")
    private Date geplantesEnddatum;

    private String[] geplanteQualifikationen;
}
