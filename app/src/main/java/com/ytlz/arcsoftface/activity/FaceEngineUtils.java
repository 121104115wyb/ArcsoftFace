package com.ytlz.arcsoftface.activity;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.ytlz.arcsoftface.faceserver.FaceServer1;
import com.ytlz.arcsoftface.util.face.FaceHelper1;

import static com.blankj.utilcode.util.StringUtils.getString;

/**
 * Created by wyb on 2019-04-26.
 */

public class FaceEngineUtils {

    private static final String TAG = "FaceEngineUtils";
    FaceEngineUtils instance;


    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    //调用native方法的辅助类
    private FaceEngine faceEngine;
    //辅助类成功标志位
    private int afCode = -1;

    private FaceHelper1 faceHelper1;

    private Context context;

    public FaceEngineUtils getInstance() {
        if (null == instance) {
            synchronized (FaceEngineUtils.class) {
                if (null == instance) {
                    instance = new FaceEngineUtils();
                }
            }
        }
        return instance;
    }

    //初始化
    static void init(Context context) {
        //本地人脸库初始化
        FaceServer1.getInstance().init(context);


    }
//
//    private String initEngine() {
//        faceEngine = new FaceEngine();
//        afCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(context),
//                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
//
//        //获取版本，没必要
////        VersionInfo versionInfo = new VersionInfo();
////        faceEngine.getVersion(versionInfo);
////        Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);
//
//        if (afCode != ErrorInfo.MOK) {
//            return getString(R.string.init_failed, afCode);
//        }
//    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {

        if (afCode == ErrorInfo.MOK) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }


}
