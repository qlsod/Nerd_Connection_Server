package pallet_spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pallet_spring.mapper.UserMapper;
import pallet_spring.model.Jwt;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.UserService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/jwt")
@Tag(name = "Jwt", description = "refreshToken 사용 관련 api")
public class JwtController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access}")
    private Long accessTokenExpiredMs;
    @Value("${jwt.refresh}")
    private Long refreshTokenExpiredMs;

    @PostMapping("/refresh")
    @Operation(summary = "accessToken 재발급",
            description = "쿠키에 있는 refreshToken 꺼내서 검증 후 새로운 accessToken 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @SecurityRequirement(name = "accessToken")
    public Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String, Object> token = new HashMap<>();

        // Cookie에서 refreshToken 꺼내기
        String refreshToken = jwtProvider.getRefreshTokenFromCookie(request);

        // 토큰 유효성 검사
        jwtProvider.validateToken(refreshToken, request, response);

        // refreshToken에서 userId 꺼내기
        String userId = jwtProvider.getUserIdInJwt(refreshToken);

        log.info("userId:{}", userId);
        // redis에서 refreshToken 꺼내기
        String refreshTokenFromRedis = jwtProvider.findRefreshTokenFromRedis(userId);

        log.info("여기까지 와야됨:{}", refreshTokenFromRedis);

        // refreshToken 비교
        if (refreshToken.equals(refreshTokenFromRedis)) {

            // userId 이용해 새로운 AccessToken, RefreshToken(redis 저장) 생성
            Jwt jwtDTO = jwtProvider.createJwtLogic(userId);

            String newAccessToken = jwtDTO.getAccessToken();
            String newRefreshToken = jwtDTO.getRefreshToken();

            // AccessToken -> body에 담아 반환
            token.put("accessToken", newAccessToken);

            // RefreshToken -> cookie 에 담아 반환
            Cookie cookie = jwtProvider.createCookie(newRefreshToken);
            response.addCookie(cookie);

            token.put("accessToken", newAccessToken);
        }

        return token;
    }
}
