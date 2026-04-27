package com.flance.framework.common.web.config;

import com.flance.framework.common.core.exception.DatasourceException;
import com.flance.framework.common.core.exception.WebException;
import com.flance.framework.common.core.response.WebResponse;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 全局异常捕捉处理
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public WebResponse errorHandler(Throwable ex) {
        log.error("未知异常[{}]", ex.toString());
        ex.printStackTrace();
        return WebResponse.getFailedDebug(-1, "未知异常", "未知异常，[{" + ex.getMessage() + "}]");
    }

    @ResponseBody
    @ExceptionHandler(value = WebException.class)
    public WebResponse errorHandler(WebException ex) {
        log.error("运行时异常{}]", ex.toString());
        ex.printStackTrace();
        return WebResponse.getFailed(-1, "业务异常[" + ex.getMsg() + "]");
    }


    @ResponseBody
    @ExceptionHandler(value = DatasourceException.class)
    public WebResponse errorHandler(DatasourceException ex) {
        log.error("运行时异常{}]", ex.toString());
        ex.printStackTrace();
        return WebResponse.getFailed(-1, "数据库异常[" + ex.getMsg() + "]");
    }


    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public WebResponse errorHandler(RuntimeException ex) {
        log.error("运行时异常{}]", ex.toString());
        ex.printStackTrace();
        return WebResponse.getFailed(-1, "业务异常[" + ex.getMessage() + "]");
    }

    /**
     * 请求方式不支持，全局异常捕捉处理
     */
    @ResponseBody
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public WebResponse errorHandler(HttpRequestMethodNotSupportedException ex) {
        log.error("请求方式不支持[{}]", ex.getMethod());
        ex.printStackTrace();
        return WebResponse.getFailed(-1, "请求方式不支持");
    }


    /**
     * 参数校验异常，全局异常捕捉处理
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public WebResponse errorHandler(MethodArgumentNotValidException ex) {
        log.error("参数校验异常[{}]", ex.getMessage());
        ex.printStackTrace();
        List<ObjectError> errors = ex.getAllErrors();
        List<String> errorMsgs = Lists.newArrayList();
        for (ObjectError error : errors) {
            errorMsgs.add(error.getDefaultMessage());
        }
        return WebResponse.getFailed(-1, "参数校验异常[{" + errorMsgs + "}]");
    }

    @ResponseBody
    @ExceptionHandler(value = IllegalStateException.class)
    public WebResponse errorHandler(IllegalStateException ex) {
        log.error("系统异常[{}]", ex.getMessage());
        return WebResponse.getFailed(-1, "系统异常，[IllegalStateException]");
    }

}
