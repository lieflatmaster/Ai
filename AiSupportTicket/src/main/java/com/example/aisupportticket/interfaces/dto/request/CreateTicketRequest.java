package com.example.aisupportticket.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建工单请求
 *用于接收创建工单的请求参数
 */
@Data
public class CreateTicketRequest {
    
    /** 工单标题 */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200")
    private String title;
    
    /** 工单描述 */
    @Size(max = 2000, message = "描述长度不能超过2000")
    private String description;
    
    /** 工单分类：LOGISTICS/REFUND/ACCOUNT/PAYMENT/PRODUCT/OTHER */
    @NotBlank(message = "分类不能为空")
    private String category;
    
    /** 关联订单号 */
    private String orderId;
}
