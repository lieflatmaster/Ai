package com.example.aisupportticket.infrastructure.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 函数调用结果
 *封装AI Function Calling的执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionCallResult {
    
    /** 函数名称 */
    private String functionName;
    
    /** 调用ID */
    private String callId;
    
    /** 执行结果 */
    private Object result;
    
    /** 是否成功 */
    private boolean success;
    
    /** 错误消息（如果失败） */
    private String errorMessage;
}
