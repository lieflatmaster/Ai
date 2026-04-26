package com.example.aisupportticket.domain.ticket.repository;

import com.example.aisupportticket.domain.ticket.entity.Ticket;
import com.example.aisupportticket.domain.ticket.vo.TicketId;
import com.example.aisupportticket.domain.ticket.vo.TicketStatus;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * 工单仓储接口
 */
public interface TicketRepository {
    
    /**
     * 保存工单
     * 
     * @param ticket 工单对象
     * @return 保存后的工单对象
     */
    Ticket save(Ticket ticket);
    
    /**
     * 根据ID查询工单
     * 
     * @param id 工单ID
     * @return 工单对象（可能为空）
     */
    Optional<Ticket> findById(TicketId id);
    
    /**
     * 根据ID和用户ID查询工单
     * 
     * <p>用于验证工单归属权限</p>
     * 
     * @param id 工单ID
     * @param userId 用户ID
     * @return 工单对象（可能为空）
     */
    Optional<Ticket> findByIdAndUserId(TicketId id, Long userId);
    
    /**
     * 查询用户的所有工单
     * 
     * @param userId 用户ID
     * @return 工单列表
     */
    List<Ticket> findByUserId(Long userId);
    
    /**
     * 查询用户指定状态的工单
     * 
     * @param userId 用户ID
     * @param status 工单状态
     * @return 工单列表
     */
    List<Ticket> findByUserIdAndStatus(Long userId, TicketStatus status);
    
    /**
     * 分页查询用户工单
     * 
     * @param userId 用户ID
     * @param status 工单状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<Ticket> findByUserId(Long userId, TicketStatus status, int page, int size);
    
    /**
     * 检查是否存在重复工单
     * 
     * <p>同一用户对同一订单在指定时间范围内创建的工单</p>
     * 
     * @param userId 用户ID
     * @param orderId 订单号
     * @param within 时间范围
     * @return 如果存在重复工单返回true
     */
    boolean existsDuplicateTicket(Long userId, String orderId, Duration within);
    
    /**
     * 删除工单
     * 
     * @param id 工单ID
     */
    void deleteById(TicketId id);
}
