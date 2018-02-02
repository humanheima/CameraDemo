package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityMultiPhotoBinding;
import com.hm.camerademo.ui.adapter.ImageAdapter;
import com.hm.camerademo.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MultiPhotoActivity extends BaseActivity<ActivityMultiPhotoBinding> {

    public static final int CHOOSE_MULTI_IMAGE = 1400;//选取多张图片
    public static final int CHOOSE_MULTI_IMAGE_OK = 700;
    public static final int IMAGE_PREVIEW = 9;//图片预览
    public static final int IMAGE_PREVIEW_OK = 10;
    public static final String MAX_COUNT = "MAX_COUNT";

    private ImageAdapter adapter;
    private List<String> imagesList;
    private int maxCount;

    public static void launch(Context context, int maxCount) {
        Intent intent = new Intent(context, MultiPhotoActivity.class);
        intent.putExtra(MAX_COUNT, maxCount);
        context.startActivity(intent);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_multi_photo;
    }

    @Override
    protected void initData() {
        maxCount = getIntent().getIntExtra(MAX_COUNT, 1);
        if (maxCount > 9) {
            maxCount = 9;
        }
        imagesList = new ArrayList<>();
        adapter = new ImageAdapter(this, R.layout.item_in_detail, imagesList, maxCount);
        adapter.setOnAddImageListener(new ImageAdapter.OnAddImageListener() {
            @Override
            public void openSelect(View view) {
                PictureSelectActivity.launch(MultiPhotoActivity.this, imagesList, maxCount);
            }

            @Override
            public void openShowImage(int position) {
                PicturePreviewActivity.launch(MultiPhotoActivity.this, imagesList, position);
            }
        });
        viewBind.gridView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_MULTI_IMAGE:
                if (resultCode == CHOOSE_MULTI_IMAGE_OK) {
                    List<String> images = (List<String>) data.getSerializableExtra(PictureSelectActivity.IMAGE_LIST);
                    imagesList.clear();
                    imagesList.addAll(images);
                    adapter.notifyDataSetChanged();
                }
                break;
            case IMAGE_PREVIEW:
                if (resultCode == IMAGE_PREVIEW_OK) {
                    List<String> images = (List<String>) data.getSerializableExtra(PictureSelectActivity.IMAGE_LIST);
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
