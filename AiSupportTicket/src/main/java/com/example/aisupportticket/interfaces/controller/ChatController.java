package com.example.aisupportticket.interfaces.controller;

import com.example.aisupportticket.application.service.ChatApplicationService;
import com.example.aisupportticket.infrastructure.ai.dto.AiChunk;
import com.example.aisupportticket.infrastructure.common.response.ApiResponse;
import com.example.aisupportticket.interfaces.dto.request.ChatRequest;
import com.example.aisupportticket.interfaces.dto.response.ChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI对话控制器
 */
@Slf4j
@Tag(name = "AI对话", description = "AI对话接口")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatApplicationService chatApplicationService;
    
    /**
     * AI对话
     * 
     * @param request 对话请求
     * @param userId 用户ID
     * @return AI响应内容
     */
    @Operation(summary = "AI对话", description = "与AI对话，支持Function Calling")
    @PostMapping
    public ApiResponse<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request,
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        
        String content = chatApplicationService.chat(request.getMessage(), userId);
        ChatResponse response = ChatResponse.of(content);
        return ApiResponse.success(response);
    }
    
    /**
     * AI流式对话
     * 
     * @param request 对话请求
     * @param userId 用户ID
     * @return SSE事件流
     */
    @Operation(summary = "AI流式对话", description = "SSE流式响应")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(
            @Valid @RequestBody ChatRequest request,
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        
        log.info("SSE流式对话开始: userId={}, message={}", userId, request.getMessage());
        
        return chatApplicationService.chatStream(request.getMessage(), userId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .event("message")
                        .data(toJson(chunk))
                        .build())
                .doOnSubscribe(s -> log.info("SSE流已订阅"))
                .doOnComplete(() -> log.info("SSE流已完成"))
                .doOnError(e -> log.error("SSE流错误", e));
    }
    
    /**
     * 将AI响应块转换为JSON字符串
     * 
     * @param chunk AI响应块
     * @return JSON字符串
     */
    private String toJson(AiChunk chunk) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(chunk);
        } catch (Exception e) {
            return "{\"content\":\"\",\"done\":true}";
        }
    }
}
