package com.hujunchina.middleware.api;

public enum StatusCode {

    Success(0, "成功"),
    Fail(-1, "失败"),
    InvalidPrams(201, "非法参数"),
    InvalidGrantType(202, "非法授权类型");

    private Integer code;
    private String msg;
    StatusCode(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
