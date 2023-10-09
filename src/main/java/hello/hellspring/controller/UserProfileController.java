package hello.hellspring.controller;

import hello.hellspring.mapper.UserProfileMapper;
import hello.hellspring.model.UserProfile;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UserProfileController {

    private UserProfileMapper mapper;

    // UserProfileController 생성자를 Parameter를 UserProfileMapper mapper로 받겠다고 선언
    public UserProfileController(UserProfileMapper mapper) {
        this.mapper = mapper;
        // 전달받은 mapper를 내부 mapper에 저장
    }

//    DB 연결 안된 경우
//    private Map<String, UserProfile> userMap;
//    @PostConstruct
//    public void init() {
//        userMap = new HashMap<String, UserProfile>();
//        userMap.put("1", new UserProfile("1", "홍길동", "pw1"));
//        userMap.put("2", new UserProfile("2", "김세민", "pw2"));
//        userMap.put("3", new UserProfile("3", "김현수", "pw3"));
//
//    }

    // Data 조회 시 Get
    @GetMapping("/user/{id}")
    public UserProfile getUserProfile(@PathVariable("id") String id) {
        return mapper.getUserProfile(id);
        // DB 연결 안된 경우
        // return userMap.get(id);
    }

    @GetMapping("/user/all")
    public List<UserProfile> getUserProfileList() {
        return mapper.getUserProfileList();
        // DB 연결 안된 경우
        // return new ArrayList<UserProfile>(userMap.values());
    }

    // Data 생성 시 POST
    @PostMapping("/user/{id}")
    public void postUserProfile(@PathVariable("id") String id,
                                @RequestParam("name") String name,
                                @RequestParam("pw") String pw,
                                @RequestParam("photoURL") String photoURL) {
        mapper.insertUserProfile(id, name, pw, photoURL);

    }

    // Data 수정 시 PUT(잘 안씀)
    // Data 수정 시 Patch
    @PutMapping("/user/{id}")
    public void putUserProfile(@PathVariable("id") String id, @RequestParam("name") String name, @RequestParam("pw") String pw) {
        mapper.updateUserProfile(id, name, pw);
        // DB 연결 안된 경우
        // UserProfile userProfile = userMap.get(id);
        // userProfile.setName(name);
        // userProfile.setPw(pw);
    }

    // Data 삭제 시 Del
    @DeleteMapping("/user/{id}")
    public void deleteUserProfile(@PathVariable("id") String id) {
        mapper.deleteProfile(id);
        // DB 연결 안된 경우
        // userMap.remove(id);


        // create, update, delete

    }
}
