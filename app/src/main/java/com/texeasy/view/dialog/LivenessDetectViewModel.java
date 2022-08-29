package com.texeasy.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.example.common.base.BaseApplication;
import com.example.common.utils.KLog;
import com.example.common.utils.ToastUtils;
import com.lazy.library.logging.Logcat;
import com.texeasy.utils.FaceHelper;
import com.texeasy.utils.FacePreviewInfo;
import com.texeasy.utils.FaceRectTransformer;
import com.texeasy.utils.RecognizeConfiguration;
import com.texeasy.utils.constants.LivenessType;
import com.texeasy.utils.constants.RecognizeColor;
import com.texeasy.view.widget.FaceRectView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LivenessDetectViewModel extends ViewModel {

    private static final String TAG = "LivenessDetectViewModel";

    private FaceEngine flEngine;
    private FaceEngine ftEngine;

    private FaceHelper faceHelper;
    private byte[] irNv21;
    private Camera.Size previewSize;

    private int dualCameraHorizontalOffset;
    private int dualCameraVerticalOffset;
    private int livenessMask;
    private boolean needUpdateFaceData;
    private ExecutorService livenessExecutor;

    private ConcurrentHashMap<Integer, Integer> rgbLivenessMap;
    private ConcurrentHashMap<Integer, Integer> irLivenessMap;
    private final ReentrantLock livenessDetectLock = new ReentrantLock();
    private FaceListener faceListener;

    @SuppressLint("CheckResult")
    public void init(Context context, FaceListener faceListener) {
        this.faceListener = faceListener;
//        String livenessTypeStr = ConfigUtil.getLivenessDetectType(ArcFaceApplication.getApplication());
        LivenessType livenessType = LivenessType.RGB;
//        if (livenessTypeStr.equals(ArcFaceApplication.getApplication().getString(R.string.value_liveness_type_ir))) {
//            livenessType = LivenessType.IR;
//        } else {
//            livenessType = LivenessType.RGB;
//        }
        rgbLivenessMap = new ConcurrentHashMap<>();
//        if (canOpenDualCamera && livenessType == LivenessType.IR) {
//            irLivenessMap = new ConcurrentHashMap<>();
//            livenessMask = FaceEngine.ASF_LIVENESS | FaceEngine.ASF_IR_LIVENESS | FaceEngine.ASF_FACE_DETECT;
//        } else {
        livenessMask = FaceEngine.ASF_LIVENESS;
//        }

//        dualCameraHorizontalOffset = ConfigUtil.getDualCameraHorizontalOffset(context);
//        dualCameraVerticalOffset = ConfigUtil.getDualCameraVerticalOffset(context);
//        if (dualCameraHorizontalOffset != 0 || dualCameraVerticalOffset != 0) {
//            needUpdateFaceData = true;
//            livenessMask |= FaceEngine.ASF_UPDATE_FACEDATA;
//        }
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            ftEngine = new FaceEngine();
            int result = ftEngine.init(
                    context, DetectMode.ASF_DETECT_MODE_VIDEO,
                    DetectFaceOrientPriority.valueOf(DetectFaceOrientPriority.ASF_OP_ALL_OUT.name()),
                    1, FaceEngine.ASF_FACE_DETECT
            );
            emitter.onNext(result);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
//                    ToastUtils.showShort("VIDEO: $result");
                    Logcat.d(KLog.TAG, "VIDEO: $result");
                });
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            flEngine = new FaceEngine();
            int result = flEngine.init(
                    context,
                    DetectMode.ASF_DETECT_MODE_IMAGE,
                    DetectFaceOrientPriority.valueOf(DetectFaceOrientPriority.ASF_OP_ALL_OUT.name()),
                    1,
                    livenessMask
            );
            emitter.onNext(result);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
//                    ToastUtils.showShort("IMAGE: $result");
                    Logcat.d(KLog.TAG, "IMAGE: $result");
                });
        livenessExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                r -> {
                    Thread t = new Thread(r);
                    t.setName("flThread-" + t.getId());
                    return t;
                });
    }

    /**
     * 销毁引擎，faceHelper中可能会有特征提取耗时操作仍在执行，加锁防止crash
     */
    private void unInit() {
        if (ftEngine != null) {
            synchronized (ftEngine) {
                int ftUnInitCode = ftEngine.unInit();
                Logcat.d(KLog.TAG, "unInitEngine: " + ftUnInitCode);
            }
        }
        if (flEngine != null) {
            synchronized (flEngine) {
                int frUnInitCode = flEngine.unInit();
                Logcat.d(KLog.TAG, "unInitEngine: " + frUnInitCode);
            }
        }
    }

    public void destroy() {
        if (livenessExecutor != null) {
            livenessExecutor.shutdown();
            livenessExecutor = null;
        }
        unInit();
    }

    public void onRgbCameraOpened(Camera camera) {
        Camera.Size lastPreviewSize = previewSize;
        previewSize = camera.getParameters().getPreviewSize();
        // 切换相机的时候可能会导致预览尺寸发生变化
        initFaceHelper(lastPreviewSize);
    }

    public void setRgbFaceRectTransformer(FaceRectTransformer rgbFaceRectTransformer) {
        faceHelper.setRgbFaceRectTransformer(rgbFaceRectTransformer);
    }

    public void setIrFaceRectTransformer(FaceRectTransformer irFaceRectTransformer) {
        faceHelper.setIrFaceRectTransformer(irFaceRectTransformer);
    }

    public List<FacePreviewInfo> onPreviewFrame(byte[] nv21) {
        List<FacePreviewInfo> facePreviewInfoList = faceHelper.onPreviewFrame(nv21, irNv21, false);
        clearLeftFace(facePreviewInfoList);
        return processLiveness(nv21, irNv21, facePreviewInfoList);
    }

    /**
     * 删除已经离开的人脸
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        Enumeration<Integer> keys = rgbLivenessMap.keys();
        while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == key) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                rgbLivenessMap.remove(key);
                if (irLivenessMap != null) {
                    irLivenessMap.remove(key);
                }
            }
        }
    }

    private List<FacePreviewInfo> processLiveness(byte[] nv21, byte[] irNv21, List<FacePreviewInfo> previewInfoList) {
        if (previewInfoList == null || previewInfoList.size() == 0) {
            return null;
        }
        if (!livenessDetectLock.isLocked() && livenessExecutor != null) {
            livenessExecutor.execute(() -> {
                List<FacePreviewInfo> facePreviewInfoList = new LinkedList<>(previewInfoList);
                livenessDetectLock.lock();
                try {
                    int processRgbLivenessCode;
                    if (facePreviewInfoList.isEmpty()) {
                        Logcat.e(KLog.TAG, "facePreviewInfoList isEmpty");
                    } else {
                        synchronized (flEngine) {
                            processRgbLivenessCode = flEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21,
                                    new ArrayList<>(Collections.singletonList(facePreviewInfoList.get(0).getFaceInfoRgb())), FaceEngine.ASF_LIVENESS);
                        }
                        if (processRgbLivenessCode != ErrorInfo.MOK) {
                            Logcat.e(KLog.TAG, "process RGB Liveness error: " + processRgbLivenessCode);
                        } else {
                            List<LivenessInfo> rgbLivenessInfoList = new ArrayList<>();
                            int getRgbLivenessCode = flEngine.getLiveness(rgbLivenessInfoList);
                            if (getRgbLivenessCode != ErrorInfo.MOK) {
                                Logcat.e(KLog.TAG, "get RGB LivenessResult error: " + getRgbLivenessCode);
                            } else {
                                rgbLivenessMap.put(facePreviewInfoList.get(0).getTrackId(), rgbLivenessInfoList.get(0).getLiveness());
                            }
                        }
                        if ((livenessMask & FaceEngine.ASF_IR_LIVENESS) != 0) {
                            List<FaceInfo> faceInfoList = new ArrayList<>();
                            FaceInfo irFaceInfo = facePreviewInfoList.get(0).getFaceInfoIr();
                            int fdCode = flEngine.detectFaces(irNv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                            if (fdCode == ErrorInfo.MOK && FaceHelper.isFaceExists(faceInfoList, irFaceInfo)) {
                                if (needUpdateFaceData) {
                                    /*
                                     * 若IR人脸框有偏移，则需要对IR的人脸数据进行updateFaceData处理，再将处理后的FaceInfo信息传输给活体检测接口
                                     */
                                    int faceDataCode = flEngine.updateFaceData(irNv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21,
                                            new ArrayList<>(Collections.singletonList(irFaceInfo)));
                                    if (faceDataCode != ErrorInfo.MOK) {
                                        Logcat.e(KLog.TAG, "process IR faceData error: " + faceDataCode);
                                    } else {
                                        processIrLive(irFaceInfo, facePreviewInfoList.get(0).getTrackId());
                                    }
                                } else {
                                    processIrLive(irFaceInfo, facePreviewInfoList.get(0).getTrackId());
                                }
                            } else {
                                Logcat.e(KLog.TAG, "process IR Liveness error: " + fdCode);
                            }
                        }
                    }
                } finally {
                    livenessDetectLock.unlock();
                }
            });
        }
        for (FacePreviewInfo facePreviewInfo : previewInfoList) {
            Integer rgbLiveness = rgbLivenessMap.get(facePreviewInfo.getTrackId());
            if (rgbLiveness != null) {
                facePreviewInfo.setRgbLiveness(rgbLiveness);
            }
            if (irLivenessMap != null) {
                Integer irLiveness = irLivenessMap.get(facePreviewInfo.getTrackId());
                if (irLiveness != null) {
                    facePreviewInfo.setIrLiveness(irLiveness);
                }
            }
        }
        return previewInfoList;
    }

    private void processIrLive(FaceInfo irFaceInfo, int trackId) {
        int processIrLivenessCode;
        synchronized (flEngine) {
            processIrLivenessCode = flEngine.processIr(irNv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21,
                    Arrays.asList(irFaceInfo), FaceEngine.ASF_IR_LIVENESS);
        }
        if (processIrLivenessCode != ErrorInfo.MOK) {
            Logcat.e(KLog.TAG, "process IR Liveness error: " + processIrLivenessCode);
        } else {
            List<LivenessInfo> irLivenessInfoList = new ArrayList<>();
            int getIrLivenessCode = flEngine.getIrLiveness(irLivenessInfoList);
            if (getIrLivenessCode != ErrorInfo.MOK) {
                Logcat.e(KLog.TAG, "get IR LivenessResult error: " + getIrLivenessCode);
            } else {
                irLivenessMap.put(trackId, irLivenessInfoList.get(0).getLiveness());
            }
        }
    }

    /**
     * 当相机打开时由activity调用，进行一些初始化操作
     *
     * @param camera 相机实例
     */
    public void onIrCameraOpened(Camera camera) {
        Camera.Size lastPreviewSize = previewSize;
        previewSize = camera.getParameters().getPreviewSize();
        // 切换相机的时候可能会导致预览尺寸发生变化
        initFaceHelper(lastPreviewSize);
        faceHelper.setDualCameraFaceInfoTransformer(faceInfo -> {
            FaceInfo irFaceInfo = new FaceInfo(faceInfo);
            irFaceInfo.getRect().offset(dualCameraHorizontalOffset, dualCameraVerticalOffset);
            return irFaceInfo;
        });
    }

    private void initFaceHelper(Camera.Size lastPreviewSize) {
        if (faceHelper == null ||
                lastPreviewSize == null ||
                lastPreviewSize.width != previewSize.width || lastPreviewSize.height != previewSize.height) {
            Integer trackedFaceCount = null;
            // 记录切换时的人脸序号
            if (faceHelper != null) {
                trackedFaceCount = faceHelper.getTrackedFaceCount();
                faceHelper.release();
            }
            Context context = BaseApplication.getInstance();

            faceHelper = new FaceHelper.Builder()
                    .ftEngine(ftEngine)
                    .previewSize(previewSize)
                    .onlyDetectLiveness(true)
                    .recognizeConfiguration(new RecognizeConfiguration.Builder().keepMaxFace(true).build())
                    .trackedFaceCount(trackedFaceCount == null ? 0 : trackedFaceCount)
                    .build();
        }
    }

    public void refreshIrPreviewData(byte[] nv21) {
        irNv21 = nv21;
    }

    /**
     * 根据预览信息生成绘制信息
     *
     * @param facePreviewInfoList 预览信息
     * @return 绘制信息
     */
    public List<FaceRectView.DrawInfo> getDrawInfo(List<FacePreviewInfo> facePreviewInfoList, LivenessType livenessType, byte[] nv21) {
        List<FaceRectView.DrawInfo> drawInfoList = new ArrayList<>();
        for (int i = 0; i < facePreviewInfoList.size(); i++) {
            int liveness = livenessType == LivenessType.RGB ? facePreviewInfoList.get(i).getRgbLiveness() : facePreviewInfoList.get(i).getIrLiveness();
            Rect rect = livenessType == LivenessType.RGB ?
                    facePreviewInfoList.get(i).getRgbTransformedRect() :
                    facePreviewInfoList.get(i).getIrTransformedRect();
            // 根据识别结果和活体结果设置颜色
            int color;
            String name;
            switch (liveness) {
                case LivenessInfo.ALIVE:
                    color = RecognizeColor.COLOR_SUCCESS;
                    name = "ALIVE";
                    try {
                        YuvImage image = new YuvImage(nv21, ImageFormat.NV21, previewSize.width, previewSize.height, null);            //ImageFormat.NV21  640 480
                        ByteArrayOutputStream outputSteam = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 70, outputSteam); // 将NV21格式图片，以质量70压缩成Jpeg，并得到JPEG数据流
                        byte[] jpegData = outputSteam.toByteArray();
                        String base64FaceData = Base64.encodeToString(jpegData, Base64.DEFAULT);
                        faceListener.callback(base64FaceData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case LivenessInfo.NOT_ALIVE:
                    color = RecognizeColor.COLOR_FAILED;
                    name = "NOT_ALIVE";
                    break;
                default:
                    color = RecognizeColor.COLOR_UNKNOWN;
                    name = "UNKNOWN";
                    break;
            }

            drawInfoList.add(new FaceRectView.DrawInfo(rect, GenderInfo.UNKNOWN,
                    AgeInfo.UNKNOWN_AGE, liveness, color, name));
        }
        return drawInfoList;
    }

//    //镜像处理
//    private Bitmap toHorizontalMirror(Bitmap bmp) {
//        int w = bmp.getWidth();
//        int h = bmp.getHeight();
//        Matrix matrix = new Matrix();
//        matrix.postScale(-1f, 1f); // 水平镜像翻转
//        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
//    }

    public Point loadPreviewSize() {
        String[] size = DEFAULT_PREVIEW_SIZE.split("x");
        return new Point(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
    }

    /**
     * 默认相机分辨率
     */
    private String DEFAULT_PREVIEW_SIZE = "960x720";

    interface FaceListener {
        void callback(String data);
    }
}
