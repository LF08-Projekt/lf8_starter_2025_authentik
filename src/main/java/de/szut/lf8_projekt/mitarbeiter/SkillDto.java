package de.szut.lf8_projekt.mitarbeiter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO zur Übertragung einer Qualifikation eines Mitarbeiters.
 */
@Data
@Schema(description = "DTO zur Übertragung einer Qualifikation/Skill eines Mitarbeiters")
public class SkillDto {

    @Schema(description = "Eindeutige ID der Qualifikation", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long id;

    @Schema(description = "Bezeichnung der Qualifikation", example = "Java", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String skill;

}
