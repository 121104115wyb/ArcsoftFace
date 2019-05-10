package com.ytlz.arcsoftface.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ytlz.arcsoftface.R;

/**
 * Created by wyb on 2019-04-29.
 */

public class DialogUI {

    static DialogUI dialogUI = null;

    Dialog dialog;

//    Context context;

    public static DialogUI getUI() {
        if (null == dialogUI) {
            synchronized (DialogUI.class) {
                if (null == dialogUI) {
                    dialogUI = new DialogUI();
                }
            }
        }

        return dialogUI;
    }


    public void showRegristDialog(Context context, ResultCallback callback) {
        if (null != context) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_recognition);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            Button button = dialog.findViewById(R.id.btn);
            EditText name = dialog.findViewById(R.id.name);
            TextView tv_notice = dialog.findViewById(R.id.tv_notice);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(name.getText().toString())) {
                        tv_notice.setText("请您输入名称！");
                        tv_notice.setVisibility(View.VISIBLE);
                    } else {
                        dialog.dismiss();
                        callback.result(name.getText().toString());
                    }
                }
            });
            dialog.show();


        }

    }


    public interface ResultCallback {

        void result(String data);


    }


}
