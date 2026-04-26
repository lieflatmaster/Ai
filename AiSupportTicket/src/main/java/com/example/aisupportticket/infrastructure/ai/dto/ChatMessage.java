package com.example.aisupportticket.infrastructure.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天消息DTO
 *用于封装AI对话中的消息，支持多种角色类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    /** 消息角色：user/assistant/system/tool */
    private String role;
    
    /** 消息内容 */
    private String content;
    
    /** 工具调用列表 */
    private List<ToolCall> toolCalls;
    
    /**
     * 工具调用信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCall {
        /** 调用ID */
        private String id;
        /** 调用类型 */
        private String type;
        /** 函数信息 */
        private Function function;
    }
    
    /**
     * 函数信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function {
        /** 函数名称 */
        private String name;
        /** 函数参数（JSON字符串） */
        private String arguments;
    }
    
    /**
     * 创建用户消息
     * 
     * @param content 消息内容
     * @return ChatMessage实例
     */
    public static ChatMessage user(String content) {
        return ChatMessage.builder()
                .role("user")
                .content(content)
                .build();
    }
    
    /**
     * 创建助手消息
     * 
     * @param content 消息内容
     * @return ChatMessage实例
     */
    public static ChatMessage assistant(String content) {
        return ChatMessage.builder()
                .role("assistant")
                .content(content)
                .build();
    }
    
    /**
     * 创建系统消息
     * 
     * @param content 消息内容
     * @return ChatMessage实例
     */
    public static ChatMessage system(String content) {
        return ChatMessage.builder()
                .role("system")
                .content(content)
                .build();
    }
    
    /**
     * 创建工具结果消息
     * 
     * @param toolCallId 工具调用ID
     * @param content 结果内容
     * @return ChatMessage实例
     */
    public static ChatMessage toolResult(String toolCallId, String content) {
        return ChatMessage.builder()
                .role("tool")
                .content(content)
                .build();
    }
}
