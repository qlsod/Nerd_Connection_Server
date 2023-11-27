package pallet_spring.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pallet_spring.model.Post;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/post")
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

    @PostMapping("write")
    public String writePost(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        // 토큰에 저장된 유저 ID 꺼내는 로직
        String userId = jwtProvider.getUserIdLogic(request);

        // S3 upload
        postService.upload(file, userId);

        return "성공";
    }

}