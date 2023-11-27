package pallet_spring.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pallet_spring.model.Login;
import pallet_spring.model.User;
import pallet_spring.security.jwt.JwtProvider;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {


    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Autowired
    private JwtProvider jwtProvider;


    public void upload(MultipartFile file, String userId) {

        // 파일 존재 여부 확인
        validateFileExists(file);

        // 파일 이름 지정
        String fileName = "pallet_post/" + userId + "-" + file.getOriginalFilename();

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentType(file.getContentType());
        objMeta.setContentLength(file.getSize());

        try {
            amazonS3.putObject(bucket, fileName, file.getInputStream(), objMeta);
        } catch (IOException e) {
            throw new RuntimeException("업로드 실패");
        }

    }

    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new RuntimeException("파일이 존재하지 않습니다");
        }
    }

}
