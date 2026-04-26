package com.example.aisupportticket.application.service;

import com.example.aisupportticket.infrastructure.ai.AiServiceAdapter;
import com.example.aisupportticket.infrastructure.ai.dto.AiChunk;
import com.example.aisupportticket.infrastructure.common.exception.RateLimitException;
import com.example.aisupportticket.infrastructure.redis.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI对话应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatApplicationService {

    // AI服务适配器
    private final AiServiceAdapter aiServiceAdapter;
    // 限流器
    private final RateLimiter rateLimiter;
    
    /** 每分钟对话次数限制 */
    private static final int CHAT_LIMIT_PER_MINUTE = 100;
    
    /**
     * 普通对话
     *同步调用AI服务，返回完整响应<
     * 
     * @param message 用户消息
     * @param userId 用户ID
     * @return AI响应内容
     * @throws RateLimitException 如果超过限流阈值
     */
    public String chat(String message, Long userId) {
        checkRateLimit(userId);
        return aiServiceAdapter.chat(message, userId);
    }
    
    /**
     * 流式对话
     *通过SSE实时返回AI响应，支持打字机效果
     * 
     * @param message 用户消息
     * @param userId 用户ID
     * @return AI响应流
     * @throws RateLimitException 如果超过限流阈值
     */
    public Flux<AiChunk> chatStream(String message, Long userId) {
        checkRateLimit(userId);
        return aiServiceAdapter.chatStream(message, userId);
    }
    
    /**
     * 检查限流
     *基于Redis实现滑动窗口限流，每分钟最多100次请求
     * 
     * @param userId 用户ID
     * @throws RateLimitException 如果超过限流阈值
     */
    private void checkRateLimit(Long userId) {
        String rateLimitKey = "chat:user:" + userId;

        if (!rateLimiter.allowRequest(rateLimitKey, CHAT_LIMIT_PER_MINUTE, 60)) {
            throw new RateLimitException("请求过于频繁，请稍后重试");
        }
    }
}
