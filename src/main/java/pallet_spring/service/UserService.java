package pallet_spring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pallet_spring.mapper.UserMapper;
import pallet_spring.model.*;
import pallet_spring.model.response.MailRes;
import pallet_spring.security.jwt.JwtProvider;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class UserService {

    private String authNum; // 인증 번호

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User user;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProvider jwtProvider;

    @Transactional
    public void signUp(SignUpDTO user) {

        // ID 중복 체크
        String userId = user.getId();
        User userIndDB = userMapper.findUserDetail(userId);

        checkNickName(user.getName());

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

    public void updatePassword(String id, PasswordDto passwordDto) {

        String encodedPassword = passwordEncoder.encode(passwordDto.getPassword());  // 암호 강도 10 사용
        userMapper.updatePassword(id, encodedPassword);

    }

    public MimeMessage sendEmail(MailRequestDto mailRequestDto) throws MessagingException {

        // 코드를 생성합니다.
        createCode();

        String setFrom = "npalette0705@gmail.com";	// 보내는 사람
        String toEmail = mailRequestDto.getEmail();		// 받는 사람(값 받아옵니다.)
        String title = "[Palette] 인증번호 안내드립니다";		// 메일 제목
        log.info("5");

        try {
            MimeMessage message = emailSender.createMimeMessage();

            message.addRecipients(MimeMessage.RecipientType.TO, toEmail);	// 받는 사람 설정
            message.setSubject(title);		// 제목 설정


            // 메일 내용 설정
            String msgOfEmail="";
            msgOfEmail += "<div style='margin:20px;'>";
            msgOfEmail += "<h1> 안녕하세요 test 입니다. </h1>";
            msgOfEmail += "<br>";
            msgOfEmail += "<p>아래 코드를 입력해주세요<p>";
            msgOfEmail += "<br>";
            msgOfEmail += "<p>감사합니다.<p>";
            msgOfEmail += "<br>";
            msgOfEmail += "<div align='center' style='border:1px solid black; font-family:verdana';>";
            msgOfEmail += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
            msgOfEmail += "<div style='font-size:130%'>";
            msgOfEmail += "CODE : <strong>";
            msgOfEmail += authNum + "</strong><div><br/> ";
            msgOfEmail += "</div>";

            message.setFrom(setFrom);		// 보내는 사람 설정

            // 위 String으로 받은 내용을 아래에 넣어 내용을 설정합니다.
            message.setText(msgOfEmail, "utf-8", "html");

            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }

    public void checkNickName(String nickName) {
        int checkNick = userMapper.checkUserNickName(nickName);
        if (checkNick != 0) {
            throw new RuntimeException("해당 닉네임이 존재합니다.");
        }

    }

    public MailRes sendMailConfirm(MailRequestDto mailRequestDto) throws MessagingException {

        MimeMessage emailForm = sendEmail(mailRequestDto);

        emailSender.send(emailForm);

        MailRes mailRes = new MailRes();
        mailRes.setCode(authNum);
        return mailRes;
    }

    // 랜덤 6자리 인증 번호 생성
    private void createCode() {
        log.info("3");

        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i=0; i<8; i++) {
            // 0~2 사이의 값을 랜덤하게 받아와 idx에 집어넣습니다.
            int idx = random.nextInt(3);

            // 랜덤하게 idx를 받았으면, 그 값을 switchcase를 통해 또 꼬아버립니다.
            // 숫자와 ASCII 코드를 이용합니다.
            switch (idx) {
                case 0 :
                    // a(97) ~ z(122)
                    key.append((char) ((int)random.nextInt(26) + 97));
                    break;
                case 1:
                    // A(65) ~ Z(90)
                    key.append((char) ((int)random.nextInt(26) + 65));
                    break;
                case 2:
                    // 0 ~ 9
                    key.append(random.nextInt(9));
                    break;
            }
        }
        authNum = key.toString();
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


    public int getUserNo(String userId) {
        // 해당 user 정보 불러오기
        User user = userMapper.findUserDetail(userId);

        if (user == null) {
            throw new RuntimeException("계정정보가 없습니다");
        } else {
            // user_no 값 불러와 postDTO에 저장
            return user.getNo();
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