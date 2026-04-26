package com.example.aisupportticket.infrastructure.config;

import com.example.aisupportticket.infrastructure.ai.AiContextHolder;
import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

/**
 * Reactor上下文传播配置
 * 配置Reactor的自动上下文传播，确保ThreadLocal中的用户ID
 * 能够在响应式流的不同线程间传递。
 */
@Configuration
public class ReactorContextConfig {

    @PostConstruct
    public void init() {
        Hooks.enableAutomaticContextPropagation();
        
        ContextRegistry.getInstance().registerThreadLocalAccessor(
                new AiContextThreadLocalAccessor()
        );
    }
}
