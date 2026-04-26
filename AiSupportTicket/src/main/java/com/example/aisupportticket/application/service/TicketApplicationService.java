package com.example.aisupportticket.application.service;

import com.example.aisupportticket.application.command.CloseTicketCommand;
import com.example.aisupportticket.application.command.CreateTicketCommand;
import com.example.aisupportticket.application.command.EscalateTicketCommand;
import com.example.aisupportticket.application.command.UpdateTicketCommand;
import com.example.aisupportticket.domain.ticket.entity.Ticket;
import com.example.aisupportticket.domain.ticket.entity.TicketLog;
import com.example.aisupportticket.domain.ticket.repository.TicketLogRepository;
import com.example.aisupportticket.domain.ticket.repository.TicketRepository;
import com.example.aisupportticket.domain.ticket.service.TicketDomainService;
import com.example.aisupportticket.domain.ticket.vo.TicketId;
import com.example.aisupportticket.domain.ticket.vo.TicketStatus;
import com.example.aisupportticket.infrastructure.common.exception.ForbiddenException;
import com.example.aisupportticket.infrastructure.common.exception.TicketNotFoundException;
import com.example.aisupportticket.infrastructure.redis.DuplicateSubmitPreventer;
import com.example.aisupportticket.interfaces.dto.response.TicketDetailResponse;
import com.example.aisupportticket.interfaces.dto.response.TicketListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工单应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketApplicationService {

    // 工单仓储
    private final TicketRepository ticketRepository;
    // 工单日志仓储
    private final TicketLogRepository ticketLogRepository;
    // 工单领域服务
    private final TicketDomainService ticketDomainService;
    // 重复提交防止器
    private final DuplicateSubmitPreventer duplicateSubmitPreventer;
    
    /**
     * 创建工单
     * 
     * @param command 创建工单命令
     * @return 工单详情响应
     * @throws IllegalStateException 如果重复提交或存在重复工单
     */
    //写事务,确保事务一致性
    @Transactional(rollbackFor = Exception.class)
    public TicketDetailResponse createTicket(CreateTicketCommand command) {
        String dedupKey = "ticket:create:" + command.getUserId() + ":" + command.getOrderId();
        
        if (!duplicateSubmitPreventer.tryAcquire(dedupKey, 300)) {
            throw new IllegalStateException("工单正在处理中，请勿重复提交");
        }
        
        try {
            Ticket ticket = Ticket.create(
                    command.getUserId(),
                    command.getTitle(),
                    command.getDescription(),
                    command.getCategory(),
                    command.getOrderId()
            );
            
            ticketDomainService.checkDuplicate(ticket);
            ticketRepository.save(ticket);
            
            TicketLog log = TicketLog.create(
                    ticket.getId().getValue(),
                    "CREATE",
                    null,
                    TicketStatus.PENDING.name(),
                    command.getUserId(),
                    "用户创建工单"
            );
            ticketLogRepository.save(log);
            
            return toDetailResponse(ticket);
        } catch (Exception e) {
            duplicateSubmitPreventer.release(dedupKey);
            throw e;
        }
    }
    
    /**
     * 查询工单详情
     * 
     * @param ticketId 工单ID
     * @param userId 用户ID（用于权限校验）
     * @return 工单详情响应
     * @throws TicketNotFoundException 如果工单不存在或不属于该用户
     */
    //只读事务
    @Transactional(readOnly = true)
    public TicketDetailResponse queryTicket(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findByIdAndUserId(new TicketId(ticketId), userId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        return toDetailResponse(ticket);
    }
    
    /**
     * 查询工单列表
     * 
     * @param userId 用户ID
     * @param status 工单状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 工单列表响应
     */
    //只读事务
    @Transactional(readOnly = true)
    public TicketListResponse listTickets(Long userId, String status, int page, int size) {
        TicketStatus ticketStatus = status != null ? TicketStatus.valueOf(status) : null;
        Page<Ticket> ticketPage = ticketRepository.findByUserId(userId, ticketStatus, page, size);
        
        List<TicketDetailResponse> tickets = ticketPage.getContent().stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
        
        return new TicketListResponse(
                ticketPage.getTotalElements(),
                tickets
        );
    }
    
    /**
     * 关闭工单
     * 
     * @param ticketId 工单ID
     * @param userId 用户ID
     * @param reason 关闭原因
     * @throws TicketNotFoundException 如果工单不存在
     * @throws IllegalStateException 如果工单已关闭
     */
    //写事务,确保事务一致性
    @Transactional(rollbackFor = Exception.class)
    public void closeTicket(Long ticketId, Long userId, String reason) {
        Ticket ticket = ticketRepository.findByIdAndUserId(new TicketId(ticketId), userId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        String oldStatus = ticket.getStatus().name();
        ticket.close(reason);
        ticketRepository.save(ticket);
        
        TicketLog log = TicketLog.create(
                ticketId,
                "CLOSE",
                oldStatus,
                TicketStatus.CLOSED.name(),
                userId,
                reason != null ? reason : "用户关闭工单"
        );
        ticketLogRepository.save(log);
    }
    
    /**
     * 催办工单
     *催办会提升工单优先级（数值降低），并记录催办次数
     * 
     * @param ticketId 工单ID
     * @param userId 用户ID
     * @return 更新后的工单详情
     * @throws TicketNotFoundException 如果工单不存在
     * @throws IllegalStateException 如果工单不在处理中状态
     */
    //写事务,确保事务一致性
    @Transactional(rollbackFor = Exception.class)
    public TicketDetailResponse escalateTicket(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findByIdAndUserId(new TicketId(ticketId), userId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        String oldStatus = ticket.getStatus().name();
        ticket.escalate();
        ticketRepository.save(ticket);
        
        TicketLog log = TicketLog.create(
                ticketId,
                "ESCALATE",
                oldStatus,
                ticket.getStatus().name(),
                userId,
                "用户催办工单"
        );
        ticketLogRepository.save(log);
        
        return toDetailResponse(ticket);
    }
    
    /**
     * 更新工单描述
     * 
     * @param ticketId 工单ID
     * @param userId 用户ID
     * @param description 新描述
     * @throws TicketNotFoundException 如果工单不存在
     * @throws IllegalStateException 如果工单已关闭
     */
    //写事务,确保事务一致性
    @Transactional(rollbackFor = Exception.class)
    public void updateTicket(Long ticketId, Long userId, String description) {
        Ticket ticket = ticketRepository.findByIdAndUserId(new TicketId(ticketId), userId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        String oldStatus = ticket.getStatus().name();
        ticket.updateDescription(description);
        ticketRepository.save(ticket);
        
        TicketLog log = TicketLog.create(
                ticketId,
                "UPDATE",
                oldStatus,
                ticket.getStatus().name(),
                userId,
                "更新工单描述: " + description
        );
        ticketLogRepository.save(log);
    }
    
    /**
     * 将工单实体转换为详情响应DTO
     * 
     * @param ticket 工单实体
     * @return 工单详情响应
     */
    private TicketDetailResponse toDetailResponse(Ticket ticket) {
        return new TicketDetailResponse(
                ticket.getId().getValue(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory().name(),
                ticket.getCategory().getDescription(),
                ticket.getStatus().name(),
                ticket.getStatus().getDescription(),
                ticket.getPriority(),
                ticket.getOrderId(),
                ticket.getEscalateCount(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getClosedAt()
        );
    }
}
