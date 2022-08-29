package com.example.common.http;

/**
 * Created by Jimmy on 2017/9/27 0027.
 */
public abstract class OnResponseListener<D> {

    private int retryTimes = 1;

    public abstract void onSuccess(D data);

    public abstract void onError(String rspcode, String rspmsg);

    public boolean needRetry() {
        return retryTimes > 0;
    }

    public void retry() {
        retryTimes--;
    }

}