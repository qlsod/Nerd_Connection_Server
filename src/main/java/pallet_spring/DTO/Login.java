package pallet_spring.DTO;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class Login {
    // 유효성 검사
    @NotBlank
    private String id;

    @NotBlank
    private String password;
}
