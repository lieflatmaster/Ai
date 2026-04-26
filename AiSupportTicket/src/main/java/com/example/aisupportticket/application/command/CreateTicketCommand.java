package com.example.aisupportticket.application.command;

import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建工单命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketCommand {
    
    /** 用户ID */
    private Long userId;
    
    /** 工单标题 */
    private String title;
    
    /** 工单描述 */
    private String description;
    
    /** 工单分类 */
    private TicketCategory category;
    
    /** 关联订单号 */
    private String orderId;
}
