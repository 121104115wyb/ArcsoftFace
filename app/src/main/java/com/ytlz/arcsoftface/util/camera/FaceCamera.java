package com.ytlz.arcsoftface.util.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.ytlz.arcsoftface.faceserver.CompareResult1;
import com.ytlz.arcsoftface.faceserver.FaceServer1;
import com.ytlz.arcsoftface.model.DrawInfo1;
import com.ytlz.arcsoftface.model.FacePreviewInfo;
import com.ytlz.arcsoftface.model.FaceRegisterData;
import com.ytlz.arcsoftface.util.ConfigUtil;
import com.ytlz.arcsoftface.util.DrawHelper1;
import com.ytlz.arcsoftface.util.face.FaceHelper1;
import com.ytlz.arcsoftface.util.face.FaceListener1;
import com.ytlz.arcsoftface.util.face.RequestFeatureStatus;
import com.ytlz.arcsoftface.widget.MyFaceRectView;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

//import com.ytlz.msirobot.activity.RegisterAndRecognizeActivity;
//import com.ytlz.msirobot.widget.ShowFaceInfoAdapter;

/**
 * Created by wyb on 2019-04-28.
 */

public class FaceCamera {

    private static final String TAG = "FaceCamera";
    private static final int MAX_DETECT_NUM = 10;
    /**
     * 当FR成功，活体未成功时，FR等待活体的时间
     */
    private static final int WAIT_LIVENESS_INTERVAL = 50;
    private CameraHelper1 cameraHelper1;
    private DrawHelper1 drawHelper1;
    private Camera.Size previewSize;
    /**
     * 优先打开的摄像头
     */
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private FaceEngine faceEngine;
    private FaceHelper1 faceHelper1;
    private List<CompareResult1> compareResult1List;
//    private ShowFaceInfoAdapter adapter;
    /**
     * 活体检测的开关
     */
    private boolean livenessDetect = true;

    /**
     * 注册人脸状态码，准备注册
     */
    private static final int REGISTER_STATUS_READY = 0;
    /**
     * 注册人脸状态码，注册中
     */
    private static final int REGISTER_STATUS_PROCESSING = 1;
    /**
     * 注册人脸状态码，注册结束（无论成功失败）
     */
    private static final int REGISTER_STATUS_DONE = 2;

    private int registerStatus = REGISTER_STATUS_DONE;


    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();
    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private View previewView;
    /**
     * 绘制人脸框的控件
     */
    private MyFaceRectView faceRectView;

    private static final float SIMILAR_THRESHOLD = 0.8F;

    //    static FaceCamera faceCamera;
    Context context;

    /**
     * 相机旋转角度
     */
    int rotation = 1;
    private String name;
    FaceCameraResult cameraResult;
    public static int codeType = 10;

    public void setCameraResult(FaceCameraResult cameraResult) {
        this.cameraResult = cameraResult;
    }

    //    public static FaceCamera getInstance() {
//        if (null == faceCamera) {
//            synchronized (FaceCamera.class) {
//                if (faceCamera == null) {
//                    faceCamera = new FaceCamera();
//                }
//            }
//        }
//        return faceCamera;
//    }


    public void init(Context context, View previewView, MyFaceRectView faceRectView, FaceEngine faceEngine, int rotation) {
        this.context = context;
        this.previewView = previewView;
        this.faceRectView = faceRectView;
        this.rotation = rotation;
        this.faceEngine = faceEngine;
        if (null != context && null != previewView && null != faceRectView && null != faceEngine) {

            initCamera();


        } else {
            throw new NullPointerException("facecamera parameter can not be null");
        }

    }


    /**
     * 销毁引擎
     */
    private void unInitEngine() {
        if (null != faceEngine) {
            int unInit = faceEngine.unInit();
            Log.d(TAG, "unInitEngine: " + unInit);
        }
    }

    //回收
    public void recycle() {
        if (cameraHelper1 != null) {
            cameraHelper1.release();
            cameraHelper1 = null;
        }
        //faceHelper中可能会有FR耗时操作仍在执行，加锁防止crash
        if (faceHelper1 != null) {
            synchronized (faceHelper1) {
                unInitEngine();
            }
            ConfigUtil.setTrackId(context, faceHelper1.getCurrentTrackId());
            faceHelper1.release();
        } else {
            unInitEngine();
        }
        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.dispose();
            getFeatureDelayedDisposables.clear();
        }

        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.dispose();
            getFeatureDelayedDisposables.clear();
        }
        FaceServer1.getInstance().unInit();

    }

    private void initCamera() {
        final FaceListener1 faceListener1 = new FaceListener1() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "onFail: " + e.getMessage());
            }

            //请求FR的回调
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId) {
                //FR成功
                if (faceFeature != null) {
//                    Log.d(TAG, "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId);

                    //不做活体检测的情况，直接搜索
                    if (!livenessDetect) {
                        searchFace(faceFeature, requestId);
                    }
                    //活体检测通过，搜索特征
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                        searchFace(faceFeature, requestId);
                    }
                    //活体检测未出结果，延迟100ms再执行该函数
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        getFeatureDelayedDisposables.add(Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                        onFaceFeatureInfoGet(faceFeature, requestId);
                                    }
                                }));
                    }
                    //活体检测失败
                    else {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.NOT_ALIVE);
                    }

                }
                //FR 失败
                else {
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                }
            }

        };


        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper1 = new DrawHelper1(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror);

                faceHelper1 = new FaceHelper1.Builder()
                        .faceEngine(faceEngine)
                        .frThreadNum(MAX_DETECT_NUM)
                        .previewSize(previewSize)
                        .faceListener(faceListener1)
                        .currentTrackId(ConfigUtil.getTrackId(context.getApplicationContext()))
                        .build();
            }


            @Override
            public void onPreview(final byte[] nv21, final Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }


                List<FacePreviewInfo> facePreviewInfoList = faceHelper1.onPreviewFrame(nv21);
                if (facePreviewInfoList != null && faceRectView != null && drawHelper1 != null) {
                    List<DrawInfo1> drawInfo1List = new ArrayList<>();
                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
                        String name = faceHelper1.getName(facePreviewInfoList.get(i).getTrackId());
                        drawInfo1List.add(new DrawInfo1(facePreviewInfoList.get(i).getFaceInfo().getRect(), GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE, LivenessInfo.UNKNOWN,
                                name == null ? String.valueOf(facePreviewInfoList.get(i).getTrackId()) : name));
                    }
                    drawHelper1.draw2(faceRectView, drawInfo1List);
                }
                if (registerStatus == REGISTER_STATUS_READY && facePreviewInfoList != null && facePreviewInfoList.size() > 0) {
                    registerStatus = REGISTER_STATUS_PROCESSING;
                    if (codeType == 10) {
                        Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(ObservableEmitter<Boolean> emitter) {
                                boolean success = FaceServer1.getInstance().register(context, nv21.clone(), previewSize.width, previewSize.height, "registered " + faceHelper1.getCurrentTrackId());
                                emitter.onNext(success);
                            }
                        })
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                    }

                                    @Override
                                    public void onNext(Boolean success) {

                                        String result = success ? "register success!" : "register failed!";
                                        Log.d(TAG, "onNext: ----------" + result);
                                        registerStatus = REGISTER_STATUS_DONE;
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        cameraResult.recognitionFails();
                                        Log.d(TAG, "onError:--------register failed!");
                                        registerStatus = REGISTER_STATUS_DONE;
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }else {
                        Observable.create(new ObservableOnSubscribe<FaceRegisterData>() {
                            @Override
                            public void subscribe(ObservableEmitter<FaceRegisterData> emitter) {
                                FaceRegisterData registerData = FaceServer1.getInstance().register(context, nv21.clone(), previewSize.width, previewSize.height, "registered ", "121104115");
                                emitter.onNext(registerData);
                            }
                        })
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<FaceRegisterData>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(FaceRegisterData success) {
                                        if (null != success) {
                                            cameraResult.resgistSuccess(success);
                                        } else {
                                            cameraResult.resgistFails(name);
                                        }
                                        Log.d(TAG, "onNext: ----------" + success);
                                        registerStatus = REGISTER_STATUS_DONE;
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        cameraResult.resgistFails(name);
                                        Log.d(TAG, "onError:--------register failed!");
                                        registerStatus = REGISTER_STATUS_DONE;
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }
                clearLeftFace(facePreviewInfoList);

                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {

                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
                        if (livenessDetect) {
                            livenessMap.put(facePreviewInfoList.get(i).getTrackId(), facePreviewInfoList.get(i).getLivenessInfo().getLiveness());
                        }
                        /**
                         * 对于每个人脸，若状态为空或者为失败，则请求FR（可根据需要添加其他判断以限制FR次数），
                         * FR回传的人脸特征结果在{@link FaceListener1#onFaceFeatureInfoGet(FaceFeature, Integer)}中回传
                         */
                        if (requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == null
                                || requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == RequestFeatureStatus.FAILED) {
                            requestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);

                            if (codeType==11){
                                faceHelper1.requestFaceFeature(nv21, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());
                            }

//                            Log.d(TAG, "onPreview: fr start = " + System.currentTimeMillis() + " trackId = " + facePreviewInfoList.get(i).getTrackId());
                        }
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.d(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.d(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper1 != null) {
                    drawHelper1.setCameraDisplayOrientation(displayOrientation);
                }
                Log.d(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };

        cameraHelper1 = new CameraHelper1.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(rotation)
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper1.init();
//        cameraHelper1.start();
    }


    /**
     * 删除已经离开的人脸
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        Set<Integer> keySet = requestFeatureStatusMap.keySet();
        if (compareResult1List != null) {
            for (int i = compareResult1List.size() - 1; i >= 0; i--) {
                if (!keySet.contains(compareResult1List.get(i).getTrackId())) {
                    compareResult1List.remove(i);
//                    adapter.notifyItemRemoved(i);
                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            requestFeatureStatusMap.clear();
            livenessMap.clear();
            return;
        }

        for (Integer integer : keySet) {
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == integer) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                requestFeatureStatusMap.remove(integer);
                livenessMap.remove(integer);
            }
        }

    }

    private void searchFace(final FaceFeature frFace, final Integer requestId) {
        Observable
                .create(new ObservableOnSubscribe<CompareResult1>() {
                    @Override
                    public void subscribe(ObservableEmitter<CompareResult1> emitter) {
//                        Log.d(TAG, "subscribe: fr search start = " + System.currentTimeMillis() + " trackId = " + requestId);
                        CompareResult1 compareResult1 = FaceServer1.getInstance().getTopOfFaceLib(frFace);
//                        Log.d(TAG, "subscribe: fr search end = " + System.currentTimeMillis() + " trackId = " + requestId);
                        if (compareResult1 == null) {
                            emitter.onError(null);
                        } else {
                            emitter.onNext(compareResult1);
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult1>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CompareResult1 compareResult) {
                        if (compareResult == null || compareResult.getUserName() == null) {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper1.addName(requestId, "VISITOR " + requestId);
                            return;
                        }

//                        Log.d(TAG, "onNext: fr search get result  = " + System.currentTimeMillis() + " trackId = " + requestId + "  similar = " + compareResult.getSimilar());
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                            FaceRegisterData data = new FaceRegisterData();
                            data.setName(name);
                            data.setSimilar(compareResult.getSimilar());

                            String faceFeature1 = Base64.encodeToString(frFace.getFeatureData(), Base64.DEFAULT);
                            data.setFaceFeature(faceFeature1);

                            cameraResult.recognitionSuccess(data);

                            codeType = 10;
                            boolean isAdded = false;
                            if (compareResult1List == null) {
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                                faceHelper1.addName(requestId, "VISITOR " + requestId);
                                return;
                            }
                            for (CompareResult1 compareResult1 : compareResult1List) {
                                if (compareResult1.getTrackId() == requestId) {
                                    isAdded = true;
                                    break;
                                }
                            }
                            if (!isAdded) {
                                //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                                if (compareResult1List.size() >= MAX_DETECT_NUM) {
                                    compareResult1List.remove(0);
//                                    adapter.notifyItemRemoved(0);
                                }
                                //添加显示人员时，保存其trackId
                                compareResult.setTrackId(requestId);
                                compareResult1List.add(compareResult);
//                                adapter.notifyItemInserted(compareResult1List.size() - 1);
                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelper1.addName(requestId, compareResult.getUserName());

                        } else {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper1.addName(requestId, "VISITOR " + requestId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void setLivenessDetect(Boolean livensss) {
        this.livenessDetect = livensss;
    }

    public boolean getLivenessDetect() {
        return livenessDetect;
    }


    /**
     * 将准备注册的状态置为{@link #REGISTER_STATUS_READY}
     */
    public void register() {
        if (registerStatus == REGISTER_STATUS_DONE) {
            registerStatus = REGISTER_STATUS_READY;
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}
