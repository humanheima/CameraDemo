package com.hm.camerademo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityPreviewBinding;
import com.hm.camerademo.listener.OnItemClickListener;
import com.hm.camerademo.ui.adapter.ImagePagerAdapter;
import com.hm.camerademo.ui.adapter.PreviewRvAdapter;
import com.hm.camerademo.ui.base.BaseActivity;
import com.hm.camerademo.util.ListUtil;
import com.hm.camerademo.util.localImages.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 1 .只预览已经选择的图片 可以取消选择
 */

public class PreviewActivity extends BaseActivity<ActivityPreviewBinding> {

    //是否是确定发送还是按返回键返回了
    public static final String PREVIEW_CONFIRM = "PREVIEW_CONFIRM";
    public static final String SELECTED_LIST = "SELECTED_LIST";
    public static final String IMAGE_LIST = "IMAGE_LIST";
    private static final int MAX_COUNT = 9;//图片选择的最大数量
    private static final String KEY_POSITION = "key_position";

    private List<ImageItem> allList;
    private int allCount;

    private List<ImageItem> selectedList;
    private int selectedCount;
    private PreviewRvAdapter previewRvAdapter;

    private boolean onlyPreviewSelected;

    public static void launch(Activity context, int position, List<ImageItem> selectedList, List<ImageItem> allList) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(KEY_POSITION, position);
        if (selectedList != null) {
            intent.putExtra(SELECTED_LIST, (Serializable) selectedList);
        }
        if (allList != null) {
            intent.putExtra(IMAGE_LIST, (Serializable) allList);
        }
        context.startActivityForResult(intent, MultiPhotoActivity.IMAGE_PREVIEW);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_preview;
    }

    @Override
    protected void initData() {
        int initPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        if (getIntent().getSerializableExtra(SELECTED_LIST) != null) {
            selectedList = (List<ImageItem>) getIntent().getSerializableExtra(SELECTED_LIST);
        }
        if (getIntent().getSerializableExtra(IMAGE_LIST) != null) {
            allList = (List<ImageItem>) getIntent().getSerializableExtra(IMAGE_LIST);
        }
        if (allList != null && selectedList != null) {
            //预览所有图片，同时又选中的图片
            onlyPreviewSelected = false;
            allCount = allList.size();
            selectedCount = selectedList.size();
            initView(initPosition, allList);
        } else if (allList != null) {
            //预览所有的图片，没有选中的图片
            onlyPreviewSelected = false;
            allCount = allList.size();
            selectedList = new ArrayList<>();
            initView(initPosition, allList);
        } else if (selectedList != null) {
            //只预览选中的图片
            onlyPreviewSelected = true;
            allCount = selectedList.size();
            selectedCount = selectedList.size();
            initView(initPosition, selectedList);
        }
        viewBind.tvNumber.setText(getString(R.string.count_format, initPosition + 1, allCount));
        changeBtnStatus(selectedCount);
        changeSelected(initPosition);
    }

    private void initView(int initPosition, List<ImageItem> list) {
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), list);
        viewBind.viewPagerFixed.setAdapter(imagePagerAdapter);
        viewBind.viewPagerFixed.setCurrentItem(initPosition);

        //第一个设置为正在预览
        if (onlyPreviewSelected) {
            selectedList.get(0).setPreview(true);
        }
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
                viewBind.tvNumber.setText(String.valueOf(position + 1).concat("/").concat(String.valueOf(allCount)));
                changeSelected(position);
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
        viewBind.llCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewBind.viewPagerFixed.getCurrentItem();
                if (onlyPreviewSelected) {
                    if (selectedList.get(position).isSelected()) {
                        selectedList.get(position).setSelected(false);
                        selectedCount--;
                    } else {
                        selectedList.get(position).setSelected(true);
                        selectedCount++;
                    }
                } else {
                    ImageItem imageItem = allList.get(position);
                    if (ListUtil.isEmpty(selectedList)) {
                        imageItem.setPreview(true);
                        imageItem.setSelected(true);
                        selectedList.add(imageItem);
                        selectedCount++;
                    } else {
                        String tempPath = imageItem.getImagePath();
                        boolean contain = false;
                        for (int i = 0; i < selectedList.size(); i++) {
                            if (selectedList.get(i).getImagePath().equals(tempPath)) {
                                contain = true;
                                selectedList.remove(i);
                                selectedCount--;
                                break;
                            }
                        }
                        if (!contain) {
                            if (selectedCount >= MAX_COUNT) {
                                Toast.makeText(PreviewActivity.this,
                                        String.format(getString(R.string.max_image_size), MAX_COUNT),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            imageItem.setPreview(true);
                            imageItem.setSelected(true);
                            selectedList.add(imageItem);
                            selectedCount++;
                        }
                    }
                }
                changeSelected(position);
                changeBtnStatus(selectedCount);
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
        if (onlyPreviewSelected) {
            for (int i = 0; i < selectedList.size(); i++) {
                selectedList.get(i).setPreview(false);
            }
            selectedList.get(position).setPreview(true);
            if (selectedList.get(position).isSelected()) {
                viewBind.imgCheck.setImageResource(R.drawable.ic_pictures_selected);
            } else {
                viewBind.imgCheck.setImageResource(R.drawable.ic_picture_unselected);
            }
            viewBind.rv.scrollToPosition(position);
            previewRvAdapter.notifyDataSetChanged();
        } else {
            int scrollToPosition = -1;
            if (!ListUtil.isEmpty(selectedList)) {
                String previewPath = allList.get(position).getImagePath();
                for (int i = 0; i < selectedList.size(); i++) {
                    if (selectedList.get(i).getImagePath().equals(previewPath)) {
                        selectedList.get(i).setPreview(true);
                        scrollToPosition = i;
                    } else {
                        selectedList.get(i).setPreview(false);
                    }
                }
            }
            if (scrollToPosition != -1) {
                viewBind.imgCheck.setImageResource(R.drawable.ic_pictures_selected);
                viewBind.rv.scrollToPosition(scrollToPosition);
            } else {
                viewBind.imgCheck.setImageResource(R.drawable.ic_picture_unselected);
            }
            previewRvAdapter.notifyDataSetChanged();
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
