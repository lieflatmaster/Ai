package com.example.aisupportticket.infrastructure.persistence.converter;

import com.example.aisupportticket.domain.ticket.entity.Ticket;
import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.domain.ticket.vo.TicketId;
import com.example.aisupportticket.domain.ticket.vo.TicketStatus;
import com.example.aisupportticket.infrastructure.persistence.po.TicketPO;
import org.springframework.stereotype.Component;

/**
 * 工单转换器
 *负责工单领域对象和持久化对象之间的相互转换
 */
@Component
public class TicketConverter {
    
    /**
     * 将持久化对象转换为领域对象
     * 
     * @param po 持久化对象
     * @return 领域对象
     */
    public Ticket toDomain(TicketPO po) {
        if (po == null) {
            return null;
        }
        
        Ticket ticket = new Ticket();
        ticket.setId(po.getId() != null ? new TicketId(po.getId()) : null);
        ticket.setUserId(po.getUserId());
        ticket.setTitle(po.getTitle());
        ticket.setDescription(po.getDescription());
        ticket.setCategory(po.getCategory() != null ? TicketCategory.valueOf(po.getCategory()) : null);
        ticket.setStatus(po.getStatus() != null ? TicketStatus.valueOf(po.getStatus()) : null);
        ticket.setPriority(po.getPriority());
        ticket.setOrderId(po.getOrderId());
        ticket.setEscalateCount(po.getEscalateCount());
        ticket.setEscalatedAt(po.getEscalatedAt());
        ticket.setVersion(po.getVersion());
        ticket.setCreatedAt(po.getCreatedAt());
        ticket.setUpdatedAt(po.getUpdatedAt());
        ticket.setClosedAt(po.getClosedAt());
        
        return ticket;
    }
    
    /**
     * 将领域对象转换为持久化对象
     * 
     * @param ticket 领域对象
     * @return 持久化对象
     */
    public TicketPO toPO(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        
        TicketPO po = new TicketPO();
        po.setId(ticket.getId() != null ? ticket.getId().getValue() : null);
        po.setUserId(ticket.getUserId());
        po.setTitle(ticket.getTitle());
        po.setDescription(ticket.getDescription());
        po.setCategory(ticket.getCategory() != null ? ticket.getCategory().name() : null);
        po.setStatus(ticket.getStatus() != null ? ticket.getStatus().name() : null);
        po.setPriority(ticket.getPriority());
        po.setOrderId(ticket.getOrderId());
        po.setEscalateCount(ticket.getEscalateCount());
        po.setEscalatedAt(ticket.getEscalatedAt());
        po.setVersion(ticket.getVersion());
        po.setCreatedAt(ticket.getCreatedAt());
        po.setUpdatedAt(ticket.getUpdatedAt());
        po.setClosedAt(ticket.getClosedAt());
        
        return po;
    }
}
