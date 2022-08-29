package com.texeasy.face

import android.content.Context
import android.os.Environment
import com.arcsoft.face.ActiveFileInfo
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.example.common.utils.StorageUtils
import com.texeasy.face.entity.ACTIVE_CONFIG_FILE_NAME
import com.texeasy.face.entity.APP_ROOT_PATH
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

object FaceManager {
    private var DEFAULT_AUTH_FILE_PATH: String? = null

    /**
     * 人脸是否已激活成功
     */
    fun isActivated(context: Context): Boolean {
        val isActivated = FaceEngine.getActiveFileInfo(context, ActiveFileInfo()) == ErrorInfo.MOK
        return isActivated
    }

    fun activeOnline(context: Context, activeKey: String, appId: String, sdkKey: String): Int {
        val result =
            FaceEngine.activeOnline(context, activeKey, appId, sdkKey)
        return result
    }

    fun loadProperties(): Properties? {
        val properties = Properties()
        var fis: FileInputStream? = null
        return try {
            StorageUtils.getDiskDir(APP_ROOT_PATH)
            val configFile =
                File(
                    Environment.getExternalStorageDirectory().absolutePath +
                            File.separator + APP_ROOT_PATH + File.separator + ACTIVE_CONFIG_FILE_NAME
                )
            fis = FileInputStream(configFile)
            properties.load(fis)
            properties
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}