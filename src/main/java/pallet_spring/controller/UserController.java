package pallet_spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pallet_spring.mapper.PostMapper;
import pallet_spring.mapper.UserMapper;
import pallet_spring.model.*;
import pallet_spring.model.response.LoginRes;
import pallet_spring.model.response.MailRes;
import pallet_spring.model.response.MyPageRes;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.UserService;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "User", description = "로그인 및 회원가입 관련 API")
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private PostMapper postMapper;

    // Data 조회 시 Get
    @GetMapping("/mypage")
    @Operation(summary = "마이페이지",
            description = "사용자 정보, 내가 쓴 글 표시")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<MyPageRes> getUserDetail(HttpServletRequest request) {

        // 토큰에 저장된 유저 ID 꺼내는 로직
        String userId = jwtProvider.getUserIdLogic(request);

        try {
            int userNo = userMapper.getUserNo(userId);

            MyProfileDTO myProfile = userMapper.getMyProfile(userNo);

            List<Image> image = postMapper.getMyPosts(userNo);
            MyPageRes myPageRes = new MyPageRes();
            myPageRes.myProfileToResDto(myProfile);
            myPageRes.setMyPost(image);

            return ResponseEntity.status(HttpStatus.OK).body(myPageRes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "로그인",
            description = "가입된 유저인지 확인하고 AccessToken, RefreshToken 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공 + Cookie로 RefreshToken 담아줌(신경 X)"),
            @ApiResponse(responseCode = "400", description = "실패")
//                    content = {
//                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MemberRes.class)))
//                    })
    })
    public ResponseEntity<LoginRes> login(@RequestBody @Valid Login login, HttpServletResponse response) {

        // 입력된 ID, PW 일치 여부 검사
        userService.login(login);

        // userId 이용한 AccessToken, RefreshToken(redis 저장) 생성
        String userId = login.getId();
        Jwt jwtDTO = jwtProvider.createJwtLogic(userId);

        String accessToken = jwtDTO.getAccessToken();
        String refreshToken = jwtDTO.getRefreshToken();

        // AccessToken -> body에 담아 반환
        LoginRes loginRes = new LoginRes();
        loginRes.setAccessToken(accessToken);

        // RefreshToken -> cookie 에 담아 반환
        Cookie cookie = jwtProvider.createCookie(refreshToken);
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(loginRes);

    }

    @GetMapping("")
    public List<User> getUserList() {
        return userService.findUserList();
    }

    // Data 생성 시 POST
    // 전처리 위한 @Vaild 이용

    @PostMapping("/signup")
    @Operation(summary = "회원가입",
            description = "가입된 유저인지 확인하고 최초 가입 시 DB 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    public ResponseEntity<Void> signup(@RequestBody @Valid SignUpDTO user) {
        userService.signUp(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃",
            description = "토큰 확인하여 Cookie에 저장된 refreshToken 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // 토큰에 저장된 유저 ID 꺼내는 로직
        String id = jwtProvider.getUserIdLogic(request);

        // 유저 존재 여부 확인
        userService.checkUserExist(id);

        // Redis에 저장된 RefreshToken 토큰 삭제
        jwtProvider.deleteRefreshToken(id);

        // Cookie에 저장된 RefreshToken 토큰 삭제
        userService.deleteCookie(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    // 비밀번호 확인
    @PostMapping("/check-password")
    @Operation(summary = "비밀번호 확인",
            description = "토큰 확인하여 계정 비밀번호 체크")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<Void> checkPassword(HttpServletRequest request, @Valid @RequestBody PasswordDto passwordDto) {
        // 토큰에 저장된 유저 ID 꺼내는 로직
        String id = jwtProvider.getUserIdLogic(request);

        String rawPassword = passwordDto.getPassword();
        String encodedPassword = userMapper.getPasswordById(id);

        userService.checkUserPW(rawPassword, encodedPassword);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 이름 수정

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 수정",
            description = "토큰 확인하여 계정 닉네임 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<NicknamePatchDto> patchNickname(HttpServletRequest request, @Valid @RequestBody NicknamePatchDto nicknamePatchDto) {
        // 토큰에 저장된 유저 ID 꺼내는 로직
        String id = jwtProvider.getUserIdLogic(request);

        // 유저 존재 여부 확인
        userService.checkUserExist(id);

        // 중보 닉네임 체크
        userService.checkNickName(nicknamePatchDto.getName());

        String newName = nicknamePatchDto.getName();

        NicknamePatchDto response = new NicknamePatchDto(newName);

        userMapper.updateNickname(id, newName);

        return ResponseEntity.ok(response);
    }



    // 비밀번호 변경
    @PatchMapping("/password-jwt")
    @Operation(summary = "비밀번호 수정",
            description = "토큰 이용한 계정 비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<Void> patchPwByJwt(HttpServletRequest request, @Valid @RequestBody PasswordDto passwordDto) {
        // 토큰에 저장된 유저 ID 꺼내는 로직
        String id = jwtProvider.getUserIdLogic(request);

        // 유저 존재 여부 확인
        userService.checkUserExist(id);

        Login login = new Login();
        login.setId(id);
        login.setPassword(passwordDto.getPassword());
        userService.updatePassword(login);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 비밀번호 변경(토큰 x)
    @PatchMapping("/password")
    @Operation(summary = "비밀번호 수정",
            description = "Email 모를 경우 해당 계정 비밀번호 변경 - 토큰 x")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public ResponseEntity<Void> patchPw(@Valid @RequestBody Login login) {
        // 유저 존재 여부 확인
        userService.checkUserExist(login.getId());

        userService.updatePassword(login);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 메일 보내기
    @PostMapping("/mail")
    @Operation(summary = "이메일 인증",
            description = "랜덤 6자리 난수 생성 후 이메일 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    public ResponseEntity<MailRes> sendMail(@Valid @RequestBody MailRequestDto mailRequestDto) throws MessagingException {

        MailRes mailRes = userService.sendMailConfirm(mailRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(mailRes);
    }




}
