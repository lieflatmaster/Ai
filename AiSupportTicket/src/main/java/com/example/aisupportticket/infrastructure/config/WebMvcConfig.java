package com.example.aisupportticket.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Web MVC配置类
 *配置Spring MVC相关设置，包括消息转换器和内容协商
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置消息转换器
     *添加字符串消息转换器，支持UTF-8编码和SSE流式响应
     * 
     * @param converters 消息转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setSupportedMediaTypes(List.of(
                MediaType.TEXT_PLAIN,
                MediaType.TEXT_HTML,
                MediaType.TEXT_EVENT_STREAM,
                new MediaType("text", "event-stream", StandardCharsets.UTF_8),
                MediaType.APPLICATION_JSON
        ));
        converters.add(0, stringConverter);
    }

    /**
     * 配置内容协商
     *设置默认响应格式和支持的媒体类型
     * 
     * @param configurer 内容协商配置器
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("html", MediaType.TEXT_HTML)
                .mediaType("plain", MediaType.TEXT_PLAIN)
                .mediaType("event-stream", MediaType.TEXT_EVENT_STREAM);
    }
}
