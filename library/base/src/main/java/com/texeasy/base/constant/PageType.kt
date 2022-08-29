package com.texeasy.base.constant

import androidx.annotation.IntDef

/**
 * 页面类型
 * @author caozhihuang
 */
@IntDef(PageType.NO_PAGE, PageType.DEVICE_SETTINGS, PageType.BASIC_SETTINGS)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class PageType {
    companion object {
        //无页面
        const val NO_PAGE = 0

        //设备设置页面
        const val DEVICE_SETTINGS = 1

        //基础设置界面
        const val BASIC_SETTINGS = 2
    }
}

/**
 * 上柜认证页面类型
 * @author caozhihuang
 */
@IntDef(PutCabinetPageType.NO_PAGE, PutCabinetPageType.CARD_LOGIN, PutCabinetPageType.ADMIN_LOGIN)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class PutCabinetPageType {
    companion object {
        //无页面
        const val NO_PAGE = 0

        //刷卡登录
        const val CARD_LOGIN = 1

        //管理员登录
        const val ADMIN_LOGIN = 2
    }
}

/**
 *操作类型: 【1：布草上柜；2：布草领用；3：布草归还；4：布草回收】
 * @author caozhihuang
 */
@IntDef(
    LinenOperationType.LINEN_PUT,
    LinenOperationType.LINEN_COLLAR,
    LinenOperationType.LINEN_RETURN,
    LinenOperationType.LINEN_RECYCLING
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class LinenOperationType {
    companion object {
        const val LINEN_PUT = 1
        const val LINEN_COLLAR = 2
        const val LINEN_RETURN = 3
        const val LINEN_RECYCLING = 4
    }
}