package com.example.aisupportticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智能客服工单系统启动类
 * 
 * <p>基于Spring Boot的智能客服工单系统，集成AI对话功能。
 * 支持工单管理和AI智能对话，通过Function Calling实现AI与业务系统的交互。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>工单管理：创建、查询、更新、关闭、催办</li>
 *   <li>AI对话：支持普通对话和流式对话</li>
 *   <li>Function Calling：AI可调用工单相关函数</li>
 * </ul>
 * 
 * @author AI Support Ticket System
 * @version 1.0
 */
@SpringBootApplication
public class AiSupportTicketApplication {

    /**
     * 应用程序入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AiSupportTicketApplication.class, args);
    }
}
