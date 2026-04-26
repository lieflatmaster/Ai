package com.example.aisupportticket.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aisupportticket.infrastructure.persistence.po.TicketPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 工单Mapper接口
 */
@Mapper
public interface TicketMapper extends BaseMapper<TicketPO> {
    
    /**
     * 检查是否存在重复工单
     * 
     * @param userId 用户ID
     * @param orderId 订单号
     * @param createdAt 创建时间阈值
     * @return 如果存在重复工单返回true
     */
    @Select("SELECT COUNT(*) > 0 FROM t_ticket WHERE user_id = #{userId} AND order_id = #{orderId} " +
            "AND created_at > #{createdAt} AND deleted = 0")
    boolean existsDuplicateTicket(@Param("userId") Long userId, 
                                   @Param("orderId") String orderId, 
                                   @Param("createdAt") LocalDateTime createdAt);
}
