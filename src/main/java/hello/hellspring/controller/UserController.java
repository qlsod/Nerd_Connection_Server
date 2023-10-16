package hello.hellspring.controller;

import hello.hellspring.mapper.UserMapper;
import hello.hellspring.model.User;
import hello.hellspring.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/all")
    public List<User> getUserList() {
        return userService.findUserList();
    }

    // Data 생성 시 POST
    // 전처리 위한 @Vaild 이용
    @PostMapping("/signUp")
    public User postUsers(@RequestBody @Valid User user, BindingResult bindingResult) {

        // 전처리(유효성) 검사 불통과 할 시
        if (bindingResult.hasErrors()) {
            log.error("hasError 발생");
            Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            throw new RuntimeException("회원가입 유효성 검사 실패");
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
