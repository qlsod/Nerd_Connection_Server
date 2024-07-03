package pallet_spring.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NicknamePatchDto {

    @NotBlank
    private String name;
}
