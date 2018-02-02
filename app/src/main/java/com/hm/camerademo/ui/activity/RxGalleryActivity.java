package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityRxGalleryBinding;
import com.hm.camerademo.ui.adapter.ImageAdapter;
import com.hm.camerademo.ui.adapter.RxGalleryAdapter;
import com.hm.camerademo.ui.base.BaseActivity;
import com.hm.camerademo.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;

public class RxGalleryActivity extends BaseActivity<ActivityRxGalleryBinding> {

    private RxGalleryAdapter adapter;
    private List<MediaBean> imagesList;

    public static void launch(Context context) {
        Intent starter = new Intent(context, RxGalleryActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_rx_gallery;
    }

    @Override
    protected void initData() {
        imagesList = new ArrayList<>();
        setAdapter();
    }

    @Override
    protected void bindEvent() {

    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new RxGalleryAdapter(this, R.layout.item_in_detail, imagesList);
            adapter.setOnAddImageListener(new RxGalleryAdapter.OnAddImageListener() {
                @Override
                public void openSelect(View view) {
                    selectImages();
                }

                @Override
                public void openShowImage(int position) {
                    //PicturePreviewActivity.launch(RxGalleryActivity.this, imagesList, position);
                }
            });
            viewBind.gridView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private void selectImages() {
        RxGalleryFinal.with(this)
                .maxSize(9)
                .image()
                .imageLoader(ImageLoaderType.GLIDE)
                .selected(imagesList)
                .subscribe(new RxBusResultSubscriber<ImageMultipleResultEvent>() {
                    @Override
                    protected void onEvent(ImageMultipleResultEvent resultEvent) throws Exception {
                        imagesList.clear();
                        imagesList.addAll(resultEvent.getResult());
                        setAdapter();
                    }
                })
                .openGallery();
    }

    public void onClick(View view) {
        RxGalleryFinal.with(this)
                .image()
                .radio()
                .crop()
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribe(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent resultEvent) throws Exception {
                        Log.e(TAG, resultEvent.getResult().getCropPath());
                        ImageUtil.load(RxGalleryActivity.this, resultEvent.getResult().getCropPath(), viewBind.imgSingle);
                    }
                }).openGallery();
    }
}
