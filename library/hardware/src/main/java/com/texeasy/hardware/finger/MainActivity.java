//package com.texeasy.hardware.finger;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.drawable.LevelListDrawable;
//import android.net.ConnectivityManager;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.telephony.PhoneStateListener;
//import android.telephony.SignalStrength;
//import android.telephony.TelephonyManager;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.alibaba.android.arouter.facade.annotation.Autowired;
//import com.alibaba.android.arouter.facade.annotation.Route;
//import com.alibaba.android.arouter.launcher.ARouter;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.sdk.android.oss.ClientConfiguration;
//import com.alibaba.sdk.android.oss.ClientException;
//import com.alibaba.sdk.android.oss.OSSClient;
//import com.alibaba.sdk.android.oss.ServiceException;
//import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
//import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
//import com.alibaba.sdk.android.oss.common.OSSLog;
//import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
//import com.alibaba.sdk.android.oss.model.PutObjectRequest;
//import com.alibaba.sdk.android.oss.model.PutObjectResult;
//import com.bumptech.glide.Glide;
//import com.jpgk.commonmodule.base.NotifyBaseBean;
//import com.jpgk.commonmodule.base.NotifyServerConBaseBean;
//import com.jpgk.commonmodule.bean.VMInfo;
//import com.jpgk.commonmodule.bean.beans.AllBoxDoorBean;
//import com.jpgk.commonmodule.bean.beans.BoxDoorBean;
//import com.jpgk.commonmodule.bean.beans.CabinetAssetsCodeBean;
//import com.jpgk.commonmodule.bean.beans.CabinetCodeBean;
//import com.jpgk.commonmodule.bean.beans.ErrorBean;
//import com.jpgk.commonmodule.bean.beans.NormalBean;
//import com.jpgk.commonmodule.constants.BusConstants;
//import com.jpgk.commonmodule.constants.LoginStatusConttants;
//import com.jpgk.commonmodule.constants.PathConstants;
//import com.jpgk.commonmodule.file.FileUtils;
//import com.jpgk.commonmodule.socket.BaseModel;
//import com.jpgk.commonmodule.socket.RequestAIDLUtils;
//import com.jpgk.commonmodule.socket.RequestConstants;
//import com.jpgk.commonmodule.socket.SocketData;
//import com.jpgk.commonmodule.timer.JPTimer;
//import com.jpgk.commonmodule.utils.CommonConstants;
//import com.jpgk.commonmodule.utils.CommonUtils;
//import com.jpgk.commonmodule.utils.Constants;
//import com.jpgk.commonmodule.utils.DateUtil;
//import com.jpgk.commonmodule.utils.ProcessUtils;
//import com.jpgk.commonmodule.utils.sp.SPUtils;
//import com.jpgk.commonmodule.utils.sp.SharedPreferencesUtil;
//import com.jpgk.commonviewmodule.base.BaseActivity;
//import com.jpgk.commonviewmodule.view.T;
//import com.jpgk.mainmodule.oss.Config;
//import com.jpgk.mainmodule.oss.MyOSSAuthCredentialsProvider;
//import com.jpgk.mainmodule.reciever.NetworkReceiver;
//import com.jpgk.mainmodule.request.MainRequestConstants;
//import com.jpgk.mainmodule.request.MainSocketData;
//
//import com.jpgk.moduledispense.DispenseRequestConstants;
//import com.jpgk.moduledispense.request.DispenseSocketData;
//import com.texeasy.hardware.R;
//import com.wellcom.GfpUtils;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import static com.jpgk.commonmodule.base.BaseApplication.serviceProxy;
//import static com.jpgk.commonmodule.base.BaseApplication.result;
//import static com.jpgk.commonmodule.socket.Constants.DOOR_NUMBER;
//import static com.jpgk.commonmodule.socket.Constants.DOOR_NUMBER_FLAG;
//import static com.jpgk.commonmodule.utils.CommonConstants.cabinetId;
//
//
///**
// * 整个项目的首页----在此页面，将各个业务模块集中起来
// */
//@Route(path = PathConstants.MAIN_ACTIVITY_PATH, extras = 0)
//public class MainActivity extends BaseActivity {
//    //TOO 可以考虑使用配置文件
//    private static final String SERVICE_PROCESS_NAME = "com.jpgk.vendingservice";
//    private static final String SETTING_PAGER = "SETTING_PAGER";
//    private static final String MAIN_PAGER = "MAIN_PAGER";
//    private static final String DISPENSE_ADD_PAGER = "DISPENSE_ADD_PAGER";
//    private String TAG = getClass().getSimpleName();
//
//    @Autowired(name = "index")
//    protected int mIndex;
//    @Autowired(name = "isExit")
//    protected boolean mIsExit;
//
//
//    @Autowired(name = PathConstants.KEY_CHANNEL_NAME)
//    protected String chanelName;
//    private NetworkReceiver networkReceiver;
//    private PhoneStateListener mListener;
//    private TelephonyManager mTelephonyManager;
//    private WifiManager wifiManager;
//    private ImageView iv_signal;
//    //测试按钮
//    private TextView tvTest;
//    private EditText tvTestText;
//    //////////////////////////////
//    private int mLevel;
//    private TextView timer;
//    private TextView main_index_title_deivcename;
//    private View main_fragment_invalid;
//    private View main_fragment_content;
//    private Fragment fragment;
//    private FragmentManager manager;
//    private ImageView main_img_sub_logo;
//    private TextView main_tx_sub_title;
//    private ImageView main_index_title_logo;
//    private View main_rl_regist;
//    private EditText main_et_regist;
//    private View main_bt_regist;
//    private Fragment settingFragment;
//    private Fragment inboxfragment;
//    private String currentShowPager = MAIN_PAGER;
//    private View main_title2;
//    private RelativeLayout main_title;
//    /**
//     * 阿里OSS
//     */
//    private OSSClient oss;
//    public GfpUtils mgfp = null;
//    public String strFtr = null;
//    private Timer fingerprintQueryTimer;
//    private boolean isLogin;//用户是否登录
//    private boolean featureCollectOk = true;//指纹是否收集完成
//    private boolean isGoFace = false;//是否进入刷脸界面
//    private boolean isExecute = true;//指纹读取是否执行完成
//    ////////////////////////////
//    String TAG1 = "AIDL对接数据:";
////    ServiceProxy serviceProxy;
////    JSONObject jsonObjectxxxxx=new JSONObject();
////
////    String result;
//    ///////////////////////////
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (mIsExit) {
//            finish();
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ARouter.getInstance().inject(this);
//        setContentView(R.layout.activity_main);
////        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
////                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        isExecute = true;
//        Constants.isLogin = false;
//        Constants.isFaceActivity = false;
//        //////////////////////////测试aidl
////        serviceProxy =  ServiceProxy.getInstance();
////        jsonObjectxxxxx.put("statusMsg", "请等待");
////        result = JSONObject.toJSONString(jsonObjectxxxxx);
////        serviceProxy.bindService(MainActivity.this);
//        ///////////////////////////////////////////
//        //初始化OSS
//        initOSSClient();
//        mgfp = new GfpUtils(getApplicationContext());
//        openWelDevice();
//        if (!TextUtils.isEmpty(String.valueOf(SPUtils.get(this,DOOR_NUMBER_FLAG,"")))) {
//           DOOR_NUMBER =  String.valueOf(SPUtils.get(this,DOOR_NUMBER_FLAG, ""));
//        }
//        Log.d("主界面","机柜门个数 "+DOOR_NUMBER);
//
//        if (!TextUtils.isEmpty(chanelName)) {
//            SPUtils.put(this, SPUtils.CHANEL_NAME, chanelName);
//        } else {
//            chanelName = (String) SPUtils.get(this, SPUtils.CHANEL_NAME, "");
//        }
//
//        networkReceiver = new NetworkReceiver();
//        onRegisterReceiver(networkReceiver);
//        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        iv_signal = findViewById(R.id.main_index_title_signal);
//        tvTest = findViewById(R.id.tv_test);
//        tvTestText = findViewById(R.id.tv_test_text);
//        tvTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//        main_title2 = findViewById(R.id.main_title2);
//        timer = findViewById(R.id.main_index_title_time);
//        main_index_title_deivcename = findViewById(R.id.main_index_title_deivcename);
//        main_index_title_logo = findViewById(R.id.main_index_title_logo);
//        main_fragment_invalid = findViewById(R.id.main_fragment_invalid);
//        main_fragment_content = findViewById(R.id.main_fragment_content);
//        //取衣柜图标
//        main_img_sub_logo = findViewById(R.id.main_img_sub_logo);
//        main_title = findViewById(R.id.main_title);
//        if (chanelName != null && chanelName.contains("shoes")) {
//            Glide.with(MainActivity.this).load(MainConstants.subDeviceLogoId).into(main_img_sub_logo);
//        } else {
//            Glide.with(MainActivity.this).load(MainConstants.subDeviceLogoIdCloth).into(main_img_sub_logo);
//        }
//        //隐藏标题栏
//        if (chanelName != null && chanelName.contains("aucland_infor_publish")) {
//            main_title.setVisibility(View.GONE);
//        } else {
//            main_title.setVisibility(View.VISIBLE);
//        }
//        main_tx_sub_title = findViewById(R.id.main_tx_sub_title);
//        //设备编号绑定弹框
//        main_rl_regist = findViewById(R.id.main_rl_regist);
//        //输入编号框
//        main_et_regist = findViewById(R.id.main_et_regist);
//        //点击绑定编号?
//        main_bt_regist = findViewById(R.id.main_bt_regist);
//        main_index_title_logo.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Log.e("调试", "onLongClick");
//                EventBus.getDefault().post(new NotifyBaseBean(CommonConstants.DRESSINGTOSETTING));
//                return false;
//            }
//        });
//        main_bt_regist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TOO 提交注册
//                String content = main_et_regist.getText().toString();
//                if (TextUtils.isEmpty(content)) {
//                    T.showToast("设备ID不能为空");
//                    return;
//                }
//                SocketData.requestConfigDeviceId(RequestConstants.REQUESTCONFIG, content);
//                ProcessUtils.startProcess(MainActivity.this, true, SERVICE_PROCESS_NAME);
//                main_fragment_invalid.setVisibility(View.VISIBLE);
//                CommonUtils.hiddenSoftInputer(MainActivity.this);
//                main_rl_regist.setVisibility(View.GONE);
//                //TOOD
//            }
//        });
//        initClockTimer();
//        manager = getSupportFragmentManager();
//        initFragment();
//        SocketData.requestVMInfo(RequestConstants.REQUEST_VMINFO);
//        main_fragment_invalid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                SocketData.getTestId(RequestConstants.REQUEST_GOODINFO);
//            }
//        });
//        if (mIsExit) {
//            finish();
//        }
//
//        if (fingerprintQueryTimer != null) {
//            fingerprintQueryTimer.cancel();
//        }
//        fingerprintQueryTimer = new Timer();
//        fingerprintQueryTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (isLogin) {
//                    Log.i(TAG, "用户已经登陆了");
//                } else {
//                    Log.i(TAG, "用户未登录，开始收集指纹");
//                    if (isGoFace) {
//                        Log.i(TAG, "进入到刷脸界面,指纹采集停止");
//                        return;
//                    }
//                    if (isExecute) {
//                        isExecute = false;
//                        boolean fingerDetect = fingerDetect();
//                        Log.i(TAG,"fingerDetect:"+fingerDetect);
//                        if (fingerDetect) {
//                            getFeature();
//                        }
//                        isExecute = true;
//                    }
//                }
//            }
//        }, 1000, 2000);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        if (!TextUtils.isEmpty(cabinetId)) {
////            //设置与否待定
////            main_index_title_deivcename.setText(getResources().getString(R.string.main_tv_title_deviceid, cabinetId));
////        }
//
////        else {
////            //TOO 展示    填寫設備号界面
////            showRegistView();
////            return;
////        }
//    }
//
//    /**
//     * 初始化oss
//     */
//    private void initOSSClient() {
//        // 配置类如果不设置，会有默认配置。
//        ClientConfiguration conf = new ClientConfiguration();
//        OSSCredentialProvider credentialProvider = new MyOSSAuthCredentialsProvider(Config.STS_SERVER_URL);
//        // 连接超时，默认15秒。
//        conf.setConnectionTimeout(15 * 1000);
//        // socket超时，默认15秒。
//        conf.setSocketTimeout(15 * 1000);
//        // 最大并发请求数，默认5个。
//        conf.setMaxConcurrentRequest(5);
//        // 失败后最大重试次数，默认2次。
//        conf.setMaxErrorRetry(2);
//        oss = new OSSClient(this, Config.OSS_ENDPOINT, credentialProvider);
//        OSSLog.enableLog();
//    }
//
//    private void initClockTimer() {
//        setDateView();
//    }
//
//    private void setDateView() {
//        JPTimer.TEventAction dateTimer = new JPTimer.TEventAction() {
//            private SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
//
//            @Override
//            public void onEndListener() {
//
//            }
//
//            @Override
//            public void onStartListener() {
//                setDateTimeView();
//                setTimeView();
//            }
//
//            @Override
//            public void onClockListener() {
//                //整点会回调
//                setDateTimeView();
//            }
//
//            @Override
//            public void onNext(int totalTimes, int currentTimes) {
//                setTimeView();
//            }
//
//            private void setTimeView() {
//                Calendar cal = Calendar.getInstance();
//                Date time = cal.getTime();
//                String format = format_time.format(time);
//                if (timer != null) {
//                    timer.setText(format);
//                }
//            }
//
//            private void setDateTimeView() {
//
//            }
//        };
//        dateTimer.setDurationTime(60);
//        dateTimer.setRepeatTimes(-1);
//        JPTimer.getInstance().setEventListener(dateTimer);
//
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onNetChange(NotifyBaseBean notifyBaseBean) {
//        if (!NetworkReceiver.NET_CHANGE_STAYE_TAG.equals(notifyBaseBean.getTAG())) {
//            return;
//        }
//        int netWorkState = (int) notifyBaseBean.object;
//        onNetChange(netWorkState);
//    }
//
//    public void onNetChange(int netWorkState) {
//        Log.d("netWorkState", netWorkState + "");
//        int level;
//        LevelListDrawable drawable;
//
//        //监听信号强度，解开注册
//        if (mListener != null) {
//            mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//        }
//        switch (netWorkState) {
//            case CommonUtils.NETWORK_ETHERNET://以太网
//                Log.d("网络状态", "以太网");
//                iv_signal.setImageResource(R.drawable.mian_ic_net_ethernet);
//                break;
//            case CommonUtils.NETWORK_MOBILE://移动网
//                iv_signal.setImageResource(R.drawable.main_level_signal);
//                //开始监听信号变化
//                mListener = new PhoneStateListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.M)
//                    @Override
//                    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//                        super.onSignalStrengthsChanged(signalStrength);
//                        mLevel = signalStrength.getLevel();
//                    }
//                };
//                //监听信号强度
//                mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//                drawable = (LevelListDrawable) iv_signal.getDrawable();
//                drawable.setLevel(mLevel);
//                break;
//
//            case CommonUtils.NETWORK_WIFI://wifi
//                iv_signal.setImageResource(R.drawable.main_level_wifi);
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                //获得信号强度值
//                level = wifiInfo.getRssi();
//                //根据获得的信号强度发送信息
//                if (level >= -60) {
//                    level = 3;
//                } else if (level >= -80) {
//                    level = 2;
//                } else {
//                    level = 1;
//                }
//                drawable = (LevelListDrawable) iv_signal.getDrawable();
//                drawable.setLevel(level);
//                break;
//
//            case CommonUtils.NETWORK_NONE://没有网络
//                iv_signal.setImageResource(R.drawable.main_level_signal);
//                level = 0;
//                drawable = (LevelListDrawable) iv_signal.getDrawable();
//                drawable.setLevel(level);
//                break;
//            default:
//                Log.w("未知状态netWorkState", netWorkState + "");
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Constants.isLogin = false;
//        Constants.isFaceActivity = false;
//        onUnregisterReceiver(networkReceiver);
//        closeWelDevice();
//        if (fingerprintQueryTimer != null) {
//            fingerprintQueryTimer.cancel();
//            fingerprintQueryTimer = null;
//        }
//        Log.d("AIDL","解除aidl服务");
//        serviceProxy.unBindService();
//    }
//
//    protected void onUnregisterReceiver(NetworkReceiver networkReceiver) {
//        if (networkReceiver != null)
//            unregisterReceiver(networkReceiver);
//    }
//
//    protected void onRegisterReceiver(NetworkReceiver networkReceiver) {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(networkReceiver, filter);
//
//    }
//
//    /**
//     * 初始化获取4个片段
//     */
//    private void initFragment() {
//        FragmentTransaction transaction = manager.beginTransaction();
//        //设置界面
//        settingFragment = (Fragment) ARouter.getInstance().build(PathConstants.MODULE_SETTING_PATH).withInt(PathConstants.KEY_PRO_NAME, CommonConstants.KEY_PROJECT_NAME_DRESSING).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
//        if (chanelName != null && chanelName.contains("aucland_recycle")) {
//            //摄像头刷脸支付
//            fragment = (Fragment) ARouter.getInstance().build(PathConstants.RECYCLE_MODULE_PATH).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
//        } else if (chanelName != null && chanelName.contains("aucland_dressing")) {
//            //柜号查找?
//            fragment = (Fragment) ARouter.getInstance().build(PathConstants.DRESSING_HOME_PATH).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
//        } else if (chanelName != null && chanelName.contains("aucland_dispense")) {
//            //补货界面
//            fragment = (Fragment) ARouter.getInstance().build(PathConstants.DISPENSE_HOME_PATH).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
//            inboxfragment = (Fragment) ARouter.getInstance().build(PathConstants.DISPENSE_HOME_INBOX_PATH).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
////            transaction.add(R.id.main_fragment_content, inboxfragment);
//        } else if (chanelName != null && chanelName.contains("publish")) {
//            fragment = (Fragment) ARouter.getInstance().build(PathConstants.PUBLISH_HOME_PATH).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
//        }
//        transaction.add(R.id.main_fragment_content, settingFragment);
//        transaction.add(R.id.main_fragment_content, fragment);
//        if (inboxfragment != null) {
//            transaction.add(R.id.main_fragment_content, inboxfragment);
//            transaction.hide(inboxfragment);
//        }
//        transaction.hide(settingFragment);
//        transaction.hide(fragment);
//        transaction.commitAllowingStateLoss();
//    }
//
//    private void visibleFragment(boolean isShow) {
//        if (fragment == null) {
//            return;
//        }
//        FragmentTransaction transaction = manager.beginTransaction();
//        if (isShow) {
//            transaction.show(fragment);
//        } else {
//            transaction.hide(fragment);
//        }
//        transaction.commitAllowingStateLoss();
//    }
//
//    @Subscribe(sticky = true)
//    public void connectSocketSuccess(String content) {
//        if (BusConstants.COMMON_SOCKET_SUCCESS.equals(content)) {
//            Log.e("基础数据测试", "onConnectSocketSuccess" + content+"CommonConstants.SERVICE_STATE_SOCKE_SUC" + CommonConstants.SERVICE_STATE_SOCKE_SUC);
//            EventBus.getDefault().removeStickyEvent(content);
//            SocketData.requestVMInfo(RequestConstants.REQUEST_VMINFO);
////            Log.e("基础信息", "CommonConstants.SERVICE_STATE_SOCKE_SUC" + CommonConstants.SERVICE_STATE_SOCKE_SUC);
//
//            SPUtils.put(this, SPUtils.TAG_KEY_SERVICE_STATE, CommonConstants.SERVICE_STATE_SOCKE_SUC);
//        }
//    }
//
//    @Subscribe
//    public void goToSettings(NotifyBaseBean notifyBaseBean) {
//        CommonUtils.hiddenSoftInputer(this);
//        FragmentTransaction transaction = manager.beginTransaction();
//        if (CommonConstants.DRESSINGTOSETTING.equals(notifyBaseBean.getTAG())) {
//            currentShowPager = SETTING_PAGER;
//            transaction.hide(fragment);
//            transaction.show(settingFragment);
//        } else if (CommonConstants.SETTINGBACKDRESSING.equals(notifyBaseBean.getTAG())) {
//            currentShowPager = MAIN_PAGER;
//            transaction.show(fragment);
//            transaction.hide(settingFragment);
//        } else if (CommonConstants.DISPENSETOADD.equals(notifyBaseBean.getTAG())) {
//            Log.e("基础信息", "添加补货界面");
//            currentShowPager = DISPENSE_ADD_PAGER;
//            transaction.hide(fragment);
//            transaction.hide(settingFragment);
//            transaction.show(inboxfragment);
//        } else if (CommonConstants.ADDTODISPENSE.equals(notifyBaseBean.getTAG())) {
//            currentShowPager = MAIN_PAGER;
//            transaction.hide(settingFragment);
//            transaction.hide(inboxfragment);
//            transaction.show(fragment);
//            Log.d("补货完成","退出登录");
//            DispenseSocketData.requestOpsLogOut(DispenseRequestConstants.REQUESTOPSLOGOUT);
//        }
//        transaction.commitAllowingStateLoss();
//    }
//
//    @Override
//    public void onSocketSuccess(String method, BaseModel baseModel) {
//        Log.e("测试数据", method + baseModel.toString());
//
//        switch (method) {
//
//
//            case RequestConstants.REQUEST_GOODINFO:
//                break;
//            case RequestConstants.REQUESTCONFIG:
//                //提交注册设备id成功
//                T.showToast("提交成功");
//                SocketData.requestVMInfo(RequestConstants.REQUEST_VMINFO);
//                break;
////            case RequestConstants.NOTIFYSERVERSTATUSUPDATED:
////                SocketData.requestVMInfo(RequestConstants.REQUEST_VMINFO);
////                break;
//            case RequestConstants.NOTIFY_VMINFO:
//                SocketData.requestVMInfo(RequestConstants.REQUEST_VMINFO);
//                break;
//            case RequestConstants.REQUEST_VMINFO:
//                if (baseModel != null) {
//                    String result = baseModel.getResult();
//                    if (result != null) {
//                        VMInfo vmInfo = JSON.parseObject(result, VMInfo.class);
//                        if (!TextUtils.isEmpty(vmInfo.getDeviceId())) {
//                            main_index_title_deivcename.setText(getResources().getString(R.string.main_tv_title_deviceid, vmInfo.getDeviceId()));
//                        } else {
//                            //TOO 展示    填寫設備号界面
//                            showRegistView();
//                            return;
//                        }
//                        SPUtils.put(this, SPUtils.APP_ID, vmInfo.getAppId());
//                        SPUtils.put(this, SPUtils.SDK_KEY, vmInfo.getSdkKey());
//                        SPUtils.put(this, SPUtils.ACTIVE_KEY, vmInfo.getActiveKey());
//                        if (!TextUtils.isEmpty(vmInfo.getSubDeviceLog())) {
////                            Glide.with(MainActivity.this).load(vmInfo.getSubDeviceLog()).into(main_img_sub_logo);
//                        } else {
//                        }
//                        if (!TextUtils.isEmpty(vmInfo.getLogoUrl())) {
////                            Glide.with(MainActivity.this).load(vmInfo.getLogoUrl()).into(main_index_title_logo);
//                        }
//                        //根据收到后台的信息来确定是那个设备名称,发衣机,发鞋机,收衣机,收鞋机
//                        if (!TextUtils.isEmpty(vmInfo.getSubDeviceName())) {
//                            main_tx_sub_title.setText(vmInfo.getSubDeviceName());
//                        } else {
//                            main_tx_sub_title.setText(MainConstants.subDeviceName);
//                        }
//                        SPUtils.put(this, SPUtils.TAG_KEY_VMINFOR, result);
//                        if (vmInfo.getServerStatus() == 1) {
//                            Log.e("基础信息", "CommonConstants.SERVICE_STATE_SERVER_SUC" + CommonConstants.SERVICE_STATE_SERVER_SUC);
//                            if ((int) SPUtils.get(this, SPUtils.TAG_KEY_SERVICE_STATE, -1) == CommonConstants.SERVICE_STATE_SERVER_SUC) {
//                                return;
//                            }
//                            SPUtils.put(this, SPUtils.TAG_KEY_SERVICE_STATE, CommonConstants.SERVICE_STATE_SERVER_SUC);
//                            NotifyServerConBaseBean notifyBaseBean = new NotifyServerConBaseBean(CommonConstants.BUS_NOTIFY_SERVER_CONNECT);
//                            EventBus.getDefault().post(notifyBaseBean);
//                            showContentView(true);
//                        } else {
//                            SPUtils.put(this, SPUtils.TAG_KEY_SERVICE_STATE, CommonConstants.SERVICE_STATE_SOCKE_SUC);
//                            showContentView(false);
//                        }
//                    } else {
//                        Log.w("基础数据", "返回数据结构内容为空");
//                    }
//                } else {
//                    Log.e("基础数据", "返回数据结构错误" + method);
//                }
//                break;
//
//            case RequestConstants.NOTIFY_SOFT_ERROR:
//                if (baseModel != null) {
//                    String result = baseModel.getResult();
//                    if (result != null) {
//                        SoftError softError = JSON.parseObject(result, SoftError.class);
//                        T.showToast(softError.msg);
//                    }
//                }
//                break;
//            case RequestConstants.NOTIFYUPLOG:
//                Log.d(TAG, "收到上传App日志的指令");
//                if (baseModel != null) {
//                    String result = baseModel.getResult();
//                    if (result != null) {
//                        NotifyUpLogBean notifyUpLogBean = JSON.parseObject(result, NotifyUpLogBean.class);
//                        logFileFilter(notifyUpLogBean.getNotifyUpLog());
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void showRegistView() {
//        main_rl_regist.setVisibility(View.VISIBLE);
//        main_fragment_invalid.setVisibility(View.GONE);
//    }
//
//    private void showContentView(boolean isok) {
//        if (!currentShowPager.equals(MAIN_PAGER)) {
//            Log.e("基础信息", "服务异常currentShowPager" + currentShowPager);
//            return;
//        }
//        CommonUtils.hiddenSoftInputer(this);
//        Log.e("基础数据", "是否展示fragment" + isok + Thread.currentThread().getName());
//        main_fragment_invalid.setVisibility(isok ? View.GONE : View.VISIBLE);
//
//        main_rl_regist.setVisibility(View.GONE);
//        visibleFragment(isok);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                Log.e("调试", "KEYCODE_BACK---");
//                EventBus.getDefault().post(new NotifyBaseBean(CommonConstants.DRESSINGTOSETTING));
//                return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param objectName 文件名称
//     * @param localFile  本地文件路径
//     */
//    private void putFile(String objectName, String localFile) {
//        Log.d(TAG, "objectName:" + objectName + "-----objectName:" + localFile);
//        if (objectName == "") {
////            Log.e(TAG, "ObjectNull");
//            return;
//        }
//        File file = new File(localFile);
//        if (!file.exists()) {
////            Log.e(TAG, "FileNotExist");
//            Log.e("LocalFile", localFile);
//            return;
//        }
//        // 构造上传请求。
//        PutObjectRequest put = new PutObjectRequest(Config.BUCKET_NAME, objectName, localFile);
//        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", String.format("currentSize: %d totalSize: %d", currentSize, totalSize));
//            }
//        });
//
//        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                Log.e("onFailure", "上传成功：" + request.toString() + "----" + result.getServerCallbackReturnBody());
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                Log.e("onFailure", "上传失败：" + request.toString());
//                // 请求异常。
//                if (clientExcepion != null) {
//                    Log.e("clientExcepion", clientExcepion.getMessage());
//                    // 本地异常，如网络异常等。
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // 服务异常。
//                    Log.e("ErrorCode", serviceException.getErrorCode()+"/r/n RequestId"+serviceException.getRequestId()
//                            +serviceException.getHostId()+"RawMessage"+serviceException.getRawMessage());
////                    Log.e("RequestId", serviceException.getRequestId());
////                    Log.e("HostId", serviceException.getHostId()+"RawMessage"+serviceException.getRawMessage());
////                    Log.e("RawMessage", serviceException.getRawMessage());
//                }
//            }
//        });
//    }
//
//    /**
//     * 日志文件过滤
//     *
//     * @param notifyUpLog
//     */
//    private void logFileFilter(String notifyUpLog) {
//        File logDir = FileUtils.getDir(Constants.DIR_LOG);
//        List<FileUtils.FileInfo> filePath = FileUtils.getFilesInfo(logDir.getPath());
////        Log.i(TAG, "filePath" + filePath);
//        String currentTime = DateUtil.convertToString(DateUtil.getCurrentTime(), DateUtil.FORMAT_YYYY_MM_DD);
//        if (!TextUtils.isEmpty(notifyUpLog)) {
//            currentTime = notifyUpLog;
//        }
//        Log.i(TAG, "当前时间:" + currentTime+"filePath" + filePath);
//
//        for (int i = 0; i < filePath.size(); i++) {
//            if (filePath.get(i).getName().contains(currentTime)) {
//                if (filePath.get(i).getName().contains("_watchdog.log")) {
//                    //看门狗日志
//                    Log.i(TAG, "看门狗日志:" + filePath.get(i).getPath());
//                    putFile(String.format("app/logs/%s", filePath.get(i).getName()), filePath.get(i).getPath());
//                } else if (filePath.get(i).getName().contains("UI.log")) {
//                    //UI日志
//                    Log.i(TAG, "UI日志:" + filePath.get(i).getPath());
//                    putFile(String.format("app/logs/%s", filePath.get(i).getName()), filePath.get(i).getPath());
//                } else if (filePath.get(i).getName().contains(".log")) {
//                    //服务日志
//                    Log.i(TAG, "服务日志:" + filePath.get(i).getPath());
//                    putFile(String.format("app/logs/%s", filePath.get(i).getName()), filePath.get(i).getPath());
//                }
//            }
//        }
//    }
//
//    @Subscribe(sticky = true)
//    public void getLoginStatus(LoginStatusConttants loginStatus) {
//        if (loginStatus != null) {
////            Log.e("基础数据测试", "LoginStatus" + loginStatus);
//            EventBus.getDefault().removeStickyEvent(loginStatus);
//            if (loginStatus.getType() == LoginStatusConttants.LOGIN) {
//                Log.i(TAG, "用户登陆成功: LoginStatus" + loginStatus);
//                isLogin = true;
//                Constants.isLogin = true;
//            } else if (loginStatus.getType() == LoginStatusConttants.LOGIN_OUT) {
//                Log.i(TAG, "用户退出登陆: LoginStatus" + loginStatus);
//                isLogin = false;
//                Constants.isLogin = false;
//            }
//        }
//    }
//
//    @Subscribe(sticky = true)
//    public void getIsGoFace(String busConstants) {
//        if (busConstants != null) {
//            Log.e("基础数据测试", "LoginStatus" + busConstants);
//            EventBus.getDefault().removeStickyEvent(busConstants);
//            if (busConstants == BusConstants.JOIN_IN_FACE) {
//                isGoFace = true;
//            } else if (busConstants == BusConstants.GO_OUT_FACE) {
//                isGoFace = false;
//            }
//        }
//    }
//
//    /**
//     * 打开设备
//     */
//    public void openWelDevice() {
//        int iRet = mgfp.openWelDevice();
//        Log.i(TAG, "openWelDevice :" + iRet);
//    }
//
//    /**
//     * 关闭设备
//     */
//    public void closeWelDevice() {
//        int iRet = mgfp.closeWelDevice();
//        Log.i(TAG, "closeWelDevice :" + iRet);
//    }
//
//    /*直接采集特征*/
//    public synchronized void getFeature() {
//        featureCollectOk = false;
//        try {
//            String[] mStr = mgfp.getFeature(2);
//            strFtr = mStr[1];
//            String[] strSize = null;
//            if (mStr[0].equals("0")) {
//                strSize = mgfp.getFeatureQuality(mStr[1]);
//                Log.i(TAG, "数据信息 :" + mStr[1] + "\r\n" + "质量/信息： " + strSize[1] + "\r\n");
//                MainSocketData.requestFingerprintInfo(MainRequestConstants.REQUESTFINGERPRINT, mStr[1]);
//            } else {
//                Log.i(TAG, "数据信息 :" + mStr[1] + "\r\n 返回码：" + mStr[0]);
//                if (mStr[0].equals("-1") || mStr[0].equals("-3")) {
//                    closeWelDevice();
//                    openWelDevice();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        featureCollectOk = true;
//    }
//
//    /**
//     * 获取手指状态
//     *
//     * @return
//     */
//    public synchronized boolean fingerDetect() {
//        try {
//            String[] mStr;
//            mStr = mgfp.fingerDetect();
//            Log.i(TAG, "操作结果:"+mStr[0]+"状态信息:"+mStr[1]);
//            if (!mStr[0].equals("0") && !mStr[0].equals("1")) {
//                mgfp.resetWelDevice();
//                return false;
//            }
//            if (0 == mStr[1].compareTo("1")) {
//                Log.i(TAG, "finger press!");
//                return true;
//            } else {
//                Log.i(TAG, "no finger press!");
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//}
