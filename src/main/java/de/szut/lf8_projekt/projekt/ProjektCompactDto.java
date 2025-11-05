package de.szut.lf8_projekt.projekt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProjektCompactDto {
    private Long id;
    private String bezeichnung;
    private Long verantwortlicherId;
    private Long kundenId;
}
