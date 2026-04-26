package com.example.aisupportticket.domain.ticket.vo;

/**
 * 工单状态枚举
 */
public enum TicketStatus {
    
    /** 待处理 - 工单刚创建，等待分配 */
    PENDING("待处理"),
    
    /** 处理中 - 工单已分配给客服，正在处理 */
    PROCESSING("处理中"),
    
    /** 已解决 - 问题已解决，等待用户确认 */
    RESOLVED("已解决"),
    
    /** 已关闭 - 工单已关闭，不再处理 */
    CLOSED("已关闭");
    
    /** 状态描述 */
    private final String description;
    
    TicketStatus(String description) {
        this.description = description;
    }
    
    /**
     * 获取状态描述
     * 
     * @return 状态的中文描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否可以转换到目标状态
     * 
     * @param target 目标状态
     * @return 如果可以转换返回true，否则返回false
     */
    public boolean canTransitionTo(TicketStatus target) {
        switch (this) {
            case PENDING:
                return target == PROCESSING || target == CLOSED;
            case PROCESSING:
                return target == RESOLVED || target == CLOSED;
            case RESOLVED:
                return target == CLOSED;
            case CLOSED:
                return false;
            default:
                return false;
        }
    }
}
