package com.example.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.common.view.activity.TemplateActivity;

public class TemplateUtils {

    public static void startTemplate(Context context, Class objClass, String title) {
        startTemplate(context, objClass, title, -1);
    }

    public static void startTemplate(Context context, Class objClass, String title, int rightResId) {
        startTemplate(context, objClass, title, -1, rightResId);
    }

    public static void startTemplate(Context context, Class objClass, String title, int leftResId, int rightResId) {
        startTemplate(context, objClass, title, leftResId, rightResId, null);
    }

    public static void startTemplate(Context context, Class objClass, String title, String rightText) {
        startTemplate(context, objClass, title, -1, rightText);
    }

    public static void startTemplate(Context context, Class objClass, String title, int leftResId, String rightText) {
        startTemplate(context, objClass, title, leftResId, rightText, null);
    }

    public static void startTemplate(Context context, Class objClass, String title, Bundle params) {
        startTemplate(context, objClass, title, -1, params);
    }

    public static void startTemplate(Context context, Class objClass, String title, int rightResId, Bundle params) {
        startTemplate(context, objClass, title, -1, rightResId, params);
    }

    public static void startTemplate(Context context, Class objClass, String title, String rightText, Bundle params) {
        startTemplate(context, objClass, title, -1, rightText, params);
    }

    public static void startTemplateForResult(Activity activity, Class objClass, String title, int requestCode) {
        startTemplateForResult(activity, objClass, title, -1, requestCode);
    }

    public static void startTemplateForResult(Activity activity, Class objClass, String title, int rightResId, int requestCode) {
        startTemplateForResult(activity, objClass, title, -1, rightResId, requestCode);
    }

    public static void startTemplateForResult(Activity activity, Class objClass, String title, int leftResId, int rightResId, int requestCode) {
        startTemplateForResult(activity, objClass, title, leftResId, rightResId, null, requestCode);
    }

    public static void startTemplateForResult(Activity activity, Class objClass, String title, String rightText, int requestCode) {
        startTemplateForResult(activity, objClass, title, -1, rightText, requestCode);
    }

    public static void startTemplateForResult(Activity activity, Class objClass, String title, int leftResId, String rightText, int requestCode) {
        startTemplateForResult(activity, objClass, title, leftResId, rightText, null, requestCode);
    }

    public static void startTemplateForResult(Activity activity, Class objClass, String title, Bundle params, int requestCode) {
        startTemplateForResult(activity, objClass, title, -1, params, requestCode);
    }

    public static void startTemplateForResult(Activity activity, Class objClass, String title, int rightResId, Bundle params, int requestCode) {
        startTemplateForResult(activity, objClass, title, -1, rightResId, params, requestCode);
    }

    /**
     * ????????????Activity
     *
     * @param context
     * @param objClass   ??????Activity???Fragment??????
     * @param title      ??????Activity?????????
     * @param leftResId  ??????Activity???????????????Id
     * @param rightResId ??????Activity???????????????Id
     * @param params     ??????Activity???Fragment???????????????
     */
    public static void startTemplate(Context context, Class objClass, String title, int leftResId, int rightResId, Bundle params) {
        context.startActivity(new Intent(context, TemplateActivity.class)
                .putExtra(TemplateActivity.NAME, objClass)
                .putExtra(TemplateActivity.TITLE, title)
                .putExtra(TemplateActivity.PARAMS, params)
                .putExtra(TemplateActivity.LEFT_RESOURCE_ID, leftResId)
                .putExtra(TemplateActivity.RIGHT_RESOURCE_ID, rightResId));
    }

    /**
     * ????????????Activity
     *
     * @param context
     * @param objClass  ??????Activity???Fragment??????
     * @param title     ??????Activity?????????
     * @param leftResId ??????Activity???????????????Id
     * @param rightText ??????Activity???????????????
     * @param params    ??????Activity???Fragment???????????????
     */
    public static void startTemplate(Context context, Class objClass, String title, int leftResId, String rightText, Bundle params) {
        context.startActivity(new Intent(context, TemplateActivity.class)
                .putExtra(TemplateActivity.NAME, objClass)
                .putExtra(TemplateActivity.TITLE, title)
                .putExtra(TemplateActivity.PARAMS, params)
                .putExtra(TemplateActivity.LEFT_RESOURCE_ID, leftResId)
                .putExtra(TemplateActivity.RIGHT_RESOURCE_ID, -1)
                .putExtra(TemplateActivity.RIGHT_TEXT, rightText));
    }

    /**
     * ????????????Activity???????????????????????????
     *
     * @param activity
     * @param objClass    ??????Activity???Fragment??????
     * @param title       ??????Activity?????????
     * @param leftResId   ??????Activity???????????????Id
     * @param rightResId  ??????Activity???????????????Id
     * @param params      ??????Activity???Fragment???????????????
     * @param requestCode ??????Activity??????Code
     */
    public static void startTemplateForResult(Activity activity, Class objClass, String title, int leftResId, int rightResId, Bundle params, int requestCode) {
        activity.startActivityForResult(new Intent(activity, TemplateActivity.class)
                .putExtra(TemplateActivity.NAME, objClass)
                .putExtra(TemplateActivity.TITLE, title)
                .putExtra(TemplateActivity.PARAMS, params)
                .putExtra(TemplateActivity.LEFT_RESOURCE_ID, leftResId)
                .putExtra(TemplateActivity.RIGHT_RESOURCE_ID, rightResId), requestCode);
    }

    /**
     * ????????????Activity???????????????????????????
     *
     * @param activity
     * @param objClass    ??????Activity???Fragment??????
     * @param title       ??????Activity?????????
     * @param leftResId   ??????Activity???????????????Id
     * @param rightText   ??????Activity???????????????
     * @param params      ??????Activity???Fragment???????????????
     * @param requestCode ??????Activity??????Code
     */
    public static void startTemplateForResult(Activity activity, Class objClass, String title, int leftResId, String rightText, Bundle params, int requestCode) {
        activity.startActivityForResult(new Intent(activity, TemplateActivity.class)
                .putExtra(TemplateActivity.NAME, objClass)
                .putExtra(TemplateActivity.TITLE, title)
                .putExtra(TemplateActivity.PARAMS, params)
                .putExtra(TemplateActivity.LEFT_RESOURCE_ID, leftResId)
                .putExtra(TemplateActivity.RIGHT_RESOURCE_ID, -1)
                .putExtra(TemplateActivity.RIGHT_TEXT, rightText), requestCode);
    }

    public static void startTemplateWithFlags(Context context, Class objClass, String title, int flags) {
        context.startActivity(new Intent(context, TemplateActivity.class)
                .putExtra(TemplateActivity.NAME, objClass)
                .putExtra(TemplateActivity.TITLE, title)
                .putExtra(TemplateActivity.LEFT_RESOURCE_ID, -1)
                .putExtra(TemplateActivity.RIGHT_RESOURCE_ID, -1)
                .addFlags(flags));
    }

}
