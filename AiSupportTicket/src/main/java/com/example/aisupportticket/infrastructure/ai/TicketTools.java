package com.example.aisupportticket.infrastructure.ai;

import com.example.aisupportticket.application.command.CreateTicketCommand;
import com.example.aisupportticket.application.service.TicketApplicationService;
import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.interfaces.dto.response.TicketDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * AI工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TicketTools {

    private final TicketApplicationService ticketApplicationService;

    @Tool(description = "创建新的客服工单。当用户想要创建工单、提交问题、反馈问题时调用此函数。")
    public String createTicket(
            @ToolParam(description = "工单标题") String title,
            @ToolParam(description = "工单描述") String description,
            @ToolParam(description = "工单分类：LOGISTICS(物流), REFUND(退款), ACCOUNT(账户), PAYMENT(支付), PRODUCT(商品), OTHER(其他)") String category,
            @ToolParam(description = "关联订单号，可选") String orderId) {
        long startTime = System.currentTimeMillis();
        log.info("---------- [Tool] createTicket 开始 ----------");
        log.info("参数: title={}, category={}, orderId={}", title, category, orderId);
        
        try {
            Long userId = AiContextHolder.getCurrentUserId();
            if (userId == null) {
                log.warn("[Tool] createTicket 失败: 无法获取用户信息");
                return "错误：无法获取用户信息";
            }
            log.info("用户ID: {}", userId);


            TicketCategory cat = parseCategory(category);
            CreateTicketCommand command = new CreateTicketCommand(userId, title, description, cat, orderId);
            TicketDetailResponse response = ticketApplicationService.createTicket(command);
            
            String result = String.format("工单创建成功！工单ID: %d, 标题: %s, 状态: %s", 
                    response.getTicketId(), response.getTitle(), response.getStatusDesc());
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[Tool] createTicket 成功: ticketId={}, 耗时={}ms", response.getTicketId(), elapsed);
            log.info("---------- [Tool] createTicket 完成 ----------");
            
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[Tool] createTicket 异常: 耗时={}ms, 错误={}", elapsed, e.getMessage(), e);
            log.info("---------- [Tool] createTicket 失败 ----------");
            return "创建失败: " + e.getMessage();
        }
    }

    @Tool(description = "根据工单ID查询工单详情。当用户想查看某个具体工单的信息时调用此函数。")
    public String queryTicket(
            @ToolParam(description = "工单ID") Long ticketId) {
        long startTime = System.currentTimeMillis();
        log.info("---------- [Tool] queryTicket 开始 ----------");
        log.info("参数: ticketId={}", ticketId);
        
        try {
            Long userId = AiContextHolder.getCurrentUserId();
            if (userId == null) {
                log.warn("[Tool] queryTicket 失败: 无法获取用户信息");
                return "错误：无法获取用户信息";
            }
            log.info("用户ID: {}", userId);
            
            TicketDetailResponse response = ticketApplicationService.queryTicket(ticketId, userId);
            
            String result = String.format("工单ID: %d\n标题: %s\n描述: %s\n状态: %s\n分类: %s\n优先级: %d\n订单号: %s", 
                    response.getTicketId(), response.getTitle(), response.getDescription(),
                    response.getStatusDesc(), response.getCategoryDesc(), response.getPriority(),
                    response.getOrderId() != null ? response.getOrderId() : "无");
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[Tool] queryTicket 成功: ticketId={}, 耗时={}ms", response.getTicketId(), elapsed);
            log.info("---------- [Tool] queryTicket 完成 ----------");
            
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[Tool] queryTicket 异常: 耗时={}ms, 错误={}", elapsed, e.getMessage(), e);
            log.info("---------- [Tool] queryTicket 失败 ----------");
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "查询用户的工单列表。当用户想查看自己的所有工单或按状态筛选工单时调用此函数。")
    public String listTickets(
            @ToolParam(description = "页码，默认1") Integer page,
            @ToolParam(description = "每页数量，默认10") Integer size,
            @ToolParam(description = "状态筛选：PENDING(待处理), PROCESSING(处理中), RESOLVED(已解决), CLOSED(已关闭)，可选") String status) {
        long startTime = System.currentTimeMillis();
        log.info("---------- [Tool] listTickets 开始 ----------");
        log.info("参数: page={}, size={}, status={}", page, size, status);
        
        try {
            Long userId = AiContextHolder.getCurrentUserId();
            if (userId == null) {
                log.warn("[Tool] listTickets 失败: 无法获取用户信息");
                return "错误：无法获取用户信息";
            }
            log.info("用户ID: {}", userId);
            
            int pageNum = page != null ? page : 1;
            int sizeNum = size != null ? size : 10;
            
            var response = ticketApplicationService.listTickets(userId, status, pageNum, sizeNum);
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("查询成功，共找到 %d 个工单：\n", response.getTotal()));
            for (var ticket : response.getList()) {
                sb.append(String.format("- ID: %d, 标题: %s, 状态: %s, 分类: %s\n",
                        ticket.getTicketId(), ticket.getTitle(), 
                        ticket.getStatusDesc(), ticket.getCategoryDesc()));
            }
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[Tool] listTickets 成功: 共{}条记录, 耗时={}ms", response.getTotal(), elapsed);
            log.info("---------- [Tool] listTickets 完成 ----------");
            
            return sb.toString();
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[Tool] listTickets 异常: 耗时={}ms, 错误={}", elapsed, e.getMessage(), e);
            log.info("---------- [Tool] listTickets 失败 ----------");
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "关闭指定工单。当用户想要关闭、结束工单时调用此函数。")
    public String closeTicket(
            @ToolParam(description = "工单ID") Long ticketId,
            @ToolParam(description = "关闭原因，可选") String reason) {
        long startTime = System.currentTimeMillis();
        log.info("---------- [Tool] closeTicket 开始 ----------");
        log.info("参数: ticketId={}, reason={}", ticketId, reason);
        
        try {
            Long userId = AiContextHolder.getCurrentUserId();
            if (userId == null) {
                log.warn("[Tool] closeTicket 失败: 无法获取用户信息");
                return "错误：无法获取用户信息";
            }
            log.info("用户ID: {}", userId);
            
            ticketApplicationService.closeTicket(ticketId, userId, reason);
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[Tool] closeTicket 成功: ticketId={}, 耗时={}ms", ticketId, elapsed);
            log.info("---------- [Tool] closeTicket 完成 ----------");
            
            return "工单已成功关闭";
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[Tool] closeTicket 异常: 耗时={}ms, 错误={}", elapsed, e.getMessage(), e);
            log.info("---------- [Tool] closeTicket 失败 ----------");
            return "关闭失败: " + e.getMessage();
        }
    }

    @Tool(description = "催办工单，提升处理优先级。当用户想要催促工单处理进度时调用此函数。")
    public String escalateTicket(
            @ToolParam(description = "工单ID") Long ticketId) {
        long startTime = System.currentTimeMillis();
        log.info("---------- [Tool] escalateTicket 开始 ----------");
        log.info("参数: ticketId={}", ticketId);
        
        try {
            Long userId = AiContextHolder.getCurrentUserId();
            if (userId == null) {
                log.warn("[Tool] escalateTicket 失败: 无法获取用户信息");
                return "错误：无法获取用户信息";
            }
            log.info("用户ID: {}", userId);
            
            TicketDetailResponse response = ticketApplicationService.escalateTicket(ticketId, userId);
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[Tool] escalateTicket 成功: ticketId={}, 新优先级={}, 耗时={}ms", 
                    ticketId, response.getPriority(), elapsed);
            log.info("---------- [Tool] escalateTicket 完成 ----------");
            
            return String.format("催办成功！工单优先级已提升，当前优先级: %d", response.getPriority());
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[Tool] escalateTicket 异常: 耗时={}ms, 错误={}", elapsed, e.getMessage(), e);
            log.info("---------- [Tool] escalateTicket 失败 ----------");
            return "催办失败: " + e.getMessage();
        }
    }

    @Tool(description = "更新工单描述信息。当用户想要修改工单内容时调用此函数。")
    public String updateTicket(
            @ToolParam(description = "工单ID") Long ticketId,
            @ToolParam(description = "新的描述内容") String description) {
        long startTime = System.currentTimeMillis();
        log.info("---------- [Tool] updateTicket 开始 ----------");
        log.info("参数: ticketId={}", ticketId);
        
        try {
            Long userId = AiContextHolder.getCurrentUserId();
            if (userId == null) {
                log.warn("[Tool] updateTicket 失败: 无法获取用户信息");
                return "错误：无法获取用户信息";
            }
            log.info("用户ID: {}", userId);
            
            ticketApplicationService.updateTicket(ticketId, userId, description);
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[Tool] updateTicket 成功: ticketId={}, 耗时={}ms", ticketId, elapsed);
            log.info("---------- [Tool] updateTicket 完成 ----------");
            
            return "工单已成功更新";
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[Tool] updateTicket 异常: 耗时={}ms, 错误={}", elapsed, e.getMessage(), e);
            log.info("---------- [Tool] updateTicket 失败 ----------");
            return "更新失败: " + e.getMessage();
        }
    }

    //将工单分类字符串转换为枚举
    private TicketCategory parseCategory(String category) {
        if (category == null) {
            return TicketCategory.OTHER;
        }
        try {
            return TicketCategory.valueOf(category.toUpperCase());
        } catch (Exception e) {
            return TicketCategory.OTHER;
        }
    }
}
