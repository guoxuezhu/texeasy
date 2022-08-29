package com.texeasy.base.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Process
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.common.base.BaseApplication
import com.texeasy.base.R
import com.texeasy.base.widget.OneButtonDialog
import kotlin.system.exitProcess


/**
 * Created by ZivChao on 2021/8/6
 * @author ZivChao
 */
object Helper {
    private fun getRestartIntent() {
        val killIntent =
            BaseApplication.getInstance().packageManager.getLaunchIntentForPackage(BaseApplication.getInstance().packageName);
        killIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        BaseApplication.getInstance().startActivity(killIntent);
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    fun restartApp(context: Context, resInt: Int) {
        OneButtonDialog(context)
            .setDetailText(resInt)
            .setButtonText(R.string.base_com_restart_tip)
            .setAllowBackPress(false)
            .setListener { getRestartIntent() }
            .show()
    }

    /**
     * 显示键盘
     *
     * @param context
     * @param editText
     */
    fun showSoftInput(context: Context, editText: EditText?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param editText
     */
    fun hideSoftInput(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    fun showSoftInputFromWindow(activity: Activity, editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    fun hintKeyBoard(context: Context?) {
        //拿到InputMethodManager
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //如果window上view获取焦点 && view不为空
        var currentFocus = (context as Activity).currentFocus
        if (imm.isActive && (currentFocus) != null) {
            //拿到view的token 不为空
            if (currentFocus.windowToken != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(
                    currentFocus.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }
}