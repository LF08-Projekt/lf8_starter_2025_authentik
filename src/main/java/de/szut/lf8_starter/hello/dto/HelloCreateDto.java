package de.szut.lf8_starter.hello.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class HelloCreateDto {

    @Size(min = 3, message = "at least length of 3")
    private String message;
}
