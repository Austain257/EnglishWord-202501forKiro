package com.austain.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String avatar;
    private String nickname;
    private String email;
    private String role;
    private Long status;

    private String learningGoal;
    private Integer dailyTarget;
    private Integer reminderEnabled;

    private java.time.LocalDateTime lastLoginTime;
    private java.time.LocalDateTime createTime;
    private java.time.LocalDateTime updateTime;
}
