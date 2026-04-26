package com.example.aisupportticket.infrastructure.ai;

import com.example.aisupportticket.application.command.*;
import com.example.aisupportticket.application.service.TicketApplicationService;
import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.infrastructure.ai.dto.FunctionCallResult;
import com.example.aisupportticket.infrastructure.common.exception.BusinessException;
import com.example.aisupportticket.infrastructure.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Function Calling处理器
 * 处理AI返回的函数调用请求，将AI意图转换为实际的业务操作。
 * 支持工单相关的所有操作函数。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FunctionCallHandler {
    
    private final TicketApplicationService ticketApplicationService;
    private final ObjectMapper objectMapper;
    
    /** 函数处理器映射表 */
    private final Map<String, Function<FunctionCallContext, FunctionCallResult>> handlers = new ConcurrentHashMap<>();
    
    /**
     * 初始化函数处理器
     */
    @PostConstruct
    public void init() {
        handlers.put("create_ticket", this::handleCreateTicket);
        handlers.put("query_ticket", this::handleQueryTicket);
        handlers.put("list_tickets", this::handleListTickets);
        handlers.put("close_ticket", this::handleCloseTicket);
        handlers.put("escalate_ticket", this::handleEscalateTicket);
        handlers.put("update_ticket", this::handleUpdateTicket);
    }
    
    /**
     * 处理函数调用
     * 
     * @param functionName 函数名称
     * @param argumentsJson 参数JSON字符串
     * @param userId 用户ID
     * @param callId 调用ID
     * @return 函数调用结果
     */
    public FunctionCallResult handle(String functionName, String argumentsJson, Long userId, String callId) {
        Function<FunctionCallContext, FunctionCallResult> handler = handlers.get(functionName);
        if (handler == null) {
            log.warn("未知的函数调用: {}", functionName);
            return FunctionCallResult.builder()
                    .functionName(functionName)
                    .callId(callId)
                    .success(false)
                    .errorMessage("未知的函数: " + functionName)
                    .build();
        }
        
        Map<String, Object> args = parseArguments(argumentsJson);
        FunctionCallContext context = new FunctionCallContext(args, userId, callId);
        
        try {
            return handler.apply(context);
        } catch (BusinessException e) {
            log.warn("业务异常: {}", e.getMessage());
            return FunctionCallResult.builder()
                    .functionName(functionName)
                    .callId(callId)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("函数调用异常: {}", functionName, e);
            return FunctionCallResult.builder()
                    .functionName(functionName)
                    .callId(callId)
                    .success(false)
                    .errorMessage("系统错误: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 处理创建工单函数
     */
    private FunctionCallResult handleCreateTicket(FunctionCallContext ctx) {
        Map<String, Object> args = ctx.args();
        String title = getString(args, "title");
        String description = getString(args, "description");
        String categoryStr = getString(args, "category");
        String orderId = getString(args, "orderId");
        
        TicketCategory category = categoryStr != null ? TicketCategory.valueOf(categoryStr) : TicketCategory.OTHER;
        
        CreateTicketCommand command = new CreateTicketCommand(
                ctx.userId(), title, description, category, orderId
        );
        
        var result = ticketApplicationService.createTicket(command);
        
        return FunctionCallResult.builder()
                .functionName("create_ticket")
                .callId(ctx.callId())
                .success(true)
                .result(result)
                .build();
    }
    
    /**
     * 处理查询工单函数
     */
    private FunctionCallResult handleQueryTicket(FunctionCallContext ctx) {
        Map<String, Object> args = ctx.args();
        Long ticketId = getLong(args, "ticketId");
        
        var result = ticketApplicationService.queryTicket(ticketId, ctx.userId());
        
        return FunctionCallResult.builder()
                .functionName("query_ticket")
                .callId(ctx.callId())
                .success(true)
                .result(result)
                .build();
    }
    
    /**
     * 处理查询工单列表函数
     */
    private FunctionCallResult handleListTickets(FunctionCallContext ctx) {
        Map<String, Object> args = ctx.args();
        String status = getString(args, "status");
        Integer page = getInteger(args, "page", 1);
        Integer size = getInteger(args, "size", 10);
        
        var result = ticketApplicationService.listTickets(ctx.userId(), status, page, size);
        
        return FunctionCallResult.builder()
                .functionName("list_tickets")
                .callId(ctx.callId())
                .success(true)
                .result(result)
                .build();
    }
    
    /**
     * 处理关闭工单函数
     */
    private FunctionCallResult handleCloseTicket(FunctionCallContext ctx) {
        Map<String, Object> args = ctx.args();
        Long ticketId = getLong(args, "ticketId");
        String reason = getString(args, "reason");
        
        ticketApplicationService.closeTicket(ticketId, ctx.userId(), reason);
        
        Map<String, Object> result = Map.of(
                "ticketId", ticketId,
                "status", "CLOSED",
                "message", "工单已关闭"
        );
        
        return FunctionCallResult.builder()
                .functionName("close_ticket")
                .callId(ctx.callId())
                .success(true)
                .result(result)
                .build();
    }
    
    /**
     * 处理催办工单函数
     */
    private FunctionCallResult handleEscalateTicket(FunctionCallContext ctx) {
        Map<String, Object> args = ctx.args();
        Long ticketId = getLong(args, "ticketId");
        
        var result = ticketApplicationService.escalateTicket(ticketId, ctx.userId());
        
        return FunctionCallResult.builder()
                .functionName("escalate_ticket")
                .callId(ctx.callId())
                .success(true)
                .result(result)
                .build();
    }
    
    /**
     * 处理更新工单函数
     */
    private FunctionCallResult handleUpdateTicket(FunctionCallContext ctx) {
        Map<String, Object> args = ctx.args();
        Long ticketId = getLong(args, "ticketId");
        String description = getString(args, "description");
        
        ticketApplicationService.updateTicket(ticketId, ctx.userId(), description);
        
        Map<String, Object> result = Map.of(
                "ticketId", ticketId,
                "message", "工单已更新"
        );
        
        return FunctionCallResult.builder()
                .functionName("update_ticket")
                .callId(ctx.callId())
                .success(true)
                .result(result)
                .build();
    }
    
    /**
     * 解析参数JSON
     */
    private Map<String, Object> parseArguments(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.warn("解析参数失败: {}", json, e);
            return Map.of();
        }
    }
    
    private String getString(Map<String, Object> args, String key) {
        Object value = args.get(key);
        return value != null ? value.toString() : null;
    }
    
    private Long getLong(Map<String, Object> args, String key) {
        Object value = args.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Integer getInteger(Map<String, Object> args, String key, Integer defaultValue) {
        Object value = args.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 函数调用上下文
     */
    private record FunctionCallContext(Map<String, Object> args, Long userId, String callId) {}
}
