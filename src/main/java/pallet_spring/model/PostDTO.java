package pallet_spring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class PostDTO {

    private String content;

    @NotBlank
    private String photo_url;

    private boolean share_check;

}
