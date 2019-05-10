package com.ytlz.mylibrary;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wyb on 2019-04-26.
 * test for litepal
 */

public class Person extends LitePalSupport {

    List<CommonData> commonData = new ArrayList<>();

    List<FaceData> faceData= new ArrayList<>();
    List<conData> conData= new ArrayList<>();

    public List<CommonData> getCommonData() {
        return commonData;
    }

    public void setCommonData(List<CommonData> commonData) {
        this.commonData = commonData;
    }

    public List<FaceData> getFaceData() {
        return faceData;
    }

    public void setFaceData(List<FaceData> faceData) {
        this.faceData = faceData;
    }

    public List<com.ytlz.mylibrary.conData> getConData() {
        return conData;
    }

    public void setConData(List<com.ytlz.mylibrary.conData> conData) {
        this.conData = conData;
    }
}
