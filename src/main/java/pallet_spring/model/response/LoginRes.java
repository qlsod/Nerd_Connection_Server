package pallet_spring.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "/user/login API 응답")
public class LoginRes {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJpZDJAbmF2ZXIuY29tIiwiaWF0IjoxNzAyOTY5OTQxLCJleHAiOjE3MDI5NzU5NDF9.1szB4PqjUPRDg72mjdcE5GS5fZnks3008MUYiHTnASY")
    String accessToken;

}