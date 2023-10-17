package hello.hellspring.mapper;
import hello.hellspring.model.LoginDTO;
import hello.hellspring.model.User;
import org.apache.ibatis.annotations.*;
import java.util.List;


@Mapper
public interface UserMapper {

    // POST 입력
    // id의 경우 sql문(@Insert) 안에서 수행되어 작성되기 때문에 users의 no가 비어 있음 이를 세팅하기 위해 @Options 사용
    @Insert("INSERT INTO users(id, name, pw)" +
            "VALUES" +
            "(#{signUp.id}, #{signUp.name}, #{signUp.pw})")
    @Options(useGeneratedKeys = true, keyProperty = "no")
    void insertUserProfile(@Param("signUp") User user);

    // GET
    @ResultMap("UserProfileMap")
    @Select("SELECT * FROM users WHERE no=#{no}")
    User getUserProfile(@Param("no") int no);

    @ResultMap("UserProfileMap")
    @Select("SELECT id FROM users")
    List<User> getAllUserId();

    // GET all
    // property와 column 매칭
    @Results(id = "UserProfileMap", value = {
            @Result(property = "no", column = "no"),
            @Result(property = "create_date", column = "create_date"),
            @Result(property = "update_date", column = "update_date"),
            @Result(property = "delete_date", column = "delete_date")
    })
    @Select("SELECT * FROM users")
    List<User> getAll();

    @Select("SELECT * FROM users WHERE id=#{id}")
    LoginDTO login(LoginDTO loginDTO);




//    // insert, update, delete의 경우 SQL문에 의해 적용된 또는 영향 받은 레코드의 개수가 반환된다.
//    @Update("UPDATE UserProfile SET name=#{name}, pw=#{pw} WHERE id=#{id}")
//    int updateUserProfile(@Param("id") String id, @Param("name") String name, @Param("pw") String pw);
//    // PUT 수정
//
//    @Delete("DELETE FROM UserProfile WHERE id=#{id}")
//    int deleteProfile(@Param("id") String id);
//    // DELETE 삭제

}
