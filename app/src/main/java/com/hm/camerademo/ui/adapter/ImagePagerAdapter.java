package com.hm.camerademo.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hm.camerademo.ui.fragment.ImagePreviewFragment;
import com.hm.camerademo.util.localImages.ImageItem;

import java.util.List;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private List<ImageItem> imageItemList;
    private boolean isLocalImage;

    public ImagePagerAdapter(FragmentManager fm, List<ImageItem> imageItemList, boolean isLocalImage) {
        super(fm);
        this.imageItemList = imageItemList;
        this.isLocalImage = isLocalImage;
    }

    @Override
    public int getCount() {
        return imageItemList == null ? 0 : imageItemList.size();
    }

    @Override
    public Fragment getItem(int position) {
        String url = imageItemList.get(position).getImagePath();
        return ImagePreviewFragment.newInstance(url, isLocalImage);
    }
}