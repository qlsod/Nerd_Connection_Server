package pallet_spring.mapper;

import org.apache.ibatis.annotations.*;
import pallet_spring.model.Image;
import pallet_spring.model.MyImage;
import pallet_spring.model.Post;
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
    @Select("SELECT photo_url, post_no FROM posts " +
            "WHERE share_check = 1 " +
            "ORDER BY update_date DESC " +
            "LIMIT 2")
    List<Image> getAll();

    @ResultMap("PostMap")
    @Select("SELECT photo_url, post_no FROM posts " +
            "WHERE share_check = 1 " +
            "AND update_date < (SELECT update_date FROM posts WHERE post_no = #{no}) " +
            "ORDER BY update_date DESC " +
            "LIMIT 2")
    List<Image> getNextImage(@Param("no") int no);

    @Results(id = "PostMap2", value = {
            @Result(property = "post_no", column = "post_no"),
            @Result(property = "user_no", column = "user_no"),
            @Result(property = "content", column = "content"),
            @Result(property = "photo_url", column = "photo_url"),
            @Result(property = "share_check", column = "share_check"),
            @Result(property = "create_date", column = "create_date"),
            @Result(property = "update_date", column = "update_date"),
            @Result(property = "delete_date", column = "delete_date")
    })
    @Select("SELECT photo_url, post_no, posts.update_date FROM users " +
            "JOIN posts On users.no = posts.user_no " +
            "WHERE users.no = #{userNo} " +
            "AND DATE_FORMAT(posts.update_date, '%Y-%m') = #{targetTime} " +
            "ORDER BY update_date ASC")
    List<MyImage> getMyImage(@Param("userNo") int userNo, @Param("targetTime") String targetTime);


}
