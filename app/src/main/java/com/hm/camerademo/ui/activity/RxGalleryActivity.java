package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hm.camerademo.R;
import com.hm.camerademo.ui.base.BaseActivity;
import com.hm.camerademo.ui.adapter.ImageAdapter;
import com.hm.camerademo.ui.adapter.RxGalleryAdapter;
import com.hm.camerademo.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;

public class RxGalleryActivity extends BaseActivity {


    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.grid_view)
    GridView gridView;
    @BindView(R.id.activity_rx_gallery)
    RelativeLayout activityRxGallery;
    @BindView(R.id.btn_single)
    Button btnSingle;
    @BindView(R.id.img_single)
    ImageView imgSingle;
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
            gridView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private void selectImages() {
        RxGalleryFinal.with(this)
                .maxSize(ImageAdapter.IMG_MAX_NUM)
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

    @OnClick(R.id.btn_single)
    public void onClick() {
        RxGalleryFinal.with(this)
                .image()
                .radio()
                .crop()
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribe(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent resultEvent) throws Exception {
                        Log.e(TAG, resultEvent.getResult().getCropPath());
                        ImageUtil.load(RxGalleryActivity.this, resultEvent.getResult().getCropPath(), imgSingle);
                    }
                }).openGallery();
    }
}
