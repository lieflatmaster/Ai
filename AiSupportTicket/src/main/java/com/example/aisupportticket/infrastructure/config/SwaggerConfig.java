package com.example.aisupportticket.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI配置类
 *配置API文档，使用OpenAPI 3.0规范
 */
@Configuration
public class SwaggerConfig {
    
    /**
     * 创建OpenAPI Bean
     *配置API文档的基本信息
     * 
     * @return OpenAPI实例
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能客服工单系统 API")
                        .description("基于AI的智能客服工单系统，支持Function Calling")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("AI Support Team")
                                .email("support@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
