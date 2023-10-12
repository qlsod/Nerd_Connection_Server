package hello.hellspring.service;

import hello.hellspring.mapper.UserMapper;
import hello.hellspring.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

//    public List<UserProfile> findById() {
//        List<UserProfile> userList = userMapper.getAll();
//        if (userList != null && !userList.isEmpty()) {
//            for (UserProfile userProfile : userList) {
//                userList.
//            }
//        }
//
//    }
//    public void signUp(User user) {
//        SimpleDateFormat currentTime = funCurrentDate();
//        user.setCreate_date(currentTime);
//        user.setUpdate_date(currentTime);
//        userMapper.insertUserProfile(user);
//    }
//
//    public SimpleDateFormat funCurrentDate() {
//        return new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss", Locale.KOREA);
//    }
//

}
