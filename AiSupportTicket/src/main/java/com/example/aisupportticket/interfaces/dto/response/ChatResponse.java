package com.example.aisupportticket.interfaces.dto.response;

import com.example.aisupportticket.infrastructure.ai.dto.FunctionCallResult;

/**
 * AI对话响应
 *返回AI对话的响应内容，可能包含函数调用结果
 */
public class ChatResponse {
    
    /** AI响应内容 */
    private String content;
    
    /** 函数调用结果（如果有） */
    private FunctionCallResult functionCall;
    
    public ChatResponse() {}
    
    public ChatResponse(String content, FunctionCallResult functionCall) {
        this.content = content;
        this.functionCall = functionCall;
    }
    
    /**
     * 创建只包含内容的响应
     * 
     * @param content 响应内容
     * @return ChatResponse实例
     */
    public static ChatResponse of(String content) {
        return new ChatResponse(content, null);
    }
    
    /**
     * 创建包含内容和函数调用的响应
     * 
     * @param content 响应内容
     * @param functionCall 函数调用结果
     * @return ChatResponse实例
     */
    public static ChatResponse of(String content, FunctionCallResult functionCall) {
        return new ChatResponse(content, functionCall);
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public FunctionCallResult getFunctionCall() { return functionCall; }
    public void setFunctionCall(FunctionCallResult functionCall) { this.functionCall = functionCall; }
}
