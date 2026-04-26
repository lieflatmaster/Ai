package com.example.aisupportticket.infrastructure.common.exception;

/**
 * 业务异常基类
 */
public class BusinessException extends RuntimeException {
    
    /** 错误码 */
    private final int code;
    
    /**
     * 构造业务异常
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 使用错误码枚举构造业务异常
     * 
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
    
    /**
     * 使用错误码枚举和自定义消息构造业务异常
     * 
     * @param errorCode 错误码枚举
     * @param message 自定义错误消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public int getCode() {
        return code;
    }
}
