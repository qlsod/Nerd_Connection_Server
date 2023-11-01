package pallet_spring.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access}")
    private Long accessTokenExpiredMs;
    @Value("${jwt.refresh}")
    private Long refreshTokenExpiredMs;

    public String createAccessJwt(String userId) {

        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // user ID 꺼내기
    public static String getUserId(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userId", String.class);
    }

    public String createRefreshToken(){
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
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




}
