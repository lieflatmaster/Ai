package com.example.aisupportticket.infrastructure.ai;

import lombok.Getter;
import reactor.util.context.Context;

import java.util.function.Function;

/**
 * AI上下文持有者
 *使用ThreadLocal存储当前请求的用户上下文，供Function调用时使用
 */
public class AiContextHolder {
    
    private static final ThreadLocal<AiContext> CONTEXT = new ThreadLocal<>();
    
    public static final String USER_ID_KEY = "userId";
    
    /**
     * 设置当前上下文
     * 
     * @param userId 用户ID
     */
    public static void setContext(Long userId) {
        CONTEXT.set(new AiContext(userId));
    }
    
    /**
     * 获取当前上下文
     * 
     * @return AI上下文
     */
    public static AiContext getContext() {
        return CONTEXT.get();
    }
    
    /**
     * 获取当前用户ID
     * 
     * <p>优先从ThreadLocal获取，如果没有则尝试从Reactor Context获取</p>
     * 
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        AiContext context = CONTEXT.get();
        return context != null ? context.getUserId() : null;
    }
    
    /**
     * 从Reactor Context获取用户ID
     * 
     * @param ctx Reactor Context
     * @return 用户ID
     */
    public static Long getUserIdFromContext(Context ctx) {
        return ctx.getOrDefault(USER_ID_KEY, null);
    }
    
    /**
     * 清除当前上下文
     */
    public static void clear() {
        CONTEXT.remove();
    }
    
    /**
     * 设置用户ID到当前线程（供响应式流中的Tool调用使用）
     * 
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        CONTEXT.set(new AiContext(userId));
    }
    
    /**
     * AI上下文
     */
    @Getter
    public static class AiContext {
        private final Long userId;
        
        public AiContext(Long userId) {
            this.userId = userId;
        }
    }
}
