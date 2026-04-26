package com.example.aisupportticket.infrastructure.common.exception;

/**
 * AI服务异常
 */
public class AiServiceException extends BusinessException {
    
    /**
     * 构造AI服务异常
     * 
     * @param message 错误消息
     */
    public AiServiceException(String message) {
        super(ErrorCode.AI_SERVICE_UNAVAILABLE, message);
    }
    
    /**
     * 使用指定错误码构造AI服务异常
     * 
     * @param errorCode 错误码枚举
     * @param message 错误消息
     */
    public AiServiceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
