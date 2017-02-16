package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;

import com.hm.camerademo.R;
import com.hm.camerademo.ui.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultiPhotoActivity extends AppCompatActivity {

    public static final int CHOOSE_MULTI_IMAGE = 1400;//选取多张图片
    public static final int CHOOSE_MULTI_IMAGE_OK = 700;
    public static final int IMAGE_PREVIEW = 9;//图片预览
    public static final int IMAGE_PREVIEW_OK = 10;

    @BindView(R.id.grid_view)
    GridView gridView;
    private ImageAdapter adapter;
    private List<String> imagesList;

    public static void launch(Context context) {
        Intent intent = new Intent(context, MultiPhotoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_photo);
        ButterKnife.bind(this);
        imagesList = new ArrayList<>();

        adapter = new ImageAdapter(imagesList);
        adapter.setOnAddImageListener(new ImageAdapter.OnAddImageListener() {
            @Override
            public void openSelect(View view) {
                PictureSelectActivity.launch(MultiPhotoActivity.this, imagesList, ImageAdapter.IMG_MAX_NUM);
            }

            @Override
            public void openShowImage(int position) {
                PicturePreviewActivity.launch(MultiPhotoActivity.this, imagesList, position);
            }
        });
        gridView.setAdapter(adapter);
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
