package com.ytlz.arcsoftface.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.VersionInfo;
import com.ytlz.arcsoftface.R;
import com.ytlz.arcsoftface.common.Constants;
import com.ytlz.arcsoftface.faceserver.FaceServer1;
import com.ytlz.arcsoftface.model.FaceRegisterData;
import com.ytlz.arcsoftface.model.PersonalInfoData;
import com.ytlz.arcsoftface.util.camera.FaceCamera;
import com.ytlz.arcsoftface.util.camera.FaceCameraResult;
import com.ytlz.arcsoftface.view.FaceEngineCallBack;
import com.ytlz.arcsoftface.view.FaceRecognitionView;
import com.ytlz.arcsoftface.view.PeronalInfoView;
import com.ytlz.arcsoftface.widget.MyFaceRectView;

import java.util.List;

/**
 * Created by wyb on 2019-04-28.
 */

public class FaceRecognition1 implements FaceCameraResult, PeronalInfoView {

    private static final String TAG = "FaceRecognition1";
    /**
     * 引擎初始化标志位
     */
    private Boolean isInitEngine = false;

    /**
     * 引擎初始化标志位
     */
    private Boolean activeEngine = false;

    /**
     * faceEngine 版本信息
     */
    String version = "";

    FaceCamera faceCamera = new FaceCamera();

    Context fRcontext;
    FaceEngineCallBack engineCallBack;
    private static final int MAX_DETECT_NUM = 10;

//    PersonalInfoPresenter presenter;
    FaceRecognitionView faceRecognitionView;
    public FaceRecognition1() {

    }

    public void setEngineCallBack(FaceEngineCallBack engineCallBack) {
        this.engineCallBack = engineCallBack;
    }


    public void init(Context context, View previewView, MyFaceRectView faceRectView, int rotation) {
        ConfigUtil.setFtOrient(context, FaceEngine.ASF_OP_270_ONLY);
        this.fRcontext = context;
        faceCamera.setCameraResult(this);
        faceCamera.init(context, previewView, faceRectView, engine(), rotation);
        FaceServer1.getInstance().init(context);

//        presenter = new PersonalInfoPresenter();
//        presenter.setView(this);


    }

    public void activeEngine() {

        FaceEngine faceEngine = new FaceEngine();
        int activeCode = faceEngine.active(fRcontext, Constants.APP_ID, Constants.SDK_KEY);
        if (activeCode == ErrorInfo.MOK) {
            Log.d(TAG, "activeEngine:------ activeFaceEnSuccess");
            engineCallBack.faceEnSuccess(fRcontext.getString(R.string.active_success));
            activeEngine = true;
        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
            Log.d(TAG, "activeEngine:------ MERR_ASF_ALREADY_ACTIVATED");
            engineCallBack.faceEnSuccess(fRcontext.getString(R.string.already_activated));
            activeEngine = true;
        } else {
            activeEngine = false;
            engineCallBack.faceEnError(fRcontext.getString(R.string.active_failed), activeCode);
            Log.d(TAG, "activeEngine:------ activeFaceEnError");
        }
    }


    public FaceEngine engine() {
        /**
         * 激活标志位
         */
        FaceEngine faceEngine = new FaceEngine();
        int afCode = faceEngine.init(fRcontext, FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(fRcontext),
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS| FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER );
        if (afCode == ErrorInfo.MOK) {
            engineCallBack.faceEnSuccess(fRcontext.getString(R.string.init_success));

        } else {
            engineCallBack.faceEnError(fRcontext.getString(R.string.init_failed), afCode);
            isInitEngine = false;
            return null;
        }

        isInitEngine = true;
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        version = versionInfo.getVersion();
        Log.d(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);

        return faceEngine;

    }

    /**
     * 注册
     */
    public void register(String name) {
        if (faceCamera != null) {
            faceCamera.setName(name);
            faceCamera.register();
            FaceCamera.codeType = 13;
        }
    }

    /**
     * 检测识别
     */
    public void detection() {
        if (faceCamera != null) {
//            faceCamera.register();
            FaceCamera.codeType = 11;
        }
    }


    public void recycle() {
        if (null != faceCamera) {
            faceCamera.recycle();
            version = null;
//            isInitEngine = null;
//            activeEngine = null;
        }
    }

    public Boolean getActiveEngine() {
        return activeEngine;
    }

    public Boolean getInitEngine() {
        return isInitEngine;
    }

    public String getVersion() {
        return version;
    }

    /**
     * 人脸注册成功
     *
     * @param data
     */
    @Override
    public void resgistSuccess(FaceRegisterData data) {
        if (null != data) {
            PersonalInfoData infoData = new PersonalInfoData();
            infoData.setAddress("苏州");
//            infoData.setAge(data.getAgeInfo().getAge());
//            infoData.setName(data.getName());
//            infoData.setSex(data.getGenderInfo().getGender());
//            infoData.setPassWord(data.getPassword());
//            presenter.addPersonalInfoData(infoData);
            Log.d(TAG, "resgistSuccess: 人脸注册成功------data："+data);

        }
    }

    /**
     * 人脸注册失败
     *
     * @param name
     */
    @Override
    public void resgistFails(String name) {
        faceRecognitionView.registError("人脸注册失败");
    }

    /**
     * 人脸检测识别成功
     *
     * @param data
     */
    @Override
    public void recognitionSuccess(FaceRegisterData data) {

        if (null!=data){

            Log.d(TAG, "recognitionSuccess: -----人脸检测识别成功，相似度："+data.getSimilar());
        }

    }

    /**
     * 人脸检测识别失败
     */
    @Override
    public void recognitionFails() {
        Log.d(TAG, "recognitionFails: -----人脸检测识别失败");
    }

    @Override
    public void IdCardContrastResult(Boolean b) {

    }

    @Override
    public void getBitmapSucess(Bitmap bitmap) {

    }

    @Override
    public void getBitmapFailed() {

    }

    @Override
    public void getDetectionSucess() {

    }

    @Override
    public void addInfoSuccess(PersonalInfoData personalInfoData) {
        faceRecognitionView.registSuccess();
    }

    @Override
    public void addInfoFailed(String message) {
        faceRecognitionView.registError(message);
    }

//    @Override
//    public void getUserInfoSuccess(GuestDO personalInfoData) {
//
//    }

    @Override
    public void getUserInfoFailed(String message) {

    }

    @Override
    public void getUserNameInfoSuccess(List<PersonalInfoData> personalInfoData) {

    }

    @Override
    public void getUserNameInfoFailed(String message) {

    }

    @Override
    public void getUserphoneNumInfoSuccess(List<PersonalInfoData> personalInfoData) {

    }

    @Override
    public void getUserphoneNumInfoFailed(String message) {

    }

    @Override
    public void getUseraddressInfoSuccess(List<PersonalInfoData> personalInfoData) {

    }

    @Override
    public void getUseraddressInfoFailed(String message) {

    }

    @Override
    public void getUserportionInfoSuccess(List<PersonalInfoData> personalInfoData, int difference) {

    }

    @Override
    public void getUserportionInfoFailed(String message) {

    }

    @Override
    public void deleteInfoSuccess() {

    }

    @Override
    public void deleteInfoFailed(String message) {

    }

    @Override
    public void getUserInfoListSuccess(List<PersonalInfoData> personalInfoData) {

    }

}
