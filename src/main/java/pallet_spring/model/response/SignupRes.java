package pallet_spring.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SignupRes {

    private int no;

    // 비어 있지 않은 문자열만 허용
    @NotBlank
    @Email
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

}
