package pallet_spring.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pallet_spring.DTO.User;
import pallet_spring.service.UserService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


// lombok 사용하여 생성자 주입(final 붙은 필드)
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;

    private final String secretKey;


    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization:{}", authorization);

        // 토큰 안보낼 시 권한 X
        if(authorization == null ) {
            log.error("authorization 이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 꺼내기
        String token = authorization.split("Bearer ")[1];

        log.info("token: {}", token);

        // 토큰 expired 되었는지 확인
        if(JwtUtil.isExpired(token, secretKey)) {
            log.info("Token이 만료되었습니다.");
            filterChain.doFilter(request, response);
        }

        // userId 꺼내서 확인
        String userId = JwtUtil.getUserId(token, secretKey);
        log.info("userId:{}", userId);


        log.info(userService.toString());
        // User 객체 null 뜨는 오류 발생
        User user = userService.checkUser(userId);
        log.info("user:{}", user);
        if (user == null ) {
            log.info("user가 널일 경우");
            filterChain.doFilter(request, response);
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


//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(userRole)));

    }
}
