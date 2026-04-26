package com.example.aisupportticket.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aisupportticket.domain.ticket.entity.Ticket;
import com.example.aisupportticket.domain.ticket.repository.TicketRepository;
import com.example.aisupportticket.domain.ticket.vo.TicketId;
import com.example.aisupportticket.domain.ticket.vo.TicketStatus;
import com.example.aisupportticket.infrastructure.persistence.converter.TicketConverter;
import com.example.aisupportticket.infrastructure.persistence.mapper.TicketMapper;
import com.example.aisupportticket.infrastructure.persistence.po.TicketPO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工单仓储实现类
 *实现工单仓储接口，负责工单数据的持久化操作
 */
@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {
    
    private final TicketMapper ticketMapper;
    private final TicketConverter ticketConverter;
    
    /**
     * 保存工单
     * 如果工单ID为空则插入新记录，否则更新已有记录
     * 
     * @param ticket 工单领域对象
     * @return 保存后的工单对象
     */
    @Override
    public Ticket save(Ticket ticket) {
        TicketPO po = ticketConverter.toPO(ticket);
        if (ticket.getId() == null) {
            ticketMapper.insert(po);
            ticket.setId(new TicketId(po.getId()));
        } else {
            ticketMapper.updateById(po);
        }
        return ticket;
    }
    
    /**
     * 根据ID查询工单
     * 
     * @param id 工单ID
     * @return 工单对象（可能为空）
     */
    @Override
    public Optional<Ticket> findById(TicketId id) {
        TicketPO po = ticketMapper.selectById(id.getValue());
        return Optional.ofNullable(ticketConverter.toDomain(po));
    }
    
    /**
     * 根据ID和用户ID查询工单
     * 
     * @param id 工单ID
     * @param userId 用户ID
     * @return 工单对象（可能为空）
     */
    @Override
    public Optional<Ticket> findByIdAndUserId(TicketId id, Long userId) {
        LambdaQueryWrapper<TicketPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketPO::getId, id.getValue())
               .eq(TicketPO::getUserId, userId);
        TicketPO po = ticketMapper.selectOne(wrapper);
        return Optional.ofNullable(ticketConverter.toDomain(po));
    }
    
    /**
     * 查询用户的所有工单
     * 
     * @param userId 用户ID
     * @return 工单列表
     */
    @Override
    public List<Ticket> findByUserId(Long userId) {
        LambdaQueryWrapper<TicketPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketPO::getUserId, userId)
               .orderByDesc(TicketPO::getCreatedAt);
        List<TicketPO> poList = ticketMapper.selectList(wrapper);
        return poList.stream()
                .map(ticketConverter::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * 查询用户指定状态的工单
     * 
     * @param userId 用户ID
     * @param status 工单状态
     * @return 工单列表
     */
    @Override
    public List<Ticket> findByUserIdAndStatus(Long userId, TicketStatus status) {
        LambdaQueryWrapper<TicketPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketPO::getUserId, userId);
        if (status != null) {
            wrapper.eq(TicketPO::getStatus, status.name());
        }
        wrapper.orderByDesc(TicketPO::getCreatedAt);
        List<TicketPO> poList = ticketMapper.selectList(wrapper);
        return poList.stream()
                .map(ticketConverter::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页查询用户工单
     * 
     * @param userId 用户ID
     * @param status 工单状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @Override
    public org.springframework.data.domain.Page<Ticket> findByUserId(Long userId, TicketStatus status, int page, int size) {
        Page<TicketPO> poPage = new Page<>(page, size);
        LambdaQueryWrapper<TicketPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketPO::getUserId, userId);
        if (status != null) {
            wrapper.eq(TicketPO::getStatus, status.name());
        }
        wrapper.orderByDesc(TicketPO::getCreatedAt);
        
        IPage<TicketPO> result = ticketMapper.selectPage(poPage, wrapper);
        
        List<Ticket> tickets = result.getRecords().stream()
                .map(ticketConverter::toDomain)
                .collect(Collectors.toList());
        
        return new PageImpl<>(tickets, PageRequest.of(page - 1, size), result.getTotal());
    }
    
    /**
     * 检查是否存在重复工单
     * 
     * @param userId 用户ID
     * @param orderId 订单号
     * @param within 时间范围
     * @return 如果存在重复工单返回true
     */
    @Override
    public boolean existsDuplicateTicket(Long userId, String orderId, Duration within) {
        LocalDateTime createdAt = LocalDateTime.now().minus(within);
        return ticketMapper.existsDuplicateTicket(userId, orderId, createdAt);
    }
    
    /**
     * 删除工单
     * 
     * @param id 工单ID
     */
    @Override
    public void deleteById(TicketId id) {
        ticketMapper.deleteById(id.getValue());
    }
}
