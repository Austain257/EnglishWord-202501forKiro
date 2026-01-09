package com.austain.mapper;

import com.austain.domain.po.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    /**
     * 插入新用户
     */
    @Insert("INSERT INTO users (username, password, email, nickname, avatar, role, status) " +
            "VALUES (#{username}, #{password}, #{email}, #{nickname}, #{avatar}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Update("UPDATE users SET last_login_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(Long userId);

    @Update("<script>" +
            "UPDATE users " +
            "<set>" +
            "<if test='username != null'>username = #{username},</if>" +
            "<if test='email != null'>email = #{email},</if>" +
            "<if test='avatar != null'>avatar = #{avatar},</if>" +
            "<if test='nickname != null'>nickname = #{nickname},</if>" +
            "<if test='learningGoal != null'>learning_goal = #{learningGoal},</if>" +
            "<if test='dailyTarget != null'>daily_target = #{dailyTarget},</if>" +
            "<if test='reminderEnabled != null'>reminder_enabled = #{reminderEnabled},</if>" +
            "update_time = NOW()" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateProfile(User user);

    @Update("UPDATE users SET password = #{password}, update_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username} AND id <> #{excludeId}")
    int countByUsernameExcluding(@Param("username") String username, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email} AND id <> #{excludeId}")
    int countByEmailExcluding(@Param("email") String email, @Param("excludeId") Long excludeId);
}
