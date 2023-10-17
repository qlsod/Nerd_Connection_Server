package hello.hellspring.model;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    // 유효성 검사
    @NotBlank
    private String id;

    @NotBlank
    private String pw;
}
