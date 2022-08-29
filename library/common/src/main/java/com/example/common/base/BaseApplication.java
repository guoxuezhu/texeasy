package com.example.common.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.common.utils.SPUtils;
import com.example.common.utils.Utils;

/**
 * Created by goldze on 2017/6/15.
 */

public class BaseApplication extends Application {
    private static Application sInstance;
    private static int versionCode;
    private static String versionName;
    private static boolean isDebug;
    private static String flavor;
    private static String orgId;
    private static int currentUserId;
    private static String faceAppId;
    private static String faceSdkKey;
    private static String faceActiveKey;

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     *
     * @param application
     */
    public static synchronized void setApplication(@NonNull Application application) {
        sInstance = application;
        //初始化工具类
        Utils.init(application);
        //注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppManager.getAppManager().addActivity(activity);
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
                AppManager.getAppManager().removeActivity(activity);
            }
        });
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("please inherit BaseApplication or call setApplication.");
        }
        return sInstance;
    }

    /**
     * 获取token
     */
    public static String getAuthToken() {
        return SPUtils.getInstance().getString("AuthToken");
    }

    /**
     * 获取token
     */
    public static void setAuthToken(String authToken) {
        SPUtils.getInstance().put("AuthToken", authToken);
    }

    /**
     * 设置版本号
     *
     * @param versionCode
     */
    public static void setVersionCode(int versionCode) {
        BaseApplication.versionCode = versionCode;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode() {
        return versionCode;
    }

    /**
     * 设置版本名称
     *
     * @param versionCode
     */
    public static void setVersionName(String versionCode) {
        BaseApplication.versionName = versionCode;
    }

    /**
     * 获取版本名称
     *
     * @return
     */
    public static String getVersionName() {
        return versionName;
    }

    /**
     * 是否debug模式
     *
     * @return
     */
    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * 是否debug模式
     *
     * @return
     */
    public static void setDebug(boolean isDebug) {
        BaseApplication.isDebug = isDebug;
    }

    /**
     * 获取渠道
     *
     * @return
     */
    public static String getFlavor() {
        return flavor;
    }

    /**
     * 设置渠道
     *
     * @param flavor
     */
    public static void setFlavor(String flavor) {
        BaseApplication.flavor = flavor;
    }

    /**
     * 获取机构id
     *
     * @return
     */
    public static String getOrgId() {
        return orgId;
    }

    /**
     * 设置机构id
     *
     * @param orgId
     */
    public static void setOrgId(String orgId) {
        BaseApplication.orgId = orgId;
    }

    /**
     * 获取当前登录的用户id
     *
     * @return
     */
    public static int getCurrentUserId() {
        return currentUserId;
    }

    /**
     * 设置当前登录的用户id
     *
     * @param currentUserId
     */
    public static void setCurrentUserId(int currentUserId) {
        BaseApplication.currentUserId = currentUserId;
    }

    /**
     * 获取人脸appId
     *
     * @return
     */
    public static String getFaceAppId() {
        return faceAppId;
    }

    /**
     * 设置人脸appId
     *
     * @param faceAppId
     */
    public static void setFaceAppId(String faceAppId) {
        BaseApplication.faceAppId = faceAppId;
    }

    /**
     * 获取人脸SdkKey
     *
     * @return
     */
    public static String getFaceSdkKey() {
        return faceSdkKey;
    }

    /**
     * 设置人脸SdkKey
     *
     * @param faceSdkKey
     */
    public static void setFaceSdkKey(String faceSdkKey) {
        BaseApplication.faceSdkKey = faceSdkKey;
    }

    /**
     * 获取人脸ActiveKey
     *
     * @return
     */
    public static String getFaceActiveKey() {
        return faceActiveKey;
    }

    /**
     * 设置人脸ActiveKey
     *
     * @param faceActiveKey
     */
    public static void setFaceActiveKey(String faceActiveKey) {
        BaseApplication.faceActiveKey = faceActiveKey;
    }
}
