package com.example.aisupportticket.infrastructure.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 统一API响应类
 *所有API接口的统一响应格式，包含状态码、消息、数据和追踪信息
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /** 状态码 */
    private int code;
    
    /** 响应消息 */
    private String message;
    
    /** 响应数据 */
    private T data;
    
    /** 响应时间戳 */
    private LocalDateTime timestamp;
    
    /** 追踪ID */
    private String traceId;
    
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
        this.traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }
    
    /**
     * 创建成功响应（带自定义消息）
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    
    /**
     * 创建错误响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
    
    /**
     * 创建错误响应（带数据）
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param data 附加数据
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
