package com.flance.framework.common.core.exception;


import com.flance.framework.common.core.response.WebResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebException extends RuntimeException {

    protected String msg;

    protected int code;

    public WebException() {

    }

    public WebException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public static WebException getNormal(int code, String msg) {
        WebException webException = new WebException();
        webException.setCode(code);
        webException.setMsg(msg);
        return webException;
    }

    public WebResponse getResponse() {
        return WebResponse.getFailed(code, msg);
    }

}
