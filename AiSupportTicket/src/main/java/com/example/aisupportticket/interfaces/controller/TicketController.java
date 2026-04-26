package com.example.aisupportticket.interfaces.controller;

import com.example.aisupportticket.application.command.CreateTicketCommand;
import com.example.aisupportticket.application.service.TicketApplicationService;
import com.example.aisupportticket.domain.ticket.vo.TicketCategory;
import com.example.aisupportticket.infrastructure.common.response.ApiResponse;
import com.example.aisupportticket.interfaces.dto.request.CloseTicketRequest;
import com.example.aisupportticket.interfaces.dto.request.CreateTicketRequest;
import com.example.aisupportticket.interfaces.dto.request.UpdateTicketRequest;
import com.example.aisupportticket.interfaces.dto.response.TicketDetailResponse;
import com.example.aisupportticket.interfaces.dto.response.TicketListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工单控制器
 */
@Tag(name = "工单管理", description = "工单相关接口")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketApplicationService ticketApplicationService;
    
    /**
     * 创建工单
     * 
     * @param request 创建工单请求
     * @param userId 用户ID（从请求头获取）
     * @return 创建成功的工单详情
     */
    @Operation(summary = "创建工单", description = "创建新的客服工单")
    @PostMapping
    public ApiResponse<TicketDetailResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        CreateTicketCommand command = new CreateTicketCommand(
                userId,
                request.getTitle(),
                request.getDescription(),
                TicketCategory.valueOf(request.getCategory()),
                request.getOrderId()
        );
        
        TicketDetailResponse response = ticketApplicationService.createTicket(command);
        return ApiResponse.success("创建成功", response);
    }
    
    /**
     * 查询工单详情
     * 
     * @param id 工单ID
     * @param userId 用户ID（用于权限校验）
     * @return 工单详情
     */
    @Operation(summary = "查询工单", description = "查询工单详情")
    @GetMapping("/{id}")
    public ApiResponse<TicketDetailResponse> getTicket(
            @Parameter(description = "工单ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        TicketDetailResponse response = ticketApplicationService.queryTicket(id, userId);
        return ApiResponse.success(response);
    }
    
    /**
     * 查询工单列表
     * 
     * @param userId 用户ID
     * @param status 状态筛选（可选）
     * @param page 页码
     * @param size 每页数量
     * @return 工单列表
     */
    @Operation(summary = "工单列表", description = "查询用户工单列表")
    @GetMapping
    public ApiResponse<TicketListResponse> listTickets(
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        
        TicketListResponse response = ticketApplicationService.listTickets(userId, status, page, size);
        return ApiResponse.success(response);
    }
    
    /**
     * 更新工单
     * 
     * @param id 工单ID
     * @param request 更新请求
     * @param userId 用户ID
     * @return 操作结果
     */
    @Operation(summary = "更新工单", description = "更新工单信息")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateTicket(
            @Parameter(description = "工单ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTicketRequest request,
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        ticketApplicationService.updateTicket(id, userId, request.getDescription());
        return ApiResponse.success("更新成功", null);
    }
    
    /**
     * 关闭工单
     * 
     * @param id 工单ID
     * @param request 关闭请求（可选原因）
     * @param userId 用户ID
     * @return 操作结果
     */
    @Operation(summary = "关闭工单", description = "关闭工单")
    @PostMapping("/{id}/close")
    public ApiResponse<Void> closeTicket(
            @Parameter(description = "工单ID") @PathVariable Long id,
            @RequestBody(required = false) CloseTicketRequest request,
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        String reason = request != null ? request.getReason() : null;
        ticketApplicationService.closeTicket(id, userId, reason);
        return ApiResponse.success("关闭成功", null);
    }
    
    /**
     * 催办工单
     *
     * 
     * @param id 工单ID
     * @param userId 用户ID
     * @return 更新后的工单详情
     */
    @Operation(summary = "催办工单", description = "催办工单，提升处理优先级")
    @PostMapping("/{id}/escalate")
    public ApiResponse<TicketDetailResponse> escalateTicket(
            @Parameter(description = "工单ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        TicketDetailResponse response = ticketApplicationService.escalateTicket(id, userId);
        return ApiResponse.success("催办成功", response);
    }
}
