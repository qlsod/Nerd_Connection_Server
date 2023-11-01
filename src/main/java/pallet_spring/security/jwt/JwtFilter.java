package pallet_spring.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import pallet_spring.DTO.User;
import pallet_spring.service.UserService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// lombok 사용하여 생성자 주입(final 붙은 필드)
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        try {
            final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

            log.info("authorization:{}", authorization);

            // 토큰 안보낼 시 권한 X
            if (authorization == null) {
                log.error("authorization 이 없습니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 꺼내기
            String token = authorization.split(BEARER_PREFIX)[1];
            log.info("token: {}", token);

            // userId 꺼내서 확인
            String userId = JwtService.getUserId(token, secretKey);
            log.info("userId:{}", userId);


            log.info(userService.toString());

            User user = userService.checkUser(userId);
            log.info("user:{}", user);
            if (user == null) {
                log.info("user가 널일 경우");
                // 해당 ID가 DB에 없을 경우
                throw new JwtException("해당 ID가 DB에 없음");
            } else {
                log.info("user가 널이 아닐 경우");


//        // 나중에 관리자 권한 생성 시 할 예정
//        String userRole = user.getRole();
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("User")));
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            }


            // 제공하는 오류 수정하기
        } catch (SecurityException e) {
            setErrorResponse(request, response, "JWT 검증 중에 보안 예외 발생");
        } catch (MalformedJwtException e) {
            setErrorResponse(request, response, "유효하지 않은 JWT 형식");
        } catch (ExpiredJwtException e) {
            setErrorResponse(request, response, "JWT 기한 만료");
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
}
