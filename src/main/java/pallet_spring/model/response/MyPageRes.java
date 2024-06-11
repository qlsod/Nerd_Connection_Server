package pallet_spring.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pallet_spring.model.Image;
import pallet_spring.model.MyProfileDTO;
import pallet_spring.model.MyProfileTotalCountDTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "/user/mypage API 응답")
public class MyPageRes {

    @NotBlank
    @Email
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private int total_like_count;

    @NotBlank
    private int total_post_count;

    @NotEmpty
    private List<String> photo_url = new ArrayList<>(); // 초기화 추가

    @NotEmpty
    private List<Integer> post_no = new ArrayList<>(); // 초기화 추가

    public void myProfileToResDto(MyProfileDTO myProfileDTO) {
        this.id = myProfileDTO.getId();
        this.name = myProfileDTO.getName();
    }

    public void myProfileTotalCountToResDto(MyProfileTotalCountDTO myProfileTotalCountDTO) {
        this.total_like_count = myProfileTotalCountDTO.getTotalLikeCount();
        this.total_post_count = myProfileTotalCountDTO.getTotalPostCount();
    }

    public void imageToResDto(List<Image> images) {
        for (Image image : images) {
            this.photo_url.add(image.getPhoto_url());
            this.post_no.add(image.getPost_no());
        }
    }

}
