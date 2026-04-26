package com.example.aisupportticket.infrastructure.common.exception;

/**
 * 工单不存在异常
 *当查询的工单不存在时抛出此异常
 */
public class TicketNotFoundException extends BusinessException {
    
    /**
     * 根据工单ID构造异常
     * 
     * @param ticketId 工单ID
     */
    public TicketNotFoundException(Long ticketId) {
        super(ErrorCode.TICKET_NOT_FOUND, "工单不存在: " + ticketId);
    }
    
    /**
     * 根据消息构造异常
     * 
     * @param message 错误消息
     */
    public TicketNotFoundException(String message) {
        super(ErrorCode.TICKET_NOT_FOUND, message);
    }
}
