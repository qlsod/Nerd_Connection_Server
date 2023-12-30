package pallet_spring.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "/posts/upload/image API 응답")
public class ImageRes {

    @Schema(example = "https://nc-bucket123.s3.ap-northeast-2.amazonaws.com/pallet_post/id2%40naver.com/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202023-10-05%20204843.png")
    String photo_url;

}
