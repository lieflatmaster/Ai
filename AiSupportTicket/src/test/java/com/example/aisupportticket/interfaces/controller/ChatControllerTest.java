package com.example.aisupportticket.interfaces.controller;

import com.example.aisupportticket.application.service.ChatApplicationService;
import com.example.aisupportticket.infrastructure.common.exception.RateLimitException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AI对话控制器单元测试
 * 
 * <p>测试AI对话控制器的HTTP接口，包括普通对话和限流控制。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatApplicationService chatApplicationService;

    @Test
    @DisplayName("AI对话 - 成功")
    void testChat_Success() throws Exception {
        Map<String, String> request = Map.of("message", "Hello");
        
        when(chatApplicationService.chat("Hello", 1L)).thenReturn("AI response");

        mockMvc.perform(post("/api/chat")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").value("AI response"));
    }

    @Test
    @DisplayName("AI对话 - 限流")
    void testChat_RateLimited() throws Exception {
        Map<String, String> request = Map.of("message", "Hello");
        
        when(chatApplicationService.chat("Hello", 1L))
                .thenThrow(new RateLimitException("请求过于频繁"));

        mockMvc.perform(post("/api/chat")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(429));
    }

    @Test
    @DisplayName("AI对话 - 默认用户ID")
    void testChat_DefaultUserId() throws Exception {
        Map<String, String> request = Map.of("message", "Hello");
        
        when(chatApplicationService.chat("Hello", 1L)).thenReturn("AI response");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
