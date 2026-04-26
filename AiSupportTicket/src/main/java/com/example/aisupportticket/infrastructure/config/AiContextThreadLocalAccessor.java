package com.example.aisupportticket.infrastructure.config;

import com.example.aisupportticket.infrastructure.ai.AiContextHolder;
import io.micrometer.context.ThreadLocalAccessor;
import org.springframework.lang.Nullable;

/**
 * AI上下文的ThreadLocal访问器
 */
public class AiContextThreadLocalAccessor implements ThreadLocalAccessor<Long> {

    public static final String KEY = "aiUserId";

    @Override
    public Object key() {
        return KEY;
    }

    @Override
    @Nullable
    public Long getValue() {
        return AiContextHolder.getCurrentUserId();
    }

    @Override
    public void setValue(Long value) {
        AiContextHolder.setContext(value);
    }

    @Override
    public void reset() {
        AiContextHolder.clear();
    }

    @Override
    public void setValue() {
        AiContextHolder.clear();
    }
}
