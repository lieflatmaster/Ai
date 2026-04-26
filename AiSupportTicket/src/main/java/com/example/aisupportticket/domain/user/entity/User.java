package com.example.aisupportticket.domain.user.entity;

import com.example.aisupportticket.domain.shared.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *系统用户实体，包含用户的基本信息和状态
 */
@Getter
@Setter
public class User extends BaseEntity {
    
    /** 用户ID */
    private Long id;
    
    /** 用户名 */
    private String username;
    
    /** 密码哈希值 */
    private String passwordHash;
    
    /** 邮箱 */
    private String email;
    
    /** 手机号 */
    private String phone;
    
    /** 用户状态 */
    private UserStatus status;
    
    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;
    
    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        /** 活跃 */
        ACTIVE,
        /** 未激活 */
        INACTIVE,
        /** 已封禁 */
        BANNED
    }
    
    /**
     * 创建新用户
     * 
     * @param username 用户名
     * @param email 邮箱
     * @param phone 手机号
     * @return 用户实体
     */
    public static User create(String username, String email, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    /**
     * 检查用户是否活跃
     * 
     * @return 如果用户活跃返回true
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
