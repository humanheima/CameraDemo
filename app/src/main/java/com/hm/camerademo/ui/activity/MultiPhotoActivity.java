package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.hm.imageslector.activity.PictureSelectActivity;
import com.hm.imageslector.base.BaseActivity;
import com.hm.imageslector.localImages.ImageItem;
import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityMultiPhotoBinding;
import com.hm.camerademo.ui.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MultiPhotoActivity extends BaseActivity<ActivityMultiPhotoBinding> {


    private ImageAdapter adapter;
    private List<ImageItem> imagesList;
    private int maxCount;

    public static void launch(Context context, int maxCount) {
        Intent intent = new Intent(context, MultiPhotoActivity.class);
        intent.putExtra(PictureSelectActivity.IMAGE_MAX_COUNT, maxCount);
        context.startActivity(intent);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_multi_photo;
    }

    @Override
    protected void initData() {
        maxCount = getIntent().getIntExtra(PictureSelectActivity.IMAGE_MAX_COUNT, 9);
        if (maxCount > 9) {
            maxCount = 9;
        }
        imagesList = new ArrayList<>();
        adapter = new ImageAdapter(this, imagesList);
        viewBind.gridView.setLayoutManager(new GridLayoutManager(this, 3));
        viewBind.gridView.setAdapter(adapter);
    }

    public void click(View view) {
        PictureSelectActivity.launch(MultiPhotoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureSelectActivity.CHOOSE_MULTI_IMAGE:
                if (resultCode == PictureSelectActivity.CHOOSE_MULTI_IMAGE_OK) {
                    List<ImageItem> images = (List<ImageItem>) data.getSerializableExtra(PictureSelectActivity.IMAGE_LIST);
                    imagesList.clear();
                    imagesList.addAll(images);
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }
}
