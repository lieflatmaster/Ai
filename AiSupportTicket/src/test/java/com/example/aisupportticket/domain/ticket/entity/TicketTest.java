package com.example.aisupportticket.domain.ticket.entity;

import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.domain.ticket.vo.TicketStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工单实体单元测试
 * 
 * <p>测试工单实体的核心业务逻辑，包括创建、关闭、催办、更新等操作。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
class TicketTest {

    @Test
    @DisplayName("创建工单 - 成功")
    void testCreate_Success() {
        Ticket ticket = Ticket.create(
                1L, "Test Ticket", "Test Description",
                TicketCategory.LOGISTICS, "ORDER1"
        );

        assertNotNull(ticket);
        assertEquals(1L, ticket.getUserId());
        assertEquals("Test Ticket", ticket.getTitle());
        assertEquals("Test Description", ticket.getDescription());
        assertEquals(TicketCategory.LOGISTICS, ticket.getCategory());
        assertEquals(TicketStatus.PENDING, ticket.getStatus());
        assertEquals(5, ticket.getPriority());
        assertEquals("ORDER1", ticket.getOrderId());
        assertEquals(0, ticket.getEscalateCount());
        assertNotNull(ticket.getCreatedAt());
        assertNotNull(ticket.getUpdatedAt());
    }

    @Test
    @DisplayName("关闭工单 - 成功")
    void testClose_Success() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.PROCESSING);

        ticket.close("问题已解决");

        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
        assertNotNull(ticket.getClosedAt());
        assertNotNull(ticket.getUpdatedAt());
    }

    @Test
    @DisplayName("关闭工单 - 已关闭的工单")
    void testClose_AlreadyClosed() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.CLOSED);

        assertThrows(IllegalStateException.class, () -> {
            ticket.close("reason");
        });
    }

    @Test
    @DisplayName("催办工单 - 成功")
    void testEscalate_Success() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.PROCESSING);
        ticket.setPriority(5);

        ticket.escalate();

        assertEquals(4, ticket.getPriority());
        assertEquals(1, ticket.getEscalateCount());
        assertNotNull(ticket.getEscalatedAt());
    }

    @Test
    @DisplayName("催办工单 - 优先级最低为1")
    void testEscalate_MinPriority() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.PROCESSING);
        ticket.setPriority(1);

        ticket.escalate();

        assertEquals(1, ticket.getPriority());
    }

    @Test
    @DisplayName("催办工单 - 状态不允许")
    void testEscalate_InvalidStatus() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.PENDING);

        assertThrows(IllegalStateException.class, () -> {
            ticket.escalate();
        });
    }

    @Test
    @DisplayName("更新描述 - 成功")
    void testUpdateDescription_Success() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.PENDING);

        ticket.updateDescription("New Description");

        assertEquals("New Description", ticket.getDescription());
        assertNotNull(ticket.getUpdatedAt());
    }

    @Test
    @DisplayName("更新描述 - 已关闭的工单")
    void testUpdateDescription_AlreadyClosed() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.CLOSED);

        assertThrows(IllegalStateException.class, () -> {
            ticket.updateDescription("New Description");
        });
    }

    @Test
    @DisplayName("分配工单 - 成功")
    void testAssignTo_Success() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.PENDING);

        ticket.assignTo(2L);

        assertEquals(TicketStatus.PROCESSING, ticket.getStatus());
    }

    @Test
    @DisplayName("分配工单 - 状态不允许")
    void testAssignTo_InvalidStatus() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);
        ticket.setStatus(TicketStatus.PROCESSING);

        assertThrows(IllegalStateException.class, () -> {
            ticket.assignTo(2L);
        });
    }

    @Test
    @DisplayName("检查工单归属 - 成功")
    void testBelongsTo_True() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);

        assertTrue(ticket.belongsTo(1L));
    }

    @Test
    @DisplayName("检查工单归属 - 失败")
    void testBelongsTo_False() {
        Ticket ticket = Ticket.create(1L, "Test", "Desc", TicketCategory.LOGISTICS, null);

        assertFalse(ticket.belongsTo(2L));
    }
}
