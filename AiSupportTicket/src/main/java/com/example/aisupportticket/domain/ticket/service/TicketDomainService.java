package com.example.aisupportticket.domain.ticket.service;

import com.example.aisupportticket.domain.ticket.entity.Ticket;
import com.example.aisupportticket.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 工单领域服务
 */
@Service
@RequiredArgsConstructor
public class TicketDomainService {
    
    private final TicketRepository ticketRepository;
    
    /**
     * 检查是否存在重复工单
     *同一用户在5分钟内对同一订单创建的工单视为重复
     * 
     * @param ticket 待检查的工单
     * @throws IllegalStateException 如果存在重复工单
     */
    public void checkDuplicate(Ticket ticket) {
        if (ticket.getOrderId() != null) {
            boolean exists = ticketRepository.existsDuplicateTicket(
                ticket.getUserId(),
                ticket.getOrderId(),
                Duration.ofMinutes(5)
            );
            if (exists) {
                throw new IllegalStateException("工单已存在，请勿重复提交");
            }
        }
    }
    
    /**
     * 验证工单状态转换是否合法
     * 
     * @param ticket 工单对象
     * @param targetStatus 目标状态
     * @throws IllegalStateException 如果状态转换不合法
     */
    public void validateStatusTransition(Ticket ticket, com.example.aisupportticket.domain.ticket.vo.TicketStatus targetStatus) {
        if (!ticket.getStatus().canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                String.format("工单状态不允许从 %s 转换为 %s", 
                    ticket.getStatus().getDescription(), 
                    targetStatus.getDescription())
            );
        }
    }
}
