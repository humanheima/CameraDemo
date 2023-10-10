package com.hm.imageslector.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hm.imageslector.fragment.PreviewFragment;
import com.hm.imageslector.localImages.ImageItem;

import java.util.List;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private List<ImageItem> imageItemList;

    public ImagePagerAdapter(FragmentManager fm, List<ImageItem> imageItemList) {
        super(fm);
        this.imageItemList = imageItemList;
    }

    @Override
    public int getCount() {
        return imageItemList == null ? 0 : imageItemList.size();
    }

    @Override
    public Fragment getItem(int position) {
        String url = imageItemList.get(position).getImagePath();
        return PreviewFragment.newInstance(url);
    }
}