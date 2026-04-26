package com.example.aisupportticket.integration;

import com.example.aisupportticket.application.service.ChatApplicationService;
import com.example.aisupportticket.infrastructure.ai.dto.AiChunk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI对话集成测试
 * 
 * <p>测试AI对话功能的完整流程，包括普通对话、流式对话和Function Calling。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("local")
class AiChatIntegrationTest {

    @Autowired
    private ChatApplicationService chatApplicationService;

    @Test
    @DisplayName("AI对话集成测试 - 真实调用AI服务")
    void testChat_RealAiService() {
        String message = "你好，请简单介绍一下你自己";
        Long userId = 1L;

        System.out.println("========================================");
        System.out.println("开始AI对话集成测试...");
        System.out.println("用户ID: " + userId);
        System.out.println("消息: " + message);
        System.out.println("========================================");

        String response = chatApplicationService.chat(message, userId);

        assertNotNull(response, "AI响应不应为空");
        assertFalse(response.isEmpty(), "AI响应不应为空字符串");
        
        System.out.println("========================================");
        System.out.println("✅ AI对话集成测试通过！");
        System.out.println("AI响应: " + response);
        System.out.println("响应长度: " + response.length() + " 字符");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("AI对话集成测试 - 工单相关查询")
    void testChat_TicketQuery() {
        String message = "帮我查询一下我的工单列表";
        Long userId = 1L;

        System.out.println("========================================");
        System.out.println("开始AI工单查询测试...");
        System.out.println("消息: " + message);
        System.out.println("========================================");

        String response = chatApplicationService.chat(message, userId);

        assertNotNull(response);
        
        System.out.println("========================================");
        System.out.println("✅ AI工单查询测试通过！");
        System.out.println("AI响应: " + response);
        System.out.println("========================================");
    }

    @Test
    @DisplayName("SSE流式响应测试 - 流式对话功能")
    void testChatStream_RealAiService() {
        String message = "请用一句话介绍什么是工单系统";
        Long userId = 1L;

        System.out.println("========================================");
        System.out.println("开始SSE流式响应测试...");
        System.out.println("消息: " + message);
        System.out.println("========================================");

        Flux<AiChunk> stream = chatApplicationService.chatStream(message, userId);
        
        List<AiChunk> chunks = stream.collectList().block();
        
        assertNotNull(chunks, "流式响应不应为空");
        assertFalse(chunks.isEmpty(), "应该有至少一个响应块");
        
        StringBuilder fullResponse = new StringBuilder();
        System.out.println("流式响应块:");
        for (int i = 0; i < chunks.size(); i++) {
            AiChunk chunk = chunks.get(i);
            fullResponse.append(chunk.getContent());
            System.out.println("  块 " + (i + 1) + ": " + chunk.getContent() + " (完成: " + chunk.isDone() + ")");
        }
        
        System.out.println("========================================");
        System.out.println("✅ SSE流式响应测试通过！");
        System.out.println("完整响应: " + fullResponse.toString());
        System.out.println("响应块数量: " + chunks.size());
        System.out.println("========================================");
    }

    @Test
    @DisplayName("AI对话集成测试 - 创建工单意图")
    void testChat_CreateTicketIntent() {
        String message = "我想创建一个工单，问题是商品没有发货，订单号是ORDER123456";
        Long userId = 1L;

        System.out.println("========================================");
        System.out.println("开始AI创建工单意图测试...");
        System.out.println("消息: " + message);
        System.out.println("========================================");

        String response = chatApplicationService.chat(message, userId);

        assertNotNull(response);
        
        System.out.println("========================================");
        System.out.println("✅ AI创建工单意图测试通过！");
        System.out.println("AI响应: " + response);
        System.out.println("========================================");
    }

    @Test
    @DisplayName("AI对话集成测试 - 多轮对话")
    void testChat_MultiTurn() {
        Long userId = 999L;

        System.out.println("========================================");
        System.out.println("开始多轮对话测试...");
        
        String response1 = chatApplicationService.chat("你好", userId);
        System.out.println("第1轮 - 用户: 你好");
        System.out.println("第1轮 - AI: " + response1);
        assertNotNull(response1);
        
        String response2 = chatApplicationService.chat("我想查询工单", userId);
        System.out.println("第2轮 - 用户: 我想查询工单");
        System.out.println("第2轮 - AI: " + response2);
        assertNotNull(response2);
        
        String response3 = chatApplicationService.chat("工单ID是1", userId);
        System.out.println("第3轮 - 用户: 工单ID是1");
        System.out.println("第3轮 - AI: " + response3);
        assertNotNull(response3);
        
        System.out.println("========================================");
        System.out.println("✅ 多轮对话测试通过！");
        System.out.println("========================================");
    }
}
