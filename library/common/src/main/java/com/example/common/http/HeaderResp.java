package com.example.common.http;

class HeaderResp {
    /**
     * 接口处理状态代码
     */
    private String rspcode;
    /**
     * 处理结果描述
     */
    private String rspmsg;

    public String getRspcode() {
        return rspcode;
    }

    public void setRspcode(String rspcode) {
        this.rspcode = rspcode;
    }

    public String getRspmsg() {
        return rspmsg;
    }

    public void setRspmsg(String rspmsg) {
        this.rspmsg = rspmsg;
    }
}
