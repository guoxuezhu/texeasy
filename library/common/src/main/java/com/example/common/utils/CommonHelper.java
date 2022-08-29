package com.example.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class CommonHelper {

    public static void hideSoftInputFromWindow(Context context) {
        if (((Activity) context).getCurrentFocus() != null && ((Activity) context).getCurrentFocus().getWindowToken() != null) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public static boolean isTablet(Context context) {
        try {
            context = context.getApplicationContext();
            return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getDeviceId(Context context) {
        String imei = UUID.randomUUID().toString();
        if (imei == null) {
            imei = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return imei;
    }

    /**
     * 限制仅输入中文和数字
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String stringFilter2(String str) throws PatternSyntaxException {
        //只允许数字和汉字
        String regEx = "[^0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public interface SaveCallback {
        void onSucceed(String path);
    }

    /**
     * 通过View获取Activity
     *
     * @param view
     * @return
     */
    public static Activity getActivityByView(View view) {
        if (null != view) {
            Context context = view.getContext();
            if (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
            }
        }
        return null;
    }

    //这种方法状态栏是空白，显示不了状态栏的信息
    public static Bitmap getViewBitmap(View rootView) {
        //获取当前屏幕的大小
        int width = rootView.getWidth();
        int height = rootView.getHeight();
        if (width == 0 || height == 0)
            return null;
        //生成相同大小的图片
        Bitmap temBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //找到当前页面的跟布局
        //设置缓存
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        temBitmap = rootView.getDrawingCache();

        return temBitmap;
    }

    /**
     * 获取当前UTC时间
     *
     * @return
     */
    public static String getUtcTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        return sdf.format(new Date());
    }

}
