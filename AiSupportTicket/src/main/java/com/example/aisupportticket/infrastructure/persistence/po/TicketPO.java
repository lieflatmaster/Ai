package com.example.aisupportticket.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单持久化对象
 */
@Data
@TableName("t_ticket")
public class TicketPO {
    
    /** 工单ID（自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 工单标题 */
    private String title;
    
    /** 工单描述 */
    private String description;
    
    /** 工单分类 */
    private String category;
    
    /** 工单状态 */
    private String status;
    
    /** 优先级（1-5） */
    private Integer priority;
    
    /** 关联订单号 */
    private String orderId;
    
    /** 催办次数 */
    private Integer escalateCount;
    
    /** 最近催办时间 */
    private LocalDateTime escalatedAt;
    
    /** 乐观锁版本号 */
    @Version
    private Integer version;
    
    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;
    
    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /** 关闭时间 */
    private LocalDateTime closedAt;
}
