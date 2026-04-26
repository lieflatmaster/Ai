package com.example.aisupportticket.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新工单命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketCommand {
    
    /** 工单ID */
    private Long ticketId;
    
    /** 用户ID */
    private Long userId;
    
    /** 新的描述内容 */
    private String description;
}
