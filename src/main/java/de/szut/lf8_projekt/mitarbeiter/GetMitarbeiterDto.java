package de.szut.lf8_projekt.mitarbeiter;


import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class GetMitarbeiterDto {

    private Long id;
    private String lastName;
    private String firstName;
    private List<SkillDto> Skillset;

}
