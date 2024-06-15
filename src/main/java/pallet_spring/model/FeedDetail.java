package pallet_spring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class FeedDetail {

    private String content;

    @NotBlank
    private String photo_url;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date create_date;

    private String name;
    private int like_count;
    boolean like;

}
