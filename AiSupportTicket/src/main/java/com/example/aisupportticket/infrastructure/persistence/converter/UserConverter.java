package com.example.aisupportticket.infrastructure.persistence.converter;

import com.example.aisupportticket.domain.user.entity.User;
import com.example.aisupportticket.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Component;

/**
 * 用户转换器
 *负责用户领域对象和持久化对象之间的相互转换
 */
@Component
public class UserConverter {
    
    /**
     * 将持久化对象转换为领域对象
     * 
     * @param po 持久化对象
     * @return 领域对象
     */
    public User toDomain(UserPO po) {
        if (po == null) {
            return null;
        }
        
        User user = new User();
        user.setId(po.getId());
        user.setUsername(po.getUsername());
        user.setPasswordHash(po.getPasswordHash());
        user.setEmail(po.getEmail());
        user.setPhone(po.getPhone());
        user.setStatus(po.getStatus() != null ? User.UserStatus.valueOf(po.getStatus()) : null);
        user.setLastLoginAt(po.getLastLoginAt());
        user.setCreatedAt(po.getCreatedAt());
        user.setUpdatedAt(po.getUpdatedAt());
        
        return user;
    }
    
    /**
     * 将领域对象转换为持久化对象
     * 
     * @param user 领域对象
     * @return 持久化对象
     */
    public UserPO toPO(User user) {
        if (user == null) {
            return null;
        }
        
        UserPO po = new UserPO();
        po.setId(user.getId());
        po.setUsername(user.getUsername());
        po.setPasswordHash(user.getPasswordHash());
        po.setEmail(user.getEmail());
        po.setPhone(user.getPhone());
        po.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        po.setLastLoginAt(user.getLastLoginAt());
        po.setCreatedAt(user.getCreatedAt());
        po.setUpdatedAt(user.getUpdatedAt());
        
        return po;
    }
}
