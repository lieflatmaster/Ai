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
        // 1. 创建一个专门处理字符串类型的消息转换器，并强制指定使用 UTF-8 字符集
        // 这样可以防止中文在传输过程中出现乱码
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        // 2. 设置该转换器支持的媒体类型（Content-Type）
        stringConverter.setSupportedMediaTypes(List.of(
                MediaType.TEXT_PLAIN,          // 支持纯文本格式
                MediaType.TEXT_HTML,           // 支持 HTML 格式
                MediaType.TEXT_EVENT_STREAM,   // 【核心】支持 SSE 流式响应格式 (text/event-stream)
                new MediaType("text", "event-stream", StandardCharsets.UTF_8), // 显式声明带 UTF-8 编码的 event-stream
                MediaType.APPLICATION_JSON     // 支持 JSON 格式
        ));
        // 3. 将配置好的转换器添加到列表的最前面（索引 0）
        // Spring MVC 会按顺序匹配转换器，放在最前面可以确保它拥有最高优先级
        // 这样当 Controller 返回 String 类型且请求头是 text/event-stream 时，会优先使用这个转换器
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
                // 禁止通过 URL 参数（如 ?format=json）来决定响应格式。
                // 这样可以让 URL 保持RESTful风格，更干净（例如 /api/tickets 而不是 /api/tickets?format=json）
                .favorParameter(false)

                // 不忽略请求头中的 "Accept" 字段。
                // 这意味着服务器会尊重前端发送的 Accept: text/event-stream 或 Accept: application/json 来返回对应格式
                .ignoreAcceptHeader(false)

                // 设置默认响应格式为 JSON。
                // 如果前端没指定想要什么格式，或者服务器无法匹配，就统一返回 JSON
                .defaultContentType(MediaType.APPLICATION_JSON)

                // 注册支持的媒体类型映射。
                // 这些配置允许在特定场景下（如使用扩展名 .json 或 .xml 时）正确识别内容类型
                .mediaType("json", MediaType.APPLICATION_JSON)       // 支持 .json 后缀
                .mediaType("xml", MediaType.APPLICATION_XML)         // 支持 .xml 后缀
                .mediaType("html", MediaType.TEXT_HTML)              // 支持 .html 后缀
                .mediaType("plain", MediaType.TEXT_PLAIN)            // 支持 .plain 后缀
                .mediaType("event-stream", MediaType.TEXT_EVENT_STREAM); // 【核心】支持 .event-stream 后缀，确保 SSE 流式响应能被正确识别
    }
}
