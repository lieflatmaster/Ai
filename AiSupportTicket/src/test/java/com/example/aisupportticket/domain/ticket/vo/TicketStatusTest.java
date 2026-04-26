package com.example.aisupportticket.domain.ticket.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工单状态枚举单元测试
 * 
 * <p>测试工单状态枚举的状态转换规则。</p>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
class TicketStatusTest {

    @Test
    @DisplayName("获取状态描述")
    void testGetDescription() {
        assertEquals("待处理", TicketStatus.PENDING.getDescription());
        assertEquals("处理中", TicketStatus.PROCESSING.getDescription());
        assertEquals("已解决", TicketStatus.RESOLVED.getDescription());
        assertEquals("已关闭", TicketStatus.CLOSED.getDescription());
    }

    @ParameterizedTest
    @MethodSource("validTransitions")
    @DisplayName("状态转换 - 有效")
    void testCanTransitionTo_Valid(TicketStatus from, TicketStatus to) {
        assertTrue(from.canTransitionTo(to));
    }

    @ParameterizedTest
    @MethodSource("invalidTransitions")
    @DisplayName("状态转换 - 无效")
    void testCanTransitionTo_Invalid(TicketStatus from, TicketStatus to) {
        assertFalse(from.canTransitionTo(to));
    }

    static Stream<Arguments> validTransitions() {
        return Stream.of(
                Arguments.of(TicketStatus.PENDING, TicketStatus.PROCESSING),
                Arguments.of(TicketStatus.PENDING, TicketStatus.CLOSED),
                Arguments.of(TicketStatus.PROCESSING, TicketStatus.RESOLVED),
                Arguments.of(TicketStatus.PROCESSING, TicketStatus.CLOSED),
                Arguments.of(TicketStatus.RESOLVED, TicketStatus.CLOSED)
        );
    }

    static Stream<Arguments> invalidTransitions() {
        return Stream.of(
                Arguments.of(TicketStatus.PENDING, TicketStatus.PENDING),
                Arguments.of(TicketStatus.PENDING, TicketStatus.RESOLVED),
                Arguments.of(TicketStatus.PROCESSING, TicketStatus.PENDING),
                Arguments.of(TicketStatus.PROCESSING, TicketStatus.PROCESSING),
                Arguments.of(TicketStatus.RESOLVED, TicketStatus.PENDING),
                Arguments.of(TicketStatus.RESOLVED, TicketStatus.PROCESSING),
                Arguments.of(TicketStatus.RESOLVED, TicketStatus.RESOLVED),
                Arguments.of(TicketStatus.CLOSED, TicketStatus.PENDING),
                Arguments.of(TicketStatus.CLOSED, TicketStatus.PROCESSING),
                Arguments.of(TicketStatus.CLOSED, TicketStatus.RESOLVED),
                Arguments.of(TicketStatus.CLOSED, TicketStatus.CLOSED)
        );
    }
}
