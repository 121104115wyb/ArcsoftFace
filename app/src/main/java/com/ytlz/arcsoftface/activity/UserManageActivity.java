package com.ytlz.arcsoftface.activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ytlz.arcsoftface.R;
import com.ytlz.arcsoftface.util.DialogUI;
import com.ytlz.arcsoftface.util.FaceRecognition1;
import com.ytlz.arcsoftface.view.FaceEngineCallBack;
import com.ytlz.arcsoftface.widget.FaceRectView;
import com.ytlz.arcsoftface.widget.MyFaceRectView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserManageActivity extends AppCompatActivity implements FaceEngineCallBack {

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    FaceRecognition1 faceRecognition1;

    View texturePreview;
    MyFaceRectView faceRectView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.use_manager_activity);
        ButterKnife.bind(this);
        initView();

    }

    @Override
    protected void onDestroy() {
        if (null != faceRecognition1) {
            faceRecognition1.recycle();
        }
        super.onDestroy();

    }

    void initView() {

        //取消显示动图
//        if (relativeLayout != null) {
//            relativeLayout.setVisibility(View.GONE);
//        }
        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().setAttributes(attributes);
        }

        texturePreview = findViewById(R.id.texture_preview);
        faceRectView = findViewById(R.id.face_rect_view);

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initFRengine();
        }
    }

    void initFRengine() {
        faceRecognition1 = new FaceRecognition1();
        faceRecognition1.setEngineCallBack(this);
        int rota = getWindowManager().getDefaultDisplay().getRotation();
        faceRecognition1.init(this,texturePreview,faceRectView,rota);
        //激活
        findViewById(R.id.active).setOnClickListener(v -> {
            faceRecognition1.activeEngine();
        });

        //注册
        findViewById(R.id.regist).setOnClickListener(v -> {
           DialogUI.getUI().showRegristDialog(this, new DialogUI.ResultCallback() {
               @Override
               public void result(String data) {
                   if (faceRecognition1.getActiveEngine()) {
                       faceRecognition1.register(data);
                   } else {
                       Toast.makeText(UserManageActivity.this, "请先激活！", Toast.LENGTH_SHORT).show();
                   }
               }
           });

//            if (faceRecognition1.getActiveEngine()) {
//                faceRecognition1.register("wyb");
//            }
        });
        //识别
        findViewById(R.id.recognition).setOnClickListener(v -> {
            if (faceRecognition1.getActiveEngine()) {
                faceRecognition1.detection();
            } else {

                Toast.makeText(this, "请先激活！", Toast.LENGTH_SHORT).show();
            }

        });
//        //识别
//        findViewById(R.id.idFace).setOnClickListener(v -> {
//
//
//        });
    }




    @Override
    public void faceEnSuccess(String sucMes) {
        Toast.makeText(UserManageActivity.this, sucMes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void faceEnError(String errorMes, int code) {
        Toast.makeText(UserManageActivity.this, errorMes + code, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                initFRengine();
//                initEngine();
//                initCamera();
//                if (cameraHelper != null) {
//                    cameraHelper.start();
//                }
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
