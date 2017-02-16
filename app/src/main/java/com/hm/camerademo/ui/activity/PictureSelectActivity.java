package com.hm.camerademo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.camerademo.R;
import com.hm.camerademo.listener.OnItemClickListener;
import com.hm.camerademo.ui.adapter.PictureSelectAdapter;
import com.hm.camerademo.util.ListUtil;
import com.hm.camerademo.util.localImages.ImageItem;
import com.hm.camerademo.util.localImages.LocalImagesUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PictureSelectActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
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

    private List<String> imageList;//前一个界面已经选中的图片的地址
    private int maxImageNum;//图片选择的最大数量
    private int selectCount;//前一个界面已经选择的数量
    private List<ImageItem> imageLocal;//本地所有的图片
    private List<String> imageNotShow;
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
        imageNotShow = new ArrayList<>();
        textDefault.setText("/".concat(String.valueOf(maxImageNum).concat("张")));
        selectCount = imageList.size();
        imageLocal = new ArrayList<>();
        Observable.create(new Observable.OnSubscribe<List<ImageItem>>() {
            @Override
            public void call(Subscriber<? super List<ImageItem>> subscriber) {
                subscriber.onNext(LocalImagesUtil.getInstance(PictureSelectActivity.this).getLocalImagesUri());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ImageItem>>() {
                    @Override
                    public void call(List<ImageItem> imageItems) {
                        imageLocal.clear();
                        imageLocal.addAll(imageItems);
                        textMaxImageSize.setText(String.format(Locale.CHINA, "所有图片 %d张", imageLocal.size()));
                        //统计前一个界面显示的非本地的图片(比如从网络上加载的图片)
                        if (!ListUtil.isEmpty(imageList)) {
                            for (String path : imageList) {
                                boolean isLocalImage = false;
                                for (ImageItem imageItem : imageLocal) {
                                    if (imageItem.getImagePath().equals(path)) {
                                        isLocalImage = true;
                                        break;
                                    }
                                }
                                if (!isLocalImage) {
                                    imageNotShow.add(path);
                                }
                            }
                        }
                        if (!ListUtil.isEmpty(imageList)) {
                            for (String imagePath : imageList) {
                                for (int i = 0; i < imageLocal.size(); i++) {
                                    ImageItem item = imageLocal.get(i);
                                    if (imagePath.equals(item.getImagePath())) {
                                        item.setSelected(true);
                                        imageLocal.set(i, item);
                                        break;
                                    }
                                }
                            }
                        }
                        textNumber.setText(String.valueOf(selectCount));
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(PictureSelectActivity.this, 3);
                        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        adapter = new PictureSelectAdapter(imageLocal);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //图片数目已经达到maxImageCount
                                ImageItem item = imageLocal.get(position);
                                if (selectCount >= maxImageNum && !item.isSelected()) {
                                    Toast.makeText(PictureSelectActivity.this,
                                            String.format(getString(R.string.max_image_size), maxImageNum),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (item.isSelected()) {
                                    selectCount--;
                                } else {
                                    selectCount++;
                                }
                                item.setSelected(!item.isSelected());
                                imageLocal.set(position, item);
                                textNumber.setText(String.valueOf(selectCount));
                                adapter.notifyItemChanged(position);
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "get Local Images error:" + throwable.getMessage());
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
            if (imageItem.isSelected()) {
                images.add(imageItem.getImagePath());
            }
        }
        Intent intent = new Intent();
        intent.putExtra(IMAGE_LIST, (Serializable) images);
        setResult(MultiPhotoActivity.CHOOSE_MULTI_IMAGE_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
