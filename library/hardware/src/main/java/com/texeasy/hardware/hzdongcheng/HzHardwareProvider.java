package com.texeasy.hardware.hzdongcheng;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.common.bus.Messenger;
import com.example.common.utils.KLog;
import com.hzdongcheng.drivers.IDriverManager;
import com.hzdongcheng.drivers.finger.IFingerController;
import com.hzdongcheng.drivers.locker.ISlaveController;
import com.hzdongcheng.drivers.peripheral.cardreader.ICardReaderController;
import com.hzdongcheng.drivers.peripheral.scanner.IScannerController;
import com.lazy.library.logging.Logcat;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by zzq on 2018/9/13
 **/
public class HzHardwareProvider {
    private static final String TAG = "HzHardwareProvider";
    private boolean isDebug = true;
    private static HzHardwareProvider instance = new HzHardwareProvider();
    private static IDriverManager driverManager;
    private static ISlaveController slaveController;
    private static IScannerController scannerController;
    private static ICardReaderController cardReaderController;
    private static IFingerController fingerController;
    private WeakReference<Context> contextWeakReference;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> schedule;
    private Intent driverIntent;

    /**
     * 监听Aidl客户端和服务端的状态
     */
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (driverManager != null && contextWeakReference != null) {
                driverManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
                contextWeakReference.get().unbindService(serviceConnection);
            }
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Logcat.d(KLog.TAG, "->东城驱动服务连接成功");
            driverManager = IDriverManager.Stub.asInterface(iBinder);
            Messenger.getDefault().sendNoMsg(HzHardwareManager.CONNECT_SUCCESS);
            try {
                iBinder.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Logcat.d(KLog.TAG, "->东城驱动服务断开连接");
            driverManager = null;
            scannerController = null;
            slaveController = null;
            cardReaderController = null;
            fingerController = null;
            reconnectService();

        }
    };


    private HzHardwareProvider() {
    }

    public static HzHardwareProvider getInstance() {
        return instance;
    }

    /**
     * 服务重新连接(延时3秒)
     */
    private synchronized void reconnectService() {

        if (schedule != null && !schedule.isDone()) {
            Logcat.d(KLog.TAG, "--> 驱动服务器已经再重连,将不会执行此次重连请求");
            return;
        }

        schedule = executorService.schedule(new Runnable() {
            @Override
            public void run() {
                Logcat.d(KLog.TAG, "--> 开始重新连接驱动服务");
                if (contextWeakReference.get() != null) {
                    boolean isBind;
                    int tryCount = 0;
                    do {
                        try {
                            Thread.sleep(tryCount * 30 * 1000);
                        } catch (InterruptedException ignored) {
                        }
                        isBind = contextWeakReference.get().bindService(driverIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                    } while (!isBind || tryCount++ < 10);
                }
            }
        }, 3, TimeUnit.SECONDS);
    }

    /**
     * 绑定东城驱动服务
     *
     * @param context
     */
    public boolean bind(Context context) {
        driverIntent = new Intent("hzdongcheng.intent.action.DRIVER");
        driverIntent.setPackage("com.hzdongcheng.drivers");
        contextWeakReference = new WeakReference<>(context);
        return context.bindService(driverIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 获取副柜
     *
     * @return
     */
    public ISlaveController getSlaveController() {
        if (slaveController != null && slaveController.asBinder().isBinderAlive()) {
            return slaveController;
        }
        if (driverManager != null) {
            try {
                slaveController = ISlaveController.Stub.asInterface(driverManager.getSlaveService());
            } catch (RemoteException e) {
                Logcat.d(KLog.TAG, ">>副柜控制器获取失败>>" + e.getMessage());
            }
        }
        if (slaveController == null) {
            Logcat.e(KLog.TAG, "未获取到服务模块");
        }
        return slaveController;
    }

    /**
     * 获取扫描枪服务
     *
     * @return
     * @throws
     */
    public IScannerController getScannerController() throws Exception {
        if (scannerController != null && scannerController.asBinder().isBinderAlive()) {
            return scannerController;
        }
        if (driverManager != null) {
            try {
                scannerController = IScannerController.Stub.asInterface(driverManager.getScannerService());
                if (scannerController == null) {
                    throw new Exception("未获取到服务模块");
                }
                scannerController.addObserver(HzHardwareManager.scannerObserver);
            } catch (RemoteException e) {
                Logcat.e(KLog.TAG, ">>扫描枪控制器获取失败>>" + e.getMessage());
            }
        }
        if (scannerController == null) {
            if (!isDebug) {
                throw new Exception("未获取到服务模块");
            }
        }
        return scannerController;
    }

    /**
     * 获取读卡器服务
     *
     * @return
     * @throws
     */
    public ICardReaderController getCardReaderController() throws Exception {
        if (cardReaderController != null && cardReaderController.asBinder().isBinderAlive()) {
            return cardReaderController;
        }
        if (driverManager != null) {
            try {
                cardReaderController = ICardReaderController.Stub.asInterface(driverManager.getCardReaderService());
                if (cardReaderController == null) {
                    throw new Exception("未获取到服务模块");
                }
                cardReaderController.addObserver(HzHardwareManager.cardReadObserver);
            } catch (RemoteException e) {
                Logcat.e(KLog.TAG, ">>读卡器控制器获取失败>>" + e.getMessage());
            }
        }
        if (cardReaderController == null) {
            if (!isDebug) {
                throw new Exception("未获取到服务模块");
            }
        }
        return cardReaderController;
    }

    /**
     * 获取指纹服务
     *
     * @return
     * @throws
     */
    public IFingerController getFingerController() throws Exception {
        if (fingerController != null && fingerController.asBinder().isBinderAlive()) {
            return fingerController;
        }
        if (driverManager != null) {
            try {
                fingerController = IFingerController.Stub.asInterface(driverManager.getService("FingerService"));
                if (fingerController == null) {
                    throw new Exception("未获取到服务模块");
                }
            } catch (RemoteException e) {
                Logcat.e(KLog.TAG, ">>指纹控制器获取失败>>" + e.getMessage());
            }
        }
        if (fingerController == null) {
            if (!isDebug) {
                throw new Exception("未获取到服务模块");
            }
        }
        return fingerController;
    }

    /**
     * 解绑
     */
    public void unBind() {
        if (contextWeakReference != null && contextWeakReference.get() != null) {
            try {
                contextWeakReference.get().unbindService(serviceConnection);
            } catch (Exception e) {
                e.printStackTrace();
                Logcat.e(KLog.TAG, e.getMessage());
            }
        }
    }
}
