package com.example.aisupportticket.domain.user.repository;

import com.example.aisupportticket.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository {
    
    /**
     * 保存用户
     * 
     * @param user 用户实体
     * @return 保存后的用户
     */
    User save(User user);
    
    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户实体（可能为空）
     */
    Optional<User> findById(Long id);
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户实体（可能为空）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户实体（可能为空）
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 分页查询所有用户
     * 
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<User> findAll(Pageable pageable);
    
    /**
     * 根据ID删除用户
     * 
     * @param id 用户ID
     */
    void deleteById(Long id);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 如果存在返回true
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 如果存在返回true
     */
    boolean existsByEmail(String email);
}
