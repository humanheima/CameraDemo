package com.hm.camerademo.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityPictureSelectBinding;
import com.hm.camerademo.listener.OnItemClickListener;
import com.hm.camerademo.ui.adapter.BucketAdapter;
import com.hm.camerademo.ui.adapter.PictureSelectAdapter;
import com.hm.camerademo.ui.base.BaseActivity;
import com.hm.camerademo.util.localImages.ImageBucket;
import com.hm.camerademo.util.localImages.ImageItem;
import com.hm.camerademo.util.localImages.LocalImagesUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PictureSelectActivity extends BaseActivity<ActivityPictureSelectBinding> {

    private final String TAG = getClass().getSimpleName();
    public static final String IMAGE_LIST = "image_list";

    private static final int MAX_COUNT = 9;//图片选择的最大数量
    private List<ImageItem> selectedList;//用来保存选中的图片
    private int selectedCount;//选中的图片的数量
    private List<ImageItem> imageLocal;//界面显示的所有图片
    private PictureSelectAdapter adapter;

    private BucketAdapter bucketAdapter;
    private List<ImageBucket> bucketList;
    private boolean bucketOpened;

    private List<ImageItem> allImages;

    public static void launch(Activity context) {
        Intent intent = new Intent(context, PictureSelectActivity.class);
        context.startActivityForResult(intent, MultiPhotoActivity.CHOOSE_MULTI_IMAGE);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_picture_select;
    }

    @Override
    protected void initData() {
        selectedList = new ArrayList<>();
        imageLocal = new ArrayList<>();
        bucketList = new ArrayList<>();
        allImages = new ArrayList<>();
        initRv();
        initBuckets();
        Observable.create(new Observable.OnSubscribe<List<ImageBucket>>() {
            @Override
            public void call(Subscriber<? super List<ImageBucket>> subscriber) {
                List<ImageBucket> bucketList = LocalImagesUtil.getInstance(PictureSelectActivity.this)
                        .getBucketList();
                subscriber.onNext(bucketList);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ImageBucket>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "get Local Images error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<ImageBucket> imageBuckets) {
                        processData(imageBuckets);
                    }
                });
    }

    private void initBuckets() {
        viewBind.rvBucket.setLayoutManager(new LinearLayoutManager(this));
        bucketAdapter = new BucketAdapter(this, bucketList);
        viewBind.rvBucket.setAdapter(bucketAdapter);
        bucketAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < bucketList.size(); i++) {
                    bucketList.get(i).setSelected(false);
                }
                bucketList.get(position).setSelected(true);
                bucketAdapter.notifyDataSetChanged();
                showImages(bucketList.get(position));
                closeBucket();
            }
        });
        viewBind.rvBucket.post(new Runnable() {
            @Override
            public void run() {
                viewBind.rvBucket.setTranslationY(viewBind.rvBucket.getHeight());
                viewBind.rvBucket.setVisibility(View.GONE);
            }
        });
    }

    private void processData(List<ImageBucket> imageBuckets) {
        this.bucketList.addAll(imageBuckets);
        showImages(bucketList.get(0));
        bucketAdapter.notifyDataSetChanged();
        for (ImageBucket bucket : bucketList) {
            allImages.addAll(bucket.getImageList());
        }
    }

    private void showImages(ImageBucket imageBucket) {
        imageLocal.clear();
        imageLocal.addAll(imageBucket.getImageList());
        adapter.notifyDataSetChanged();
        viewBind.tvBucket.setText(imageBucket.getBucketName());
    }

    private void initRv() {
        viewBind.rvImages.setLayoutManager(new GridLayoutManager(PictureSelectActivity.this, 3));
        adapter = new PictureSelectAdapter(imageLocal);
        viewBind.rvImages.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //图片数目已经达到maxImageCount
                ImageItem item = imageLocal.get(position);
                if (selectedCount >= MAX_COUNT && !item.isSelected()) {
                    Toast.makeText(PictureSelectActivity.this,
                            String.format(getString(R.string.max_image_size), MAX_COUNT),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (item.isSelected()) {
                    selectedCount--;
                    for (int i = 0; i < selectedList.size(); i++) {
                        ImageItem imageItem = selectedList.get(i);
                        if (imageItem.getImagePath().equals(item.getImagePath())) {
                            selectedList.remove(imageItem);
                            break;
                        }
                    }
                    // TODO: 2018/2/6 0006 错误代码 在遍历过程中不能删除，too young too simple ，sometimes naive
                    /*for (ImageItem imageItem : selectedList) {
                        if (imageItem.getImagePath().equals(item.getImagePath())) {
                            selectedList.remove(imageItem);
                        }
                    }*/
                } else {
                    selectedCount++;
                    selectedList.add(item);
                }
                item.setSelected(!item.isSelected());
                imageLocal.set(position, item);
                adapter.notifyItemChanged(position);
                changeBtnStatus();
            }
        });
    }

    private void changeBtnStatus() {
        if (selectedCount > 0) {
            viewBind.tvConfirm.setEnabled(true);
            viewBind.tvConfirm.setText(getString(R.string.send_count_format, selectedCount, MAX_COUNT));
            viewBind.tvPreview.setEnabled(true);
            viewBind.tvPreview.setText(getString(R.string.preview_count_format, selectedCount));
        } else {
            viewBind.tvConfirm.setEnabled(false);
            viewBind.tvConfirm.setText(getString(R.string.send));
            viewBind.tvPreview.setEnabled(false);
            viewBind.tvPreview.setText(getString(R.string.preview));
        }
    }

    /**
     * 弹出文件夹列表
     */
    private void openBucket() {
        if (!bucketOpened) {
            Log.e(TAG, "openBucket: ");
            ObjectAnimator animator = ObjectAnimator.ofFloat(viewBind.rvBucket, "translationY",
                    viewBind.rvBucket.getHeight(), 0).setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    viewBind.rvBucket.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
            bucketOpened = true;
        }
    }

    /**
     * 收起文件夹列表
     */
    private void closeBucket() {
        if (bucketOpened) {
            Log.e(TAG, "closeBucket: ");
            ObjectAnimator animator = ObjectAnimator.ofFloat(viewBind.rvBucket, "translationY",
                    0, viewBind.rvBucket.getHeight()).setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    viewBind.rvBucket.setVisibility(View.GONE);
                }
            });
            animator.start();
            bucketOpened = false;
        }
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_confirm:
                confirm();
                break;
            case R.id.ll_bucket:
                if (bucketOpened) {
                    closeBucket();
                } else {
                    openBucket();
                }
                break;
            case R.id.tv_preview:
                PicturePreviewActivity.launch(this, selectedList, 0, selectedCount);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MultiPhotoActivity.IMAGE_PREVIEW) {
            if (resultCode == MultiPhotoActivity.IMAGE_PREVIEW_OK) {
                boolean confirm = data.getBooleanExtra(PicturePreviewActivity.PREVIEW_CONFIRM, false);
                if (confirm) {
                    selectedList = (List<ImageItem>) data.getSerializableExtra(PictureSelectActivity.IMAGE_LIST);
                    confirm();
                } else {
                    //更新当前界面
                    selectedList = (List<ImageItem>) data.getSerializableExtra(PictureSelectActivity.IMAGE_LIST);
                    selectedCount = selectedList.size();
                    for (int i = 0; i < imageLocal.size(); i++) {
                        imageLocal.get(i).setSelected(false);
                    }
                    for (ImageItem imageItem : selectedList) {
                        for (int i = 0; i < imageLocal.size(); i++) {
                            if (imageItem.getImagePath().equals(imageLocal.get(i).getImagePath())) {
                                imageLocal.get(i).setSelected(true);
                                break;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    changeBtnStatus();
                }
            }
        }
    }

    private void confirm() {
        Intent intent = new Intent();
        intent.putExtra(IMAGE_LIST, (Serializable) selectedList);
        setResult(MultiPhotoActivity.CHOOSE_MULTI_IMAGE_OK, intent);
        finish();
    }

}
