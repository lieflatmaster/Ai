package com.example.aisupportticket.infrastructure.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI响应块
 *用于SSE流式响应，表示AI返回的一个响应块
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChunk {
    
    /** 响应内容 */
    private String content;
    
    /** 是否完成 */
    private boolean done;
    
    /** 函数调用结果（如果有） */
    private FunctionCallResult functionCall;
    
    /** 追踪ID */
    private String traceId;
    
    /**
     * 创建普通响应块
     * 
     * @param content 内容
     * @param done 是否完成
     * @return AiChunk实例
     */
    public static AiChunk of(String content, boolean done) {
        return AiChunk.builder()
                .content(content)
                .done(done)
                .traceId(java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .build();
    }
    
    /**
     * 创建包含函数调用的响应块
     * 
     * @param content 内容
     * @param done 是否完成
     * @param functionCall 函数调用结果
     * @return AiChunk实例
     */
    public static AiChunk of(String content, boolean done, FunctionCallResult functionCall) {
        return AiChunk.builder()
                .content(content)
                .done(done)
                .functionCall(functionCall)
                .traceId(java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .build();
    }
    
    /**
     * 创建降级响应块
     * 
     * @param message 降级消息
     * @return AiChunk实例
     */
    public static AiChunk fallback(String message) {
        return AiChunk.builder()
                .content(message)
                .done(true)
                .build();
    }
}
