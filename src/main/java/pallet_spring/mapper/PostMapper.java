package pallet_spring.mapper;

import org.apache.ibatis.annotations.*;
import pallet_spring.model.*;
import pallet_spring.model.response.MyImagesRes;
import pallet_spring.model.response.PostTimeRes;

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

    @Update("UPDATE posts " +
            "SET content = #{post.content}, " +
            "photo_url = #{post.photo_url}, " +
            "share_check = #{post.share_check}, " +
            "update_date = NOW() " +
            "WHERE post_no = #{post.post_no}")
    void updatePost(@Param("post") Post post);




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
    @Select("SELECT * FROM posts WHERE post_no = #{post_no}")
    Post getPostDetail(@Param("post_no") int post_no);


    @Results(id = "FeedDetailMap", value = {
            @Result(property = "post_no", column = "post_no"),
            @Result(property = "content", column = "content"),
            @Result(property = "photo_url", column = "photo_url"),
            @Result(property = "name", column = "name"),
            @Result(property = "like_count", column = "like_count"),
            @Result(property = "create_date", column = "create_date")
    })
    @Select("SELECT posts.content, posts.photo_url, posts.create_date, users.name, like_count FROM posts JOIN users ON posts.user_no = users.no WHERE post_no = #{post_no}")
    FeedDetail getFeedDetail(@Param("post_no") int post_no);

    @Update("UPDATE posts SET like_count = like_count + 1 WHERE post_no = #{post_no}")
    void increaseLikeCount(@Param("post_no") int post_no);

    @Update("UPDATE posts SET like_count = like_count - 1 WHERE post_no = #{post_no}")
    void decreaseLikeCount(@Param("post_no") int post_no);


    @Select("SELECT COUNT(*) > 0 FROM posts WHERE post_no = #{post_no}")
    boolean validatePost(@Param("post_no") int post_no);

    @Select("SELECT SUM(like_count) AS total_like_count, COUNT(user_no) AS total_post_count FROM posts WHERE user_no = #{user_no}")
    MyProfileTotalCountDTO getTotalPosts(@Param("user_no") int user_no);

    @Result(property = "user_no", column = "user_no")
    @Result(property = "photo_url", column = "photo_url")
    @Result(property = "post_no", column = "post_no")
    @Select("SELECT photo_url, post_no FROM posts WHERE user_no = #{user_no} ORDER BY create_date DESC")
    List<Image> getMyPosts(@Param("user_no") int user_no);

    @Results(id = "PostLikeMap", value = {
            @Result(property = "post_no", column = "post_no"),
            @Result(property = "like_count", column = "like_count"),
            @Result(property = "photo_url", column = "photo_url")
    })
    @Select("SELECT like_count, photo_url FROM posts WHERE post_no = #{post_no}")
    PostLikeDto getPostUrl(@Param("post_no") int post_no);

    @ResultMap("PostMap")
    @Select("DELETE FROM posts WHERE post_no = #{post_no}")
    void deletePost(@Param("post_no") int post_no);

    @Results(id = "ImageMap", value = {
            @Result(property = "post_no", column = "post_no"),
            @Result(property = "photo_url", column = "photo_url")
    })
    @Select("SELECT post_no, photo_url FROM posts " +
            "WHERE share_check = 1 " +
            "ORDER BY update_date DESC " +
            "LIMIT 18")
    List<Image> getAll();

    @Delete("DELETE FROM posts WHERE user_no = (SELECT no FROM users WHERE id = #{id})")
    void deletePostFromId(@Param("id") String id);

    @ResultMap("ImageMap")
    @Select("SELECT post_no, photo_url  FROM posts " +
            "WHERE share_check = 1 " +
            "AND update_date < (SELECT update_date FROM posts WHERE post_no = #{no}) " +
            "ORDER BY update_date DESC " +
            "LIMIT 18")
    List<Image> getNextImage(@Param("no") int no);

    @Results(id = "MyImageMap", value = {
            @Result(property = "post_no", column = "post_no"),
            @Result(property = "photo_url", column = "photo_url"),
            @Result(property = "content", column = "content"),
            @Result(property = "like_count", column = "like_count"),
            @Result(property = "create_date", column = "create_date"),
            @Result(property = "like", column = "is_liked") // 변경된 필드 이름과 매핑
    })
    @Select("SELECT posts.post_no, posts.photo_url, posts.content, posts.like_count, " +
            "CASE WHEN hearts.user_no IS NOT NULL THEN true ELSE false END AS `is_liked` " +  // like 대신 is_liked로 변경
            "FROM users " +
            "JOIN posts ON users.no = posts.user_no " +
            "LEFT JOIN hearts ON posts.post_no = hearts.post_no AND hearts.user_no = #{userNo} " +
            "WHERE users.no = #{userNo} " +
            "AND DATE_FORMAT(posts.create_date, '%Y-%m-%d') = #{targetTime} " +
            "ORDER BY posts.create_date ASC")
    List<MyImagesRes> getMyImage(@Param("userNo") int userNo, @Param("targetTime") String targetTime);



    @Results(id = "MyPostTime", value = {
            @Result(property = "create_date", column = "create_date"),
    })
    @Select("SELECT create_date FROM posts WHERE user_no = #{userNo} AND DATE_FORMAT(posts.create_date, '%Y-%m') = #{targetTime}")
    List<PostTimeRes> getPostTime(@Param("userNo") int userNo, @Param("targetTime") String targetTime);


    // update_date 포함 sql문
//    @Select("SELECT post_no, photo_url, posts.update_date, content FROM users " +
//            "JOIN posts On users.no = posts.user_no " +
//            "WHERE users.no = #{userNo} " +
//            "AND DATE_FORMAT(posts.update_date, '%Y-%m-%d') = #{targetTime} " +
//            "ORDER BY update_date ASC")
//    List<MyImage> getMyImage(@Param("userNo") int userNo, @Param("targetTime") String targetTime);


}
