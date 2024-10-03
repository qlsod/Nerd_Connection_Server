package pallet_spring.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class PostLikeDto {

    @NotBlank
    private String photo_url;

    private int like_count;
}
