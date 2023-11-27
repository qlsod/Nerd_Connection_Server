package pallet_spring.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@ControllerAdvice
public class ExceptionResponseHandler {

    // 예외 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> customRuntimeException(RuntimeException e, HttpServletResponse response) {
        response.setStatus(response.SC_BAD_REQUEST);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", e.getMessage());

        return body;
    }

    // valid 불충
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> validationException(MethodArgumentNotValidException e, HttpServletResponse response) {

        response.setStatus(response.SC_BAD_REQUEST);
        BindingResult bindingResult = e.getBindingResult();

        final Map<String, Object> body = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            body.put("status", response.SC_BAD_REQUEST);
            body.put("field", error.getField());
            body.put("message", error.getDefaultMessage());
        }
        return body;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception e) {
        return "An unexpected error occurred!";
    }

}