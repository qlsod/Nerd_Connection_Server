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

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User user;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void signUp(User user) {
        if (!user.getId().isEmpty() && !user.getName().isEmpty() && !user.getPw().isEmpty()) {
            log.info("빈칸없음");

            String encodedPassword = passwordEncoder.encode(user.getPw());
            user.setPw(encodedPassword);
            log.info(String.valueOf(user));

            boolean isIdExists = false;

            List<User> userIdList = findAllUserId();
            if (userIdList.isEmpty()) {
                log.info("userIdList 값 없을 경우(초기세팅)");
                userMapper.insertUserProfile(user);
            } else {
                log.info("DB의 id의 값이 존재할 경우");
                for (User userDB : userIdList) {
                    log.info("입력한 id의 값과 DB의 id의 값 비교");
                    if (user.getId().equals(userDB.getId())) {
                        isIdExists = true;
                        log.info("이미 가입된 ID입니다");

                        // Exception 발생 시키기
                        throw new RuntimeException("이미 가입된 ID입니다.");
                    }
                }
                if (!isIdExists) {
                    log.info("같은 id 없을 경우 insertUserProfile 실행");
                    userMapper.insertUserProfile(user);
                }
            }
        } else {
            log.info("빈칸있음");
        }
    }


    public List<User> findAllUserId() {
        log.info("모든 user의 id 값 조회");
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
