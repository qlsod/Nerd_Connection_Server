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
import pallet_spring.mapper.HeartMapper;
import pallet_spring.mapper.PostMapper;
import pallet_spring.mapper.UserMapper;
import pallet_spring.model.HeartDto;
import pallet_spring.model.Post;
import pallet_spring.model.PostDTO;
import pallet_spring.model.response.PostTimeRes;
import pallet_spring.security.jwt.JwtProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private UserService userService;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private HeartMapper heartMapper;

    @Transactional
    public void postUpload(PostDTO postDTO, String userId) {

        // userNo 받아오기
        int userNo = userService.getUserNo(userId);

        Post post = toEntity(postDTO, userNo);

        postMapper.insertPost(post);
        userMapper.increaseTotalPostCount(userNo);
    }

    public void postUpdate(PostDTO postDTO, int post_no, String userId) {
        // userNo 받아오기
        int userNo = userService.getUserNo(userId);
        Post post = toEntity(postDTO, userNo);
        post.setPost_no(post_no);
        postMapper.updatePost(post);
    }

    public List<PostTimeRes> RemoveDuplicates(List<PostTimeRes> postTimeList) {
        Set<String> uniqueDates = new HashSet<>();
        List<PostTimeRes> uniquePosts = new ArrayList<>();

        for (PostTimeRes postTime : postTimeList) {
            String dateWithoutTime = getDateWithoutTime((Date) postTime.getCreate_date());
            if (!uniqueDates.contains(dateWithoutTime)) {
                uniqueDates.add(dateWithoutTime);
                uniquePosts.add(postTime);
            }
        }

        log.info(uniquePosts.toString());
        return uniquePosts;
    }

    private String getDateWithoutTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }



    public static Post toEntity(PostDTO dto, int userNo) {
        Post entity = new Post();
        entity.setContent(dto.getContent());
        entity.setUser_no(userNo);
        entity.setPhoto_url(dto.getPhoto_url());
        entity.setShare_check(dto.isShare_check());
        return entity;
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

    @Transactional
    public void increaseLikeCount(HeartDto heartDto) {
        postMapper.increaseLikeCount(heartDto.getPost_no());
        userMapper.increaseTotalLikeCount(heartDto.getPost_no());
        heartMapper.insertHeart(heartDto);
    }

    @Transactional
    public void decreaseLikeCount(HeartDto heartDto) {
        postMapper.decreaseLikeCount(heartDto.getPost_no());
        userMapper.decreaseTotalLikeCount(heartDto.getPost_no());
        heartMapper.deleteHeart(heartDto);
    }

    // post_no에 해당 하는 게시글의 like_count 가져와 해당 게시글의 존재 여부 확인
    public void validatePost(int post_no) {

        boolean checkPost = postMapper.validatePost(post_no);

        if (!checkPost) {
            throw new RuntimeException("해당 게시글이 존재하지 않습니다.");
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
