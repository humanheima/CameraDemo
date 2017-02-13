package com.hm.camerademo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.camerademo.R;
import com.hm.camerademo.listener.OnItemClickListener;
import com.hm.camerademo.ui.adapter.PictureSelectAdapter;
import com.hm.camerademo.util.ListUtil;
import com.hm.camerademo.util.localImages.ImageItem;
import com.hm.camerademo.util.localImages.LocalImagesUri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class PictureSelectActivity extends AppCompatActivity {

    public static final String IMAGE_LIST = "image_list";
    public static final String IMAGE_MAX_NUM = "image_max_num";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    @BindView(R.id.text_default)
    TextView textDefault;
    @BindView(R.id.text_number)
    TextView textNumber;
    @BindView(R.id.text_max_image_size)
    TextView textMaxImageSize;

    private List<String> imageList;
    private int maxImageNum;//图片选择的最大数量
    private int selectCount;
    private List<Boolean> isSelected;
    private List<ImageItem> imageLocal;
    private List<String> imageNotShow; //当前界面未显示且已经选择到的图片
    private PictureSelectAdapter adapter;

    public static void launch(Activity context, List<String> list, int maxNum) {
        Intent intent = new Intent(context, PictureSelectActivity.class);
        intent.putExtra(IMAGE_LIST, (Serializable) list);
        intent.putExtra(IMAGE_MAX_NUM, maxNum);
        context.startActivityForResult(intent, MultiPhotoActivity.CHOOSE_MULTI_IMAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_select);
        ButterKnife.bind(this);
        imageList = (List<String>) getIntent().getSerializableExtra(IMAGE_LIST);
        maxImageNum = getIntent().getIntExtra(IMAGE_MAX_NUM, 0);
        isSelected = new ArrayList<>();
        imageNotShow = new ArrayList<>();
        textDefault.setText("/".concat(String.valueOf(maxImageNum).concat("张")));
        selectCount = imageList.size();
        Observable.create(new Observable.OnSubscribe<List<ImageItem>>() {
            @Override
            public void call(Subscriber<? super List<ImageItem>> subscriber) {
                imageLocal = LocalImagesUri.getLocalImagesUri(PictureSelectActivity.this);
                subscriber.onNext(imageLocal);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ImageItem>>() {
                    @Override
                    public void call(List<ImageItem> imageItems) {
                        textMaxImageSize.setText(String.format("所有图片 %d张", imageItems.size()));
                        //统计当前界面未显示且已经选择到的图片
                        if (!ListUtil.isEmpty(imageList)) {
                            for (String path : imageList) {
                                boolean haveImage = false;
                                for (ImageItem imageItem : imageLocal) {
                                    if (imageItem.getImagePath().equals(path)) {
                                        haveImage = true;
                                        break;
                                    }
                                }
                                if (!haveImage) {
                                    imageNotShow.add(path);
                                }
                            }
                        }
                        //统计选中与未选中
                        for (int i = 0; i < imageLocal.size(); i++) {
                            ImageItem imageItem = imageLocal.get(i);
                            isSelected.add(false);
                            if (!ListUtil.isEmpty(imageList)) {
                                for (String imagePath : imageList) {
                                    if (imagePath.equals(imageItem.getImagePath())) {
                                        isSelected.set(i, true);
                                        break;
                                    }
                                }
                            }
                        }
                        textNumber.setText(String.valueOf(selectCount));
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(PictureSelectActivity.this, 3);
                        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        adapter = new PictureSelectAdapter(imageLocal, isSelected);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //图片数目已经达到maxImageCount
                                if (selectCount >= maxImageNum && !isSelected.get(position)) {
                                    Toast.makeText(PictureSelectActivity.this,
                                            String.format(getString(R.string.max_image_size), maxImageNum),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (isSelected.get(position)) {
                                    selectCount--;
                                } else {
                                    selectCount++;
                                }
                                isSelected.set(position, !isSelected.get(position));
                                textNumber.setText(String.valueOf(selectCount));
                                adapter.notifyItemChanged(position);
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

    }

    @OnClick(R.id.btn_complete)
    public void onClick() {
        List<String> images = new ArrayList<>();
        if (!ListUtil.isEmpty(imageNotShow)) {
            images.addAll(imageNotShow);
        }
        for (int i = 0; i < imageLocal.size(); i++) {
            ImageItem imageItem = imageLocal.get(i);
            if (isSelected.get(i)) {
                images.add(imageItem.getImagePath());
            }
        }
        Intent intent = new Intent();
        intent.putExtra(IMAGE_LIST, (Serializable) images);
        setResult(MultiPhotoActivity.CHOOSE_MULTI_IMAGE_OK, intent);
        finish();
    }
}
