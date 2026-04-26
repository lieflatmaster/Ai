package com.example.aisupportticket.infrastructure.ai;

import com.example.aisupportticket.application.service.TicketApplicationService;
import com.example.aisupportticket.infrastructure.ai.dto.FunctionCallResult;
import com.example.aisupportticket.infrastructure.common.exception.TicketNotFoundException;
import com.example.aisupportticket.interfaces.dto.response.TicketDetailResponse;
import com.example.aisupportticket.interfaces.dto.response.TicketListResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Function Calling处理器单元测试
 * 
 * <p>测试AI Function Calling的处理逻辑，包括工单相关函数的调用处理。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class FunctionCallHandlerTest {

    @Mock
    private TicketApplicationService ticketApplicationService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private FunctionCallHandler functionCallHandler;

    private TicketDetailResponse testTicketResponse;

    @BeforeEach
    void setUp() {
        functionCallHandler.init();

        testTicketResponse = new TicketDetailResponse(
                1L, "Test Ticket", "Test Description",
                "LOGISTICS", "物流问题", "PENDING", "待处理",
                5, "ORDER123", 0,
                LocalDateTime.now(), LocalDateTime.now(), null
        );
    }

    @Test
    @DisplayName("处理create_ticket函数 - 成功")
    void testHandleCreateTicket_Success() {
        String arguments = "{\"title\":\"Test Ticket\",\"description\":\"Test Description\",\"category\":\"LOGISTICS\",\"orderId\":\"ORDER123\"}";

        when(ticketApplicationService.createTicket(any())).thenReturn(testTicketResponse);

        FunctionCallResult result = functionCallHandler.handle("create_ticket", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
        assertEquals("create_ticket", result.getFunctionName());
        assertEquals("call_123", result.getCallId());
        assertNotNull(result.getResult());
    }

    @Test
    @DisplayName("处理query_ticket函数 - 成功")
    void testHandleQueryTicket_Success() {
        String arguments = "{\"ticketId\":1}";

        when(ticketApplicationService.queryTicket(1L, 1L)).thenReturn(testTicketResponse);

        FunctionCallResult result = functionCallHandler.handle("query_ticket", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
        assertEquals("query_ticket", result.getFunctionName());
    }

    @Test
    @DisplayName("处理query_ticket函数 - 工单不存在")
    void testHandleQueryTicket_NotFound() {
        String arguments = "{\"ticketId\":999}";

        when(ticketApplicationService.queryTicket(999L, 1L))
                .thenThrow(new TicketNotFoundException(999L));

        FunctionCallResult result = functionCallHandler.handle("query_ticket", arguments, 1L, "call_123");

        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    @DisplayName("处理list_tickets函数 - 成功")
    void testHandleListTickets_Success() {
        String arguments = "{}";

        TicketListResponse listResponse = new TicketListResponse(1L, List.of(testTicketResponse));
        when(ticketApplicationService.listTickets(1L, null, 1, 10)).thenReturn(listResponse);

        FunctionCallResult result = functionCallHandler.handle("list_tickets", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
        assertEquals("list_tickets", result.getFunctionName());
    }

    @Test
    @DisplayName("处理list_tickets函数 - 带状态筛选")
    void testHandleListTickets_WithStatus() {
        String arguments = "{\"status\":\"PENDING\",\"page\":1,\"size\":20}";

        TicketListResponse listResponse = new TicketListResponse(1L, List.of(testTicketResponse));
        when(ticketApplicationService.listTickets(1L, "PENDING", 1, 20)).thenReturn(listResponse);

        FunctionCallResult result = functionCallHandler.handle("list_tickets", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("处理close_ticket函数 - 成功")
    void testHandleCloseTicket_Success() {
        String arguments = "{\"ticketId\":1,\"reason\":\"问题已解决\"}";

        doNothing().when(ticketApplicationService).closeTicket(1L, 1L, "问题已解决");

        FunctionCallResult result = functionCallHandler.handle("close_ticket", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
        assertEquals("close_ticket", result.getFunctionName());
    }

    @Test
    @DisplayName("处理escalate_ticket函数 - 成功")
    void testHandleEscalateTicket_Success() {
        String arguments = "{\"ticketId\":1}";

        when(ticketApplicationService.escalateTicket(1L, 1L)).thenReturn(testTicketResponse);

        FunctionCallResult result = functionCallHandler.handle("escalate_ticket", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
        assertEquals("escalate_ticket", result.getFunctionName());
    }

    @Test
    @DisplayName("处理update_ticket函数 - 成功")
    void testHandleUpdateTicket_Success() {
        String arguments = "{\"ticketId\":1,\"description\":\"Updated description\"}";

        doNothing().when(ticketApplicationService).updateTicket(1L, 1L, "Updated description");

        FunctionCallResult result = functionCallHandler.handle("update_ticket", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
        assertEquals("update_ticket", result.getFunctionName());
    }

    @Test
    @DisplayName("处理未知函数")
    void testHandle_UnknownFunction() {
        String arguments = "{}";

        FunctionCallResult result = functionCallHandler.handle("unknown_function", arguments, 1L, "call_123");

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("未知的函数"));
    }

    @Test
    @DisplayName("处理空参数")
    void testHandle_EmptyArguments() {
        String arguments = "";

        when(ticketApplicationService.listTickets(1L, null, 1, 10))
                .thenReturn(new TicketListResponse(0L, List.of()));

        FunctionCallResult result = functionCallHandler.handle("list_tickets", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("处理无效JSON参数")
    void testHandle_InvalidJson() {
        String arguments = "invalid json";

        when(ticketApplicationService.listTickets(1L, null, 1, 10))
                .thenReturn(new TicketListResponse(0L, List.of()));

        FunctionCallResult result = functionCallHandler.handle("list_tickets", arguments, 1L, "call_123");

        assertTrue(result.isSuccess());
    }
}
