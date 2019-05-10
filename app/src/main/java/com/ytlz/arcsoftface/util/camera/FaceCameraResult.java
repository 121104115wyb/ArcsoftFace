package com.ytlz.arcsoftface.util.camera;

import android.graphics.Bitmap;

import com.ytlz.arcsoftface.model.FaceRegisterData;


public interface FaceCameraResult {

   void resgistSuccess(FaceRegisterData data);
   void resgistFails(String name);

   void recognitionSuccess(FaceRegisterData data);
   void recognitionFails();

   void IdCardContrastResult(Boolean b);
   void getBitmapSucess(Bitmap bitmap);


   void getBitmapFailed();
   void getDetectionSucess();
}