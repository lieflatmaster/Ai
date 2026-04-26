package com.example.aisupportticket.domain.ticket.entity;

import com.example.aisupportticket.domain.shared.BaseEntity;
import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.domain.ticket.vo.TicketId;
import com.example.aisupportticket.domain.ticket.vo.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 工单实体类
 */
@Getter
@Setter
public class Ticket extends BaseEntity {
    
    /** 工单唯一标识 */
    private TicketId id;
    
    /** 工单所属用户ID */
    private Long userId;
    
    /** 工单标题 */
    private String title;
    
    /** 工单详细描述 */
    private String description;
    
    /** 工单分类 */
    private TicketCategory category;
    
    /** 工单当前状态 */
    private TicketStatus status;
    
    /** 工单优先级（1-5，1最高，5最低） */
    private Integer priority;
    
    /** 关联订单号 */
    private String orderId;
    
    /** 催办次数 */
    private Integer escalateCount;
    
    /** 最近催办时间 */
    private LocalDateTime escalatedAt;
    
    /** 乐观锁版本号 */
    private Integer version;
    
    /** 工单关闭时间 */
    private LocalDateTime closedAt;
    
    /**
     * 创建新工单
     * 
     * @param userId 用户ID
     * @param title 工单标题
     * @param description 工单描述
     * @param category 工单分类
     * @param orderId 关联订单号
     * @return 新创建的工单对象
     */
    public static Ticket create(Long userId, String title, String description, 
                                 TicketCategory category, String orderId) {
        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setCategory(category);
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setPriority(5);
        ticket.setOrderId(orderId);
        ticket.setEscalateCount(0);
        ticket.setVersion(0);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticket;
    }
    
    /**
     * 关闭工单
     * 
     * @param reason 关闭原因
     * @throws IllegalStateException 如果工单已关闭
     */
    public void close(String reason) {
        if (this.status == TicketStatus.CLOSED) {
            throw new IllegalStateException("工单已关闭，无法重复操作");
        }
        this.status = TicketStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 催办工单
     * 
     * <p>催办会提升工单优先级（数值降低），并记录催办次数和时间</p>
     * 
     * @throws IllegalStateException 如果工单不在处理中状态
     */
    public void escalate() {
        if (this.status != TicketStatus.PROCESSING) {
            throw new IllegalStateException("只有处理中的工单可以催办");
        }
        this.priority = Math.max(1, this.priority - 1);
        this.escalateCount = this.escalateCount + 1;
        this.escalatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新工单描述
     * 
     * @param newDescription 新的描述内容
     * @throws IllegalStateException 如果工单已关闭
     */
    public void updateDescription(String newDescription) {
        if (this.status == TicketStatus.CLOSED) {
            throw new IllegalStateException("工单已关闭，无法更新");
        }
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 分配工单给客服人员
     * 
     * <p>分配后工单状态自动变为处理中</p>
     * 
     * @param assigneeId 客服人员ID
     * @throws IllegalStateException 如果工单不在待处理状态
     */
    public void assignTo(Long assigneeId) {
        if (this.status != TicketStatus.PENDING) {
            throw new IllegalStateException("只有待处理的工单可以分配");
        }
        this.status = TicketStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查工单是否属于指定用户
     * 
     * @param userId 用户ID
     * @return 如果工单属于该用户返回true，否则返回false
     */
    public boolean belongsTo(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }
}
