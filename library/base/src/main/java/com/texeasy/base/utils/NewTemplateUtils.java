package com.texeasy.base.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.common.view.activity.TemplateActivity;
import com.texeasy.base.ui.activity.NewTemplateActivity;

public class NewTemplateUtils {

    /**
     * 启动模板Activity
     *
     * @param context
     * @param objClass 模板Activity中Fragment键名
     * @param title    模板Activity的标题
     */
    public static void startTemplate(Context context, Class objClass, String title) {
        context.startActivity(new Intent(context, NewTemplateActivity.class)
                .putExtra(NewTemplateActivity.NAME, objClass)
                .putExtra(NewTemplateActivity.TITLE, title));
    }

    /**
     * 启动模板Activity
     *
     * @param context
     * @param objClass 模板Activity中Fragment键名
     * @param title    模板Activity的标题
     * @param params   模板Activity向Fragment传递的参数
     */
    public static void startTemplate(Context context, Class objClass, String title, Bundle params) {
        context.startActivity(new Intent(context, NewTemplateActivity.class)
                .putExtra(NewTemplateActivity.NAME, objClass)
                .putExtra(NewTemplateActivity.TITLE, title)
                .putExtra(NewTemplateActivity.PARAMS, params));
    }

}
