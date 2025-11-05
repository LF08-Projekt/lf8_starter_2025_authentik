package de.szut.lf8_projekt.mitarbeiter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * DTO zur Übertragung einer Qualifikation eines Mitarbeiters.
 */
@Data
public class SkillDto {

    /** Eindeutige ID der Qualifikation */
    @NotNull
    private Long id;

    /** Bezeichnung der Qualifikation */
    @NotBlank
    private String skill;

}
