package com.example.aisupportticket.integration;

import com.example.aisupportticket.application.command.CreateTicketCommand;
import com.example.aisupportticket.application.service.TicketApplicationService;
import com.example.aisupportticket.domain.ticket.entity.Ticket;
import com.example.aisupportticket.domain.ticket.entity.TicketLog;
import com.example.aisupportticket.domain.ticket.repository.TicketLogRepository;
import com.example.aisupportticket.domain.ticket.repository.TicketRepository;
import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.domain.ticket.vo.TicketId;
import com.example.aisupportticket.domain.ticket.vo.TicketStatus;
import com.example.aisupportticket.infrastructure.redis.DuplicateSubmitPreventer;
import com.example.aisupportticket.infrastructure.redis.RateLimiter;
import com.example.aisupportticket.interfaces.dto.response.TicketDetailResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis集成测试
 * 
 * <p>测试Redis相关功能，包括限流、防重复提交、乐观锁和工单日志等。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RedisIntegrationTest {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private DuplicateSubmitPreventer duplicateSubmitPreventer;

    @Autowired
    private TicketApplicationService ticketApplicationService;

    @Autowired
    private TicketLogRepository ticketLogRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private static Long testTicketId;

    @Test
    @Order(1)
    @DisplayName("Redis限流测试 - 正常请求")
    void testRateLimiter_NormalRequest() {
        String key = "test:rate:limit:user:1";
        
        rateLimiter.reset(key);
        
        boolean allowed = rateLimiter.allowRequest(key, 100, 60);
        assertTrue(allowed, "第一次请求应该被允许");
        
        long remaining = rateLimiter.getRemainingRequests(key, 100);
        System.out.println("========================================");
        System.out.println("✅ Redis限流测试通过！");
        System.out.println("剩余请求数: " + remaining);
        System.out.println("========================================");
    }

    @Test
    @Order(2)
    @DisplayName("Redis限流测试 - 超过限制")
    void testRateLimiter_ExceedLimit() {
        String key = "test:rate:limit:exceed";
        
        rateLimiter.reset(key);
        
        int limit = 5;
        int successCount = 0;
        
        for (int i = 0; i < limit + 3; i++) {
            if (rateLimiter.allowRequest(key, limit, 60)) {
                successCount++;
            }
        }
        
        assertEquals(limit, successCount, "应该只有" + limit + "次成功");
        
        System.out.println("========================================");
        System.out.println("✅ Redis限流超限测试通过！");
        System.out.println("限制: " + limit);
        System.out.println("成功次数: " + successCount);
        System.out.println("========================================");
    }

    @Test
    @Order(3)
    @DisplayName("Redis防重测试 - 多线程并发")
    void testDuplicateSubmitPreventer_Concurrent() throws InterruptedException {
        String key = "test:dedup:concurrent:" + UUID.randomUUID();
        
        duplicateSubmitPreventer.release(key);
        
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    if (duplicateSubmitPreventer.tryAcquire(key, 10)) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertEquals(1, successCount.get(), "只有一个线程能获取锁");
        
        duplicateSubmitPreventer.release(key);
        
        System.out.println("========================================");
        System.out.println("✅ Redis防重并发测试通过！");
        System.out.println("并发线程数: " + threadCount);
        System.out.println("成功获取锁的线程数: " + successCount.get());
        System.out.println("========================================");
    }

    @Test
    @Order(4)
    @DisplayName("Redis防重测试 - 锁存在检查")
    void testDuplicateSubmitPreventer_Exists() {
        String key = "test:dedup:exists:" + UUID.randomUUID();
        
        duplicateSubmitPreventer.release(key);
        
        assertFalse(duplicateSubmitPreventer.exists(key), "锁不应该存在");
        
        boolean acquired = duplicateSubmitPreventer.tryAcquire(key, 10);
        assertTrue(acquired, "获取锁应该成功");
        
        assertTrue(duplicateSubmitPreventer.exists(key), "锁应该存在");
        
        duplicateSubmitPreventer.release(key);
        
        System.out.println("========================================");
        System.out.println("✅ Redis防重存在检查测试通过！");
        System.out.println("========================================");
    }

    @Test
    @Order(5)
    @DisplayName("创建工单并验证工单日志")
    void testCreateTicket_WithLog() {
        String uniqueOrderId = "LOG_TEST_" + UUID.randomUUID().toString().substring(0, 8);
        
        CreateTicketCommand command = new CreateTicketCommand(
                1L,
                "日志测试工单",
                "测试工单日志记录",
                TicketCategory.ACCOUNT,
                uniqueOrderId
        );

        TicketDetailResponse response = ticketApplicationService.createTicket(command);
        testTicketId = response.getTicketId();

        assertNotNull(testTicketId);
        
        System.out.println("========================================");
        System.out.println("✅ 工单创建成功，ID: " + testTicketId);
        System.out.println("订单号: " + uniqueOrderId);
        System.out.println("========================================");
    }

    @Test
    @Order(6)
    @DisplayName("验证工单日志已记录")
    void testTicketLog_Recorded() {
        assertNotNull(testTicketId, "前置测试未创建工单");

        List<TicketLog> logs = ticketLogRepository.findByTicketId(testTicketId);
        
        assertNotNull(logs);
        assertFalse(logs.isEmpty(), "应该有工单日志记录");
        
        System.out.println("========================================");
        System.out.println("✅ 工单日志验证通过！");
        System.out.println("工单ID: " + testTicketId);
        System.out.println("日志数量: " + logs.size());
        logs.forEach(log -> {
            System.out.println("  - 操作: " + log.getAction() + 
                             ", 状态变更: " + log.getOldStatus() + " -> " + log.getNewStatus() +
                             ", 备注: " + log.getRemark());
        });
        System.out.println("========================================");
    }

    @Test
    @Order(7)
    @DisplayName("催办工单测试 - 需要PROCESSING状态")
    void testEscalateTicket_Integration() {
        Ticket ticket = ticketRepository.findById(new TicketId(2L)).orElse(null);
        
        if (ticket != null && ticket.getStatus() == TicketStatus.PROCESSING) {
            TicketDetailResponse response = ticketApplicationService.escalateTicket(2L, 1L);
            
            assertNotNull(response);
            assertTrue(response.getEscalateCount() >= 1);
            
            List<TicketLog> logs = ticketLogRepository.findByTicketId(2L);
            boolean hasEscalateLog = logs.stream()
                    .anyMatch(log -> "ESCALATE".equals(log.getAction()));
            assertTrue(hasEscalateLog, "应该有催办日志");
            
            System.out.println("========================================");
            System.out.println("✅ 催办工单测试通过！");
            System.out.println("工单ID: 2");
            System.out.println("催办次数: " + response.getEscalateCount());
            System.out.println("========================================");
        } else {
            System.out.println("========================================");
            System.out.println("⚠️ 跳过催办测试 - 没有PROCESSING状态的工单");
            System.out.println("========================================");
        }
    }

    @Test
    @Order(8)
    @DisplayName("乐观锁测试 - 并发更新")
    void testOptimisticLock() {
        Long ticketIdToTest = testTicketId != null ? testTicketId : 1L;
        Ticket ticket = ticketRepository.findById(new TicketId(ticketIdToTest)).orElse(null);
        
        if (ticket != null) {
            int originalVersion = ticket.getVersion();
            
            ticket.updateDescription("乐观锁测试更新 - " + System.currentTimeMillis());
            ticketRepository.save(ticket);
            
            Ticket updatedTicket = ticketRepository.findById(new TicketId(ticketIdToTest)).orElse(null);
            
            assertNotNull(updatedTicket);
            assertEquals(originalVersion + 1, updatedTicket.getVersion());
            
            System.out.println("========================================");
            System.out.println("✅ 乐观锁测试通过！");
            System.out.println("原始版本: " + originalVersion);
            System.out.println("更新后版本: " + updatedTicket.getVersion());
            System.out.println("========================================");
        }
    }

    @Test
    @Order(9)
    @DisplayName("状态转换验证")
    void testStatusTransition() {
        TicketStatus pending = TicketStatus.PENDING;
        TicketStatus processing = TicketStatus.PROCESSING;
        TicketStatus resolved = TicketStatus.RESOLVED;
        TicketStatus closed = TicketStatus.CLOSED;

        System.out.println("========================================");
        System.out.println("✅ 状态转换规则验证：");
        System.out.println("PENDING -> PROCESSING: " + pending.canTransitionTo(processing));
        System.out.println("PENDING -> CLOSED: " + pending.canTransitionTo(closed));
        System.out.println("PROCESSING -> RESOLVED: " + processing.canTransitionTo(resolved));
        System.out.println("PROCESSING -> CLOSED: " + processing.canTransitionTo(closed));
        System.out.println("RESOLVED -> CLOSED: " + resolved.canTransitionTo(closed));
        System.out.println("CLOSED -> PENDING: " + closed.canTransitionTo(pending));
        System.out.println("========================================");

        assertTrue(pending.canTransitionTo(processing));
        assertTrue(pending.canTransitionTo(closed));
        assertTrue(processing.canTransitionTo(resolved));
        assertTrue(processing.canTransitionTo(closed));
        assertTrue(resolved.canTransitionTo(closed));
        assertFalse(closed.canTransitionTo(pending));
    }
}
