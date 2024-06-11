package pallet_spring.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class MyProfileDTO {

    @NotBlank
    @Email
    private String id;

    @NotBlank
    private String name;

}
