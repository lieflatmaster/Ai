package com.example.aisupportticket.infrastructure.ai;

import com.example.aisupportticket.infrastructure.ai.dto.AiChunk;
import com.example.aisupportticket.infrastructure.common.exception.AiServiceException;
import com.example.aisupportticket.infrastructure.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * AI服务适配器
 *封装AI服务的调用逻辑，提供重试机制和降级策略。
 * 支持普通对话和流式对话两种模式。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceAdapter {
    
    private final AiClient aiClient;
    
    private static final int MAX_RETRY = 3;
    
    private static final long RETRY_DELAY_MS = 1000;
    
    /**
     * 普通对话
     *支持自动重试，最多重试3次。Tool Calling由AiClient内置处理
     * 
     * @param message 用户消息
     * @param userId 用户ID
     * @return AI响应内容
     */
    public String chat(String message, Long userId) {
        AiContextHolder.setContext(userId);
        try {
            return chatWithRetry(message);
        } finally {
            AiContextHolder.clear();
        }
    }
    
    /**
     * 流式对话
     *通过SSE实时返回AI响应，支持打字机效果
     * 用户ID通过Reactor Context传递，自动上下文传播确保Tool调用时能获取到用户信息
     * 
     * @param message 用户消息
     * @param userId 用户ID
     * @return AI响应流
     */
    public Flux<AiChunk> chatStream(String message, Long userId) {
        //设置用户ID
        AiContextHolder.setContext(userId);
        
        return aiClient.chatStream(message)//调用底层的 AI 客户端（通义千问），发起流式请求
                .map(this::convertToAiChunk)//转换为AiChunk
                .concatWith(Mono.just(AiChunk.of("", true)))//添加一个done=true的标记
                .doOnTerminate(() -> {
                    AiContextHolder.clear();
                })//确保在 AI 流式响应结束后，无论成功还是失败，都会清理掉之前设置的用户上下文信息
                .onErrorResume(e -> {
                    AiContextHolder.clear();
                    log.error("AI流式服务错误", e);
                    return Flux.just(AiChunk.fallback("AI服务暂时不可用，请稍后重试"));
                })//错误处理
                .contextWrite(ctx -> ctx.put(AiContextHolder.USER_ID_KEY, userId));//设置用户ID
    }
    
    /**
     * 带重试的对话调用
     */
    private String chatWithRetry(String message) {
        int retryCount = 0;
        Exception lastException = null;
        
        while (retryCount < MAX_RETRY) {
            try {
                return aiClient.chat(message);
            } catch (AiServiceException e) {
                lastException = e;
                retryCount++;
                log.warn("AI服务调用失败，正在重试第{}次", retryCount, e);
                try {
                    Thread.sleep(RETRY_DELAY_MS * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                log.error("AI服务调用异常", e);
                throw new AiServiceException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI服务调用失败");
            }
        }
        
        return "AI服务暂时不可用，请稍后重试";
    }
    
    /**
     * 将ChatResponse转换为AiChunk
     *流式响应中的每个chunk标记为done=false，
     * 在流的最后会添加一个done=true的标记
     */
    private AiChunk convertToAiChunk(ChatResponse response) {
        //响应为空或者结果为空
        if (response == null || response.getResult() == null) {
            return AiChunk.of("", false);
        }

        String content = response.getResult().getOutput().getText();
        if (content == null) {
            content = "";
        }
        
        return AiChunk.of(content, false);
    }
}
