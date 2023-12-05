package pallet_spring.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pallet_spring.mapper.PostMapper;
import pallet_spring.mapper.UserMapper;
import pallet_spring.model.Image;
import pallet_spring.model.Post;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.PostService;
import pallet_spring.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {


    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access}")
    private Long accessTokenExpiredMs;
    @Value("${jwt.refresh}")
    private Long refreshTokenExpiredMs;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;


    // 따로 뺄 수도 있음(현재는 post에서만 사용)
    @PostMapping("image")
    public String postImageUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        // 토큰에 저장된 유저 ID 꺼내는 로직
        String userId = jwtProvider.getUserIdLogic(request);

        // S3 upload
        return postService.uploadS3(file, userId);
    }

    @PostMapping("upload")
    public String postUpload(@RequestBody Post post, HttpServletRequest request, HttpServletResponse response) {

        String userId = jwtProvider.getUserIdLogic(request);

        postService.postUpload(post, userId);
        return "성공";
    }

    // 공유 가능 이미지 불러오기
    @GetMapping("image/all")
    public List<Image> returnAllImageURL() {
        return postMapper.getAll();
    }

    // 마지막 image의 no값 받아 다음 아이템 표시
    @GetMapping("image/{no}")
    public List<Image> returnNextImageURL(@PathVariable("no") int no) {
        return postMapper.getNextImage(no);
    }

}