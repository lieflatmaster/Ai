package com.example.aisupportticket.integration;

import com.example.aisupportticket.application.command.CreateTicketCommand;
import com.example.aisupportticket.application.service.TicketApplicationService;
import com.example.aisupportticket.domain.ticket.repository.TicketRepository;
import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.interfaces.dto.response.TicketDetailResponse;
import com.example.aisupportticket.interfaces.dto.response.TicketListResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工单集成测试
 * 
 * <p>测试工单功能的完整流程，包括数据库写入和查询验证。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TicketIntegrationTest {

    @Autowired
    private TicketApplicationService ticketApplicationService;

    @Autowired
    private TicketRepository ticketRepository;

    private static Long createdTicketId;

    @Test
    @Order(1)
    @DisplayName("集成测试 - 创建工单并写入数据库")
    void testCreateTicket_SaveToDatabase() {
        CreateTicketCommand command = new CreateTicketCommand(
                1L,
                "集成测试工单",
                "这是一个集成测试工单描述",
                TicketCategory.LOGISTICS,
                "TEST_ORDER_INTEGRATION_001"
        );

        TicketDetailResponse response = ticketApplicationService.createTicket(command);

        assertNotNull(response);
        assertNotNull(response.getTicketId());
        assertEquals("集成测试工单", response.getTitle());
        assertEquals("PENDING", response.getStatus());
        assertEquals("LOGISTICS", response.getCategory());
        
        createdTicketId = response.getTicketId();
        
        System.out.println("========================================");
        System.out.println("✅ 工单创建成功并写入数据库！");
        System.out.println("工单ID: " + createdTicketId);
        System.out.println("工单标题: " + response.getTitle());
        System.out.println("工单状态: " + response.getStatus());
        System.out.println("工单分类: " + response.getCategory());
        System.out.println("========================================");
    }

    @Test
    @Order(2)
    @DisplayName("集成测试 - 从数据库查询刚创建的工单")
    void testQueryTicket_FromDatabase() {
        assertNotNull(createdTicketId, "前置测试未创建工单");

        TicketDetailResponse response = ticketApplicationService.queryTicket(createdTicketId, 1L);

        assertNotNull(response);
        assertEquals(createdTicketId, response.getTicketId());
        assertEquals("集成测试工单", response.getTitle());
        
        System.out.println("========================================");
        System.out.println("✅ 从数据库查询工单成功！");
        System.out.println("工单ID: " + response.getTicketId());
        System.out.println("工单标题: " + response.getTitle());
        System.out.println("工单状态: " + response.getStatus());
        System.out.println("========================================");
    }

    @Test
    @Order(3)
    @DisplayName("集成测试 - 查询工单列表")
    void testListTickets_FromDatabase() {
        TicketListResponse response = ticketApplicationService.listTickets(1L, null, 1, 10);

        assertNotNull(response);
        assertNotNull(response.getList());
        assertTrue(response.getTotal() >= 1, "应该至少有一个工单");
        
        System.out.println("========================================");
        System.out.println("✅ 工单列表查询成功！");
        System.out.println("用户1的工单总数: " + response.getTotal());
        response.getList().forEach(ticket -> {
            System.out.println("  - 工单ID: " + ticket.getTicketId() + 
                             ", 标题: " + ticket.getTitle() + 
                             ", 状态: " + ticket.getStatus());
        });
        System.out.println("========================================");
    }

    @Test
    @Order(4)
    @DisplayName("集成测试 - 更新工单描述")
    void testUpdateTicket_SaveToDatabase() {
        assertNotNull(createdTicketId, "前置测试未创建工单");

        String newDescription = "更新后的描述 - " + System.currentTimeMillis();
        ticketApplicationService.updateTicket(createdTicketId, 1L, newDescription);

        TicketDetailResponse response = ticketApplicationService.queryTicket(createdTicketId, 1L);
        assertNotNull(response);
        
        System.out.println("========================================");
        System.out.println("✅ 工单更新成功！");
        System.out.println("工单ID: " + createdTicketId);
        System.out.println("========================================");
    }

    @Test
    @Order(5)
    @DisplayName("集成测试 - 关闭工单")
    void testCloseTicket_SaveToDatabase() {
        assertNotNull(createdTicketId, "前置测试未创建工单");

        ticketApplicationService.closeTicket(createdTicketId, 1L, "集成测试关闭工单");

        TicketDetailResponse response = ticketApplicationService.queryTicket(createdTicketId, 1L);
        assertNotNull(response);
        assertEquals("CLOSED", response.getStatus());
        
        System.out.println("========================================");
        System.out.println("✅ 工单关闭成功！");
        System.out.println("工单ID: " + createdTicketId);
        System.out.println("最终状态: " + response.getStatus());
        System.out.println("========================================");
    }

    @Test
    @Order(6)
    @DisplayName("集成测试 - 直接查询数据库验证所有数据")
    void testDirectDatabaseQuery() {
        var tickets = ticketRepository.findByUserId(1L);
        
        assertNotNull(tickets);
        assertTrue(tickets.size() >= 1, "数据库中应该有工单数据");
        
        System.out.println("========================================");
        System.out.println("✅ 直接查询数据库验证成功！");
        System.out.println("数据库中用户1的工单总数: " + tickets.size());
        System.out.println("工单列表:");
        tickets.forEach(ticket -> {
            System.out.println("  - ID: " + ticket.getId().getValue() + 
                             ", 标题: " + ticket.getTitle() + 
                             ", 状态: " + ticket.getStatus() +
                             ", 分类: " + ticket.getCategory() +
                             ", 订单号: " + ticket.getOrderId());
        });
        System.out.println("========================================");
    }
}
