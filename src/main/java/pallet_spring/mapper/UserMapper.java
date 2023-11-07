package pallet_spring.mapper;
import pallet_spring.model.Login;
import pallet_spring.model.User;
import org.apache.ibatis.annotations.*;
import java.util.List;


@Mapper
public interface UserMapper {

    // POST 입력
    // id의 경우 sql문(@Insert) 안에서 수행되어 작성되기 때문에 users의 no가 비어 있음 이를 세팅하기 위해 @Options 사용
    @Insert("INSERT INTO users(id, name, password)" +
            "VALUES" +
            "(#{signUp.id}, #{signUp.name}, #{signUp.password})")
    @Options(useGeneratedKeys = true, keyProperty = "no")
    void insertUserProfile(@Param("signUp") User user);

    // GET
    @ResultMap("UserProfileMap")
    @Select("SELECT * FROM users WHERE no=#{no}")
    User getUserProfile(@Param("no") int no);

    @Select("SELECT * FROM users WHERE id=#{id}")
    User getUserDetail(@Param("id") String id);

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
    Login login(Login login);



}
