package pallet_spring.mapper;

import org.apache.ibatis.annotations.*;
import pallet_spring.model.Image;
import pallet_spring.model.Post;
import pallet_spring.model.User;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface PostMapper {

    // POST 입력
    // id의 경우 sql문(@Insert) 안에서 수행되어 작성되기 때문에 users의 no가 비어 있음 이를 세팅하기 위해 @Options 사용
    @Insert("INSERT INTO posts(user_no, content, photo_url, share_check)" +
            "VALUES" +
            "(#{post.user_no}, #{post.content}, #{post.photo_url}, #{post.share_check})")
    @Options(useGeneratedKeys = true, keyProperty = "post_no")
    void insertPost(@Param("post") Post post);

//    // GET
//    @ResultMap("UserProfileMap")
//    @Select("SELECT * FROM users WHERE no=#{no}")
//    User getUserProfile(@Param("no") int no);
//
//    @ResultMap("UserProfileMap")
//    @Select("SELECT * FROM users WHERE id=#{id}")
//    User findUserDetail(@Param("id") String id);
//



    // 시간 순 정렬하여 url 불러오기
    // property와 column 매칭
    @Results(id = "PostMap", value = {
            @Result(property = "post_no", column = "post_no"),
            @Result(property = "user_no", column = "user_no"),
            @Result(property = "content", column = "content"),
            @Result(property = "photo_url", column = "photo_url"),
            @Result(property = "share_check", column = "share_check"),
            @Result(property = "create_date", column = "create_date"),
            @Result(property = "update_date", column = "update_date"),
            @Result(property = "delete_date", column = "delete_date")
    })
    @Select("SELECT photo_url FROM posts WHERE share_check = 1 ORDER BY update_date DESC")
    List<Image> getAll();


}
