package pallet_spring.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pallet_spring.mapper.PostMapper;
import pallet_spring.mapper.UserMapper;
import pallet_spring.model.Post;
import pallet_spring.model.User;
import pallet_spring.security.jwt.JwtProvider;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {


    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.object}")
    private String object;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostMapper postMapper;

    @Transactional
    public void postUpload(Post post, String userId) {

        // userNo 받아오기
        Post postDTO = getUserNo(post, userId);
        postMapper.insertPost(postDTO);
    }

    public void postUpdate(Post post, String userId) {
        // userNo 받아오기
        Post postDTO = getUserNo(post, userId);
        postMapper.updatePost(postDTO);
    }

    public Post getUserNo(Post post, String userId) {
        // 해당 user 정보 불러오기
        User user = userMapper.findUserDetail(userId);

        if (user == null) {
            throw new RuntimeException("계정정보가 없습니다");
        } else {
            // user_no 값 불러와 postDTO에 저장
            int userNo = user.getNo();
            post.setUser_no(userNo);
            return post;
        }
    }

    public String uploadS3(MultipartFile file, String userId) {

        // 파일 이름 지정
        String keyName = "pallet_post/" + userId + "/" + file.getOriginalFilename();

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentType(file.getContentType());
        objMeta.setContentLength(file.getSize());

        try {
            amazonS3.putObject(bucket, keyName, file.getInputStream(), objMeta);
        } catch (IOException e) {
            throw new RuntimeException("업로드 실패");
        }

        // 업로드 된 이미지 URL 받기
//        String imageURL = getImageURL(keyName);
        String imageURL = object + keyName;

        log.info("imageURL:{}", imageURL);
        return imageURL;
    }

    public void deleteS3(String keyName) {
        try {
            amazonS3.deleteObject(bucket, keyName);
        } catch (AmazonS3Exception e) {
            throw new RuntimeException("삭제 실패");
        }

    }

    private String getImageURL(String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty() || !multipartFile.getName().equals("file")) {
            throw new RuntimeException("파일이 존재하지 않습니다");
        }
    }

}
