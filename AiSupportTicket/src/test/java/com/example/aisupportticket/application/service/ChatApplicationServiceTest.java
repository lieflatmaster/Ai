package com.example.aisupportticket.application.service;

import com.example.aisupportticket.infrastructure.ai.AiServiceAdapter;
import com.example.aisupportticket.infrastructure.ai.dto.AiChunk;
import com.example.aisupportticket.infrastructure.common.exception.RateLimitException;
import com.example.aisupportticket.infrastructure.redis.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AI对话应用服务单元测试
 * 
 * <p>测试AI对话应用服务的核心功能，包括普通对话、流式对话和限流控制。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class ChatApplicationServiceTest {

    @Mock
    private AiServiceAdapter aiServiceAdapter;

    @Mock
    private RateLimiter rateLimiter;

    @InjectMocks
    private ChatApplicationService chatApplicationService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("AI对话 - 成功")
    void testChat_Success() {
        when(rateLimiter.allowRequest(anyString(), anyInt(), anyInt())).thenReturn(true);
        when(aiServiceAdapter.chat(anyString(), anyLong())).thenReturn("AI response");

        String response = chatApplicationService.chat("Hello", 1L);

        assertEquals("AI response", response);
        verify(rateLimiter).allowRequest("chat:user:1", 100, 60);
        verify(aiServiceAdapter).chat("Hello", 1L);
    }

    @Test
    @DisplayName("AI对话 - 限流")
    void testChat_RateLimited() {
        when(rateLimiter.allowRequest(anyString(), anyInt(), anyInt())).thenReturn(false);

        assertThrows(RateLimitException.class, () -> {
            chatApplicationService.chat("Hello", 1L);
        });

        verify(aiServiceAdapter, never()).chat(anyString(), anyLong());
    }

    @Test
    @DisplayName("AI流式对话 - 成功")
    void testChatStream_Success() {
        when(rateLimiter.allowRequest(anyString(), anyInt(), anyInt())).thenReturn(true);
        AiChunk chunk1 = AiChunk.of("Hello", false);
        AiChunk chunk2 = AiChunk.of(" World", true);
        when(aiServiceAdapter.chatStream(anyString(), anyLong()))
                .thenReturn(Flux.just(chunk1, chunk2));

        Flux<AiChunk> result = chatApplicationService.chatStream("Hello", 1L);

        List<AiChunk> chunks = result.collectList().block();
        
        assertNotNull(chunks);
        assertEquals(2, chunks.size());
        assertEquals("Hello", chunks.get(0).getContent());
        assertEquals(" World", chunks.get(1).getContent());

        verify(rateLimiter).allowRequest("chat:user:1", 100, 60);
    }

    @Test
    @DisplayName("AI流式对话 - 限流")
    void testChatStream_RateLimited() {
        when(rateLimiter.allowRequest(anyString(), anyInt(), anyInt())).thenReturn(false);

        assertThrows(RateLimitException.class, () -> {
            chatApplicationService.chatStream("Hello", 1L);
        });

        verify(aiServiceAdapter, never()).chatStream(anyString(), anyLong());
    }
}
