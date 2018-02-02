package com.hm.camerademo.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.hm.camerademo.R;

public class LoadingDialog extends Dialog {

    private TextView textContent;
    private String strContent;

    public LoadingDialog(Context context, String content) {
        super(context, R.style.MyDialog);
        this.strContent = content;
    }

    public static LoadingDialog show(Context context, String content) {
        LoadingDialog loadingDialog = new LoadingDialog(context, content);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
        return loadingDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        textContent = findViewById(R.id.dialog_loading_text);
        if (!TextUtils.isEmpty(strContent)) {
            textContent.setText(strContent);
        }
    }

    public void setContent(String content) {
        textContent.setText(content);
    }
}