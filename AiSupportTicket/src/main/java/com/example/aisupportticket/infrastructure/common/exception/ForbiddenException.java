package com.example.aisupportticket.infrastructure.common.exception;

/**
 * 无权限异常
 *当用户无权限访问资源时抛出此异常
 */
public class ForbiddenException extends BusinessException {
    
    /**
     * 构造无权限异常
     * 
     * @param message 错误消息
     */
    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
