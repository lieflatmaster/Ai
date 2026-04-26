package com.example.aisupportticket.domain.ticket.vo;

import java.io.Serializable;

/**
 * 工单ID值对象
 */
public class TicketId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** ID值 */
    private final Long value;
    
    /**
     * 构造工单ID
     * 
     * @param value ID值，必须为正数
     * @throws IllegalArgumentException 如果ID为null或非正数
     */
    public TicketId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("工单ID无效");
        }
        this.value = value;
    }
    
    /**
     * 获取ID值
     * 
     * @return ID值
     */
    public Long getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketId ticketId = (TicketId) o;
        return value.equals(ticketId.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
