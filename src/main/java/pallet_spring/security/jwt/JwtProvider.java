package pallet_spring.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Date;

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
    public boolean validateRefreshToken(String refreshToken) {
        String userId = getUserId(refreshToken, secretKey);
        String refreshTokenFromRedis = findRefreshTokenFromRedis(userId);
        if (refreshTokenFromRedis != null) {
            return true;
        }
        return false;
    }
    public String findRefreshTokenFromRedis(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] requestCookies = request.getCookies();
        String refreshToken = null;
        if (requestCookies != null) {
            for (Cookie cookie : requestCookies) {
                refreshToken = cookie.getValue();
            }
        }
        return refreshToken;
    }

    // user ID 꺼내기
    public String getUserId(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userId", String.class);
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

    public String getAuthorization(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public String getAccessToken(String authorization) {
        String BEARER_PREFIX = "Bearer ";
        return authorization.split(BEARER_PREFIX)[1];
    }
}
