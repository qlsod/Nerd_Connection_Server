package pallet_spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springdoc.core.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pallet_spring.mapper.UserMapper;
import pallet_spring.model.Jwt;
import pallet_spring.model.Login;
import pallet_spring.model.SignUpDTO;
import pallet_spring.model.User;
import pallet_spring.model.response.LoginRes;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    // Data 조회 시 Get
    @GetMapping("/id/{id}")
    public User getUser(@PathVariable("id") String id) {
        User user = userMapper.findUserDetail(id);
        if (user == null) {
            throw new RuntimeException("계정정보가 없습니다");
        } else {
            return user;
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

        log.info("여기 요청 옴");

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

        return new ResponseEntity<>(loginRes, HttpStatus.OK);

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
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // 토큰에 저장된 유저 ID 꺼내는 로직
        String id = jwtProvider.getUserIdLogic(request);

        // Redis에 저장된 RefreshToken 토큰 삭제
        jwtProvider.deleteRefreshToken(id);

        // Cookie에 저장된 RefreshToken 토큰 삭제
        userService.deleteCookie(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
