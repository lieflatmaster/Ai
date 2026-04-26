package com.example.aisupportticket.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aisupportticket.infrastructure.persistence.po.TicketLogPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单日志Mapper接口
 */
@Mapper
public interface TicketLogMapper extends BaseMapper<TicketLogPO> {
}
