package com.ytlz.arcsoftface.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ytlz.arcsoftface.R;
import com.ytlz.arcsoftface.util.CameraUtil;
import com.ytlz.arcsoftface.widget.MyFaceRectView;

public class FaceTestActivity1 extends AppCompatActivity {
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;

    //摄像图像
    View texturePreview;
    //人脸边框
    MyFaceRectView faceRectView;
    ImageView testImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_test1);
        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().setAttributes(attributes);
        }
        // Activity启动后就锁定为启动时的方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        texturePreview = findViewById(R.id.texture_preview);
        faceRectView = findViewById(R.id.face_rect_view);
        testImageView = findViewById(R.id.testImageView);
        CameraUtil.getInstance().setCameraLitener(lisenter);
        int rota = getWindowManager().getDefaultDisplay().getRotation();
//        CameraUtil.getInstance().setRoate(rota);

        CameraUtil.getInstance().setTEsRotate(rota);

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            CameraUtil.getInstance().init(this, texturePreview, faceRectView);
        }


        findViewById(R.id.takephoto).setOnClickListener(v -> {

            CameraUtil.getInstance().takePhoto();

        });

    }


    CameraUtil.CameraLisenter lisenter = new CameraUtil.CameraLisenter() {
        @Override
        public void start(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
            System.out.println("---------start");
        }

        @Override
        public void Completion(String path) {
            System.out.println("---------path" + path);
            Glide.with(FaceTestActivity1.this).load(path).into(testImageView);

        }

        @Override
        public void Error(String var1) {
            System.out.println("---------Error" + var1);
        }

        @Override
        public void Close() {
            System.out.println("---------Close");
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                CameraUtil.getInstance().init(this, texturePreview, faceRectView);
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }
}
