package pallet_spring.service;

import pallet_spring.mapper.UserMapper;
import pallet_spring.model.Login;
import pallet_spring.model.SignUpDTO;
import pallet_spring.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pallet_spring.security.jwt.JwtProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User user;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProvider jwtProvider;

    @Transactional
    public void signUp(SignUpDTO user) {

        // ID 중복 체크
        String userId = user.getId();
        User userIndDB = userMapper.findUserDetail(userId);

        if (userIndDB == null) {
            // 입력한 Pw 암호화
            String encodedPassword = passwordEncoder.encode(user.getPassword());  // 암호 강도 10 사용
            user.setPassword(encodedPassword);

            // 해당 유저 회원 가입
            userMapper.insertUserProfile(user);
        } else {
            throw new RuntimeException("이미 가입된 ID입니다");
        }

    }

    public List<User> findUserList() {
        return userMapper.getAll();
    }

    public void login(Login login) {

        // 해당 ID의 유저 가입 여부 확인
        String userId = login.getId();
        User userInDB = userMapper.findUserDetail(userId);

        if(userInDB == null ) {
            throw new RuntimeException("가입된 유저가 아닙니다");
        } else {
            // Password 비교
            String rawPassword = login.getPassword();
            String encodedPassword = userInDB.getPassword();
            checkUserPW(rawPassword, encodedPassword);
        }
    }

    // user 정보 여부 확인
    public User checkUserId(String id) {
        return userMapper.findUserDetail(id);
    }

    public void checkUserPW(String rawPW, String encodedPw) {

        if (!passwordEncoder.matches(rawPW, encodedPw)) {
            throw new RuntimeException("입력한 비밀번호가 맞지 않습니다");
        }
    }

    public void deleteCookie(HttpServletResponse response) {

        Cookie cookie = new Cookie("refreshToken", null);

        // 쿠키의 expiration 타임을 0으로 하여 없앤다.
        cookie.setMaxAge(0);

        // 모든 경로에서 삭제 됬음을 알린다.
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}