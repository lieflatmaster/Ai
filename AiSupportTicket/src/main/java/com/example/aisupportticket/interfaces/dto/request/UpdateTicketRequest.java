package com.example.aisupportticket.interfaces.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新工单请求
 *用于接收更新工单的请求参数
 */
@Data
public class UpdateTicketRequest {
    
    /** 新的工单描述 */
    @Size(max = 2000, message = "描述长度不能超过2000")
    private String description;
}
