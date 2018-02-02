package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityCompressBinding;
import com.hm.camerademo.ui.base.BaseActivity;
import com.hm.camerademo.util.ImageUtil;
import com.hm.camerademo.util.ScreenUtil;

import java.io.IOException;

public class CompressActivity extends BaseActivity<ActivityCompressBinding> {

    private static final String TAG = "CompressActivity";

    public static void launch(Context context) {
        Intent starter = new Intent(context, CompressActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_compress;
    }

    @Override
    protected void initData() {
        Bitmap bitmap = ImageUtil.getSampledBitmapFromResource(getResources(), R.drawable.airplane,
                ScreenUtil.dipToPx(this, 200), ScreenUtil.dipToPx(this, 140));
        Log.e(TAG, "bitmap占用的内存" + bitmap.getByteCount() / 1024 / 1024 + "MB");
        viewBind.imgLarge.setImageBitmap(bitmap);
    }

    /**
     * 把imageView上的bitmap保存到本地，直接在主线程保存了
     */
    public void saveBitmap(View view) {
        String destination = null;
        viewBind.imgLarge.setDrawingCacheEnabled(true);
        Bitmap bitmap = viewBind.imgLarge.getDrawingCache();
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
        viewBind.imgLarge.setDrawingCacheEnabled(false);
    }
}
