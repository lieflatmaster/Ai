package com.example.aisupportticket.domain.ticket.repository;

import com.example.aisupportticket.domain.ticket.entity.TicketLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 工单日志仓储接口
 */
public interface TicketLogRepository {
    
    /**
     * 保存工单日志
     * 
     * @param ticketLog 工单日志
     * @return 保存后的工单日志
     */
    TicketLog save(TicketLog ticketLog);
    
    /**
     * 根据工单ID查询日志列表
     * 
     * @param ticketId 工单ID
     * @return 日志列表
     */
    List<TicketLog> findByTicketId(Long ticketId);
    
    /**
     * 分页查询工单日志
     * 
     * @param ticketId 工单ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TicketLog> findByTicketId(Long ticketId, Pageable pageable);
    
    /**
     * 根据工单ID删除日志
     * 
     * @param ticketId 工单ID
     */
    void deleteByTicketId(Long ticketId);
}
