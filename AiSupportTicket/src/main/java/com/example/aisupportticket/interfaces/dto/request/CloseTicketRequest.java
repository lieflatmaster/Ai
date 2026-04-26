package com.example.aisupportticket.interfaces.dto.request;

import lombok.Data;

/**
 * 关闭工单请求
 *用于接收关闭工单的请求参数
 */
@Data
public class CloseTicketRequest {
    
    /** 关闭原因 */
    private String reason;
}
