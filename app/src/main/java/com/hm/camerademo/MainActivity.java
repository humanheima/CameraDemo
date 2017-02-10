package com.hm.camerademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_take_photo)
    Button btnTakePhoto;
    @BindView(R.id.btn_choose_photo)
    Button btnChoosePhoto;
    @BindView(R.id.btn_compress_bitmap)
    Button btnCompressBitmap;
    @BindView(R.id.btn_compress_photo)
    Button btnCompressPhoto;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_choose_photo, R.id.btn_compress_bitmap, R.id.btn_compress_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take_photo:
                break;
            case R.id.btn_choose_photo:
                break;
            case R.id.btn_compress_bitmap:
                break;
            case R.id.btn_compress_photo:
                break;
        }
    }
}
