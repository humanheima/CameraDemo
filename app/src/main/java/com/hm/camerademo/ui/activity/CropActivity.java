package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityCropBinding;
import com.hm.camerademo.ui.base.BaseActivity;

public class CropActivity extends BaseActivity<ActivityCropBinding> {

    public static void launch(Context context) {
        Intent starter = new Intent(context, CropActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_crop;
    }

    @Override
    protected void initData() {

    }
}
