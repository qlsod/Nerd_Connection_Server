package pallet_spring.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class Login {
    // 유효성 검사
    @NotBlank
    @Schema(description = "사용자 id", example = "id1@naver.com")
    private String id;

    @NotBlank
    @Schema(description = "사용자 password", example = "pw111")
    private String password;
}
