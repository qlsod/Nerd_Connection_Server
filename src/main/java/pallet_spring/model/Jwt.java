package pallet_spring.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class Jwt {

    @NotBlank
    String accessToken;

    @NotBlank
    String refreshToken;
}
