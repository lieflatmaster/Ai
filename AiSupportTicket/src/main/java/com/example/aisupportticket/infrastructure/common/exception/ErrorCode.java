package com.example.aisupportticket.infrastructure.common.exception;

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {
    
    /** 操作成功 */
    SUCCESS(200, "操作成功"),
    
    /** 参数错误 */
    BAD_REQUEST(400, "参数错误"),
    
    /** 未登录或会话过期 */
    UNAUTHORIZED(401, "未登录或会话过期"),
    
    /** 无权限访问 */
    FORBIDDEN(403, "无权限访问"),
    
    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),
    
    /** 请求过于频繁 */
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    
    /** 工单不存在 */
    TICKET_NOT_FOUND(5001, "工单不存在"),
    
    /** 工单状态不允许此操作 */
    INVALID_TICKET_STATUS(5002, "工单状态不允许此操作"),
    
    /** 工单已关闭 */
    TICKET_CLOSED(5003, "工单已关闭"),
    
    /** 工单已存在 */
    TICKET_EXISTS(5004, "工单已存在"),
    
    /** 工单已分配 */
    TICKET_ASSIGNED(5005, "工单已分配"),
    
    /** AI服务响应超时 */
    AI_SERVICE_TIMEOUT(6001, "AI服务响应超时，请稍后重试"),
    
    /** AI服务暂时不可用 */
    AI_SERVICE_UNAVAILABLE(6002, "AI服务暂时不可用，请稍后重试"),
    
    /** 数据库操作失败 */
    DATABASE_ERROR(9001, "数据库操作失败"),
    
    /** 数据已被修改 */
    CONCURRENT_MODIFICATION(9002, "数据已被修改，请刷新后重试"),
    
    /** 系统繁忙 */
    SYSTEM_ERROR(9999, "系统繁忙，请稍后重试");
    
    /** 错误码 */
    private final int code;
    
    /** 错误消息 */
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
