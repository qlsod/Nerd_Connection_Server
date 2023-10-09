package hello.hellspring.mapper;

import hello.hellspring.model.UserProfile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserProfileMapper {

    @Select("SELECT * FROM UserProfile WHERE id=#{id}")
    UserProfile getUserProfile(@Param("id") String id);
    // GET

    @Select("SELECT * FROM UserProfile")
    List<UserProfile> getUserProfileList();
    // GET all


    // insert, update, delete의 경우 SQL문에 의해 적용된 또는 영향 받은 레코드의 개수가 반환된다.
    @Insert("INSERT INTO UserProfile(id, name, pw, photoURL) VALUES (#{id}, #{name}, #{pw}, #{photoURL})")
    int insertUserProfile(@Param("id") String id, @Param("name") String name, @Param("pw") String pw, @Param("photoURL") String photoURL);
    // POST 입력

    @Update("UPDATE UserProfile SET name=#{name}, pw=#{pw} WHERE id=#{id}")
    int updateUserProfile(@Param("id") String id, @Param("name") String name, @Param("pw") String pw);
    // PUT 수정

    @Delete("DELETE FROM UserProfile WHERE id=#{id}")
    int deleteProfile(@Param("id") String id);
    // DELETE 삭제

}
