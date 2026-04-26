package com.example.aisupportticket.application.service;

import com.example.aisupportticket.application.command.CreateTicketCommand;
import com.example.aisupportticket.domain.ticket.entity.Ticket;
import com.example.aisupportticket.domain.ticket.entity.TicketLog;
import com.example.aisupportticket.domain.ticket.repository.TicketLogRepository;
import com.example.aisupportticket.domain.ticket.repository.TicketRepository;
import com.example.aisupportticket.domain.ticket.service.TicketDomainService;
import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.domain.ticket.vo.TicketId;
import com.example.aisupportticket.domain.ticket.vo.TicketStatus;
import com.example.aisupportticket.infrastructure.common.exception.TicketNotFoundException;
import com.example.aisupportticket.infrastructure.redis.DuplicateSubmitPreventer;
import com.example.aisupportticket.interfaces.dto.response.TicketDetailResponse;
import com.example.aisupportticket.interfaces.dto.response.TicketListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 工单应用服务单元测试
 * 
 * <p>测试工单应用服务的核心业务逻辑，包括工单的创建、查询、更新、关闭和催办等功能。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class TicketApplicationServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketLogRepository ticketLogRepository;

    @Mock
    private TicketDomainService ticketDomainService;

    @Mock
    private DuplicateSubmitPreventer duplicateSubmitPreventer;

    @InjectMocks
    private TicketApplicationService ticketApplicationService;

    private Ticket testTicket;
    private CreateTicketCommand createCommand;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setId(new TicketId(1L));
        testTicket.setUserId(1L);
        testTicket.setTitle("Test Ticket");
        testTicket.setDescription("Test Description");
        testTicket.setCategory(TicketCategory.LOGISTICS);
        testTicket.setStatus(TicketStatus.PENDING);
        testTicket.setPriority(5);
        testTicket.setOrderId("ORDER123");
        testTicket.setEscalateCount(0);

        createCommand = new CreateTicketCommand(
                1L, "Test Ticket", "Test Description",
                TicketCategory.LOGISTICS, "ORDER123"
        );
    }

    @Test
    @DisplayName("创建工单 - 成功")
    void testCreateTicket_Success() {
        when(duplicateSubmitPreventer.tryAcquire(anyString(), anyLong())).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            ticket.setId(new TicketId(1L));
            return ticket;
        });
        when(ticketLogRepository.save(any(TicketLog.class))).thenReturn(null);

        TicketDetailResponse response = ticketApplicationService.createTicket(createCommand);

        assertNotNull(response);
        assertEquals("Test Ticket", response.getTitle());
        assertEquals("PENDING", response.getStatus());
        verify(ticketRepository).save(any(Ticket.class));
        verify(ticketLogRepository).save(any(TicketLog.class));
    }

    @Test
    @DisplayName("创建工单 - 防重复提交")
    void testCreateTicket_DuplicateSubmit() {
        when(duplicateSubmitPreventer.tryAcquire(anyString(), anyLong())).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> {
            ticketApplicationService.createTicket(createCommand);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("查询工单 - 成功")
    void testQueryTicket_Success() {
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.of(testTicket));

        TicketDetailResponse response = ticketApplicationService.queryTicket(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getTicketId());
        assertEquals("Test Ticket", response.getTitle());
    }

    @Test
    @DisplayName("查询工单 - 工单不存在")
    void testQueryTicket_NotFound() {
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> {
            ticketApplicationService.queryTicket(999L, 1L);
        });
    }

    @Test
    @DisplayName("查询工单列表 - 成功")
    void testListTickets_Success() {
        Page<Ticket> ticketPage = new PageImpl<>(List.of(testTicket));
        when(ticketRepository.findByUserId(eq(1L), isNull(), eq(1), eq(10)))
                .thenReturn(ticketPage);

        TicketListResponse response = ticketApplicationService.listTickets(1L, null, 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getTotal());
        assertEquals(1, response.getList().size());
    }

    @Test
    @DisplayName("查询工单列表 - 按状态筛选")
    void testListTickets_WithStatus() {
        Page<Ticket> ticketPage = new PageImpl<>(List.of(testTicket));
        when(ticketRepository.findByUserId(eq(1L), eq(TicketStatus.PENDING), eq(1), eq(10)))
                .thenReturn(ticketPage);

        TicketListResponse response = ticketApplicationService.listTickets(1L, "PENDING", 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getTotal());
    }

    @Test
    @DisplayName("关闭工单 - 成功")
    void testCloseTicket_Success() {
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketLogRepository.save(any(TicketLog.class))).thenReturn(null);

        assertDoesNotThrow(() -> {
            ticketApplicationService.closeTicket(1L, 1L, "问题已解决");
        });

        verify(ticketRepository).save(any(Ticket.class));
        verify(ticketLogRepository).save(any(TicketLog.class));
    }

    @Test
    @DisplayName("关闭工单 - 工单不存在")
    void testCloseTicket_NotFound() {
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> {
            ticketApplicationService.closeTicket(999L, 1L, "reason");
        });
    }

    @Test
    @DisplayName("关闭工单 - 已关闭的工单")
    void testCloseTicket_AlreadyClosed() {
        testTicket.setStatus(TicketStatus.CLOSED);
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.of(testTicket));

        assertThrows(IllegalStateException.class, () -> {
            ticketApplicationService.closeTicket(1L, 1L, "reason");
        });
    }

    @Test
    @DisplayName("催办工单 - 成功")
    void testEscalateTicket_Success() {
        testTicket.setStatus(TicketStatus.PROCESSING);
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketLogRepository.save(any(TicketLog.class))).thenReturn(null);

        TicketDetailResponse response = ticketApplicationService.escalateTicket(1L, 1L);

        assertNotNull(response);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    @DisplayName("催办工单 - 状态不允许")
    void testEscalateTicket_InvalidStatus() {
        testTicket.setStatus(TicketStatus.PENDING);
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.of(testTicket));

        assertThrows(IllegalStateException.class, () -> {
            ticketApplicationService.escalateTicket(1L, 1L);
        });
    }

    @Test
    @DisplayName("更新工单 - 成功")
    void testUpdateTicket_Success() {
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(ticketLogRepository.save(any(TicketLog.class))).thenReturn(null);

        assertDoesNotThrow(() -> {
            ticketApplicationService.updateTicket(1L, 1L, "Updated description");
        });

        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    @DisplayName("更新工单 - 已关闭的工单")
    void testUpdateTicket_AlreadyClosed() {
        testTicket.setStatus(TicketStatus.CLOSED);
        when(ticketRepository.findByIdAndUserId(any(TicketId.class), eq(1L)))
                .thenReturn(Optional.of(testTicket));

        assertThrows(IllegalStateException.class, () -> {
            ticketApplicationService.updateTicket(1L, 1L, "new description");
        });
    }
}
