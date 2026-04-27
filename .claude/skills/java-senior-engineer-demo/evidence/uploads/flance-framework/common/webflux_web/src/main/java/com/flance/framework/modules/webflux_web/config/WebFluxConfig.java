package com.flance.framework.modules.webflux_web.config;

import com.flance.framework.modules.webflux.config.GlobalWebfluxResponseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.accept.RequestedContentTypeResolverBuilder;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.reactive.result.view.ViewResolutionResultHandler;

import java.util.Collections;

@Configuration
public class WebFluxConfig {

    // 1. 映射：查找接口
    @Bean("webFluxRequestMappingHandlerMapping")
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    // 2. 适配器：执行接口方法
    @Bean("webFluxRequestMappingHandlerAdapter")
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }

    // 3. 内容类型解析器
    @Bean
    public RequestedContentTypeResolver requestedContentTypeResolver() {
        return new RequestedContentTypeResolverBuilder().build();
    }

    // 4. 消息转换器
    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }

    @Bean
    public ReactiveAdapterRegistry reactiveAdapterRegistry() {
        return ReactiveAdapterRegistry.getSharedInstance();
    }

    // 5. 结果处理器：处理 @ResponseBody 返回值（JSON）
//    @Bean("webFluxResponseResultHandler")
//    public ResponseBodyResultHandler responseBodyResultHandler(RequestedContentTypeResolver resolver, ServerCodecConfigurer codecConfigurer, ReactiveAdapterRegistry reactiveAdapterRegistry) {
//        return new ResponseBodyResultHandler(codecConfigurer.getWriters(), resolver, reactiveAdapterRegistry);
//    }

    // 5. 结果处理器：全局响应
    @Bean("webFluxResponseResultHandler")
    public GlobalWebfluxResponseHandler globalResponseBodyResultHandler(
            RequestedContentTypeResolver resolver,
            ServerCodecConfigurer codecConfigurer,
            ReactiveAdapterRegistry registry) {
        return new GlobalWebfluxResponseHandler(codecConfigurer, resolver, registry);
    }

    // 6. 视图结果处理器（兼容页面渲染，必须加）
    @Bean("webFluxViewResolutionResultHandler")
    public ViewResolutionResultHandler viewResolutionResultHandler(RequestedContentTypeResolver resolver) {
        return new ViewResolutionResultHandler(Collections.emptyList(), resolver);
    }


}
