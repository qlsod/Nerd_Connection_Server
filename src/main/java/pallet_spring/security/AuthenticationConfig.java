package pallet_spring.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pallet_spring.security.jwt.JwtFilter;
import pallet_spring.security.jwt.JwtProvider;
import pallet_spring.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {
    private final UserService userService;
    private final JwtProvider jwtService;
    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public BCryptPasswordEncoder encoder() {
        // 비밀번호를 DB에 저장하기 전 사용할 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .httpBasic().disable() // 기본 HTTP 인증을 비활성화
                .csrf().disable() // csrf 보호 비활성화
                .cors()// CORS 보호를 활성화 (필요하다면 `.configurationSource()`를 사용하여 추가 구성 가능)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/users/signup", "/users/login").permitAll()// /users/signup 경로에 대한 POST 요청은 모두 허용(회원가입 용도)
                .antMatchers(HttpMethod.GET,"/posts/image/**").permitAll()// /users/signup 경로에 대한 POST 요청은 모두 허용
                .antMatchers(HttpMethod.POST, "/jwt/refresh").permitAll() // /users/refresh 경로에 대한 POST 요청 모두 허용(token 재발급 용도)
                .antMatchers(HttpMethod.GET,"/users/id/**").permitAll()// /users/id/ 모든 경로에 대한 GET 요청은 모두 허용(TEST 용도)
                .anyRequest().authenticated() // 다른 경로에 대한 요청 차단
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// jwt 활용 시 사용 코드
                .and()
                //jwtFilter 가 UsernamePasswordAuthenticationFilter 필터보다 앞에서 동작
                .addFilterBefore(new JwtFilter(userService, jwtService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}