package com.texeasy.hardware.hzdongcheng;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.databinding.ObservableField;

import com.example.common.utils.KLog;
import com.google.gson.Gson;
import com.hzdongcheng.drivers.bean.Result;
import com.hzdongcheng.drivers.peripheral.IObserver;
import com.lazy.library.logging.Logcat;
import com.texeasy.base.constant.ConfigCenter;
import com.texeasy.base.widget.picker.PickerDialog;
import com.texeasy.hardware.hzdongcheng.entity.BoxStatus;
import com.texeasy.hardware.hzdongcheng.entity.SlaveStatus;
import com.texeasy.hardware.hzdongcheng.listener.ICallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzq on 2018/9/13
 *
 * @author Zhihu
 */
public class HzHardwareManager {
    private static ObservableField<Boolean> isDebug = ConfigCenter.newInstance().isDebug;
    private static final String TAG = "HAL -> ";
    public static final String CONNECT_SUCCESS = "CONNECT_SUCCESS";
    private static ICallBack scannerCallBack;
    private static ICallBack cardReadCallBack;

    public static boolean init(Context context) {
        return HzHardwareProvider.getInstance().bind(context);
    }

    public static void release() {
        HzHardwareProvider.getInstance().unBind();
    }

    //设置扫描枪回调
    public static void setScannerCallBack(ICallBack _callBack) {
        scannerCallBack = _callBack;
    }

    //设置读卡器回调
    public static void setCardReadCallBack(ICallBack _callBack) {
        cardReadCallBack = _callBack;
    }

    //设置读卡器回调
    public static void removeCardReadCallBack() {
        cardReadCallBack = null;
    }

    //#region 扫描枪
    public static IObserver scannerObserver = new IObserver.Stub() {
        @Override
        public void onMessage(String msg) throws RemoteException {
            if (scannerCallBack != null) {
                scannerCallBack.onMessage(msg, 0);
            }
        }
    };

    //#region 读卡器
    public static IObserver cardReadObserver = new IObserver.Stub() {
        @Override
        public void onMessage(String msg) throws RemoteException {
            if (cardReadCallBack != null) {
                cardReadCallBack.onMessage(msg, 1);
            }
        }
    };

    public static boolean isIsDebug() {
        return isDebug.get();
    }

    /**
     * 打开指定箱门
     *
     * @param boxName 格口名称
     * @return 打开结果
     */
    public static boolean openBox(String boxName) {
        try {
            Logcat.d(KLog.TAG, TAG + "打开箱门: ");
            if (HzHardwareProvider.getInstance().getSlaveController() != null) {
                Result result = HzHardwareProvider.getInstance().getSlaveController().openBoxByName(boxName);
                if (result.getCode() == 0) {
                    Logcat.d(KLog.TAG, TAG + "[HAL] 打开箱门成功：" + boxName);
                    return true;
                }
                Logcat.e(KLog.TAG, TAG + "[HAL] 箱门打开失败：" + boxName + ",code " + result.getCode());
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 开箱服务调用失败，箱门 " + boxName);
        }
        return isDebug.get();
    }


    /**
     * 获取箱门状态
     *
     * @param boxName
     * @return
     */
    public static BoxStatus getBoxStatus(String boxName) {
        try {
            if (HzHardwareProvider.getInstance().getSlaveController() != null) {
                Result result = HzHardwareProvider.getInstance().getSlaveController().queryBoxStatusByName(boxName);
                if (result.getCode() == 0) {
                    Logcat.e(KLog.TAG, TAG + "[HAL] 获取格口状态成功 -->" + new Gson().toJson(result));
                    return new Gson().fromJson(result.getData(), BoxStatus.class);
                }
                Logcat.e(KLog.TAG, TAG + "[HAL] 获取格口状态失败 boxName " + boxName);
            }
            BoxStatus boxStatus = new BoxStatus();
            if (isDebug.get()) {
                boxStatus.setOpenStatus(BoxStatus.CLOSE);
            }
            return boxStatus;
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 获取格口状态服务出错, boxName " + boxName);
            BoxStatus boxStatus = new BoxStatus();
            if (isDebug.get()) {
                boxStatus.setOpenStatus(BoxStatus.CLOSE);
            }
            return boxStatus;
        }
    }

    /**
     * 根据整组副柜，获取箱门状态
     */
    public static void getBoxStatue() {
        Map<Integer, SlaveStatus> boxStatusMap = new HashMap<>();
        //假设 快递柜一共有 4组 副柜
        for (int i = 0; i < 4; i++) {
            try {
                boxStatusMap.put(i, getSlaveStatus(i));
            } catch (Exception e) {
                Logcat.e(KLog.TAG, TAG + "获取箱门状态错误 ");
            }
        }
    }

    /**
     * 获取副柜状态
     *
     * @param boardId 副柜编码
     * @return 副柜状态
     */
    public static SlaveStatus getSlaveStatus(int boardId) throws Exception {
        try {
            Result result = HzHardwareProvider.getInstance().getSlaveController().queryStatusById((byte) boardId);
            if (result.getCode() == 0) {
                return new Gson().fromJson(result.getData(), SlaveStatus.class);
            }
            Logcat.d(KLog.TAG, TAG + "[HAL] 获取副柜状态失败 board" + boardId);
            throw new HzException("获取副柜状态失败");
        } catch (RemoteException e) {
            Logcat.d(KLog.TAG, TAG + "[HAL] 获取副柜状态服务出错, board " + boardId);
            throw new HzException("获取副柜状态服务出错");
        }
    }

    /**
     * 添加监听
     *
     * @deprecated 监听器在获取Controller时已经添加
     */
    public static void addObserver() throws Exception {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 添加监听 ");
            if (HzHardwareProvider.getInstance().getScannerController() != null) {
                HzHardwareProvider.getInstance().getScannerController().addObserver(scannerObserver);
            }
            if (HzHardwareProvider.getInstance().getCardReaderController() != null) {
                HzHardwareProvider.getInstance().getCardReaderController().addObserver(cardReadObserver);
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 添加监听 " + e.getMessage());
        }
    }

    /**
     * todo
     * 移除监听
     */
    public static void removeObserver() throws Exception {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 移除监听 ");
            if (HzHardwareProvider.getInstance().getScannerController() != null) {
                HzHardwareProvider.getInstance().getScannerController().removeObserver(scannerObserver);
            }
            if (HzHardwareProvider.getInstance().getCardReaderController() != null) {
                HzHardwareProvider.getInstance().getCardReaderController().removeObserver(cardReadObserver);
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 移除监听 " + e.getMessage());
        }
    }

    /**
     * todo
     * 移除监听
     */
    public static void removeCardReadObserver() throws Exception {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 移除读卡监听 ");
            if (HzHardwareProvider.getInstance().getCardReaderController() != null) {
                HzHardwareProvider.getInstance().getCardReaderController().removeObserver(cardReadObserver);
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 移除读卡监听 " + e.getMessage());
        }
    }

    /**
     * todo
     * 移除监听
     */
    public static void removeScannerObserver() throws Exception {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 移除扫码监听 ");
            if (HzHardwareProvider.getInstance().getScannerController() != null) {
                HzHardwareProvider.getInstance().getScannerController().removeObserver(scannerObserver);
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 移除扫码监听 " + e.getMessage());
        }
    }

    /**
     * 设置扫描枪是否可以扫描条码
     *
     * @param enabled true可以识别条码，false 禁止识别条码
     */
    public static void toggleBarcode(boolean enabled) throws Exception {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 扫描枪条码设置 " + enabled);
            if (HzHardwareProvider.getInstance().getScannerController() != null) {
                HzHardwareProvider.getInstance().getScannerController().toggleBarcode(enabled);
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 扫描枪条码设置出错 " + e.getMessage());
        }
    }

    /**
     * 设置扫描枪是否可以扫描二维码
     *
     * @param enabled true可以识别，false 禁止识别
     */
    public static void toggleQRCode(boolean enabled) throws Exception {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 扫描枪二维码设置 " + enabled);
            if (HzHardwareProvider.getInstance().getScannerController() != null) {
                HzHardwareProvider.getInstance().getScannerController().toggleQRCode(enabled);
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 扫描枪二维码设置出错 " + e.getMessage());
        }
    }

    /**
     * 开始扫描（扫描枪命令模式下生效）
     */
    public static void startScanning() throws Exception {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 扫描枪开始扫描 ");
            if (HzHardwareProvider.getInstance().getScannerController() != null) {
                HzHardwareProvider.getInstance().getScannerController().start();
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 扫描枪开始扫描出错 " + e.getMessage());
        }
    }

    /**
     * 停止扫描（扫描枪命令模式下生效）
     */
    public static void stopScanning() throws Exception {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 扫描枪停止扫描 ");
            HzHardwareManager.setScannerCallBack(null);
            if (HzHardwareProvider.getInstance().getScannerController() != null) {
                HzHardwareProvider.getInstance().getScannerController().stop();
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 扫描枪停止扫描出错 " + e.getMessage());
        }
    }

    /**
     * 开始读卡
     */
    public static void startReadCard(PickerDialog.Action1<Boolean> action1) {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 开始读卡 ");
            if (HzHardwareProvider.getInstance().getCardReaderController() != null) {
                HzHardwareProvider.getInstance().getCardReaderController().start();
                action1.call(true);
            }
        } catch (Exception e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 读卡出错 " + e.getMessage());
            action1.call(false);
        }
    }

    /**
     * 停止读卡
     */
    public static void stopReadCard() {
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 停止读卡 ");
            HzHardwareManager.setCardReadCallBack(null);
            if (HzHardwareProvider.getInstance().getCardReaderController() != null) {
                HzHardwareProvider.getInstance().getCardReaderController().stop();
            }
        } catch (Exception e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 停止读卡出错 " + e.getMessage());
        }
    }

    /**
     * 打开指纹
     */
    public static boolean openFinger() throws Exception {
        boolean isOpen = false;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 打开指纹 ");
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                isOpen = HzHardwareProvider.getInstance().getFingerController().open();
                Logcat.e(KLog.TAG, isOpen ? TAG + "[HAL] 指纹打开成功 " : TAG + "[HAL] 指纹打开失败 ");
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 打开指纹出错 " + e.getMessage());
        }
        return isOpen;
    }

    /**
     * 关闭指纹
     */
    public static boolean closeFinger() throws Exception {
        boolean isClose = false;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 关闭指纹 ");
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                isClose = HzHardwareProvider.getInstance().getFingerController().close();
                Logcat.e(KLog.TAG, isClose ? TAG + "[HAL] 指纹关闭成功 " : TAG + "[HAL] 指纹关闭失败 ");
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 关闭指纹出错 " + e.getMessage());
        }
        return isClose;
    }

    /**
     * 获取指纹特征码
     */
    public static Result getFingerFeature() throws Exception {
        Result result = null;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 获取指纹特征码 ");
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                result = HzHardwareProvider.getInstance().getFingerController().getFeature();
                Logcat.e(KLog.TAG, result != null && result.getCode() == 0 && !TextUtils.isEmpty(result.getData()) ?
                        TAG + "[HAL] 获取指纹特征码成功，特征值为：" + result.getData() :
                        TAG + "[HAL] 获取指纹特征码失败，错误为：" + (result != null ? result.getErrorMsg() : null));
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 获取指纹特征码出错 " + e.getMessage());
        }
        return result;
    }

    /**
     * 比对指纹特征码
     */
    public static Result matchFingerFeature(String feature1, String feature2, int level) throws Exception {
        Result result = null;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 比对指纹特征码 ：" + "\nfeature1 = " + feature1 + "\nfeature2" + feature2);
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                result = HzHardwareProvider.getInstance().getFingerController().match(feature1, feature2, level);
                Logcat.e(KLog.TAG, result != null && result.getCode() == 0 && !TextUtils.isEmpty(result.getData()) ?
                        TAG + "[HAL] 比对指纹特征码成功，特征值为：" + result.getData() :
                        TAG + "[HAL] 比对指纹特征码失败，错误为：" + (result != null ? result.getErrorMsg() : null));
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 比对指纹特征码出错 " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取指纹模板
     */
    public static Result getFingerTemplate() throws Exception {
        Result result = null;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 获取指纹模板 ");
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                result = HzHardwareProvider.getInstance().getFingerController().getTemplate();
                Logcat.e(KLog.TAG, result != null && result.getCode() == 0 && !TextUtils.isEmpty(result.getData()) ?
                        TAG + "[HAL] 获取指纹模板成功，值为：" + result.getData() :
                        TAG + "[HAL] 获取指纹模板失败，错误为：" + (result != null ? result.getErrorMsg() : null));
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 获取指纹模板出错 " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取指纹图像
     */
    public static Result onGetFingerImage() throws Exception {
        Result result = null;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 获取指纹图像 ");
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                result = HzHardwareProvider.getInstance().getFingerController().getImage();
                Logcat.e(KLog.TAG, result != null && result.getCode() == 0 && !TextUtils.isEmpty(result.getData()) ?
                        TAG + "[HAL] 获取指纹图像成功，值为：" + result.getData() :
                        TAG + "[HAL] 获取指纹图像失败，错误为：" + (result != null ? result.getErrorMsg() : null));
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 获取指纹图像出错 " + e.getMessage());
        }
        return result;
    }

    /**
     * 检测是否指纹按下
     */
    public static boolean onPressDetect() throws Exception {
        boolean result = false;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 检测是否指纹按下 ");
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                result = HzHardwareProvider.getInstance().getFingerController().pressDetect();
                Logcat.e(KLog.TAG, result ? TAG + "[HAL] 检测是否指纹按下成功 " : "[HAL] 检测是否指纹按下失败 ");
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 检测是否指纹按下出错 " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取指纹仪版本
     */
    public static Result onGetFingerVersion() throws Exception {
        Result result = null;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 获取指纹仪版本 ");
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                result = HzHardwareProvider.getInstance().getFingerController().getVersion();
                Logcat.e(KLog.TAG, result != null && result.getCode() == 0 && !TextUtils.isEmpty(result.getData()) ?
                        TAG + "[HAL] 获取指纹仪版本成功，值为：" + result.getData() :
                        TAG + "[HAL] 获取指纹仪版本失败，错误为：" + (result != null ? result.getErrorMsg() : null));
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 获取指纹仪版本出错 " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取指纹仪厂家
     */
    public static Result onGetFingerVendor() throws Exception {
        Result result = null;
        try {
            Logcat.d(KLog.TAG, TAG + "[HAL] 获取指纹仪厂家 ");
            if (HzHardwareProvider.getInstance().getFingerController() != null) {
                result = HzHardwareProvider.getInstance().getFingerController().getVendor();
                Logcat.e(KLog.TAG, result != null && result.getCode() == 0 && !TextUtils.isEmpty(result.getData()) ?
                        TAG + "[HAL] 获取指纹仪厂家成功，值为：" + result.getData() :
                        TAG + "[HAL] 获取指纹仪厂家失败，错误为：" + (result != null ? result.getErrorMsg() : null));
            }
        } catch (RemoteException e) {
            Logcat.e(KLog.TAG, TAG + "[HAL] 获取指纹仪厂家出错 " + e.getMessage());
        }
        return result;
    }
}
