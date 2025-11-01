package de.szut.lf8_projekt.mitarbeiter;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
public class SkillDto {

    @NotBlank
    private Long id;

    @NotBlank
    private String skill;

}
