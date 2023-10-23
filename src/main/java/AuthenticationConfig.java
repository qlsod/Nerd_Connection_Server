import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthenticationConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable() // 기본 HTTP 인증을 비활성화
                .csrf().disable() // CSRF 보호를 비활성화
                .cors().and() // CORS 보호를 활성화 (필요하다면 `.configurationSource()`를 사용하여 추가 구성 가능)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/users/signup").permitAll() // /users/signup 경로에 대한 POST 요청은 모두 허용
                .and();

        return httpSecurity.build();
    }
}
