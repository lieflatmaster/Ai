package com.example.aisupportticket.infrastructure.common.util;

import java.util.UUID;

/**
 * 追踪ID工具类
 */
public class TraceIdUtil {
    
    /** 线程本地存储的追踪ID */
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();
    
    /**
     * 生成新的追踪ID
     * 
     * @return 16位追踪ID
     */
    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * 设置追踪ID
     * 
     * @param traceId 追踪ID
     */
    public static void set(String traceId) {
        TRACE_ID.set(traceId);
    }
    
    /**
     * 获取当前追踪ID
     * 
     * @return 追踪ID（可能为null）
     */
    public static String get() {
        return TRACE_ID.get();
    }
    
    /**
     * 清除追踪ID
     */
    public static void clear() {
        TRACE_ID.remove();
    }
    
    /**
     * 获取当前追踪ID，如果不存在则生成新的
     * 
     * @return 追踪ID
     */
    public static String current() {
        String traceId = TRACE_ID.get();
        if (traceId == null) {
            traceId = generate();
            TRACE_ID.set(traceId);
        }
        return traceId;
    }
}
