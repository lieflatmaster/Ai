package com.example.aisupportticket.domain.ticket.vo;

/**
 * 工单分类枚举
 */
public enum TicketCategory {
    
    /** 物流问题 - 配送、发货、收货相关问题 */
    LOGISTICS("物流问题"),
    
    /** 退款问题 - 退款申请、退款进度相关问题 */
    REFUND("退款问题"),
    
    /** 账户问题 - 登录、注册、账户安全相关问题 */
    ACCOUNT("账户问题"),
    
    /** 支付问题 - 支付失败、支付方式相关问题 */
    PAYMENT("支付问题"),
    
    /** 商品问题 - 商品质量、商品信息相关问题 */
    PRODUCT("商品问题"),
    
    /** 其他问题 - 不属于以上分类的问题 */
    OTHER("其他问题");
    
    /** 分类描述 */
    private final String description;
    
    TicketCategory(String description) {
        this.description = description;
    }
    
    /**
     * 获取分类描述
     * 
     * @return 分类的中文描述
     */
    public String getDescription() {
        return description;
    }
}
