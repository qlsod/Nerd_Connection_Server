package pallet_spring.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "/posts/calendar/{targetTime} API 응답")
public class PostTimeRes {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",
            timezone = "Asia/Seoul")
    private Date update_date;

}
