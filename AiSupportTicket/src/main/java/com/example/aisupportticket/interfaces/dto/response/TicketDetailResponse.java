package com.example.aisupportticket.interfaces.dto.response;

import java.time.LocalDateTime;

/**
 * 工单详情响应
 *返回工单的详细信息，包括基本信息、状态和分类描述等
 */
public class TicketDetailResponse {
    
    /** 工单ID */
    private Long ticketId;
    
    /** 工单标题 */
    private String title;
    
    /** 工单描述 */
    private String description;
    
    /** 工单分类代码 */
    private String category;
    
    /** 工单分类描述 */
    private String categoryDesc;
    
    /** 工单状态代码 */
    private String status;
    
    /** 工单状态描述 */
    private String statusDesc;
    
    /** 优先级（1-5，1最高） */
    private Integer priority;
    
    /** 关联订单号 */
    private String orderId;
    
    /** 催办次数 */
    private Integer escalateCount;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 关闭时间 */
    private LocalDateTime closedAt;
    
    public TicketDetailResponse() {}
    
    public TicketDetailResponse(Long ticketId, String title, String description, String category,
                                 String categoryDesc, String status, String statusDesc, Integer priority,
                                 String orderId, Integer escalateCount, LocalDateTime createdAt,
                                 LocalDateTime updatedAt, LocalDateTime closedAt) {
        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.categoryDesc = categoryDesc;
        this.status = status;
        this.statusDesc = statusDesc;
        this.priority = priority;
        this.orderId = orderId;
        this.escalateCount = escalateCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
    }
    
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCategoryDesc() { return categoryDesc; }
    public void setCategoryDesc(String categoryDesc) { this.categoryDesc = categoryDesc; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusDesc() { return statusDesc; }
    public void setStatusDesc(String statusDesc) { this.statusDesc = statusDesc; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Integer getEscalateCount() { return escalateCount; }
    public void setEscalateCount(Integer escalateCount) { this.escalateCount = escalateCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}
