package pallet_spring.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "/user/mypage API 응답")
public class MyPageRes {

    @NotBlank
    @Email
    private String id;

    @NotBlank
    private String name;

}
