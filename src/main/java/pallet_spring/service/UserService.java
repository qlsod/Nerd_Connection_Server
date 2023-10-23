package pallet_spring.service;

import pallet_spring.mapper.UserMapper;
import pallet_spring.model.LoginDTO;
import pallet_spring.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User user;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void signUp(User user) {

        // 입력한 Pw 암호화
        String encodedPassword = passwordEncoder.encode(user.getPw());
        user.setPw(encodedPassword);

        List<User> userIdList = findAllUserId();
        if (userIdList.isEmpty()) {
            log.info("초기 세팅");
            userMapper.insertUserProfile(user);
        } else {
            for (User userDB : userIdList) {
                if (user.getId().equals(userDB.getId())) {
                    // Exception 발생 시키기
                    throw new RuntimeException("이미 가입된 ID입니다.");
                }
            }
            log.info("같은 id 없을 경우 insertUserProfile 실행");
            userMapper.insertUserProfile(user);
        }
    }

    public List<User> findAllUserId() {
        return userMapper.getAllUserId();
    }

    public List<User> findUserList() {
        return userMapper.getAll();
    }

    public boolean login(LoginDTO loginDTO) {

        LoginDTO loginMember = userMapper.login(loginDTO);
        log.info(String.valueOf(loginMember));

        if (loginMember == null ) {
            return false;
        } else {
            String rawPassword = loginDTO.getPw();
            String encodedPassword = loginMember.getPw();

            if (passwordEncoder.matches(rawPassword, encodedPassword)) {
                return true;
            } else {
                throw new RuntimeException("입력한 비밀번호가 맞지 않습니다");
            }
        }
    }

}