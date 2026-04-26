package com.example.aisupportticket.domain.ticket.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 工单日志实体类
 */
@Getter
@Setter
public class TicketLog {
    
    /** 日志唯一标识 */
    private Long id;
    
    /** 关联的工单ID */
    private Long ticketId;
    
    /** 操作类型：CREATE/UPDATE/CLOSE/ESCALATE等 */
    private String action;
    
    /** 操作前状态 */
    private String oldStatus;
    
    /** 操作后状态 */
    private String newStatus;
    
    /** 操作人ID */
    private Long operatorId;
    
    /** 操作备注说明 */
    private String remark;
    
    /** 操作时间 */
    private LocalDateTime createdAt;
    
    /**
     * 创建工单日志
     * 
     * @param ticketId 工单ID
     * @param action 操作类型
     * @param oldStatus 操作前状态
     * @param newStatus 操作后状态
     * @param operatorId 操作人ID
     * @param remark 操作备注
     * @return 工单日志对象
     */
    public static TicketLog create(Long ticketId, String action, String oldStatus, 
                                    String newStatus, Long operatorId, String remark) {
        TicketLog log = new TicketLog();
        log.setTicketId(ticketId);
        log.setAction(action);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }
}
