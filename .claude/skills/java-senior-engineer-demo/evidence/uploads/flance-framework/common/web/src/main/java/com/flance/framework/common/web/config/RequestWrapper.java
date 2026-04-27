package com.flance.framework.common.web.config;



import com.flance.framework.common.web.utils.HttpContextUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

public class RequestWrapper extends HttpServletRequestWrapper {

    private String tempBody;

    private final HttpServletRequest request;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        tempBody = HttpContextUtils.getBodyString(request);
        this.request = request;
    }

    @Override
    public String getMethod() {
        return this.request.getMethod();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return this.request.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return this.request.getHeaderNames();
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tempBody.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public int readLine(byte[] b, int off, int len) throws IOException {
                return super.readLine(b, off, len);
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getBody() {
        return this.tempBody;
    }

    public void setBody(String body) {
        this.tempBody = body;
    }

}
