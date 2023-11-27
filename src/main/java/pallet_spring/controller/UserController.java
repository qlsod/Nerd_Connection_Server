package pallet_spring.controller;

import pallet_spring.mapper.UserMapper;
import pallet_spring.model.Jwt;
import pallet_spring.model.Login;
import pallet_spring.model.User;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
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
    public Map<String, Object> login(@RequestBody @Valid Login login, HttpServletResponse response) {

        log.info("여기 요청 옴");

        // 입력된 ID, PW 일치 여부 검사
        userService.login(login);

        // userId 이용한 AccessToken, RefreshToken(redis 저장) 생성
        String userId = login.getId();
        Jwt jwtDTO = jwtProvider.createJwtLogic(userId);

        String accessToken = jwtDTO.getAccessToken();
        String refreshToken = jwtDTO.getRefreshToken();

        // AccessToken -> body에 담아 반환
        Map<String, Object> token = new HashMap<>();
        token.put("accessToken", accessToken);

        // RefreshToken -> cookie 에 담아 반환
        Cookie cookie = jwtProvider.createCookie(refreshToken);
        response.addCookie(cookie);

        return token;

    }

    @PostMapping("/logout")


    @GetMapping("")
    public List<User> getUserList() {
        return userService.findUserList();
    }

    // Data 생성 시 POST
    // 전처리 위한 @Vaild 이용
    @PostMapping("/signup")
    public String signup(@RequestBody @Valid User user) {
        userService.signUp(user);
        return "회원가입 성공";
    }
}
