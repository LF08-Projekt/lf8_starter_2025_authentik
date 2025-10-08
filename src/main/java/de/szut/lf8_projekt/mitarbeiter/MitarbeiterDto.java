package de.szut.lf8_projekt.mitarbeiter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MitarbeiterDto {
    private Long id;
    private String lastName;
    private String firstName;
    private String street;
    private String postcode;
    private String city;
    private String phone;
    //ggf skillset ergänzen

    public String getFullName() {
        return firstName + " " + lastName;
    }
}