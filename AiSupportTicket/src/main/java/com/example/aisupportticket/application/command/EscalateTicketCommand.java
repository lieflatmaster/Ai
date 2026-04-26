package com.example.aisupportticket.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 催办工单命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EscalateTicketCommand {
    
    /** 工单ID */
    private Long ticketId;
    
    /** 用户ID */
    private Long userId;
}
