package com.ytlz.arcsoftface.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.ytlz.arcsoftface.util.camera.CameraHelper1;
import com.ytlz.arcsoftface.util.camera.CameraListener;
import com.ytlz.arcsoftface.util.face.FaceHelper1;
import com.ytlz.arcsoftface.widget.MyFaceRectView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wyb on 2019-05-10.
 */

public class CameraUtil {

    private static final String TAG = "CameraUtil";

    static CameraUtil cameraUtil;
    CameraHelper1 cameraHelper1;
    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private View previewView;
    /**
     * 绘制人脸框的控件
     */
    private MyFaceRectView faceRectView;

    DrawHelper1 drawHelper1;
    private Camera.Size previewSize;
    /**
     * 优先打开的摄像头
     */
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    /**
     * 拍照状态码，准备拍照
     */
    private static final int TAKE_PHOTO_READY = 3;
    /**
     * 拍照状态码，拍照中
     */
    private static final int TAKE_PHOTO_PROCESSING = 4;
    /**
     * 拍照状态码，拍照结束（无论成功失败）
     */
    private static final int TAKE_PHOTO_DONE = 5;
    private int takePhotoStatus = TAKE_PHOTO_DONE;

    /**
     * 相机旋转角度
     */
    private int rotation = 1;


    public void setTEsRotate(float rotate) {
        this.rotate = rotate;
    }

    private Context context;
    private CameraLisenter lisenter;

    private static final String SAVE_IMG_DIR = "takephoto" + File.separator + "imgs";

    private static String ROOT_PATH;
    private float rotate;

    public static CameraUtil getInstance() {
        if (cameraUtil == null) {
            synchronized (CameraUtil.class) {
                if (cameraUtil == null) {
                    cameraUtil = new CameraUtil();
                }
            }
        }
        return cameraUtil;
    }

    //初始化
    public void init(Context context, View previewView, MyFaceRectView faceRectView) {
        this.context = context;
        this.previewView = previewView;
        this.faceRectView = faceRectView;
        if (null != context && null != previewView && null != faceRectView) {
            openCamera();
        } else {
            throw new NullPointerException("CameraUtil parameter can not be null");
        }
    }

    public void setRoate(float rotate) {
        this.rotate = rotate;
    }

    public void setCameraLitener(CameraLisenter litener) {
        this.lisenter = litener;
    }


    private void openCamera() {
        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {

                previewSize = camera.getParameters().getPreviewSize();
//                drawHelper1 = new DrawHelper1(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
//                        , cameraId, isMirror);

                lisenter.start(camera, cameraId, displayOrientation, isMirror);
            }


            @Override
            public void onPreview(final byte[] nv21, final Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                if (takePhotoStatus == TAKE_PHOTO_READY) {
                    takePhoto(nv21);
                }
//                List<FacePreviewInfo> facePreviewInfoList = faceHelper1.onPreviewFrame(nv21);
//                if (facePreviewInfoList != null && faceRectView != null && drawHelper1 != null) {
//                    List<FaceDrawInfo> faceDrawInfos = new ArrayList<>();
//                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
//                        //String name = faceHelper1.getName(facePreviewInfoList.get(i).getTrackId());
//                        faceDrawInfos.add(new FaceDrawInfo(facePreviewInfoList.get(i).getFaceInfo().getRect(), GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE, LivenessInfo.UNKNOWN,
//                                null));
//                    }
//                    drawHelper1.drawFaceFrame(faceRectView, faceDrawInfos);
//                }
            }

            @Override
            public void onCameraClosed() {
                lisenter.Close();
                Log.d(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                lisenter.Error(e.getMessage());
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
    }

    /**
     * 获取图片
     */
    public void takePhoto() {
        if (takePhotoStatus == TAKE_PHOTO_DONE) {
            takePhotoStatus = TAKE_PHOTO_READY;
        }
    }

    /**
     * 关闭
     */
    public void close() {
        if (null != cameraUtil) {
            cameraUtil = null;
        }
        if (null != cameraHelper1) {
            cameraHelper1.stop();
            cameraHelper1.release();
            cameraHelper1 = null;
        }
    }

    //拍照
    private void takePhoto(byte[] faceByte) {
        takePhotoStatus = TAKE_PHOTO_PROCESSING;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                String cameraDate = getBitmap(faceByte, previewSize.width, previewSize.height, rotate);
                emitter.onNext(cameraDate);
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String result) {
                        takePhotoStatus = TAKE_PHOTO_DONE;
                        if (TextUtils.isEmpty(result)) {
                            lisenter.Error("未知错误");
                        } else {
                            lisenter.Completion(result);
                        }
                        Log.e(TAG, "onNext: " + result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        takePhotoStatus = TAKE_PHOTO_DONE;
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    //保存到文件夹
    public String getBitmap(byte[] nv21, int width, int height, float rotate) {
        byte[] data = nv21;
        int frameSize = width * height;
        int[] rgba = new int[frameSize];

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int y = 255 & data[i * width + j];
                int u = 255 & data[frameSize + (i >> 1) * width + (j & -2) + 0];
                int v = 255 & data[frameSize + (i >> 1) * width + (j & -2) + 1];
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164F * (float) (y - 16) + 1.596F * (float) (v - 128));
                int g = Math.round(1.164F * (float) (y - 16) - 0.813F * (float) (v - 128) - 0.391F * (float) (u - 128));
                int b = Math.round(1.164F * (float) (y - 16) + 2.018F * (float) (u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = -16777216 + (b << 16) + (g << 8) + r;
            }
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        Bitmap temp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        if (ROOT_PATH == null) {
            ROOT_PATH = context.getFilesDir().getAbsolutePath();
        }
        boolean dirExists = true;
        //图片存储的文件夹
        File imgDir = new File(ROOT_PATH + File.separator + SAVE_IMG_DIR);
        if (!imgDir.exists()) {
            dirExists = imgDir.mkdirs();
        }
        if (!dirExists) {
            return "";
        }
        File pathFile = new File(imgDir, System.currentTimeMillis() + ".jpg");
        FileOutputStream fosImage = null;
        try {
            fosImage = new FileOutputStream(pathFile);
            temp.compress(Bitmap.CompressFormat.JPEG, 100, fosImage);
            fosImage.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathFile.getAbsolutePath();
    }


    public interface CameraLisenter {
        void start(Camera camera, int cameraId, int displayOrientation, boolean isMirror);

        void Completion(String path);

        void Error(String var1);

        void Close();
    }

}
