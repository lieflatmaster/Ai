package com.example.aisupportticket.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单日志持久化对象
 */
@Data
@TableName("t_ticket_log")
public class TicketLogPO {
    
    /** 日志ID（自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 关联工单ID */
    private Long ticketId;
    
    /** 操作类型：CREATE/UPDATE/CLOSE/ESCALATE等 */
    private String action;
    
    /** 操作前状态 */
    private String oldStatus;
    
    /** 操作后状态 */
    private String newStatus;
    
    /** 操作人ID */
    private Long operatorId;
    
    /** 操作备注 */
    private String remark;
    
    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
