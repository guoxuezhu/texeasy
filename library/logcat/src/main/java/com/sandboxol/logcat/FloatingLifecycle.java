package com.sandboxol.logcat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/Logcat
 * time   : 2020/01/24
 * desc   : 悬浮窗生命控制
 */
final class FloatingLifecycle implements Application.ActivityLifecycleCallbacks {

    private static boolean isShowFloatingWindow;
    private static FloatingWindow floatingWindow;
    private static FloatingWindow homeFloatingWindow;

    static void with(Application application) {
        application.registerActivityLifecycleCallbacks(new FloatingLifecycle());
    }

    static void setFloatingWindow(boolean isShow) {
        isShowFloatingWindow = isShow;
        if (floatingWindow != null) {
            if (isShow) {
                floatingWindow.show();
            } else {
                floatingWindow.cancel();
            }
        }
        if (homeFloatingWindow != null) {
            if (isShow) {
                homeFloatingWindow.show();
            } else {
                homeFloatingWindow.cancel();
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof LogcatActivity) {
            return;
        }
        if ("com.texeasy.view.activity.home.HomeActivity".equals(activity.getLocalClassName())) {
            homeFloatingWindow = new FloatingWindow(activity);
            if (isShowFloatingWindow) {
                homeFloatingWindow.show();
            }
        } else {
            floatingWindow = new FloatingWindow(activity);
            if (isShowFloatingWindow) {
                floatingWindow.show();
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (floatingWindow != null) {
            floatingWindow.cancel();
            floatingWindow = null;
        }
        if ("com.texeasy.view.activity.home.HomeActivity".equals(activity.getLocalClassName())) {
            if (homeFloatingWindow != null) {
                homeFloatingWindow.cancel();
                homeFloatingWindow = null;
            }
        }
    }
}