package pallet_spring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class User {

    private int no;

    // 비어 있지 않은 문자열만 허용
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul")
    private Date create_date;

//  한국 시간에 맞춰 get할 때 사용
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul")
    private Date update_date;

    private Date delete_date;
}
