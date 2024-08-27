package pallet_spring.mapper;

import org.apache.ibatis.annotations.*;
import pallet_spring.model.HeartDto;

@Mapper
public interface HeartMapper {

    @Select("SELECT COUNT(*) > 0 FROM hearts JOIN users ON hearts.user_no = users.no WHERE users.id = #{user_id} AND post_no = #{post_no}")
    boolean checkLikeUser(@Param("user_id") String user_id, @Param("post_no") int post_no);

    @Insert("INSERT INTO hearts(user_no, post_no)" +
            "VALUES" +
            "(#{heart.user_no}, #{heart.post_no})")
    void insertHeart(@Param("heart") HeartDto heartDto);

    @Delete("DELETE FROM hearts WHERE user_no = #{heart.user_no} AND post_no = #{heart.post_no}")
    void deleteHeart(@Param("heart") HeartDto heartDto);

    @Delete("DELETE FROM hearts WHERE user_no = (SELECT no FROM users WHERE id = #{id})")
    void deleteHeartById(@Param("id") String id);

}
