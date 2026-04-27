package com.flance.framework.modules.webflux.config;

import com.flance.framework.common.core.response.WebResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GlobalWebfluxResponseHandler extends ResponseBodyResultHandler {

    public GlobalWebfluxResponseHandler(ServerCodecConfigurer codecConfigurer,
                                        RequestedContentTypeResolver resolver,
                                        ReactiveAdapterRegistry registry) {
        super(codecConfigurer.getWriters(), resolver, registry);
    }

    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();
        MethodParameter returnType = result.getReturnTypeSource();
        MediaType contentType = exchange.getResponse().getHeaders().getContentType();

        // 已经包装过，直接返回
        if (returnValue instanceof WebResponse) {
            return super.handleResult(exchange, result);
        }

        ReactiveAdapter adapter = getAdapterRegistry().getAdapter(
                ResolvableType.forMethodParameter(returnType).resolve(),
                returnValue
        );

        // 不管是不是 SSE，全部统一包装
        // 包装后必须保持原始类型：Flux -> Flux，Mono -> Mono
        Object wrappedResult;

        if (adapter != null) {
            if (adapter.isMultiValue()) {
                // Flux 类型 → 包装成 Flux<WebResponse>
                Flux<?> flux = Flux.from(adapter.toPublisher(returnValue));
                wrappedResult = flux.map(WebResponse::getSucceed);
            } else {
                // Mono 类型 → 包装成 Mono<WebResponse>
                Mono<?> mono = Mono.from(adapter.toPublisher(returnValue));
                wrappedResult = mono.map(WebResponse::getSucceed);
            }
        } else {
            // 普通对象
            wrappedResult = WebResponse.getSucceed(returnValue);
        }

        // 构造新的返回值
        HandlerResult newResult = new HandlerResult(
                result.getHandler(),
                wrappedResult,
                result.getReturnTypeSource(),
                result.getBindingContext()
        );

        return super.handleResult(exchange, newResult);
    }

}
