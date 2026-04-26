package com.example.aisupportticket.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关闭工单命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseTicketCommand {
    
    /** 工单ID */
    private Long ticketId;
    
    /** 用户ID */
    private Long userId;
    
    /** 关闭原因 */
    private String reason;
}
