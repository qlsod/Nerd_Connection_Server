package pallet_spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class NicknamePatchDto {

    @NotBlank
    private String name;
}
