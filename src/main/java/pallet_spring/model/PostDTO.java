package pallet_spring.model;

import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotBlank;

@Data
@ToString
public class PostDTO {

    private String content;

    @NotBlank
    private String photo_url;

    private boolean share_check;



}
