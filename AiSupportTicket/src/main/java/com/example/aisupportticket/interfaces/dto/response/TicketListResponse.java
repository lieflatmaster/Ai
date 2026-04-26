package com.example.aisupportticket.interfaces.dto.response;

import java.util.List;

/**
 * 工单列表响应
 */
public class TicketListResponse {
    
    /** 总记录数 */
    private Long total;
    
    /** 工单列表 */
    private List<TicketDetailResponse> list;
    
    public TicketListResponse() {}
    
    public TicketListResponse(Long total, List<TicketDetailResponse> list) {
        this.total = total;
        this.list = list;
    }
    
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public List<TicketDetailResponse> getList() { return list; }
    public void setList(List<TicketDetailResponse> list) { this.list = list; }
}
