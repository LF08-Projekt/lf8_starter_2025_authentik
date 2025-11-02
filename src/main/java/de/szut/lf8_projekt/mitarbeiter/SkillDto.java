package de.szut.lf8_projekt.mitarbeiter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
public class SkillDto {

    @NotNull
    private Long id;

    @NotBlank
    private String skill;

}
