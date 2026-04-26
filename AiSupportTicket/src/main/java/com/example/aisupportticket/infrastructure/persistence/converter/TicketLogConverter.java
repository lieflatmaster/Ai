package com.example.aisupportticket.infrastructure.persistence.converter;

import com.example.aisupportticket.domain.ticket.entity.TicketLog;
import com.example.aisupportticket.infrastructure.persistence.po.TicketLogPO;
import org.springframework.stereotype.Component;

/**
 * 工单日志转换器
 *负责工单日志领域对象和持久化对象之间的相互转换
 */
@Component
public class TicketLogConverter {
    
    /**
     * 将持久化对象转换为领域对象
     * 
     * @param po 持久化对象
     * @return 领域对象
     */
    public TicketLog toDomain(TicketLogPO po) {
        if (po == null) {
            return null;
        }
        
        TicketLog log = new TicketLog();
        log.setId(po.getId());
        log.setTicketId(po.getTicketId());
        log.setAction(po.getAction());
        log.setOldStatus(po.getOldStatus());
        log.setNewStatus(po.getNewStatus());
        log.setOperatorId(po.getOperatorId());
        log.setRemark(po.getRemark());
        log.setCreatedAt(po.getCreatedAt());
        
        return log;
    }
    
    /**
     * 将领域对象转换为持久化对象
     * 
     * @param log 领域对象
     * @return 持久化对象
     */
    public TicketLogPO toPO(TicketLog log) {
        if (log == null) {
            return null;
        }
        
        TicketLogPO po = new TicketLogPO();
        po.setId(log.getId());
        po.setTicketId(log.getTicketId());
        po.setAction(log.getAction());
        po.setOldStatus(log.getOldStatus());
        po.setNewStatus(log.getNewStatus());
        po.setOperatorId(log.getOperatorId());
        po.setRemark(log.getRemark());
        po.setCreatedAt(log.getCreatedAt());
        
        return po;
    }
}
