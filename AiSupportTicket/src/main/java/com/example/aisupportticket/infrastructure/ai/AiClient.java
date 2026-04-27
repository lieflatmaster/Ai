package com.example.aisupportticket.infrastructure.ai;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.aisupportticket.infrastructure.common.exception.AiServiceException;
import com.example.aisupportticket.infrastructure.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * AI客户端
 *封装与通义千问AI服务的交互，支持普通对话和流式对话
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiClient {

    // DashScope Chat模型
    private final DashScopeChatModel chatModel;
    //工具
    private final TicketTools ticketTools;
    
    /**
     * 普通对话（带工具调用）
     * 
     * @param message 用户消息
     * @return AI响应
     */
    public String chat(String message) {
        try {
            ChatClient chatClient = ChatClient.builder(chatModel)
                    .defaultTools(ticketTools)
                    .build();
            
            return chatClient.prompt()
                    .user(message)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("AI服务调用失败", e);
            throw new AiServiceException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI service call failed: " + e.getMessage());
        }
    }
    
    /**
     * 流式对话（带工具调用）
     *用户上下文通过Reactor自动上下文传播机制传递
     * 
     * @param message 用户消息
     * @return AI响应流
     */
    public Flux<ChatResponse> chatStream(String message) {
        try {
            ChatClient chatClient = ChatClient.builder(chatModel)
                    .defaultTools(ticketTools)
                    .build();
            
            return chatClient.prompt()
                    .user(message)
                    .stream()
                    .chatResponse();
        } catch (Exception e) {
            log.error("AI流式服务调用失败", e);
            return Flux.error(new AiServiceException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI service call failed: " + e.getMessage()));
        }
    }
}
