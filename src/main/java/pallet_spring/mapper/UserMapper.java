package pallet_spring.mapper;

import org.apache.ibatis.annotations.*;
import pallet_spring.model.MyProfileDTO;
import pallet_spring.model.SignUpDTO;
import pallet_spring.model.User;

import java.util.List;

@Mapper
public interface UserMapper {

    // POST 입력
    // id의 경우 sql문(@Insert) 안에서 수행되어 작성되기 때문에 users의 no가 비어 있음 이를 세팅하기 위해 @Options 사용
    @Insert("INSERT INTO users(id, name, password)" +
            "VALUES" +
            "(#{signUp.id}, #{signUp.name}, #{signUp.password})")
//    @Options(useGeneratedKeys = true, keyProperty = "no")
    void insertUserProfile(@Param("signUp") SignUpDTO user);

    // GET
    @ResultMap("UserProfileMap")
    @Select("SELECT * FROM users WHERE id=#{id}")
    User findUserDetail(@Param("id") String id);

    @Results(id = "UserProfile", value = {
            @Result(property = "no", column = "no"),
            @Result(property = "name", column = "name"),
            @Result(property = "id", column = "id")
    })
    @Select("SELECT id, name FROM users WHERE no = #{no}")
    MyProfileDTO getMyProfile(@Param("no") int no);

    @Update("UPDATE users SET total_like_count = total_like_count + 1 WHERE no = #{user_no}")
    void increaseTotalLikeCount(@Param("user_no") int user_no);

    @Update("UPDATE users SET total_post_count = total_post_count + 1 WHERE no = #{user_no}")
    void increaseTotalPostCount(@Param("user_no") int user_no);

    @Update("UPDATE users SET total_post_count = total_post_count - 1 WHERE no = #{user_no}")
    void decreaseTotalPostCount(@Param("user_no") int user_no);

    @Select("SELECT photo_url, post_no FROM posts WHERE user_no = #{userNo}")
    List<String> getPhotoUrlsByUserId(@Param("userNo") int userNo);

//    @Results(id = "UserProfileDetailMap", value = {
//            @Result(property = "no", column = "no"),
//            @Result(property = "name", column = "name"),
//            @Result(property = "photo_url", column = "photo_url"),
//            @Result(property = "post_no", column = "post_no"),
//            @Result(property = "user_no", column = "user_no"),
//            @Result(property = "id", column = "id")
//    })
//    @Select("SELECT id, name, photo_url, post_no FROM users Join posts ON users.no = posts.user_no WHERE users.no = #{no}")
//    MyPageRes getMyPage(@Param("no") int no);

    // GET all
    // property와 column 매칭
    @Results(id = "UserProfileMap", value = {
            @Result(property = "no", column = "no"),
            @Result(property = "name", column = "name"),
            @Result(property = "password", column = "password"),
            @Result(property = "create_date", column = "create_date"),
            @Result(property = "update_date", column = "update_date"),
            @Result(property = "delete_date", column = "delete_date")
    })
    @Select("SELECT * FROM users")
    List<User> getAll();

    @Result(property = "no", column = "no")
    @Result(property = "id", column = "id")
    @Select("SELECT no FROM users WHERE id=#{id}")
    int getUserNo(@Param("id") String id);

}
