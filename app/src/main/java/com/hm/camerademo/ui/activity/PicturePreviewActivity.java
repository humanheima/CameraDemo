package com.hm.camerademo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityPicturePreviewBinding;
import com.hm.camerademo.listener.OnItemClickListener;
import com.hm.camerademo.ui.adapter.ImagePagerAdapter;
import com.hm.camerademo.ui.adapter.PreviewRvAdapter;
import com.hm.camerademo.ui.base.BaseActivity;
import com.hm.camerademo.util.localImages.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 1 .只预览已经选择的图片 可以取消选择
 */

public class PicturePreviewActivity extends BaseActivity<ActivityPicturePreviewBinding> {

    //是否是确定发送还是按返回键返回了
    public static final String PREVIEW_CONFIRM = "PREVIEW_CONFIRM";

    private static final int MAX_COUNT = 9;//图片选择的最大数量
    private static final String KEY_POSITION = "key_position";
    public static final String SELECTED_COUNT = "SELECTED_COUNT";

    private List<ImageItem> selectedList;
    private int selectedCount;
    private int initialSelectedCount;
    private PreviewRvAdapter previewRvAdapter;

    public static void launch(Activity context, List<ImageItem> selectedList, int position, int selectedCount) {
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putExtra(PictureSelectActivity.IMAGE_LIST, (Serializable) selectedList);
        intent.putExtra(KEY_POSITION, position);
        intent.putExtra(SELECTED_COUNT, selectedCount);
        context.startActivityForResult(intent, MultiPhotoActivity.IMAGE_PREVIEW);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_picture_preview;
    }

    @Override
    protected void initData() {
        selectedList = (List<ImageItem>) getIntent().getSerializableExtra(PictureSelectActivity.IMAGE_LIST);
        int initPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        initialSelectedCount = selectedList.size();
        selectedCount = initialSelectedCount;
        viewBind.tvNumber.setText(getString(R.string.count_format, initPosition + 1, initialSelectedCount));
        changeBtnStatus(selectedCount);
        initViewPagerAdapter(initPosition);
        initRecyclerViewAdapter();
        changeSelected(0);
    }

    private void initViewPagerAdapter(int initPosition) {
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), selectedList, true);
        viewBind.viewPagerFixed.setAdapter(imagePagerAdapter);
        viewBind.viewPagerFixed.setCurrentItem(initPosition);
    }

    private void initRecyclerViewAdapter() {
        //第一个设置为正在预览
        selectedList.get(0).setPreview(true);
        viewBind.rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        previewRvAdapter = new PreviewRvAdapter(this, selectedList);
        viewBind.rv.setAdapter(previewRvAdapter);
        previewRvAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                viewBind.viewPagerFixed.setCurrentItem(position);
            }
        });
    }

    @Override
    protected void bindEvent() {
        viewBind.viewPagerFixed.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                viewBind.tvNumber.setText(String.valueOf(position + 1).concat("/").concat(String.valueOf(initialSelectedCount)));
                changeSelected(position);
                for (int i = 0; i < selectedList.size(); i++) {
                    selectedList.get(i).setPreview(false);
                }
                selectedList.get(position).setPreview(true);
                previewRvAdapter.notifyDataSetChanged();
                viewBind.rv.scrollToPosition(position);
            }
        });
        viewBind.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrBack(false);
            }
        });
        viewBind.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               confirmOrBack(true);
            }
        });
        viewBind.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewBind.viewPagerFixed.getCurrentItem();
                if (selectedList.get(position).isSelected()) {
                    selectedList.get(position).setSelected(false);
                    changeSelected(position);
                    selectedCount--;
                    changeBtnStatus(selectedCount);
                } else {
                    selectedList.get(position).setSelected(true);
                    changeSelected(position);
                    selectedCount++;
                    changeBtnStatus(selectedCount);
                }
                previewRvAdapter.notifyItemChanged(position);

            }
        });
    }

    @Override
    public void onBackPressed() {
        confirmOrBack(false);
    }

    private void confirmOrBack(boolean confirm) {
        List<ImageItem> backList = new ArrayList<>();
        for (ImageItem imageItem : selectedList) {
            if (imageItem.isSelected()) {
                backList.add(imageItem);
            }
        }
        Intent intent = new Intent();
        intent.putExtra(PictureSelectActivity.IMAGE_LIST, (Serializable) backList);
        //按返回键返回
        intent.putExtra(PREVIEW_CONFIRM, confirm);
        setResult(MultiPhotoActivity.IMAGE_PREVIEW_OK, intent);
        finish();
    }

    private void changeSelected(int position) {
        if (selectedList.get(position).isSelected()) {
            viewBind.imgCheck.setImageResource(R.drawable.ic_pictures_selected);
        } else {
            viewBind.imgCheck.setImageResource(R.drawable.ic_picture_unselected);
        }
    }

    private void changeBtnStatus(int count) {
        if (count > 0) {
            viewBind.tvConfirm.setEnabled(true);
            viewBind.tvConfirm.setText(getString(R.string.send_count_format, count, MAX_COUNT));
        } else {
            viewBind.tvConfirm.setEnabled(false);
            viewBind.tvConfirm.setText(getString(R.string.send));
        }
    }

}
