package com.flance.framework.common.core.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebResponse {

    private Boolean success;

    private String msg;

    private Integer code;

    /**
     * 如果加密
     * 则：原数据转 to jsonStr 加密 -> base64字符串
     */
    private Object data;

    /**
     * 签名 = sign(data转base64 + timestamp)
     */
    private String sign;

    private Long timestamp;

    /**
     * 相响应是否加密
     */
    private Integer isEncode;

    /**
     * 加密方式
     */
    private String encodeType;

    /**
     * 签名方式
     */
    private String signType;

    /**
     * debug信息
     */
    private String debug;

    public WebResponse() {

    }

    public WebResponse(Boolean success, String msg, Integer code, Object data, String sign, Long timestamp, Integer isEncode, String encodeType, String signType, String debug) {
        this.success = success;
        this.msg = msg;
        this.code = code;
        this.data = data;
        this.sign = sign;
        this.timestamp = timestamp;
        this.isEncode = isEncode;
        this.encodeType = encodeType;
        this.signType = signType;
        this.debug = debug;
    }

    public static WebResponse getSucceed(Object data) {
        return WebResponse.builder().code(200).success(true).msg("请求成功").data(data).build();
    }

    public static WebResponse getSucceed(Object data, String msg) {
        return WebResponse.builder().code(200).success(true).msg(msg).data(data).build();
    }

    public static WebResponse getFailed(Integer code, String msg) {
        return WebResponse.builder().code(code).success(false).msg(msg).build();
    }

    public static WebResponse getFailed(Object data, Integer code, String msg) {
        return WebResponse.builder().data(data).code(code).success(false).msg(msg).build();
    }

    public static WebResponse getFailedDebug(Integer code, String msg) {
        return WebResponse.builder().code(code).success(false).msg(msg).build();
    }

    public static WebResponse getFailedDebug(Integer code, String msg, String debug) {
        return WebResponse.builder().code(code).success(false).msg(msg).debug(debug).build();
    }

    public static WebResponse getFailedDebug(Object data, Integer code, String msg, String debug) {
        return WebResponse.builder().data(data).code(code).success(false).msg(msg).debug(debug).build();
    }

}
