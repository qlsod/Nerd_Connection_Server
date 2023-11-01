package pallet_spring.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pallet_spring.security.jwt.JwtFilter;
import pallet_spring.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {
    private final UserService userService;
    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .httpBasic().disable() // 기본 HTTP 인증을 비활성화
                .csrf().disable() // csrf 보호 비활성화
                .cors() // CORS 보호를 활성화 (필요하다면 `.configurationSource()`를 사용하여 추가 구성 가능)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/users/signup", "/users/login", "/users/id/**").permitAll()// /users/signup 경로에 대한 POST 요청은 모두 허용
                .antMatchers("/**").authenticated() // 다른 경로에 대한 요청 차단
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// jwt 활용 시 사용 코드
                .and()
                //jwtFilter 가 UsernamePasswordAuthenticationFilter 필터보다 앞에서 동작
                .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}


