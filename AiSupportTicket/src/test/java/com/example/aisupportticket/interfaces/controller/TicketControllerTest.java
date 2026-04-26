package com.example.aisupportticket.interfaces.controller;

import com.example.aisupportticket.application.command.CreateTicketCommand;
import com.example.aisupportticket.application.service.TicketApplicationService;
import com.example.aisupportticket.infrastructure.common.exception.TicketNotFoundException;
import com.example.aisupportticket.interfaces.dto.request.CloseTicketRequest;
import com.example.aisupportticket.interfaces.dto.request.CreateTicketRequest;
import com.example.aisupportticket.interfaces.dto.request.UpdateTicketRequest;
import com.example.aisupportticket.interfaces.dto.response.TicketDetailResponse;
import com.example.aisupportticket.interfaces.dto.response.TicketListResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 工单控制器单元测试
 * 
 * <p>测试工单控制器的HTTP接口，包括创建、查询、更新、关闭和催办工单等操作。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketApplicationService ticketApplicationService;

    @Test
    @DisplayName("创建工单 - 成功")
    void testCreateTicket_Success() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setTitle("Test Ticket");
        request.setDescription("Test Description");
        request.setCategory("LOGISTICS");
        request.setOrderId("ORDER123");

        TicketDetailResponse response = new TicketDetailResponse(
                1L, "Test Ticket", "Test Description",
                "LOGISTICS", "物流问题", "PENDING", "待处理",
                5, "ORDER123", 0,
                LocalDateTime.now(), LocalDateTime.now(), null
        );

        when(ticketApplicationService.createTicket(any(CreateTicketCommand.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/tickets")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("Test Ticket"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("创建工单 - 参数校验失败")
    void testCreateTicket_ValidationFailed() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setDescription("Test Description");

        mockMvc.perform(post("/api/tickets")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("查询工单 - 成功")
    void testGetTicket_Success() throws Exception {
        TicketDetailResponse response = new TicketDetailResponse(
                1L, "Test Ticket", "Test Description",
                "LOGISTICS", "物流问题", "PENDING", "待处理",
                5, "ORDER123", 0,
                LocalDateTime.now(), LocalDateTime.now(), null
        );

        when(ticketApplicationService.queryTicket(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/api/tickets/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.ticketId").value(1));
    }

    @Test
    @DisplayName("查询工单 - 工单不存在")
    void testGetTicket_NotFound() throws Exception {
        when(ticketApplicationService.queryTicket(999L, 1L))
                .thenThrow(new TicketNotFoundException(999L));

        mockMvc.perform(get("/api/tickets/999")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001));
    }

    @Test
    @DisplayName("查询工单列表 - 成功")
    void testListTickets_Success() throws Exception {
        TicketDetailResponse ticket = new TicketDetailResponse(
                1L, "Test Ticket", "Test Description",
                "LOGISTICS", "物流问题", "PENDING", "待处理",
                5, "ORDER123", 0,
                LocalDateTime.now(), LocalDateTime.now(), null
        );

        TicketListResponse response = new TicketListResponse(1L, List.of(ticket));

        when(ticketApplicationService.listTickets(1L, null, 1, 10))
                .thenReturn(response);

        mockMvc.perform(get("/api/tickets")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("更新工单 - 成功")
    void testUpdateTicket_Success() throws Exception {
        UpdateTicketRequest request = new UpdateTicketRequest();
        request.setDescription("Updated description");

        doNothing().when(ticketApplicationService).updateTicket(1L, 1L, "Updated description");

        mockMvc.perform(put("/api/tickets/1")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("关闭工单 - 成功")
    void testCloseTicket_Success() throws Exception {
        CloseTicketRequest request = new CloseTicketRequest();
        request.setReason("问题已解决");

        doNothing().when(ticketApplicationService).closeTicket(1L, 1L, "问题已解决");

        mockMvc.perform(post("/api/tickets/1/close")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("催办工单 - 成功")
    void testEscalateTicket_Success() throws Exception {
        TicketDetailResponse response = new TicketDetailResponse(
                1L, "Test Ticket", "Test Description",
                "LOGISTICS", "物流问题", "PROCESSING", "处理中",
                4, "ORDER123", 1,
                LocalDateTime.now(), LocalDateTime.now(), null
        );

        when(ticketApplicationService.escalateTicket(1L, 1L)).thenReturn(response);

        mockMvc.perform(post("/api/tickets/1/escalate")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.priority").value(4));
    }
}
