package com.ytlz.arcsoftface.model;

import android.graphics.Bitmap;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;

/**
 * Created by wyb on 2019-04-28.
 * 注册时可以获取到面部的所有信息，当注册成功时，就把他保存到数据库中
 */

public class FaceRegisterData {
    private FaceInfo faceInfo;
    private LivenessInfo livenessInfo;
    private AgeInfo ageInfo;
    private GenderInfo genderInfo;
    private String name;
    private String password;

    private int trackId;

    private String errorMes;

    private int type;

    private Bitmap bitmap;


    private String faceFeature;

    private float similar;

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    public float getSimilar() {
        return similar;
    }
    public FaceRegisterData() {

    }

    public FaceRegisterData(FaceInfo faceInfo, LivenessInfo livenessInfo, AgeInfo ageInfo, GenderInfo genderInfo) {

        this.faceInfo = faceInfo;
        this.livenessInfo = livenessInfo;
        this.ageInfo = ageInfo;
        this.genderInfo = genderInfo;
    }

    public GenderInfo getGenderInfo() {
        return genderInfo;
    }

    public void setGenderInfo(GenderInfo genderInfo) {
        this.genderInfo = genderInfo;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    public LivenessInfo getLivenessInfo() {
        return livenessInfo;
    }

    public void setLivenessInfo(LivenessInfo livenessInfo) {
        this.livenessInfo = livenessInfo;
    }

    public AgeInfo getAgeInfo() {
        return ageInfo;
    }

    public void setAgeInfo(AgeInfo ageInfo) {
        this.ageInfo = ageInfo;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getErrorMes() {
        return errorMes;
    }

    public void setErrorMes(String errorMes) {
        this.errorMes = errorMes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(String faceFeature) {
        this.faceFeature = faceFeature;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
