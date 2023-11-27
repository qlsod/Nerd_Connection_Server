package pallet_spring.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import pallet_spring.model.Jwt;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access}")
    private Long accessTokenExpiredMs;
    @Value("${jwt.refresh}")
    private Long refreshTokenExpiredMs;

    private final RedisTemplate<String, String> redisTemplate;

    public Jwt createJwtLogic(String userId) {
        Jwt jwtDTO = new Jwt();
        // jwt create 메소드
        String accessToken = createAccessToken(userId);
        String refreshToken = createRefreshToken(userId);

        // 생성한 RefreshToken redis에 저장
        saveRefreshToken(userId, refreshToken);

        jwtDTO.setAccessToken(accessToken);
        jwtDTO.setRefreshToken(refreshToken);

        return jwtDTO;
    }

    // AccessToken 생성
    public String createAccessToken(String userId) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // refreshToken 생성
    public String createRefreshToken(String userId){
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    // 생성된 refreshToken redis에 저장
    public void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(userId, refreshToken, Duration.ofDays(14));
        log.info("저장된 Token:{}", redisTemplate.opsForValue().get(userId));
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(userId);
    }



    // RefreshToken 검사하기
    public boolean validateRefreshToken(HttpServletRequest request) {
        return false;
    }


    // 해당 오류 400으로 표시되는 거 수정 필요
    public String findRefreshTokenFromRedis(String userId) {
        String refreshTokenFromRedis = redisTemplate.opsForValue().get(userId);

        if (refreshTokenFromRedis == null) {
            throw new RuntimeException("Redis에 저장되지 않은 토큰");
        }
        return refreshTokenFromRedis;
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] requestCookies = request.getCookies();

        String refreshToken = "";
        if (requestCookies == null) {
            throw new RuntimeException("Cookie가 존재하지 않습니다");
        } else {
            for (Cookie cookie : requestCookies) {
                refreshToken = cookie.getValue();
            }
        }
        return refreshToken;
    }

    // user ID 꺼내기
    public String getUserIdInJwt(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userId", String.class);
    }

    public void validateToken(String token, HttpServletRequest request, HttpServletResponse response) throws IOException {

        log.info("여기일껄");
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (SecurityException e) {
            setErrorResponse(request, response, "JWT 검증 중에 보안 예외 발생");
        } catch (MalformedJwtException e) {
            setErrorResponse(request, response, "유효하지 않은 JWT 형식");
        } catch (ExpiredJwtException e) {
            setErrorResponse(request, response, "만료된 JWT");
        } catch (UnsupportedJwtException e) {
            setErrorResponse(request, response, "지원되지 않는 JWT 형식");
        } catch (SignatureException e) {
            setErrorResponse(request, response, "잘못된 JWT Signature 형식");
        } catch (IllegalArgumentException e) {
            setErrorResponse(request, response, "JWT 안에 Id 문자열 없음");
        }
    }

    public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, String message) throws IOException {

        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final Map<String, Object> body = new HashMap<>();

        // 401 반환
        res.setStatus(res.SC_UNAUTHORIZED);
        body.put("status", res.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        // message : 입력한 메시지 반환.
        body.put("message", message);
        body.put("path", req.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(res.getOutputStream(), body);
    }

    public Cookie createCookie(String refreshToken){
        String cookieName = "refreshToken";
        Cookie cookie = new Cookie(cookieName, refreshToken);
        // Cookie 설정
        cookie.setHttpOnly(true);     // httpOnly 옵션 설정
        cookie.setSecure(true);       // https 옵션 설정
        cookie.setPath("/");          // 모든 곳에서 쿠키 열람 가능
        cookie.setMaxAge(60*60*24);   // 쿠키 만료시간 설정
        return cookie;
    }

    public String getUserIdLogic(HttpServletRequest request) {
        // Header에서 authorization 꺼내기
        String authorization = getAuthorization(request);
        // authorization 에서 AccessToken 꺼내기
        String token = getAccessToken(authorization);
        // UserId 꺼내기
        return getUserIdInJwt(token);
    }

    public String getAuthorization(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public String getAccessToken(String authorization) {
        String BEARER_PREFIX = "Bearer ";
        return authorization.split(BEARER_PREFIX)[1];
    }
}
