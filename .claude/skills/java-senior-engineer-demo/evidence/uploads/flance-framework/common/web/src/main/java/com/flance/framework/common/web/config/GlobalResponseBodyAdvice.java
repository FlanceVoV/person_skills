package com.flance.framework.common.web.config;

import com.alibaba.fastjson2.JSONObject;
import com.flance.framework.common.core.exception.WebException;
import com.flance.framework.common.core.response.WebResponse;
import com.flance.framework.common.core.utils.UrlMatchUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {


    @Resource
    private GlobalResponseBodyAdviceConfig globalResponseBodyAdviceConfig;

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> clazz) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter methodParameter,
                                  MediaType mediaType, @NonNull Class<? extends HttpMessageConverter<?>> clazz,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        Object result  = null;
        if(mediaType.includes(MediaType.APPLICATION_OCTET_STREAM)){
            return body;
        }
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (body instanceof WebResponse ||
            ignore(request.getURI().getPath())) {
            result = body;
        } else if (body instanceof String) {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(WebResponse.getSucceed(body));
        } else if (body instanceof Map) {
            Object status = ((Map<?, ?>) body).get("status");
            if (null != status && !status.toString().equals("200")) {
                try {
                    log.info("[{}] 自定义异常转换 -> [{}]", request.getURI() ,request.getClass());
                    ServletServerHttpRequest sReq = (ServletServerHttpRequest) request;
                    log.info("自定义异常转换 转换 ServletServerHttpRequest -> [{}]", sReq.getClass());
                    log.info("自定义异常转换 解析 ErrorAttributes -> [{}]", sReq.getServletRequest().getAttribute("org.springframework.boot.web.servlet.error.ErrorAttributes.error").getClass());
                    WebException webException = (WebException) sReq.getServletRequest().getAttribute("org.springframework.boot.web.servlet.error.ErrorAttributes.error");
                    log.info("自定义异常转换 转换 BaseException -> [{}]", webException.getClass());
                    result = webException.getResponse();
                    log.info("自定义异常转换 完成 result -> [{}]", JSONObject.toJSONString(result));
                } catch (Exception e) {
                    log.error("自定义异常转换 失败 err -> [{}]", e.getMessage());
                    result = WebResponse.getFailedDebug(-1, "请求失败[" + e.getMessage() + "]");
                }
            } else {
                result = WebResponse.getSucceed(body);
            }
        } else {
            try {
                result = WebResponse.getSucceed(body);
            } catch (Exception e) {
                e.printStackTrace();
                result = WebResponse.getFailed(-1, "请求失败[" + e.getMessage() + "]");
            }

        }
        // 不含null值
        String resultStr = JSONObject.toJSONString(result);
        String url = ((ServletServerHttpRequest) request).getServletRequest().getRequestURI();
        if (!url.startsWith("/actuator")) {
            if (resultStr.length() < 1000) {
                log.info("接口响应：" + resultStr);
            } else {
                log.info("接口响应：" + resultStr.substring(0, 1000) + "...");
                log.debug("接口响应：" + resultStr);
            }
        }
        return result;
    }


    private boolean ignore(String url) {
        if (null != globalResponseBodyAdviceConfig.getIgnoreUrls()) {
            return UrlMatchUtil.matchUrl(url, globalResponseBodyAdviceConfig.getIgnoreUrls());
        }
        return false;
    }


}
