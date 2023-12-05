package pallet_spring.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class Image {

    @NotBlank
    private String photo_url;

    private int post_no;
}
