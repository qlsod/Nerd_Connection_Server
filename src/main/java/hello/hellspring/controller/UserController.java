package hello.hellspring.controller;

import hello.hellspring.mapper.UserMapper;
import hello.hellspring.model.LoginDTO;
import hello.hellspring.model.User;
import hello.hellspring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    // Data 조회 시 Get
    @GetMapping("/no/{no}")
    public User getUser(@PathVariable("no") int no) {
        return userMapper.getUserProfile(no);
    }

    @PostMapping("/login")
    public String userLogin(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            log.error("유효성 검사 오류 발생");
            Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            throw new RuntimeException("NotBlank 조건 충족 못함");
        }else {

            boolean loginResult = userService.login(loginDTO);
            log.info(String.valueOf(loginDTO));
            if (loginResult) {
                session.setAttribute("loginId", loginDTO.getId());
                return "성공";
            } else {
                throw new RuntimeException("가입된 계정이 없습니다.");
            }
        }

    }

    @GetMapping("/all")
    public List<User> getUserList() {
        return userService.findUserList();
    }


    // Data 생성 시 POST
    // 전처리 위한 @Vaild 이용
    @PostMapping("/signUp")
    public User postUsers(@RequestBody @Valid User user, BindingResult bindingResult) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();  // 암호 강도 Default 10
        String result = encoder.encode("pw");

        // 전처리(유효성) 검사 불통과 할 시
        if (bindingResult.hasErrors()) {
            log.error("hasError 발생");
            Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            throw new RuntimeException("NotBlank 충족 못함");
        } else {
            userService.signUp(user);
            return user;
        }

    }

//    // Data 수정 시 PUT(잘 안씀)
//    // Data 수정 시 Patch
//    @PutMapping("/user/{id}")
//    public void putUserProfile(@PathVariable("id") String id, @RequestParam("name") String name, @RequestParam("pw") String pw) {
//        userMapper.updateUserProfile(id, name, pw);
//        // DB 연결 안된 경우
//        // UserProfile userProfile = userMap.get(id);
//        // userProfile.setName(name);
//        // userProfile.setPw(pw);
//    }
//
//    // Data 삭제 시 Del
//    @DeleteMapping("/user/{id}")
//    public void deleteUserProfile(@PathVariable("id") String id) {
//        userMapper.deleteProfile(id);
//        // DB 연결 안된 경우
//        // userMap.remove(id);
//
//
//        // create, update, delete
//
//    }
}
