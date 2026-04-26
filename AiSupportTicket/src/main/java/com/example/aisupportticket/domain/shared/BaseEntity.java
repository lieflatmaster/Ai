package com.example.aisupportticket.domain.shared;

import java.time.LocalDateTime;

/**
 * 实体基类
 */
public abstract class BaseEntity {
    
    /** 创建时间 */
    protected LocalDateTime createdAt;
    
    /** 更新时间 */
    protected LocalDateTime updatedAt;
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
