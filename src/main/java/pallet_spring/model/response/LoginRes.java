package pallet_spring.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "/user/login API 응답")
public class LoginRes {

    @Schema(description = "사용자 id", example = "sdlafjdsklfj")
    String accessToken;

}


