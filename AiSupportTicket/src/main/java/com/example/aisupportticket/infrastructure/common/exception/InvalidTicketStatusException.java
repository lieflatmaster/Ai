package com.example.aisupportticket.infrastructure.common.exception;

/**
 * 工单状态非法异常
 *当工单状态不允许执行某操作时抛出此异常
 */
public class InvalidTicketStatusException extends BusinessException {
    
    /**
     * 构造工单状态非法异常
     * 
     * @param message 错误消息
     */
    public InvalidTicketStatusException(String message) {
        super(ErrorCode.INVALID_TICKET_STATUS, message);
    }
}
