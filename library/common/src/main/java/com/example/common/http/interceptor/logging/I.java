package com.example.common.http.interceptor.logging;


import com.example.common.utils.KLog;
import com.lazy.library.logging.Logcat;

import okhttp3.internal.platform.Platform;

/**
 * @author ihsan on 10/02/2017.
 */
class I {

    protected I() {
        throw new UnsupportedOperationException();
    }

    static void log(int type, String tag, String msg) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(tag);
        switch (type) {
            case Platform.INFO:
                Logcat.i(KLog.TAG, msg);
                break;
            default:
                Logcat.w(KLog.TAG, msg);
                break;
        }
    }
}
