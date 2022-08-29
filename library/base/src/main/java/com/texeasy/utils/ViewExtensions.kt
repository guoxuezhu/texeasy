package com.texeasy.utils

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.common.base.BaseApplication
import com.texeasy.base.R

/**
 * 涉及view的扩展方法
 * Created by ZivChao on 2021/6/13
 * @author ZivChao
 */
class ViewExtensions {
    companion object {
        /**
         * 获取配送员对话框左侧tab背景
         */
        @JvmStatic
        fun getShipperDialogLeftTabDrawable(isCard: Boolean): Drawable? {
            return if (isCard) {
                ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_dialog_shipper_left_selected_shape
                )
            } else {
                ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_dialog_shipper_left_shape
                )
            }
        }

        /**
         * 获取配送员对话框右侧tab背景
         */
        @JvmStatic
        fun getShipperDialogRightTabDrawable(isCard: Boolean): Drawable? {
            return if (isCard) {
                ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_dialog_shipper_right_shape
                )
            } else {
                ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_dialog_shipper_right_selected_shape
                )
            }
        }

        /**
         * 获取配送员对话框tab字体颜色
         */
        @JvmStatic
        fun getShipperDialogTabTextColor(isCard: Boolean): Int {
            return if (isCard) {
                ContextCompat.getColor(
                    BaseApplication.getInstance(),
                    R.color.colorAccent
                )
            } else {
                ContextCompat.getColor(
                    BaseApplication.getInstance(),
                    R.color.textColorPrimary
                )
            }
        }

        /**
         * 获取主页面背景
         */
        @JvmStatic
        fun getHomeContentDrawable(): Drawable? {
            return when (BaseApplication.getFlavor()) {
                "comprehensive" -> ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_bg_comprehensive
                )
                "department" -> ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_bg_department_2
                )
                "dirty" -> ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_bg_dirty
                )
                "shoe" -> ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_bg_comprehensive
                )
                else -> ContextCompat.getDrawable(
                    BaseApplication.getInstance(),
                    R.drawable.base_bg_comprehensive
                )
            }
        }

        /**
         * 获取主页面底部名字
         */
        @JvmStatic
        fun getHomeBottomName(): String {
            return when (BaseApplication.getFlavor()) {
                "comprehensive" -> "医院自助综合系统"
                "department" -> "医院自助发衣柜系统"
                "dirty" -> "医院自助收脏系统"
                "shoe" -> "医院自助发鞋系统"
                else -> "医院自助综合系统"
            }
        }

        /**
         * 获取主页面顶部名字
         */
        @JvmStatic
        fun getHomeTopName(): String {
            return when (BaseApplication.getFlavor()) {
                "comprehensive" -> "智慧医院智能综合机"
                "department" -> "智慧医院智能发衣机"
                "dirty" -> "智慧医院智能收脏机"
                "shoe" -> "智慧医院智能发鞋机"
                else -> "智慧医院智能综合机"
            }
        }
    }
}