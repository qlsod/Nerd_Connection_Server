package pallet_spring.model;

import lombok.Data;

@Data
public class MyImage {

    private String content;

    private String photo_url;

    private int post_no;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",
//            timezone = "Asia/Seoul")
//    private Date update_date;
}
