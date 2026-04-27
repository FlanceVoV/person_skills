package com.flance.framework.common.core.exception;

import com.flance.framework.common.core.response.WebResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatasourceException extends RuntimeException {

    protected String msg;

    protected int code;

    public DatasourceException() {

    }

    public DatasourceException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public static DatasourceException getNormal(int code, String msg) {
        DatasourceException webException = new DatasourceException();
        webException.setCode(code);
        webException.setMsg(msg);
        return webException;
    }

    public WebResponse getResponse() {
        return WebResponse.getFailed(code, msg);
    }
}
