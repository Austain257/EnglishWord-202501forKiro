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
    int insert(User user);

    // 根据ID查询用户
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    // 更新用户最后登录时间
    @Update("UPDATE users SET last_login_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(Long userId);

    // 更新用户信息
    int updateProfile(User user);

    // 更新用户密码
    @Update("UPDATE users SET password = #{password}, update_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    // 检查用户名是否已存在
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username} AND id <> #{excludeId}")
    int countByUsernameExcluding(@Param("username") String username, @Param("excludeId") Long excludeId);

    // 检查邮箱是否已存在
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email} AND id <> #{excludeId}")
    int countByEmailExcluding(@Param("email") String email, @Param("excludeId") Long excludeId);
}
