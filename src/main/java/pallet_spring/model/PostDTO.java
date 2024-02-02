package pallet_spring.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class PostDTO {

    private String content;

    @NotBlank
    private String photo_url;

    private boolean share_check;



}
