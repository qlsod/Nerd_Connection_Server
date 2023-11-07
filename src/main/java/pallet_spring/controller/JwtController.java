package pallet_spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pallet_spring.mapper.UserMapper;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.UserService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/jwt")
public class JwtController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProvider jwtService;
    @Autowired
    private UserService userService;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access}")
    private Long accessTokenExpiredMs;
    @Value("${jwt.refresh}")
    private Long refreshTokenExpiredMs;


    @PostMapping("/refresh")
    public Map<String, Object> refreshToken(HttpServletResponse response, HttpServletRequest request) {

        log.info("/refresh 접속");
        Map<String, Object> token = new HashMap<>();

        // Cookie에서 refreshToken 꺼내기
        String refreshToken = jwtService.getRefreshTokenFromCookie(request);
        log.info("Cookie에서 refreshToken 꺼내기");
        if (refreshToken == null) {
            throw new RuntimeException("Cookie에 refreshToken이 없습니다");
        }


        // refreshToken에서 userId 꺼내기
        String userId = jwtService.getUserId(refreshToken, secretKey);
        log.info("refreshToken에서 userId 꺼내기");

        if (userId == null ) {
            throw new RuntimeException("refreshToken의 signature 값 오류");
        }

        // redis에서 refreshToken 꺼내기
        String refreshTokenFromRedis = jwtService.findRefreshTokenFromRedis(userId);

        // refreshToken 비교
        if (refreshToken.equals(refreshTokenFromRedis)) {
            // new AccessJWT 생성
            String newAccessToken = jwtService.createAccessToken(userId);
            String newRefreshToken = jwtService.createRefreshToken(userId);

            // 저장되어 있던 refreshToken redis에서 삭제
            jwtService.deleteRefreshToken(userId);

            // 새롭게 생성한 refreshToken redis에 저장
            jwtService.saveRefreshToken(userId, newRefreshToken);

            // Cookie로 보내기
            Cookie cookie = jwtService.createCookie(newRefreshToken);
            response.addCookie(cookie);

            token.put("newAccessToken", newAccessToken);
        }

        return token;
    }
}
