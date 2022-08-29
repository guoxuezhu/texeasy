package com.example.common.http;

/**
 * Created by goldze on 2017/5/10.
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
public class BaseResponse<T> {
    private HeaderResp header;
    private T body;

    public HeaderResp getHeader() {
        return header;
    }

    public void setHeader(HeaderResp header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public boolean isOk() {
        return header != null && "00".equals(header.getRspcode());
    }

    public String getMessage() {
        if (header == null) {
            return null;
        }
        return header.getRspmsg();
    }

    public String getCode() {
        if (header == null) {
            return null;
        }
        return header.getRspcode();
    }
}
