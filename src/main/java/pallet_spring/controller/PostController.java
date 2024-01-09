package pallet_spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pallet_spring.mapper.PostMapper;
import pallet_spring.mapper.UserMapper;
import pallet_spring.model.Image;
import pallet_spring.model.MyImage;
import pallet_spring.model.Post;
import pallet_spring.model.response.ImageRes;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.PostService;
import pallet_spring.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "Post", description = "게시글 관련 api")
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
    @Operation(summary = "이미지 URL 반환",
            description = "이미지 S3 저장 후 URL 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<ImageRes> postImageUpload(
            @Parameter(description = "이미지 form-data 형식으로 담아 키값 이름 file로 설정하여 요청")
            @RequestParam(name = "file", required = false) MultipartFile file, HttpServletRequest request) {

        // 파일 존재 여부 확인
        postService.validateFileExists(file);

        // 토큰에 저장된 유저 ID 꺼내는 로직
        String userId = jwtProvider.getUserIdLogic(request);

        // S3 upload
        String photo_url = postService.uploadS3(file, userId);

        ImageRes imageRes = new ImageRes();
        imageRes.setPhoto_url(photo_url);

        return ResponseEntity.status(HttpStatus.CREATED).body(imageRes);
    }

    @PostMapping("")
    @Operation(summary = "다이어리 게시글 저장",
            description = "이미지 URL 받아 게시글과 함께 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<Void> postUpload(@RequestBody @Valid Post post, HttpServletRequest request) {

        // 토큰에 저장된 유저 ID 꺼내는 로직
        String userId = jwtProvider.getUserIdLogic(request);

        postService.postUpload(post, userId);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 공유 가능 이미지 불러오기
    @GetMapping("share-images/first")
    @Operation(summary = "공유 가능 이미지 불러오기 처음 시도하는 api",
            description = "공유 체크한 이미지 불러오기(페이징 처리로 인한 18개 제한)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    public List<Image> returnAllImageURL() {
        return postMapper.getAll();
    }

    // 마지막 image의 no값 받아 다음 아이템 표시
    @GetMapping("share-images/{no}")
    @Operation(summary = "공유가능 이미지 불러오기 두번째 부터",
            description = "첫번째 api에서 반환된 post_no 중 가장 마지막 post_no 입력받아 다음 이미지 표시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    public List<Image> returnNextImageURL(
            @Parameter(description = "마지막 post_no 값", example = "1")
            @PathVariable("no") int no) {

        return postMapper.getNextImage(no);
    }

    @GetMapping("myimages/{targetTime}")
    @Operation(summary = "달력에 표시할 이미지 불러오기",
            description = "해당 월의 작성된 이미지 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public List<MyImage> returnMyImageURL(
            @Parameter(description = "해당 연도, 월 입력", example = "2023-12")
            @PathVariable("targetTime") String targetTime,
            HttpServletRequest request) {

        // 토큰에 저장된 유저 ID 꺼내는 로직
        String id = jwtProvider.getUserIdLogic(request);

        // 해당 유저 PK 값 꺼내기
        int userNo = userMapper.getUserNo(id);

        // 해당 유저의 Image 받아옴
        return postMapper.getMyImage(userNo, targetTime);
    }

    // 해당글 조회
    @GetMapping("{post_no}")
    @Operation(summary = "달력에 표시된 이미지의 상세내용 불러오기",
            description = "해당 게시글의 상세내용 표시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    public Post getPostDetail(
            @Parameter(description = "해당 post_no 입력", example = "3")
            @PathVariable("post_no") int post_no) {
        Post post = postMapper.getPostDetail(post_no);
        if (post == null) {
            throw new RuntimeException("해당 글이 존재하지 않습니다.");
        }
        return post;
    }

    @PatchMapping("image/{post_no}")
    @Operation(summary = "S3 이미지 교체",
            description = "S3 저장된 이미지 URL 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<ImageRes> updatedUpload(
            @Parameter(description = "해당글 no 입력")
            @PathVariable("post_no") int post_no, HttpServletRequest request,
            @Parameter(description = "새로운 이미지 form-data 형식으로 담아 키값 이름 file로 설정하여 요청") MultipartFile file) {

        // 토큰에 저장된 유저 ID 꺼내는 로직
        String userId = jwtProvider.getUserIdLogic(request);

        Post postEntity = postMapper.getPostDetail(post_no);
        String photo_url = postEntity.getPhoto_url();

        log.info("post_no에 해당하는 url:{}", photo_url);

        String keyName = photo_url.substring(53);

        log.info("post_no에 해당하는 key:{}", keyName);
        postService.deleteS3(keyName);

        // S3 upload
        String newPhoto_url = postService.uploadS3(file, userId);

        ImageRes imageRes = new ImageRes();
        imageRes.setPhoto_url(newPhoto_url);

        return ResponseEntity.status(HttpStatus.CREATED).body(imageRes);

    }

    @PatchMapping("{post_no}")
    @Operation(summary = "다이어리 게시글 수정",
            description = "이미지 URL 받아 게시글과 함께 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<Void> patchUpload(@RequestBody @Valid Post post, HttpServletRequest request,
                                            @PathVariable("post_no") int post_no) {

        // 토큰에 저장된 유저 ID 꺼내는 로직
        String userId = jwtProvider.getUserIdLogic(request);

        post.setPost_no(post_no);
        postService.postUpdate(post, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }



}