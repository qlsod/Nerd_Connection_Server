package pallet_spring.model.response;

import lombok.Data;

@Data
public class MyImagesRes {

    private String content;

    private String photo_url;

    private int post_no;

    private int like_count;
    boolean like;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",
//            timezone = "Asia/Seoul")
//    private Date update_date;
}
