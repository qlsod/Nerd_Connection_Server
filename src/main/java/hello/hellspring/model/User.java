package hello.hellspring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@Data
public class User {
    private int no;
    private String id;
    private String name;
    private String pw;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",
//            timezone = "Asia/Seoul")
    private Date create_date;
//
//
//    //  한국 시간에 맞춰 get할 때 사용
////    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",
////            timezone = "Asia/Seoul")
    private Date update_date;
//
    private Date delete_date;
}
