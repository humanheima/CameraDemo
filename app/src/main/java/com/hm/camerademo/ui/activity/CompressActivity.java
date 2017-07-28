package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.hm.camerademo.R;
import com.hm.camerademo.util.ImageUtil;
import com.hm.camerademo.util.ScreenUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompressActivity extends AppCompatActivity {

    private static final String TAG = "CompressActivity";
    @BindView(R.id.img_large)
    ImageView imgLarge;

    private int width, height;

    public static void launch(Context context) {
        Intent starter = new Intent(context, CompressActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress);
        ButterKnife.bind(this);
        Bitmap bitmap = ImageUtil.getSampledBitmapFromResource(getResources(), R.drawable.airplane,
                ScreenUtil.dipToPx(this, 200), ScreenUtil.dipToPx(this, 140));
        Log.e(TAG, "bitmap占用的内存" + bitmap.getByteCount() / 1024 / 1024 + "MB");
        imgLarge.setImageBitmap(bitmap);
    }

    /**
     * 把imageView上的bitmap保存到本地，直接在主线程保存了
     */
    @OnClick(R.id.btn_save_bitmap)
    public void saveBitmap() {
        String destination = null;
        imgLarge.setDrawingCacheEnabled(true);
        Bitmap bitmap = imgLarge.getDrawingCache();
        if (bitmap != null) {
            try {
                destination = ImageUtil.compressImage(CompressActivity.this, bitmap, 70);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(destination)) {
                Toast.makeText(CompressActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CompressActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        }
        imgLarge.setDrawingCacheEnabled(false);
    }
}
