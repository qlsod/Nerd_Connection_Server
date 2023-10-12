package hello.hellspring.controller;

import hello.hellspring.mapper.UserMapper;
import hello.hellspring.model.User;
import hello.hellspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        // DB 연결 안된 경우
        // return userMap.get(id);
    }

    @GetMapping("/all")
    public List<User> getUserList() {
        return userMapper.getAll();
        // DB 연결 안된 경우
        // return new ArrayList<UserProfile>(userMap.values());
    }

    // Data 생성 시 POST
    @PostMapping("/signUp")
    public User postUsers(@RequestBody User user) {
//        userService.currentDate();
//        userService.signUp(user);
        userMapper.insertUserProfile(user);
        return user;
        // insert문에 의한 return 결과는 입력된 데이터 개수 반환 (입력 성공 = 1, 실패 = 0)
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
