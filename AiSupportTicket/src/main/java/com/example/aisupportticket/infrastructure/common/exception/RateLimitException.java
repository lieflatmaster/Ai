package com.example.aisupportticket.infrastructure.common.exception;

/**
 * 限流异常
 *当请求超过限流阈值时抛出此异常
 */
public class RateLimitException extends BusinessException {
    
    /**
     * 构造限流异常
     * 
     * @param message 错误消息
     */
    public RateLimitException(String message) {
        super(ErrorCode.TOO_MANY_REQUESTS, message);
    }
}
