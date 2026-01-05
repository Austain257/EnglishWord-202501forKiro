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

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE users SET last_login_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(Long userId);
}
