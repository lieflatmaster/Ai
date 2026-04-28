package com.example.aisupportticket.infrastructure.ai;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

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
    
    private static final Duration STREAM_TIMEOUT = Duration.ofSeconds(60);
    
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
        AiContextHolder.setContext(userId);

        return aiClient.chatStream(message)
                // 设置流式响应超时时间（30秒），防止AI服务响应过慢导致连接长时间挂起
                .timeout(STREAM_TIMEOUT)

                // 将AI原始响应转换为统一的AiChunk格式
                .map(this::convertToAiChunk)

                // 在流的末尾添加一个done=true的标记，表示流式传输完成
                .concatWith(Mono.just(AiChunk.of("", true)))

                // 流终止时（无论成功或失败）清理用户上下文，避免内存泄漏
                .doOnTerminate(() -> {
                    AiContextHolder.clear();
                })

                // 专门处理超时异常，返回友好的超时提示
                .onErrorResume(TimeoutException.class, e -> {
                    AiContextHolder.clear();
                    log.error("AI流式服务超时: userId={}", userId, e);
                    return Flux.just(AiChunk.fallback("服务响应超时，请稍后重试"));
                })

                // 处理其他所有异常（网络错误、AI服务异常等），统一降级处理
                .onErrorResume(e -> {
                    AiContextHolder.clear();
                    log.error("AI流式服务错误: userId={}, 错误类型={}", userId, e.getClass().getSimpleName(), e);
                    return Flux.just(AiChunk.fallback("AI服务暂时不可用，请稍后重试"));
                })

                // 将userId写入Reactor Context，通过自动上下文传播机制传递给下游
                // 这样在Tool调用时可以获取到用户信息，无需手动传递参数
                .contextWrite(ctx -> ctx.put(AiContextHolder.USER_ID_KEY, userId));
    }
    
    /**
     * 带重试的对话调用
     */
    private String chatWithRetry(String message) {
        int retryCount = 0;
        Exception lastException = null;

        // 循环重试，最多执行MAX_RETRY次（3次）
        while (retryCount < MAX_RETRY) {
            try {
                // 调用AI客户端进行对话
                return aiClient.chat(message);

            } catch (AiServiceException e) {
                // 捕获AI服务异常（如网络超时、API限流、服务端错误等），进行重试
                lastException = e;
                retryCount++;
                log.warn("AI服务调用失败，正在重试第{}次/共{}次", retryCount, MAX_RETRY, e);

                // 递增退避策略：第1次等待1秒，第2次等待2秒，第3次等待3秒
                // 避免频繁重试加重服务端压力，同时给服务恢复时间
                try {
                    Thread.sleep(RETRY_DELAY_MS * retryCount);
                } catch (InterruptedException ie) {
                    // 如果线程在等待期间被中断，恢复中断标志并退出重试循环
                    Thread.currentThread().interrupt();
                    log.warn("重试等待被中断");
                    break;
                }

            } catch (Exception e) {
                // 捕获其他未预期的异常（如参数错误、序列化失败等），直接抛出，不重试
                // 这类异常通常是业务逻辑问题，重试无法解决
                log.error("AI服务调用异常（不可重试）", e);
                throw new AiServiceException(ErrorCode.AI_SERVICE_UNAVAILABLE, "AI服务调用失败");
            }
        }

        // 所有重试都失败后，返回友好的降级提示，而不是抛出异常
        // 这样可以让用户体验更平滑，避免因AI服务波动导致完全不可用
        log.error("AI服务重试{}次后仍然失败", MAX_RETRY, lastException);
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
