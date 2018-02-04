package com.hm.camerademo.util.localImages;

import android.support.annotation.NonNull;

import java.util.List;

public class ImageItem implements Comparable<ImageItem> {

    private String id;
    private String orientation;
    private String imagePath;
    private String thumbnailPath;
    private boolean selected = false;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * {@link java.util.Collections#sort(List)}这个方法排序是 ASC的
     * 因为我们想倒序，所以 compareTo 方法，大的时候我们返回 -1 而小的时候我们返回1
     */
    @Override
    public int compareTo(@NonNull ImageItem o) {
        return this.time >= o.time ? -1 : 1;
    }
}
