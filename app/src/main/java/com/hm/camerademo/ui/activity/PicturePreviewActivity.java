package com.hm.camerademo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityPicturePreviewBinding;
import com.hm.camerademo.ui.base.BaseActivity;
import com.hm.camerademo.ui.fragment.ImagePreviewFragment;
import com.hm.camerademo.util.ListUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PicturePreviewActivity extends BaseActivity<ActivityPicturePreviewBinding> {

    private static final String KEY_POSITION = "key_position";

    private List<String> imageSelect;
    private List<String> imageSelectTemp;

    private ImagePagerAdapter imagePagerAdapter;

    private int initPosition = 0;

    private int maxImagesSize = 0;

    public static void launch(Activity context, List<String> list, int position) {
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putExtra(PictureSelectActivity.IMAGE_LIST, (Serializable) list);
        intent.putExtra(KEY_POSITION, position);
        context.startActivityForResult(intent, MultiPhotoActivity.IMAGE_PREVIEW);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_picture_preview;
    }

    @Override
    protected void initData() {
        imageSelectTemp = new ArrayList<>();
        imageSelect = (List<String>) getIntent().getSerializableExtra(PictureSelectActivity.IMAGE_LIST);
        initPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        imageSelectTemp.addAll(imageSelect);

        maxImagesSize = imageSelect.size();
        viewBind.textNumber.setText(String.valueOf(initPosition + 1).concat("/").concat(String.valueOf(maxImagesSize)));

        imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), (ArrayList<String>) imageSelect, true);
        viewBind.viewPagerFixed.setAdapter(imagePagerAdapter);
        // 更新下标
        viewBind.viewPagerFixed.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                viewBind.textNumber.setText(String.valueOf(position + 1).concat("/").concat(String.valueOf(maxImagesSize)));
            }
        });

        viewBind.viewPagerFixed.setCurrentItem(initPosition);
        viewBind.textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewBind.textComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(PictureSelectActivity.IMAGE_LIST, (Serializable) imageSelect);
                setResult(MultiPhotoActivity.IMAGE_PREVIEW_OK, intent);
                finish();
            }
        });
        viewBind.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewBind.viewPagerFixed.getCurrentItem();
                if (!ListUtil.isEmpty(imageSelect)) {
                    imageSelectTemp.remove(imageSelect.get(current));
                    imageSelect.clear();
                    imageSelect.addAll(imageSelectTemp);
                    imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), (ArrayList<String>) imageSelect, true);
                    viewBind.viewPagerFixed.setAdapter(imagePagerAdapter);
                    viewBind.viewPagerFixed.setCurrentItem(current >= imageSelect.size() ? imageSelect.size() : current);
                    maxImagesSize = imageSelect.size();
                    viewBind.textNumber.setText(String.valueOf(current == maxImagesSize ? current : current + 1).concat("/").concat(String.valueOf(maxImagesSize)));
                }
            }
        });
    }


    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<String> fileList;
        private boolean isLocalImage = false;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList, boolean isLocalImage) {
            super(fm);
            this.fileList = fileList;
            this.isLocalImage = isLocalImage;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            return ImagePreviewFragment.newInstance(url, isLocalImage);
        }
    }
}
