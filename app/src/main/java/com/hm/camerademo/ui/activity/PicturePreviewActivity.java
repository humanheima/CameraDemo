package com.hm.camerademo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hm.camerademo.R;
import com.hm.camerademo.ui.fragment.ImagePreviewFragment;
import com.hm.camerademo.ui.widget.zoom.ViewPagerFixed;
import com.hm.camerademo.util.ListUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PicturePreviewActivity extends AppCompatActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    private static final String KEY_POSITION = "key_position";
    @BindView(R.id.text_cancel)
    TextView textCancel;
    @BindView(R.id.text_complete)
    TextView textComplete;
    @BindView(R.id.text_number)
    TextView textNumber;
    @BindView(R.id.viewPagerFixed)
    ViewPagerFixed viewPagerFixed;
    @BindView(R.id.btn_delete)
    Button btnDelete;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        ButterKnife.bind(this);
        imageSelectTemp = new ArrayList<>();

        imageSelect = (List<String>) getIntent().getSerializableExtra(PictureSelectActivity.IMAGE_LIST);
        initPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        imageSelectTemp.addAll(imageSelect);

        maxImagesSize = imageSelect.size();
        textNumber.setText(String.valueOf(initPosition + 1).concat("/").concat(String.valueOf(maxImagesSize)));

        imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), (ArrayList<String>) imageSelect, true);
        viewPagerFixed.setAdapter(imagePagerAdapter);
        // 更新下标
        viewPagerFixed.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                textNumber.setText(String.valueOf(position + 1).concat("/").concat(String.valueOf(maxImagesSize)));
            }
        });
        if (savedInstanceState != null) {
            initPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        viewPagerFixed.setCurrentItem(initPosition);
        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(PictureSelectActivity.IMAGE_LIST, (Serializable) imageSelect);
                setResult(MultiPhotoActivity.IMAGE_PREVIEW_OK, intent);
                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPagerFixed.getCurrentItem();
                if (!ListUtil.isEmpty(imageSelect)) {
                    imageSelectTemp.remove(imageSelect.get(current));
                    imageSelect.clear();
                    imageSelect.addAll(imageSelectTemp);
                    imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), (ArrayList<String>) imageSelect, true);
                    viewPagerFixed.setAdapter(imagePagerAdapter);
                    viewPagerFixed.setCurrentItem(current >= imageSelect.size() ? imageSelect.size() : current);
                    maxImagesSize = imageSelect.size();
                    textNumber.setText(String.valueOf(current == maxImagesSize ? current : current + 1).concat("/").concat(String.valueOf(maxImagesSize)));
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, viewPagerFixed.getCurrentItem());
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
