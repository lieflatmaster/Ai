package com.example.aisupportticket.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI对话请求
 *用于接收AI对话的请求参数
 */
@Data
public class ChatRequest {
    
    /** 用户消息内容 */
    @NotBlank(message = "消息不能为空")
    private String message;
    
    /** 会话ID（可选，用于多轮对话） */
    private String conversationId;
}
