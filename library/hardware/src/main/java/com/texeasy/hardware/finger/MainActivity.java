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
// * ?????????????????????----????????????????????????????????????????????????
// */
//@Route(path = PathConstants.MAIN_ACTIVITY_PATH, extras = 0)
//public class MainActivity extends BaseActivity {
//    //TOO ??????????????????????????????
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
//    //????????????
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
//     * ??????OSS
//     */
//    private OSSClient oss;
//    public GfpUtils mgfp = null;
//    public String strFtr = null;
//    private Timer fingerprintQueryTimer;
//    private boolean isLogin;//??????????????????
//    private boolean featureCollectOk = true;//????????????????????????
//    private boolean isGoFace = false;//????????????????????????
//    private boolean isExecute = true;//??????????????????????????????
//    ////////////////////////////
//    String TAG1 = "AIDL????????????:";
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
//        //////////////////////////??????aidl
////        serviceProxy =  ServiceProxy.getInstance();
////        jsonObjectxxxxx.put("statusMsg", "?????????");
////        result = JSONObject.toJSONString(jsonObjectxxxxx);
////        serviceProxy.bindService(MainActivity.this);
//        ///////////////////////////////////////////
//        //?????????OSS
//        initOSSClient();
//        mgfp = new GfpUtils(getApplicationContext());
//        openWelDevice();
//        if (!TextUtils.isEmpty(String.valueOf(SPUtils.get(this,DOOR_NUMBER_FLAG,"")))) {
//           DOOR_NUMBER =  String.valueOf(SPUtils.get(this,DOOR_NUMBER_FLAG, ""));
//        }
//        Log.d("?????????","??????????????? "+DOOR_NUMBER);
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
//        //???????????????
//        main_img_sub_logo = findViewById(R.id.main_img_sub_logo);
//        main_title = findViewById(R.id.main_title);
//        if (chanelName != null && chanelName.contains("shoes")) {
//            Glide.with(MainActivity.this).load(MainConstants.subDeviceLogoId).into(main_img_sub_logo);
//        } else {
//            Glide.with(MainActivity.this).load(MainConstants.subDeviceLogoIdCloth).into(main_img_sub_logo);
//        }
//        //???????????????
//        if (chanelName != null && chanelName.contains("aucland_infor_publish")) {
//            main_title.setVisibility(View.GONE);
//        } else {
//            main_title.setVisibility(View.VISIBLE);
//        }
//        main_tx_sub_title = findViewById(R.id.main_tx_sub_title);
//        //????????????????????????
//        main_rl_regist = findViewById(R.id.main_rl_regist);
//        //???????????????
//        main_et_regist = findViewById(R.id.main_et_regist);
//        //???????????????????
//        main_bt_regist = findViewById(R.id.main_bt_regist);
//        main_index_title_logo.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Log.e("??????", "onLongClick");
//                EventBus.getDefault().post(new NotifyBaseBean(CommonConstants.DRESSINGTOSETTING));
//                return false;
//            }
//        });
//        main_bt_regist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TOO ????????????
//                String content = main_et_regist.getText().toString();
//                if (TextUtils.isEmpty(content)) {
//                    T.showToast("??????ID????????????");
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
//                    Log.i(TAG, "?????????????????????");
//                } else {
//                    Log.i(TAG, "????????????????????????????????????");
//                    if (isGoFace) {
//                        Log.i(TAG, "?????????????????????,??????????????????");
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
////            //??????????????????
////            main_index_title_deivcename.setText(getResources().getString(R.string.main_tv_title_deviceid, cabinetId));
////        }
//
////        else {
////            //TOO ??????    ?????????????????????
////            showRegistView();
////            return;
////        }
//    }
//
//    /**
//     * ?????????oss
//     */
//    private void initOSSClient() {
//        // ????????????????????????????????????????????????
//        ClientConfiguration conf = new ClientConfiguration();
//        OSSCredentialProvider credentialProvider = new MyOSSAuthCredentialsProvider(Config.STS_SERVER_URL);
//        // ?????????????????????15??????
//        conf.setConnectionTimeout(15 * 1000);
//        // socket???????????????15??????
//        conf.setSocketTimeout(15 * 1000);
//        // ??????????????????????????????5??????
//        conf.setMaxConcurrentRequest(5);
//        // ????????????????????????????????????2??????
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
//                //???????????????
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
//        //?????????????????????????????????
//        if (mListener != null) {
//            mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//        }
//        switch (netWorkState) {
//            case CommonUtils.NETWORK_ETHERNET://?????????
//                Log.d("????????????", "?????????");
//                iv_signal.setImageResource(R.drawable.mian_ic_net_ethernet);
//                break;
//            case CommonUtils.NETWORK_MOBILE://?????????
//                iv_signal.setImageResource(R.drawable.main_level_signal);
//                //????????????????????????
//                mListener = new PhoneStateListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.M)
//                    @Override
//                    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//                        super.onSignalStrengthsChanged(signalStrength);
//                        mLevel = signalStrength.getLevel();
//                    }
//                };
//                //??????????????????
//                mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//                drawable = (LevelListDrawable) iv_signal.getDrawable();
//                drawable.setLevel(mLevel);
//                break;
//
//            case CommonUtils.NETWORK_WIFI://wifi
//                iv_signal.setImageResource(R.drawable.main_level_wifi);
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                //?????????????????????
//                level = wifiInfo.getRssi();
//                //???????????????????????????????????????
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
//            case CommonUtils.NETWORK_NONE://????????????
//                iv_signal.setImageResource(R.drawable.main_level_signal);
//                level = 0;
//                drawable = (LevelListDrawable) iv_signal.getDrawable();
//                drawable.setLevel(level);
//                break;
//            default:
//                Log.w("????????????netWorkState", netWorkState + "");
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
//        Log.d("AIDL","??????aidl??????");
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
//     * ???????????????4?????????
//     */
//    private void initFragment() {
//        FragmentTransaction transaction = manager.beginTransaction();
//        //????????????
//        settingFragment = (Fragment) ARouter.getInstance().build(PathConstants.MODULE_SETTING_PATH).withInt(PathConstants.KEY_PRO_NAME, CommonConstants.KEY_PROJECT_NAME_DRESSING).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
//        if (chanelName != null && chanelName.contains("aucland_recycle")) {
//            //?????????????????????
//            fragment = (Fragment) ARouter.getInstance().build(PathConstants.RECYCLE_MODULE_PATH).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
//        } else if (chanelName != null && chanelName.contains("aucland_dressing")) {
//            //?????????????
//            fragment = (Fragment) ARouter.getInstance().build(PathConstants.DRESSING_HOME_PATH).withString(PathConstants.KEY_CHANNEL_NAME, chanelName).navigation();
//        } else if (chanelName != null && chanelName.contains("aucland_dispense")) {
//            //????????????
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
//            Log.e("??????????????????", "onConnectSocketSuccess" + content+"CommonConstants.SERVICE_STATE_SOCKE_SUC" + CommonConstants.SERVICE_STATE_SOCKE_SUC);
//            EventBus.getDefault().removeStickyEvent(content);
//            SocketData.requestVMInfo(RequestConstants.REQUEST_VMINFO);
////            Log.e("????????????", "CommonConstants.SERVICE_STATE_SOCKE_SUC" + CommonConstants.SERVICE_STATE_SOCKE_SUC);
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
//            Log.e("????????????", "??????????????????");
//            currentShowPager = DISPENSE_ADD_PAGER;
//            transaction.hide(fragment);
//            transaction.hide(settingFragment);
//            transaction.show(inboxfragment);
//        } else if (CommonConstants.ADDTODISPENSE.equals(notifyBaseBean.getTAG())) {
//            currentShowPager = MAIN_PAGER;
//            transaction.hide(settingFragment);
//            transaction.hide(inboxfragment);
//            transaction.show(fragment);
//            Log.d("????????????","????????????");
//            DispenseSocketData.requestOpsLogOut(DispenseRequestConstants.REQUESTOPSLOGOUT);
//        }
//        transaction.commitAllowingStateLoss();
//    }
//
//    @Override
//    public void onSocketSuccess(String method, BaseModel baseModel) {
//        Log.e("????????????", method + baseModel.toString());
//
//        switch (method) {
//
//
//            case RequestConstants.REQUEST_GOODINFO:
//                break;
//            case RequestConstants.REQUESTCONFIG:
//                //??????????????????id??????
//                T.showToast("????????????");
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
//                            //TOO ??????    ?????????????????????
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
//                        //?????????????????????????????????????????????????????????,?????????,?????????,?????????,?????????
//                        if (!TextUtils.isEmpty(vmInfo.getSubDeviceName())) {
//                            main_tx_sub_title.setText(vmInfo.getSubDeviceName());
//                        } else {
//                            main_tx_sub_title.setText(MainConstants.subDeviceName);
//                        }
//                        SPUtils.put(this, SPUtils.TAG_KEY_VMINFOR, result);
//                        if (vmInfo.getServerStatus() == 1) {
//                            Log.e("????????????", "CommonConstants.SERVICE_STATE_SERVER_SUC" + CommonConstants.SERVICE_STATE_SERVER_SUC);
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
//                        Log.w("????????????", "??????????????????????????????");
//                    }
//                } else {
//                    Log.e("????????????", "????????????????????????" + method);
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
//                Log.d(TAG, "????????????App???????????????");
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
//            Log.e("????????????", "????????????currentShowPager" + currentShowPager);
//            return;
//        }
//        CommonUtils.hiddenSoftInputer(this);
//        Log.e("????????????", "????????????fragment" + isok + Thread.currentThread().getName());
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
//                Log.e("??????", "KEYCODE_BACK---");
//                EventBus.getDefault().post(new NotifyBaseBean(CommonConstants.DRESSINGTOSETTING));
//                return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    /**
//     * ????????????
//     *
//     * @param objectName ????????????
//     * @param localFile  ??????????????????
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
//        // ?????????????????????
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
//                Log.e("onFailure", "???????????????" + request.toString() + "----" + result.getServerCallbackReturnBody());
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                Log.e("onFailure", "???????????????" + request.toString());
//                // ???????????????
//                if (clientExcepion != null) {
//                    Log.e("clientExcepion", clientExcepion.getMessage());
//                    // ????????????????????????????????????
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // ???????????????
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
//     * ??????????????????
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
//        Log.i(TAG, "????????????:" + currentTime+"filePath" + filePath);
//
//        for (int i = 0; i < filePath.size(); i++) {
//            if (filePath.get(i).getName().contains(currentTime)) {
//                if (filePath.get(i).getName().contains("_watchdog.log")) {
//                    //???????????????
//                    Log.i(TAG, "???????????????:" + filePath.get(i).getPath());
//                    putFile(String.format("app/logs/%s", filePath.get(i).getName()), filePath.get(i).getPath());
//                } else if (filePath.get(i).getName().contains("UI.log")) {
//                    //UI??????
//                    Log.i(TAG, "UI??????:" + filePath.get(i).getPath());
//                    putFile(String.format("app/logs/%s", filePath.get(i).getName()), filePath.get(i).getPath());
//                } else if (filePath.get(i).getName().contains(".log")) {
//                    //????????????
//                    Log.i(TAG, "????????????:" + filePath.get(i).getPath());
//                    putFile(String.format("app/logs/%s", filePath.get(i).getName()), filePath.get(i).getPath());
//                }
//            }
//        }
//    }
//
//    @Subscribe(sticky = true)
//    public void getLoginStatus(LoginStatusConttants loginStatus) {
//        if (loginStatus != null) {
////            Log.e("??????????????????", "LoginStatus" + loginStatus);
//            EventBus.getDefault().removeStickyEvent(loginStatus);
//            if (loginStatus.getType() == LoginStatusConttants.LOGIN) {
//                Log.i(TAG, "??????????????????: LoginStatus" + loginStatus);
//                isLogin = true;
//                Constants.isLogin = true;
//            } else if (loginStatus.getType() == LoginStatusConttants.LOGIN_OUT) {
//                Log.i(TAG, "??????????????????: LoginStatus" + loginStatus);
//                isLogin = false;
//                Constants.isLogin = false;
//            }
//        }
//    }
//
//    @Subscribe(sticky = true)
//    public void getIsGoFace(String busConstants) {
//        if (busConstants != null) {
//            Log.e("??????????????????", "LoginStatus" + busConstants);
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
//     * ????????????
//     */
//    public void openWelDevice() {
//        int iRet = mgfp.openWelDevice();
//        Log.i(TAG, "openWelDevice :" + iRet);
//    }
//
//    /**
//     * ????????????
//     */
//    public void closeWelDevice() {
//        int iRet = mgfp.closeWelDevice();
//        Log.i(TAG, "closeWelDevice :" + iRet);
//    }
//
//    /*??????????????????*/
//    public synchronized void getFeature() {
//        featureCollectOk = false;
//        try {
//            String[] mStr = mgfp.getFeature(2);
//            strFtr = mStr[1];
//            String[] strSize = null;
//            if (mStr[0].equals("0")) {
//                strSize = mgfp.getFeatureQuality(mStr[1]);
//                Log.i(TAG, "???????????? :" + mStr[1] + "\r\n" + "??????/????????? " + strSize[1] + "\r\n");
//                MainSocketData.requestFingerprintInfo(MainRequestConstants.REQUESTFINGERPRINT, mStr[1]);
//            } else {
//                Log.i(TAG, "???????????? :" + mStr[1] + "\r\n ????????????" + mStr[0]);
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
//     * ??????????????????
//     *
//     * @return
//     */
//    public synchronized boolean fingerDetect() {
//        try {
//            String[] mStr;
//            mStr = mgfp.fingerDetect();
//            Log.i(TAG, "????????????:"+mStr[0]+"????????????:"+mStr[1]);
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
