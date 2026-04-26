package com.example.aisupportticket.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aisupportticket.domain.ticket.entity.TicketLog;
import com.example.aisupportticket.domain.ticket.repository.TicketLogRepository;
import com.example.aisupportticket.infrastructure.persistence.converter.TicketLogConverter;
import com.example.aisupportticket.infrastructure.persistence.mapper.TicketLogMapper;
import com.example.aisupportticket.infrastructure.persistence.po.TicketLogPO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工单日志仓储实现类
 * 实现工单日志仓储接口，负责工单日志数据的持久化操作
 */
@Repository
@RequiredArgsConstructor
public class TicketLogRepositoryImpl implements TicketLogRepository {
    
    private final TicketLogMapper ticketLogMapper;
    private final TicketLogConverter ticketLogConverter;
    
    @Override
    public TicketLog save(TicketLog ticketLog) {
        TicketLogPO po = ticketLogConverter.toPO(ticketLog);
        if (ticketLog.getId() == null) {
            ticketLogMapper.insert(po);
            ticketLog.setId(po.getId());
        } else {
            ticketLogMapper.updateById(po);
        }
        return ticketLog;
    }
    
    @Override
    public List<TicketLog> findByTicketId(Long ticketId) {
        LambdaQueryWrapper<TicketLogPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketLogPO::getTicketId, ticketId)
               .orderByDesc(TicketLogPO::getCreatedAt);
        List<TicketLogPO> poList = ticketLogMapper.selectList(wrapper);
        return poList.stream()
                .map(ticketLogConverter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public org.springframework.data.domain.Page<TicketLog> findByTicketId(Long ticketId, Pageable pageable) {
        Page<TicketLogPO> poPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<TicketLogPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketLogPO::getTicketId, ticketId)
               .orderByDesc(TicketLogPO::getCreatedAt);
        
        IPage<TicketLogPO> result = ticketLogMapper.selectPage(poPage, wrapper);
        
        List<TicketLog> logs = result.getRecords().stream()
                .map(ticketLogConverter::toDomain)
                .collect(Collectors.toList());
        
        return new PageImpl<>(logs, pageable, result.getTotal());
    }
    
    @Override
    public void deleteByTicketId(Long ticketId) {
        LambdaQueryWrapper<TicketLogPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketLogPO::getTicketId, ticketId);
        ticketLogMapper.delete(wrapper);
    }
}
