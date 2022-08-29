package com.example.common.http;


import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class HttpDisposableObserver<T> extends DisposableObserver<T> {
    protected OnResponseListener<T> listener;

    private HttpDisposableObserver() {
    }

    public HttpDisposableObserver(OnResponseListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public void onNext(@NonNull T t) {
        BaseResponse baseResponse = (BaseResponse) t;
        if (listener != null) {
            if (baseResponse.isOk()) {
                // 请求成功, 正确的操作方式, 并消息提示
                listener.onSuccess((T) baseResponse.getBody());
            } else {
                listener.onError(baseResponse.getHeader().getRspcode(), baseResponse.getHeader().getRspmsg());
            }
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (listener != null) {
            if (e instanceof ResponseThrowable) {
                listener.onError(((ResponseThrowable) e).code + "", ((ResponseThrowable) e).message);
            } else {
                listener.onError("-1", e.getMessage());
            }
        }
    }

    @Override
    public void onComplete() {

    }
}