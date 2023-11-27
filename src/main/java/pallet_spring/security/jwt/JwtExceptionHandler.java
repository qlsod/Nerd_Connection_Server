package pallet_spring.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String exception = String.valueOf(authException);
        setErrorResponse(request, response, exception);
    }

    public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, String message) throws IOException {


        res.setCharacterEncoding("utf-8");
        res.setStatus(res.SC_UNAUTHORIZED);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 401 반환

        final Map<String, Object> body = new HashMap<>();

        body.put("status", res.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        // message : 입력한 메시지 반환.
        body.put("message", message);
        body.put("path", req.getServletPath());

        objectMapper.writeValue(res.getWriter(), body);

    }
}
